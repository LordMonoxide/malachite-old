package game.network.packet;

import game.Game;
import game.world.Entity;
import io.netty.buffer.ByteBuf;
import network.packet.Packet;

public class EntityStats extends Packet {
  public int getIndex() {
    return 23;
  }
  
  public ByteBuf serialize() {
    return null;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    Entity e = Game.getInstance().getWorld().getEntity(data.readInt());
    
    for(int i = 0; i < Entity.Stats.STATS; i++) {
      e.stats().stat(i).val = data.readInt();
    }
  }
  
  public void process() {
    
  }
}