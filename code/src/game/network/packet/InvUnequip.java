package game.network.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import network.packet.Packet;

public class InvUnequip extends Packet {
  public static final int HAND   = 0;
  public static final int ARMOUR = 1;
  public static final int BLING  = 2;
  
  private int _type, _slot;
  
  public InvUnequip(int type, int slot) {
    _type = type;
    _slot = slot;
  }
  
  public int getIndex() {
    return 31;
  }
  
  public ByteBuf serialize() {
    ByteBuf b = Unpooled.buffer();
    b.writeByte(_type);
    b.writeByte(_slot);
    return b;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    
  }
  
  public void process() {
    
  }
}