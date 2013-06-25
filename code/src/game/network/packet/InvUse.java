package game.network.packet;

import game.data.Item;
import game.world.Entity;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import network.packet.Packet;

public class InvUse extends Packet {
  private Entity.Inv _inv;
  private int _slot;
  
  public InvUse(Entity.Inv inv) { this(inv, 0); }
  public InvUse(Entity.Inv inv, int slot) {
    _inv = inv;
    _slot = slot;
  }
  
  public int getIndex() {
    return 27;
  }
  
  public ByteBuf serialize() {
    ByteBuf b = Unpooled.buffer();
    b.writeInt(_inv.index());
    
    switch(_inv.item().getType() & Item.ITEM_TYPE_BITMASK) {
      case Item.ITEM_TYPE_WEAPON:
      case Item.ITEM_TYPE_SHIELD:
        b.writeByte(_slot);
    }
    
    return b;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    
  }
  
  public void process() {
    
  }
}