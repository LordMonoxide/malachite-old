package game.network.packet;

import game.Game;
import game.world.Entity;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import network.packet.Packet;

public class EntityMoveStop extends Packet {
  private int _id;
  private float _x, _y;
  
  public EntityMoveStop() { }
  public EntityMoveStop(Entity e) {
    _x = e.getX();
    _y = e.getY();
  }
  
  public int getIndex() {
    return 13;
  }
  
  public ByteBuf serialize() {
    ByteBuf b = Unpooled.buffer();
    b.writeFloat(_x);
    b.writeFloat(_y);
    return b;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    _id = data.readInt();
    _x = data.readFloat();
    _y = data.readFloat();
  }
  
  public void process() {
    System.out.println("MoveStop " + _id);
    Entity e = Game.getInstance().getWorld().getEntity(_id);
    e.setX(_x);
    e.setY(_y);
    e.stopMoving();
  }
}