package game.network.packet;

import game.Game;
import game.settings.Settings;
import game.world.Entity;
import io.netty.buffer.ByteBuf;
import network.packet.Packet;

public class EntityInv extends Packet {
  private int _id;
  private TempData[] _data;
  
  public int getIndex() {
    return 25;
  }
  
  public ByteBuf serialize() {
    return null;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    _id = data.readInt();
    _data = new TempData[Settings.Player.Inventory.Size];
    
    int size;
    byte[] arr;
    for(int i = 0; i < Settings.Player.Inventory.Size; i++) {
      size = data.readByte();
      
      if(size != 0) {
        arr = new byte[size];
        data.readBytes(arr);
        
        _data[i] = new TempData();
        _data[i].file = new String(arr);
        _data[i].val  = data.readInt();
      }
    }
  }
  
  public void process() {
    Entity e = Game.getInstance().getWorld().getEntity(_id);
    
    Entity.Inv inv[] = new Entity.Inv[_data.length];
    for(int i = 0; i < _data.length; i++) {
      if(_data[i] != null) {
        inv[i] = new Entity.Inv(i);
        inv[i].item(Game.getInstance().getItem(_data[i].file));
        inv[i].val(_data[i].val);
      }
    }
    
    e.inv(inv);
  }
  
  private class TempData {
    private String file;
    private int val;
  }
}