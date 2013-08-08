package game.network.packet;

import game.Game;
import game.world.Entity;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import network.packet.Packet;

public class EntityAttack extends Packet {
  private double _angle;
  
  private Entity _attacker, _defender;
  private int _damage;
  
  public EntityAttack() { }
  public EntityAttack(double angle) {
    _angle = angle;
  }
  
  public Entity attacker() { return _attacker; }
  public Entity defender() { return _defender; }
  public int damage() { return _damage; }
  
  public int getIndex() {
    return 39;
  }
  
  public ByteBuf serialize() {
    ByteBuf b = Unpooled.buffer();
    b.writeDouble(_angle);
    return b;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    _attacker = Game.getInstance().getWorld().getEntity(data.readInt());
    
    int defender = data.readInt();
    if(defender != 0) {
      _defender = Game.getInstance().getWorld().getEntity(defender);
      _damage = data.readInt();
    }
  }
  
  public void process() {
    
  }
}