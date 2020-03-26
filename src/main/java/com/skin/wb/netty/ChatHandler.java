package com.skin.wb.netty;

import com.alibaba.fastjson.JSON;
import com.skin.wb.enums.RedisEnums;
import com.skin.wb.entity.DataContent;
import com.skin.wb.service.KafkaMessageSend;
import com.skin.wb.utils.BeanUtil;
import com.skin.wb.utils.UserUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.springframework.data.redis.core.RedisTemplate;

import java.net.InetSocketAddress;

/**
 * @description:
 * @author: moshiqing
 * @time: 2020/3/9 17:24
 */
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    public static ChannelGroup channelGroup =new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    RedisTemplate redisTemplate = BeanUtil.getBean(RedisTemplate.class);

    KafkaMessageSend kafkaMessageSend = BeanUtil.getBean(KafkaMessageSend.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg){
        /**
         * 测试用的数据
         * {"message":"我要给2发送信息","toId":"1","type":2,"userId":"2"}
         */
        DataContent dataContent = JSON.parseObject(msg.text(),DataContent.class);
        String userId=dataContent.getUserId();
        Channel channel = ctx.channel();
        Integer type =1;
        if(dataContent.getType().equals(type)){
            redisTemplate.delete(RedisEnums.USERSCHANNEL.getValue()+userId);
            UserUtil.setUser(userId,channel);
            userOnline(userId,ctx);
        }else{
            System.out.println("服务端获取到的信息："+dataContent.getMessage());
            String toId = dataContent.getToId();
            Channel user = UserUtil.getUser(toId);
            if(user==null){
                System.out.println("系统找不到用户,需要向kafka发送订阅信息");
                kafkaMessageSend.sendMessage("serverMessage",JSON.toJSONString(dataContent));
            }else{
                System.out.println("直接发送信息");
                user.writeAndFlush(dataContent);
            }
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx){
        channelGroup.add(ctx.channel());
        System.out.println("当前客户端连接数量" + channelGroup.size());

//        InetSocketAddress k = (InetSocketAddress) ctx.channel().remoteAddress();
//        int clientPort = k.getPort();
//        String clienthostAddress = k.getAddress().getHostAddress();
//        System.out.println("客户端报道的地址:" + clienthostAddress + ":" + clientPort);
//        redisTemplate.opsForValue().increment(clienthostAddress+":"+port, 1);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx){
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx){
        channelGroup.add(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
    }

    /**
     * 用户上线 放入缓存
     * @param ctx
     */
    private void userOnline(String userId,ChannelHandlerContext ctx){
        InetSocketAddress k = (InetSocketAddress) ctx.channel().remoteAddress();
        int clientPort = k.getPort();
        String clienthostAddress = k.getAddress().getHostAddress();
        redisTemplate.opsForSet().add(RedisEnums.USERSCHANNEL.getValue()+userId,clienthostAddress + ":" + clientPort);

        //本机地址上有的用户缓存
        InetSocketAddress serverAddress = (InetSocketAddress) ctx.channel().localAddress();
        redisTemplate.opsForSet().add(RedisEnums.NETTYUSERSERVER.getValue()+":"+serverAddress.getAddress().getHostAddress()+":"+serverAddress.getPort(),userId);
    }

//    /**
//     * 用户下线
//     * @param ctx
//     */
//    private void userOffline(String userId,ChannelHandlerContext ctx){
//        InetSocketAddress k = (InetSocketAddress) ctx.channel().remoteAddress();
//        int clientPort = k.getPort();
//        String clienthostAddress = k.getAddress().getHostAddress();
//        redisTemplate.opsForSet().remove(RedisEnums.USERSCHANNEL.getValue()+":"+clienthostAddress,clienthostAddress + ":" + clientPort);
//    }
}
