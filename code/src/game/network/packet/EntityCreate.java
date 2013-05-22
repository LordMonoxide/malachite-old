package game.network.packet;

import game.Game;
import game.world.Entity;
import io.netty.buffer.ByteBuf;
import network.packet.Packet;

public class EntityCreate extends Packet {
  private Entity _entity;
  
  public int getIndex() {
    return 10;
  }
  
  public Entity getEntity() {
    return _entity;
  }
  
  public ByteBuf serialize() {
    return null;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    _entity = new Entity();
    _entity.setName(new String(data.readBytes(data.readShort()).array()));
    _entity.setSprite(Game.getInstance().getSprite(new String(data.readBytes(data.readShort()).array())));
    _entity.setAcc(data.readFloat());
    _entity.setDec(data.readFloat());
    _entity.setVelTerm(data.readFloat());
    _entity.setX(data.readFloat());
    _entity.setY(data.readFloat());
    _entity.setZ(data.readInt());
  }
  
  public void process() {
    Game.getInstance().getWorld().addEntity(_entity);
  }
}