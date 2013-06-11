package game.network.packet;

import game.Game;
import game.world.Entity;
import io.netty.buffer.ByteBuf;
import network.packet.Packet;

public class EntityInvUpdate extends Packet {
  private int _id;
  private int _index;
  private String _file;
  private int _val;
  
  public int getIndex() {
    return 26;
  }
  
  public ByteBuf serialize() {
    return null;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    _id    = data.readInt();
    _index = data.readByte();
    
    int size = data.readByte();
    
    if(size != 0) {
      byte[] arr = new byte[size];
      data.readBytes(arr);
      _file = new String(arr);
      _val  = data.readInt();
    }
  }
  
  public void process() {
    Entity e = Game.getInstance().getWorld().getEntity(_id);
    Entity.Inv inv = null;
    
    if(_file != null) {
      inv = new Entity.Inv(_index);
      inv.item(Game.getInstance().getItem(_file));
      inv.val(_val);
    }
    
    e.inv(_index, inv);
  }
}