package game.network.packet;

import game.world.Entity;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import network.packet.Packet;

public class InvUse extends Packet {
  private Entity.Inv _inv;
  
  public InvUse(Entity.Inv inv) {
    _inv = inv;
  }
  
  public int getIndex() {
    return 28;
  }
  
  public ByteBuf serialize() {
    ByteBuf b = Unpooled.buffer();
    b.writeInt(_inv.index());
    return b;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    
  }
  
  public void process() {
    
  }
}