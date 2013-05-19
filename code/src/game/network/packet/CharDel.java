package game.network.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import network.packet.Packet;

public class CharDel extends Packet {
  private int _id;
  
  public CharDel(int id) {
    _id = id;
  }
  
  public int getIndex() {
    return 4;
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
      return 5;
    }
    
    public ByteBuf serialize() {
      return null;
    }
    
    public void deserialize(ByteBuf data) throws NotEnoughDataException {
      
    }
    
    public void process() {
      
    }
  }
}