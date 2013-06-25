package game.network.packet;

import game.Game;
import game.data.Map;
import game.data.util.Buffer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import network.packet.Packet;

public class DataMap {
  public static class Request extends Packet {
    private int _x, _y;
    private int _crc;
    
    public Request(Map map) {
      _x = map.getX();
      _y = map.getY();
      _crc = map.getRev();
    }
    
    public int getIndex() {
      return 18;
    }
    
    public ByteBuf serialize() {
      ByteBuf b = Unpooled.buffer();
      b.writeInt(_x);
      b.writeInt(_y);
      b.writeInt(_crc);
      return b;
    }
    
    public void deserialize(ByteBuf data) throws NotEnoughDataException {
      _x = data.readInt();
      _y = data.readInt();
      _crc = data.readInt();
    }
    
    public void process() {
      if(!Game.getInstance().getWorld().hasMap(_x, _y, _crc)) {
        Game.getInstance().getWorld().getRegion(_x, _y).getMap().request();
      }
    }
  }
  
  public static class Response extends Packet {
    private int _x, _y;
    private byte[] _data;
    
    public int getIndex() {
      return 19;
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
      
      int length = data.readInt();
      
      if(length != 0) {
        _data = new byte[length];
        data.readBytes(_data);
      }
    }
    
    public void process() {
      Map m = Game.getInstance().getWorld().getRegion(_x, _y).getMap();
      m.deserialize(new Buffer(_data));
      m.save();
    }
  }
}