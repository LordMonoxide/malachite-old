package game.network.packet;

import game.data.Map;
import game.data.util.Serializable;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import network.packet.Packet;

public class Data {
  public static final byte DATA_TYPE_MAP = 1;
  
  public static class Request extends Packet {
    private Serializable _data;
    private int _type;
    
    public Request() { }
    public Request(Serializable data) {
      _data = data;
      
      if(_data instanceof Map) _type = DATA_TYPE_MAP;
    }
    
    public int getIndex() {
      return 14;
    }
    
    public ByteBuf serialize() {
      ByteBuf b = Unpooled.buffer();
      b.writeByte(_type);
      
      if(_type == DATA_TYPE_MAP) {
        Map m = (Map)_data;
        b.writeInt(m.getX());
        b.writeInt(m.getY());
      } else {
        b.writeShort(_data.getFile().length());
        b.writeBytes(_data.getFile().getBytes());
      }
      
      b.writeInt(_data.getCRC());
      return b;
    }
    
    public void deserialize(ByteBuf data) throws NotEnoughDataException {
      /*int x = 0, y = 0;
      
      byte type = data.readByte();
      
      if(type == DATA_TYPE_MAP) {
        x = data.readInt();
        y = data.readInt();
      } else {
        byte[] arr = new byte[data.readShort()];
        data.readBytes(arr);
        String file = new String(arr);
      }
      
      int crc = data.readInt();
      
      switch(type) {
        case DATA_TYPE_MAP:
          if(!Game.getInstance().getWorld().hasMap(x, y, crc)) {
            
          }
          
          break;
      }*/
    }
    
    public void process() {
      
    }
  }
  
  public static class Response extends Packet {
    private byte _type;
    private int _x, _y;
    private String _file;
    private byte[] _data;
    
    public int getIndex() {
      return 15;
    }
    
    public int getX() { return _x; }
    public int getY() { return _y; }
    public String getFile() { return _file; }
    public byte[] getData() { return _data; }
    
    public ByteBuf serialize() {
      return null;
    }
    
    public void deserialize(ByteBuf data) throws NotEnoughDataException {
      _type = data.readByte();
      
      if(_type == DATA_TYPE_MAP) {
        _x = data.readInt();
        _y = data.readInt();
      } else {
        byte[] arr = new byte[data.readShort()];
        data.readBytes(arr);
        _file = new String(arr);
      }
      
      int length = data.readInt();
      
      if(length != 0) {
        _data = new byte[length];
        data.readBytes(_data);
      }
    }
    
    public void process() {
      
    }
  }
}