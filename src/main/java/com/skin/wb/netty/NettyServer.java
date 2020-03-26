package com.skin.wb.netty;

import com.skin.wb.enums.RedisEnums;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetAddress;

/**
 * @description:
 * @author: moshiqing
 * @time: 2020/3/9 17:09
 */
@Component
public class NettyServer {

    @Value("${server.netty.port}")
    private Integer port;

    @Value("${server.netty.ip}")
    private String ip;

    private NioEventLoopGroup boss = new NioEventLoopGroup();
    private NioEventLoopGroup workes = new NioEventLoopGroup();

    @Autowired
    private RedisTemplate redisTemplate;

    @PostConstruct
    public void start() throws Exception {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boss, workes)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new MyChannelInitializer());
            ChannelFuture sync = serverBootstrap.bind(port).sync();
            if(sync.isDone()){
                serverOnline(port);
            }
    }

    @PreDestroy
    public void destory() throws Exception{
        boss.shutdownGracefully().sync();
        workes.shutdownGracefully().sync();
        InetAddress inetAddress=InetAddress.getLocalHost();
        String addAndPort=inetAddress.getHostAddress()+":"+port;
        redisTemplate.opsForSet().remove(RedisEnums.NETTYSERVER.getValue(),addAndPort);
        System.out.println("netty关闭"+addAndPort);
    }

    /**
     * 服务器上线
     */
    public void serverOnline(Integer port) throws Exception{
        InetAddress inetAddress=InetAddress.getLocalHost();
        String addAndPort=inetAddress.getHostAddress()+":"+port;
        System.out.println("netty启动------"+addAndPort);

        redisTemplate.opsForSet().remove(RedisEnums.NETTYSERVER.getValue(),addAndPort);
        redisTemplate.delete(RedisEnums.NETTYUSERSERVER.getValue()+":"+addAndPort);
        System.out.println("清空redis缓存-------启动完成");
        redisTemplate.opsForSet().add(RedisEnums.NETTYSERVER.getValue(),addAndPort);
        System.out.println("服务器的地址:" +  redisTemplate.opsForSet().members(RedisEnums.NETTYSERVER.getValue()));



    }
}
