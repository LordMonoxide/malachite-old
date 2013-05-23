package game.network.packet;

import game.Game;
import game.world.Entity;
import game.world.World;
import io.netty.buffer.ByteBuf;
import network.packet.Packet;

public class EntityDestroy extends Packet {
  private int _id;
  
  public int getIndex() {
    return 11;
  }
  
  public ByteBuf serialize() {
    return null;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    _id = data.readInt();
  }
  
  public void process() {
    World w = Game.getInstance().getWorld();
    Entity e = w.getEntity(_id);
    w.removeEntity(e);
  }
}