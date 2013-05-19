package network.codec;

import network.packet.Packet;
import network.packet.Packet.NotEnoughDataException;
import network.packet.Packets;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.MessageBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

public class Decoder extends MessageToMessageDecoder<ByteBuf> {
  protected void decode(ChannelHandlerContext ctx, ByteBuf msg, MessageBuf<Object> out) {
    byte index = msg.readByte();
    Packet packet = null;
    
    try {
      packet = Packets.create(index, msg);
    } catch(IndexOutOfBoundsException e) {
      //TODO: disconnect or do something here
      e.printStackTrace();
      return;
    } catch(NotEnoughDataException e) {
      e.printStackTrace();
      return;
    }
    
    out.add(packet);
  }
}