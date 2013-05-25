package game.network.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import network.packet.Packet;
import game.Game;
import game.world.Entity;

public class EntityMoveStart extends Packet {
  private int _id;
  private float _x, _y;
  private float _bear;
  
  public EntityMoveStart() { }
  public EntityMoveStart(float x, float y, float bear) {
    _x    = x;
    _y    = y;
    _bear = bear;
  }
  
  public int getIndex() {
    return 12;
  }
  
  public ByteBuf serialize() {
    ByteBuf b = Unpooled.buffer();
    b.writeFloat(_x);
    b.writeFloat(_y);
    b.writeFloat(_bear);
    return b;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    _id = data.readInt();
    _x = data.readFloat();
    _y = data.readFloat();
    _bear = data.readFloat();
  }
  
  public void process() {
    System.out.println("MoveStart " + _id);
    Entity e = Game.getInstance().getWorld().getEntity(_id);
    e.setX(_x);
    e.setY(_y);
    e.setBear(_bear);
    e.startMoving();
  }
}