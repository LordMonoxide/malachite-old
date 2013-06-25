package game.network.packet;

import game.Game;
import game.world.Entity;
import io.netty.buffer.ByteBuf;
import network.packet.Packet;

public class EntityVitals extends Packet {
  private int _id;
  private int _hp, _hpMax;
  private int _mp, _mpMax;
  
  public int getIndex() {
    return 21;
  }
  
  public ByteBuf serialize() {
    return null;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    _id    = data.readInt();
    _hpMax = data.readInt();
    _hp    = data.readInt();
    _mpMax = data.readInt();
    _mp    = data.readInt();
  }
  
  public void process() {
    Entity e = Game.getInstance().getWorld().getEntity(_id);
    e.stats().vitalHP().set(_hpMax, _hp);
    e.stats().vitalMP().set(_mpMax, _mp);
  }
}