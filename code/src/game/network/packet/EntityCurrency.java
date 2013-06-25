package game.network.packet;

import game.Game;
import game.world.Entity;
import io.netty.buffer.ByteBuf;
import network.packet.Packet;

public class EntityCurrency extends Packet {
  private int _id;
  private long _curr;
  
  public int getIndex() {
    return 32;
  }
  
  public ByteBuf serialize() {
    return null;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    _id = data.readInt();
    _curr = data.readLong();
  }
  
  public void process() {
    Entity e = Game.getInstance().getWorld().getEntity(_id);
    e.currency(_curr);
  }
}