package network.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.MessageBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;

public class DecoderLength extends ByteToMessageDecoder {
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, MessageBuf<Object> out) throws Exception {
    in.markReaderIndex();
    if(in.readableBytes() < 4) return;
    
    int length = in.readInt();
    
    if(length < 0) {
      throw new CorruptedFrameException("Negative length: " + length);
    }
    
    if(in.readableBytes() < length) {
      in.resetReaderIndex();
    } else {
      out.add(in.readBytes(length));
    }
  }
}