package game.network.packet.menu;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import network.packet.Packet;

public class CharNew extends Packet {
  private String _name;
  private String _sprite;
  
  public CharNew(String name, String sprite) {
    _name = name;
    _sprite = sprite;
  }
  
  public int getIndex() {
    return 6;
  }
  
  public ByteBuf serialize() {
    ByteBuf b = Unpooled.buffer(_name.length() + 2);
    b.writeShort(_name.length());
    b.writeBytes(_name.getBytes());
    b.writeShort(_sprite.length());
    b.writeBytes(_sprite.getBytes());
    return b;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    
  }
  
  public void process() {
    
  }
  
  public static class Response extends Packet {
    public static final byte RESPONSE_OKAY = 0;
    public static final byte RESPONSE_EXISTS = 1;
    public static final byte RESPONSE_SQL_EXCEPTION = 2;
    
    private byte _response;
    
    public int getIndex() {
      return 7;
    }
    
    public byte getResponse() {
      return _response;
    }
    
    public ByteBuf serialize() {
      return null;
    }
    
    public void deserialize(ByteBuf data) throws NotEnoughDataException {
      _response = data.readByte();
    }
    
    public void process() {
      
    }
  }
}