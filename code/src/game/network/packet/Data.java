package game.network.packet;

import game.Game;
import game.data.Map;
import game.data.Sprite;
import game.data.util.Buffer;
import game.data.util.Serializable;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import network.packet.Packet;

public class Data {
  public static final byte DATA_TYPE_MAP = 1;
  public static final byte DATA_TYPE_SPRITE = 2;
  
  public static class Info extends Packet {
    private int _type;
    private Serializable[] _data;
    
    public int getIndex() {
      return 19;
    }
    
    public ByteBuf serialize() {
      return null;
    }
    
    public void deserialize(ByteBuf data) throws NotEnoughDataException {
      _type = data.readByte();
      _data = new Serializable[data.readInt()];
      
      byte[] arr;
      String name;
      int crc;
      
      for(int i = 0; i < _data.length; i++) {
        arr = new byte[data.readShort()];
        data.readBytes(arr);
        name = new String(arr);
        crc = data.readInt();
        
        switch(_type) {
          case DATA_TYPE_SPRITE: _data[i] = new Sprite(name, crc); break;
        }
      }
    }
    
    public void process() {
      switch(_type) {
        case DATA_TYPE_SPRITE: Game.getInstance().loadSprites(_data); break;
      }
    }
  }
  
  public static class Request extends Packet {
    private byte _type;
    private String _file;
    
    public Request(Serializable data) {
      if(data instanceof Sprite) _type = DATA_TYPE_SPRITE;
      _file = data.getFile();
    }
    
    public int getIndex() {
      return 14;
    }
    
    public ByteBuf serialize() {
      ByteBuf b = Unpooled.buffer();
      b.writeByte(_type);
      b.writeShort(_file.length());
      b.writeBytes(_file.getBytes());
      return b;
    }
    
    public void deserialize(ByteBuf data) throws NotEnoughDataException {
      
    }
    
    public void process() {
      
    }
  }
  
  public static class Response extends Packet {
    private byte _type;
    private String _file;
    private byte[] _data;
    
    public int getIndex() {
      return 15;
    }
    
    public ByteBuf serialize() {
      return null;
    }
    
    public void deserialize(ByteBuf data) throws NotEnoughDataException {
      _type = data.readByte();
      byte[] arr = new byte[data.readShort()];
      data.readBytes(arr);
      _file = new String(arr);
      _data = new byte[data.readInt()];
      data.readBytes(_data);
    }
    
    public void process() {
      Serializable data = null;
      
      switch(_type) {
        case DATA_TYPE_SPRITE: data = Game.getInstance().getSprite(_file); break;
      }
      
      data.deserialize(new Buffer(_data));
      data.save();
    }
  }
  
  public static class MapRequest extends Packet {
    private int _x, _y;
    private int _crc;
    
    public MapRequest(Map map) {
      _x = map.getX();
      _y = map.getY();
      _crc = map.getCRC();
    }
    
    public int getIndex() {
      return 18;
    }
    
    public ByteBuf serialize() {
      ByteBuf b = Unpooled.buffer();
      b.writeInt(_x);
      b.writeInt(_y);
      b.writeInt(_crc);
      return b;
    }
    
    public void deserialize(ByteBuf data) throws NotEnoughDataException {
      _x = data.readInt();
      _y = data.readInt();
      _crc = data.readInt();
    }
    
    public void process() {
      if(!Game.getInstance().getWorld().hasMap(_x, _y, _crc)) {
        Game.getInstance().getWorld().getRegion(_x, _y).getMap().request();
      }
    }
  }
  
  public static class MapResponse extends Packet {
    private int _x, _y;
    private byte[] _data;
    
    public int getIndex() {
      return 20;
    }
    
    public int getX() { return _x; }
    public int getY() { return _y; }
    public byte[] getData() { return _data; }
    
    public ByteBuf serialize() {
      return null;
    }
    
    public void deserialize(ByteBuf data) throws NotEnoughDataException {
      _x = data.readInt();
      _y = data.readInt();
      
      int length = data.readInt();
      
      if(length != 0) {
        _data = new byte[length];
        data.readBytes(_data);
      }
    }
    
    public void process() {
      Map m = Game.getInstance().getWorld().getRegion(_x, _y).getMap();
      m.deserialize(new Buffer(_data));
      m.save();
    }
  }
}