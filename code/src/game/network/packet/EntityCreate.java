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
    _entity.setID(data.readInt());
    _entity.setName(new String(data.readBytes(data.readShort()).array()));
    _entity.setSprite(new String(data.readBytes(data.readShort()).array()));
    _entity.setInitialXY(data.readFloat(), data.readFloat());
    _entity.setZ(data.readByte());
    if(data.readBoolean()) _entity.spawn();
    //_entity.setType(Entity.Type.valueOf(data.readInt()));
  }
  
  public void process() {
    Game.getInstance().addEntity(_entity);
  }
}