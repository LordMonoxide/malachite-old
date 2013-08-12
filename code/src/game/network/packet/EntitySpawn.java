package game.network.packet;

import game.Game;
import game.world.Entity;
import io.netty.buffer.ByteBuf;
import network.packet.Packet;

public class EntitySpawn extends Packet {
  private int _id;
  private float _x, _y;
  private byte _z;
  
  public int id() { return _id; }
  
  public int getIndex() {
    return 41;
  }
  
  public ByteBuf serialize() {
    return null;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    _id = data.readInt();
    _x = data.readFloat();
    _y = data.readFloat();
    _z = data.readByte();
  }
  
  public void process() {
    Entity e = Game.getInstance().getWorld().getEntity(_id);
    e.xyz(_x, _y, _z);
    e.spawn();
  }
}