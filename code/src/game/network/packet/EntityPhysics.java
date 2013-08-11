package game.network.packet;

import game.Game;
import game.world.Entity;
import io.netty.buffer.ByteBuf;
import network.packet.Packet;

public class EntityPhysics extends Packet {
  private int _id;
  private float _acc, _dec, _velTerm;
  
  public int getIndex() {
    return 40;
  }
  
  public ByteBuf serialize() {
    return null;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    _id = data.readInt();
    _acc = data.readFloat();
    _dec = data.readFloat();
    _velTerm = data.readFloat();
  }
  
  public void process() {
    Entity e = Game.getInstance().getWorld().getEntity(_id);
    e.setAcc(_acc);
    e.setDec(_dec);
    e.setVelTerm(_velTerm);
  }
}