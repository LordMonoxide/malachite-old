package game.network.packet;

import game.Game;
import game.world.Entity;
import io.netty.buffer.ByteBuf;
import network.packet.Packet;

public class EntityVitals extends Packet {
  public int getIndex() {
    return 22;
  }
  
  public ByteBuf serialize() {
    return null;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    Entity e = Game.getInstance().getWorld().getEntity(data.readInt());
    e.stats().vitalHP().set(data.readInt(), data.readInt());
    e.stats().vitalMP().set(data.readInt(), data.readInt());
  }
  
  public void process() {
    
  }
}