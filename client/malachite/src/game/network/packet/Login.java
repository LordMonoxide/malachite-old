package game.network.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import network.packet.Packet;

public class Login extends Packet {
  private String _name;
  private String _pass;
  
  public Login(String name, String pass) {
    _name = name;
    _pass = pass;
  }
  
  public int getIndex() {
    return 1;
  }
  
  public ByteBuf serialize() {
    ByteBuf b = Unpooled.buffer(_name.length() + _pass.length() + 4);
    b.writeShort(_name.length());
    b.writeBytes(_name.getBytes());
    b.writeShort(_pass.length());
    b.writeBytes(_pass.getBytes());
    return b;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    
  }
  
  public void process() {
    
  }
  
  public static class Response extends Packet {
    public static final byte RESPONSE_OKAY = 0;
    public static final byte RESPONSE_NOT_AUTHD = 1;
    public static final byte RESPONSE_INVALID = 2;
    public static final byte RESPONSE_SQL_EXCEPTION = 3;
    
    private byte _response;
    
    public int getIndex() {
      return 2;
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
      switch(_response) {
        case RESPONSE_OKAY:
          System.out.println("Okay");
          break;
        case RESPONSE_NOT_AUTHD:
          System.out.println("Not auth'd");
          break;
        case RESPONSE_INVALID:
          System.out.println("Invalid");
          break;
        case RESPONSE_SQL_EXCEPTION:
          System.out.println("DEAD");
          break;
        default:
          System.out.println("Other?");
      }
    }
  }
}