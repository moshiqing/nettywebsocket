package com.skin.wb.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * @description:
 * @author: moshiqing
 * @time: 2020/3/31 14:45
 */
public class ClientIdleStateTrigger extends ChannelInboundHandlerAdapter {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            IdleState state = ((IdleStateEvent) evt).state();
            if(state ==IdleState.READER_IDLE){
                System.out.println("产生读空闲");
            }else if(state ==IdleState.WRITER_IDLE){
                System.out.println("产生写空闲");
            } else if (state ==IdleState.ALL_IDLE) {
                System.out.println("读写空闲");
            }
        }
    }
}
