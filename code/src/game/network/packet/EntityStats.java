package game.network.packet;

import game.Game;
import game.world.Entity;
import io.netty.buffer.ByteBuf;
import network.packet.Packet;

public class EntityStats extends Packet {
  private int _id;
  private int _str, _int, _dex;
  private float _weight;
  
  public int getIndex() {
    return 23;
  }
  
  public ByteBuf serialize() {
    return null;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    _id  = data.readInt();
    _str = data.readInt();
    _int = data.readInt();
    _dex = data.readInt();
    _weight = data.readFloat();
  }
  
  public void process() {
    Entity e = Game.getInstance().getWorld().getEntity(_id);
    e.stats().statSTR().val(_str);
    e.stats().statINT().val(_int);
    e.stats().statDEX().val(_dex);
    e.stats().weight(_weight);
  }
}