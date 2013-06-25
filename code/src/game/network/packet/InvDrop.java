package game.network.packet;

import game.world.Entity;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import network.packet.Packet;

public class InvDrop extends Packet {
  private Entity.Inv _inv;
  
  public InvDrop(Entity.Inv inv) {
    _inv = inv;
  }
  
  public int getIndex() {
    return 30;
  }
  
  public ByteBuf serialize() {
    ByteBuf b = Unpooled.buffer();
    b.writeInt(_inv.index());
    b.writeInt(_inv.val());
    return b;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    
  }
  
  public void process() {
    
  }
}