package game.network.packet.editors;

import java.util.ArrayList;

import game.data.Map;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import network.packet.Packet;

public class SaveMap extends Packet {
  private ArrayList<Map> _map = new ArrayList<Map>();
  
  public int getIndex() {
    return 17;
  }
  
  public void addMap(Map map) {
    _map.add(map);
  }
  
  public int size() {
    return _map.size();
  }
  
  public ByteBuf serialize() {
    ByteBuf b = Unpooled.buffer();
    
    byte[] data;
    for(Map map : _map) {
      data = map.serialize().serialize();
      b.writeInt(map.getX());
      b.writeInt(map.getY());
      b.writeInt(data.length);
      b.writeBytes(data);
    }
    
    return b;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    
  }
  
  public void process() {
    
  }
}