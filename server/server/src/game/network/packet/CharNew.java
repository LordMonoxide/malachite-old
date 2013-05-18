package game.network.packet;

import java.sql.SQLException;

import game.data.account.Character;
import game.network.Connection;
import game.sql.CharactersTable;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import network.packet.Packet;
import network.util.Crypto;

public class CharNew extends Packet {
  private String _name;
  
  public int getIndex() {
    return 6;
  }
  
  public ByteBuf serialize() {
    return null;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    _name = new String(data.readBytes(data.readShort()).array());
  }
  
  public void process() {
    Connection c = (Connection)_connection;
    if(c.getAccount() == null) {
      c.kick("Not logged in");
      return;
    }
    
    if(!Crypto.validateText(_name)) {
      c.kick("Invalid name");
      return;
    }
    
    Response response = new Response();
    
    CharactersTable table = CharactersTable.getInstance();
    int index;
    
    try {
      index = table.find(_name);
      
      if(index == -1) {
        Character character = new Character(0, c.getAccount());
        character.setName(_name);
        character.setWorld("default");
        character.setX(256);
        character.setY(256);
        character.setZ(2);
        table.insert(character);
        response._response = Response.RESPONSE_OKAY;
      } else {
        response._response = Response.RESPONSE_EXISTS;
      }
    } catch(SQLException e) {
      response._response = Response.RESPONSE_SQL_EXCEPTION;
      e.printStackTrace();
    }
    
    c.send(response);
  }
  
  public static class Response extends Packet {
    public static final byte RESPONSE_OKAY = 0;
    public static final byte RESPONSE_EXISTS = 1;
    public static final byte RESPONSE_SQL_EXCEPTION = 2;
    
    private byte _response;
    
    public int getIndex() {
      return 7;
    }
    
    public ByteBuf serialize() {
      ByteBuf b = Unpooled.buffer(1);
      b.writeByte(_response);
      return b;
    }
    
    public void deserialize(ByteBuf data) throws NotEnoughDataException {
      
    }
    
    public void process() {
      
    }
  }
}