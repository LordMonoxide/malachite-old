package game.network.packet.editors;

import game.data.Item;
import game.data.NPC;
import game.data.Sprite;
import game.data.util.GameData;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import network.packet.Packet;

public class EditorData {
  public static final byte DATA_TYPE_SPRITE = 1;
  public static final byte DATA_TYPE_ITEM = 2;
  public static final byte DATA_TYPE_NPC = 3;
  
  public static class List extends Packet {
    private int _type;
    private ListData[] _data;
    
    public List() { }
    public List(int type) {
      _type = type;
    }
    
    public int getIndex() {
      return 38;
    }
    
    public int type() {
      return _type;
    }
    
    public ListData[] data() {
      return _data;
    }
    
    public ByteBuf serialize() {
      ByteBuf b = Unpooled.buffer();
      b.writeByte(_type);
      return b;
    }
    
    public void deserialize(ByteBuf data) throws NotEnoughDataException {
      byte[] arr;
      int length;
      
      _type = data.readByte();
      _data = new ListData[data.readInt()];
      for(int i = 0; i < _data.length; i++) {
        _data[i] = new ListData();
        
        if((length = data.readByte()) != 0) {
          arr = new byte[length];
          data.readBytes(arr);
          _data[i]._file = new String(arr);
        }
        
        if((length = data.readByte()) != 0) {
          arr = new byte[length];
          data.readBytes(arr);
          _data[i]._name = new String(arr);
        }
        
        if((length = data.readShort()) != 0) {
          arr = new byte[length];
          data.readBytes(arr);
          _data[i]._note = new String(arr);
        }
      }
    }
    
    public void process() {
      
    }
    
    public static class ListData {
      private String _file, _name, _note;
      
      public String file() { return _file; }
      public String name() { return _name; }
      public String note() { return _note; }
    }
  }
  
  public static class Request extends Packet {
    private byte _type;
    private String _file;
    
    public Request(GameData data) {
      if(data instanceof Sprite)     _type = DATA_TYPE_SPRITE;
      if(data instanceof Item)       _type = DATA_TYPE_ITEM;
      if(data instanceof NPC)        _type = DATA_TYPE_NPC;
      
      _file = data.getFile();
    }
    
    public int getIndex() {
      return 36;
    }
    
    public ByteBuf serialize() {
      System.out.println("Requesting EditorData " + _file + " of type " + _type);
      
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
      return 37;
    }
    
    public byte   getType() { return _type; }
    public String getFile() { return _file; }
    public byte[] getData() { return _data; }
    
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
      
    }
  }
}