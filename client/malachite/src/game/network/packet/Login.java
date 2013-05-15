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
}