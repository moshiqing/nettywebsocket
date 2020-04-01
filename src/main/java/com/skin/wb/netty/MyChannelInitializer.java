package com.skin.wb.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: moshiqing
 * @time: 2020/3/9 17:18
 */
@Component
public class MyChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(new HttpServerCodec());
        ch.pipeline().addLast(new ChunkedWriteHandler());
        ch.pipeline().addLast(new IdleStateHandler(5,7,15, TimeUnit.SECONDS));
        ch.pipeline().addLast(new ClientIdleStateTrigger());
        ch.pipeline().addLast(new HttpObjectAggregator(1024*64));
        ch.pipeline().addLast(new WebSocketServerProtocolHandler("/ws"));
        ch.pipeline().addLast(new ChatHandler());
    }
}
