package game.network.packet;

import game.Game;
import game.data.Item;
import game.data.NPC;
import game.data.Sprite;
import game.data.util.Buffer;
import game.data.util.GameData;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import network.packet.Packet;

public class Data {
  public static final byte DATA_TYPE_SPRITE = 1;
  public static final byte DATA_TYPE_ITEM = 2;
  public static final byte DATA_TYPE_NPC = 3;
  
  public static class Request extends Packet {
    private byte _type;
    private String _file;
    
    public Request(GameData data) {
      if(data instanceof Sprite) _type = DATA_TYPE_SPRITE;
      if(data instanceof Item)   _type = DATA_TYPE_ITEM;
      if(data instanceof NPC)    _type = DATA_TYPE_NPC;
      
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
    
    public String getFile() {
      return _file;
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
      GameData data = null;
      
      switch(_type) {
        case DATA_TYPE_SPRITE: data = Game.getInstance().getSprite(_file); break;
        case DATA_TYPE_ITEM:   data = Game.getInstance().getItem(_file);   break;
        case DATA_TYPE_NPC:    data = Game.getInstance().getNPC(_file);    break;
      }
      
      data.deserialize(new Buffer(_data));
      data.save();
    }
  }
}