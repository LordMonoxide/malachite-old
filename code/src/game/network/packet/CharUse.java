package game.network.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import network.packet.Packet;

public class CharUse extends Packet {
  private int _id;
  
  public CharUse(int id) {
    _id = id;
  }
  
  public int getIndex() {
    return 8;
  }
  
  public ByteBuf serialize() {
    ByteBuf b = Unpooled.buffer(4);
    b.writeInt(_id);
    return b;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    
  }
  
  public void process() {
    
  }
  
  public static class Response extends Packet {
    public int getIndex() {
      return 9;
    }
    
    public ByteBuf serialize() {
      return Unpooled.EMPTY_BUFFER;
    }
    
    public void deserialize(ByteBuf data) throws NotEnoughDataException {
      
    }
    
    public void process() {
      
    }
  }
}