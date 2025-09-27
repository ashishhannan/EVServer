package com.ev07b.handler;

import com.ev07b.model.EV07BMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.logging.Logger;

public class EV07BBusinessHandler extends SimpleChannelInboundHandler<EV07BMessage> {
    private static final Logger log = Logger.getLogger(EV07BBusinessHandler.class.getName());

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, EV07BMessage msg) throws Exception {
        log.info("Received: " + msg);
        if (msg.ackRequested()) {
            byte ackProperties = 0x00;
            byte[] ackBody = new byte[] { (byte)0x7F, 0x01, 0x00 };
            EV07BMessage ack = new EV07BMessage(ackProperties, ackBody.length, 0, msg.sequenceId, ackBody);
            ctx.writeAndFlush(ack);
            log.info("Sent ACK for seq=" + msg.sequenceId);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.warning("Exception: " + cause.getMessage());
        ctx.close();
    }
}
