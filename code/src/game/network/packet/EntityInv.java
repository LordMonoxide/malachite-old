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
    
    for(int i = 0; i < _data.length; i++) {
      if(_data[i] != null) {
        e.inv(i).item(Game.getInstance().getItem(_data[i].file));
        e.inv(i).val(_data[i].val);
      } else {
        e.inv(i).item(null);
        e.inv(i).val(0);
      }
    }
  }
  
  private class TempData {
    private String file;
    private int val;
  }
}