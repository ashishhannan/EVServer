package com.ev07b.codec;

import com.ev07b.model.EV07BMessage;
import com.ev07b.util.Crc16;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class EV07BEncoder extends MessageToByteEncoder<EV07BMessage> {
    private static final byte HEADER = (byte)0xAB;

    @Override
    protected void encode(ChannelHandlerContext ctx, EV07BMessage msg, ByteBuf out) throws Exception {
        out.writeByte(HEADER);
        out.writeByte(msg.properties);
        out.writeShortLE(msg.length & 0xffff);
        int crc = Crc16.compute(msg.body) & 0xffff;
        out.writeShortLE(crc);
        out.writeShortLE(msg.sequenceId & 0xffff);
        out.writeBytes(msg.body);
    }
}
