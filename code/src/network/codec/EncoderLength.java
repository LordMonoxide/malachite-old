package network.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class EncoderLength extends MessageToByteEncoder<ByteBuf> {
  protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
    int length = msg.readableBytes();
    out.ensureWritable(length + 4);
    out.writeInt(length);
    out.writeBytes(msg, msg.readerIndex(), length);
  }
}