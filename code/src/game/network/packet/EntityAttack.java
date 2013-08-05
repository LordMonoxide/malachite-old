package game.network.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import network.packet.Packet;

public class EntityAttack extends Packet {
  private double _angle;
  
  public EntityAttack(double angle) {
    _angle = angle;
  }
  
  public int getIndex() {
    return 39;
  }
  
  public ByteBuf serialize() {
    ByteBuf b = Unpooled.buffer();
    b.writeDouble(_angle);
    return b;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    
  }
  
  public void process() {
    
  }
}