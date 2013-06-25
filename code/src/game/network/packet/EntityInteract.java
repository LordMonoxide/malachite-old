package game.network.packet;

import game.world.Entity;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import network.packet.Packet;

public class EntityInteract extends Packet {
  private Entity _entity;
  
  public EntityInteract(Entity entity) {
    _entity = entity;
  }
  
  public int getIndex() {
    return 26;
  }
  
  public ByteBuf serialize() {
    ByteBuf b = Unpooled.buffer();
    b.writeInt(_entity.getID());
    return b;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    
  }
  
  public void process() {
    
  }
}