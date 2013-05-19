package game.network.packet;

import game.settings.Settings;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import network.packet.Packet;

public class Connect extends Packet {
  public int getIndex() {
    return 0;
  }
  
  public ByteBuf serialize() {
    ByteBuf b = Unpooled.buffer(4);
    b.writeDouble(Settings.Net.Version);
    return b;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    
  }
  
  public void process() {
    
  }
}