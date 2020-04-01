package com.skin.wb.netty;

import com.alibaba.fastjson.JSON;
import com.skin.wb.enums.ChatTypes;
import com.skin.wb.enums.RedisEnums;
import com.skin.wb.entity.DataContent;
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

//    KafkaMessageSend kafkaMessageSend = BeanUtil.getBean(KafkaMessageSend.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg){
        /**
         * 测试用的数据
         * {"message":"我要给2发送信息","toId":"1","type":2,"userId":"2"}
         */
        DataContent dataContent = JSON.parseObject(msg.text(),DataContent.class);
        String userId=dataContent.getUserId();
        Channel channel = ctx.channel();
        if(dataContent.getType().equals(ChatTypes.register.getValue())){
            redisTemplate.delete(RedisEnums.USERSCHANNEL.getValue()+userId);
            UserUtil.setUser(userId,channel);
            userOnline(userId,ctx);
        }else if(dataContent.getType().equals(ChatTypes.Single.getValue())){
            //开始单人聊天
            System.out.println("服务端获取到的信息："+dataContent.getMessage());
            String toId = dataContent.getToId();
            //用户是否是系统内在线，并且存在这个服务器内的 不在线则直接离线 在线就直接确定服务器
            Channel user = UserUtil.getUser(toId);
            if(user==null){
                System.out.println("系统找不到用户,需要向kafka发送订阅信息");
                redisTemplate.convertAndSend("serverMessage",JSON.toJSONString(dataContent));
            }else{
                Channel currentChannel = channelGroup.find(user.id());
                if(currentChannel==null){
                    System.out.println("用户在channel已经被移除了,已经下线");
                    //todo 保存聊天信息到数据库，为未接收状态
                }else{
                    System.out.println("直接发送信息");
                    currentChannel.writeAndFlush(dataContent);
                }
            }
        }else if(dataContent.getType().equals(ChatTypes.gourp.getValue())){
            //异步发送群组在线用户
            //1.在本机用户直接发送
            //2.查找非本机用户发送到指定的主题
            //离线用户直接存聊天记录

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
        channelGroup.remove(ctx.channel());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx){
        channelGroup.add(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        channelGroup.remove(ctx.channel());
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
}
