package game.network.packet;

import game.Game;
import game.data.Map;
import game.data.util.Serializable;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import network.packet.Packet;

public class Data {
  public static final byte DATA_TYPE_MAP = 1;
  
  public static class Request extends Packet {
    private byte _type;
    private int _x, _y;
    private String _file;
    private int _crc;
    
    public Request() { }
    public Request(Serializable data) {
      if(data instanceof Map) {
        _type = DATA_TYPE_MAP;
        
        Map m = (Map)data;
        _x = m.getX();
        _y = m.getY();
      } else {
        _file = data.getFile();
      }
      
      _crc = data.getCRC();
    }
    
    public int getIndex() {
      return 14;
    }
    
    public ByteBuf serialize() {
      ByteBuf b = Unpooled.buffer();
      b.writeByte(_type);
      
      if(_type == DATA_TYPE_MAP) {
        b.writeInt(_x);
        b.writeInt(_y);
      } else {
        b.writeShort(_file.length());
        b.writeBytes(_file.getBytes());
      }
      
      b.writeInt(_crc);
      return b;
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
      
      _crc = data.readInt();
    }
    
    public void process() {
      switch(_type) {
        case DATA_TYPE_MAP:
          if(!Game.getInstance().getWorld().hasMap(_x, _y, _crc)) {
            Game.getInstance().getWorld().getRegion(_x, _y).getMap().request();
          }
          
          break;
      }
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