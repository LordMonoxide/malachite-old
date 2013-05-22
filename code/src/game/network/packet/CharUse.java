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
    public static final byte RESPONSE_OKAY = 0;
    public static final byte RESPONSE_SQL_ERROR = 1;
    
    private byte _response;
    private String _world;
    
    public int getIndex() {
      return 9;
    }
    
    public byte getResponse() {
      return _response;
    }
    
    public String getWorld() {
      return _world;
    }
    
    public ByteBuf serialize() {
      return null;
    }
    
    public void deserialize(ByteBuf data) throws NotEnoughDataException {
      _response = data.readByte();
      
      if(_response == RESPONSE_OKAY) {
        _world = new String(data.readBytes(data.readShort()).array());
      }
    }
    
    public void process() {
      
    }
  }
}