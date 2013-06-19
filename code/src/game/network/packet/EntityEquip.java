package game.network.packet;

import game.Game;
import game.data.Item;
import game.world.Entity;
import io.netty.buffer.ByteBuf;
import network.packet.Packet;

public class EntityEquip extends Packet {
  private int      _id;
  private String   _hand1;
  private String   _hand2;
  private String[] _armour = new String[Item.ITEM_TYPE_ARMOUR_COUNT];
  private String[] _bling  = new String[Item.ITEM_TYPE_BLING_COUNT];
  
  public int getIndex() {
    return 29;
  }
  
  public ByteBuf serialize() {
    return null;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    _id = data.readInt();
    
    int size;
    byte[] arr;
    
    if((size = data.readByte()) != 0) {
      arr = new byte[size];
      data.readBytes(arr);
      _hand1 = new String(arr);
    }
    
    if((size = data.readByte()) != 0) {
      arr = new byte[size];
      data.readBytes(arr);
      _hand2 = new String(arr);
    }
    
    for(int i = 0; i < Item.ITEM_TYPE_ARMOUR_COUNT; i++) {
      if((size = data.readByte()) != 0) {
        arr = new byte[size];
        data.readBytes(arr);
        _armour[i] = new String(arr);
      }
    }
    
    for(int i = 0; i < Item.ITEM_TYPE_BLING_COUNT; i++) {
      if((size = data.readByte()) != 0) {
        arr = new byte[size];
        data.readBytes(arr);
        _bling[i] = new String(arr);
      }
    }
  }
  
  public void process() {
    Game game = Game.getInstance();
    
    Entity e = game.getWorld().getEntity(_id);
    
    if(_hand1 != null) e.equip().hand1(game.getItem(_hand1));
    if(_hand2 != null) e.equip().hand2(game.getItem(_hand2));
    
    for(int i = 0; i < Item.ITEM_TYPE_ARMOUR_COUNT; i++) {
      if(_armour[i] != null) e.equip().armour(i, game.getItem(_armour[i]));
    }
    
    for(int i = 0; i < Item.ITEM_TYPE_BLING_COUNT; i++) {
      if(_bling[i] != null) e.equip().bling(i, game.getItem(_bling[i]));
    }
  }
}