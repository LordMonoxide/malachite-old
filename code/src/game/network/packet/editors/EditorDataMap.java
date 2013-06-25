package game.network.packet.editors;

import game.Game;
import game.data.Map;
import game.data.util.Buffer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import network.packet.Packet;

public class EditorDataMap {
  public static class Request extends Packet {
    private int _x, _y;
    
    public Request(Map map) {
      _x = map.getX();
      _y = map.getY();
    }
    
    public int getIndex() {
      return 33;
    }
    
    public ByteBuf serialize() {
      ByteBuf b = Unpooled.buffer();
      b.writeInt(_x);
      b.writeInt(_y);
      return b;
    }
    
    public void deserialize(ByteBuf data) throws NotEnoughDataException {
      
    }
    
    public void process() {
      
    }
  }
  
  public static class Response extends Packet {
    private int _x, _y;
    private byte[] _data;
    
    public int getIndex() {
      return 34;
    }
    
    public int getX() { return _x; }
    public int getY() { return _y; }
    public byte[] getData() { return _data; }
    
    public ByteBuf serialize() {
      return null;
    }
    
    public void deserialize(ByteBuf data) throws NotEnoughDataException {
      _x = data.readInt();
      _y = data.readInt();
      _data = new byte[data.readInt()];
      data.readBytes(_data);
    }
    
    public void process() {
      Map m = Game.getInstance().getWorld().getRegion(_x, _y).getMap();
      m.deserialize(new Buffer(_data));
    }
  }
}