package com.ev07b.codec;

import com.ev07b.model.EV07BMessage;
import com.ev07b.util.Crc16;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;

public class EV07BFrameDecoder extends ByteToMessageDecoder {
    private static final byte HEADER = (byte)0xAB;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        in.markReaderIndex();
        if (in.readableBytes() < 1) {
            return;
        }
        byte header = in.readByte();
        if (header != HEADER) {
            boolean found = false;
            while (in.isReadable()) {
                if (in.readByte() == HEADER) { found = true; break; }
            }
            if (!found) { return; }
        }

        if (in.readableBytes() < 1 + 2 + 2 + 2) {
            in.resetReaderIndex();
            return;
        }
        byte properties = in.readByte();
        int lenLow = in.readUnsignedByte();
        int lenHigh = in.readUnsignedByte();
        int bodyLen = (lenHigh << 8) | lenLow;
        if (bodyLen < 0 || bodyLen > 1024) {
            ctx.fireExceptionCaught(new IllegalArgumentException("Invalid bodyLen=" + bodyLen));
            return;
        }
        if (in.readableBytes() < 2 + 2 + bodyLen) {
            in.resetReaderIndex();
            return;
        }
        int checksum = in.readUnsignedShortLE();
        int seqId = in.readUnsignedShortLE();
        byte[] bodyBytes = new byte[bodyLen];
        in.readBytes(bodyBytes);

        int calculated = Crc16.compute(bodyBytes) & 0xffff;
        if (calculated != (checksum & 0xffff)) {
            ctx.fireUserEventTriggered("CRC_MISMATCH");
            out.add(new EV07BMessage(properties, bodyLen, checksum, seqId, bodyBytes));
            return;
        }
        out.add(new EV07BMessage(properties, bodyLen, checksum, seqId, bodyBytes));
    }
}
