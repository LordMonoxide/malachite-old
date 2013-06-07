package game.network.packet.editors;

import java.util.ArrayList;

import game.data.Map;
import game.data.util.Serializable;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import network.packet.Packet;

public abstract class Save extends Packet {
  protected ArrayList<Serializable> _data = new ArrayList<Serializable>();
  
  public abstract int getIndex();
  
  public void addData(Serializable data) {
    _data.add(data);
  }
  
  public int size() {
    return _data.size();
  }
  
  public ByteBuf serialize() {
    ByteBuf b = Unpooled.buffer();
    
    byte[] arr;
    for(Serializable data : _data) {
      arr = data.serialize().serialize();
      b.writeShort(data.getFile().length());
      b.writeBytes(data.getFile().getBytes());
      b.writeInt(arr.length);
      b.writeBytes(arr);
    }
    
    return b;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    
  }
  
  public void process() {
    
  }
  
  public static class MapData extends Save {
    public int getIndex() {
      return 17;
    }
    
    public ByteBuf serialize() {
      ByteBuf b = Unpooled.buffer();
      
      byte[] arr;
      for(Serializable data : _data) {
        Map m = (Map)data;
        arr = m.serialize().serialize();
        b.writeInt(m.getX());
        b.writeInt(m.getY());
        b.writeInt(arr.length);
        b.writeBytes(arr);
      }
      
      return b;
    }
  }
  
  public static class SpriteData extends Save {
    public int getIndex() {
      return 21;
    }
  }
}