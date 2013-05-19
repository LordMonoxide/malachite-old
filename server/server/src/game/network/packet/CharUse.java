package game.network.packet;

import game.network.Connection;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import network.packet.Packet;

public class CharUse extends Packet {
  private int _id;
  
  public int getIndex() {
    return 8;
  }
  
  public ByteBuf serialize() {
    return null;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    _id = data.readInt();
  }
  
  public void process() {
    Connection c = (Connection)_connection;
    if(c.getAccount() == null) {
      c.kick("Not logged in");
      return;
    }
    
    if(_id < 0 || _id >= c.getPlayer().size()) {
      c.kick("Invalid char index");
      return;
    }
    
    c.setInGame(true);
    c.send(new Response());
  }
  
  public static class Response extends Packet {
    public int getIndex() {
      return 9;
    }
    
    public ByteBuf serialize() {
      return Unpooled.EMPTY_BUFFER;
    }
    
    public void deserialize(ByteBuf data) throws NotEnoughDataException {
      
    }
    
    public void process() {
      
    }
  }
}