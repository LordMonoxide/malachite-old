package game.network.packet;

import io.netty.buffer.ByteBuf;
import network.packet.Packet;

public class Login extends Packet {
  private String _name;
  private String _pass;
  
  public int getIndex() {
    return 1;
  }
  
  public ByteBuf serialize() {
    return null;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    _name = new String(data.readBytes(data.readShort()).array());
    _pass = new String(data.readBytes(data.readShort()).array());
  }
  
  public void process() {
    
  }
}