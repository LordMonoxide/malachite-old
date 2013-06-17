package game.network.packet;

import game.Game;
import game.data.Item;
import game.world.Entity;
import io.netty.buffer.ByteBuf;
import network.packet.Packet;

public class EntityEquip extends Packet {
  private int   _id;
  private int   _hand1;
  private int   _hand2;
  private int[] _armour = new int[Item.ITEM_TYPE_ARMOUR_COUNT];
  private int[] _bling  = new int[Item.ITEM_TYPE_BLING_COUNT];
  
  public int getIndex() {
    return 29;
  }
  
  public ByteBuf serialize() {
    return null;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    _id = data.readInt();
    _hand1 = data.readByte();
    _hand2 = data.readByte();
    
    for(int i = 0; i < Item.ITEM_TYPE_ARMOUR_COUNT; i++) {
      _armour[i] = data.readByte();
    }
    
    for(int i = 0; i < Item.ITEM_TYPE_BLING_COUNT; i++) {
      _bling[i] = data.readByte();
    }
  }
  
  public void process() {
    Entity e = Game.getInstance().getWorld().getEntity(_id);
    
    if(_hand1 != -1) e.equip().hand1(e.inv(_hand1));
    if(_hand2 != -1) e.equip().hand2(e.inv(_hand1));
    
    for(int i = 0; i < Item.ITEM_TYPE_ARMOUR_COUNT; i++) {
      if(_armour[i] != -1) e.equip().armour(i, e.inv(_armour[i]));
    }
    
    for(int i = 0; i < Item.ITEM_TYPE_BLING_COUNT; i++) {
      if(_bling[i] != -1) e.equip().bling(i, e.inv(_bling[i]));
    }
  }
}