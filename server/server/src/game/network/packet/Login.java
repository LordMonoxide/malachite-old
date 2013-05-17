package game.network.packet;

import java.sql.SQLException;
import java.util.ArrayList;

import game.data.account.Account;
import game.data.account.Player;
import game.network.Connection;
import game.sql.AccountsTable;
import game.sql.CharactersTable;
import game.sql.PermissionsTable;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import network.packet.Packet;
import network.util.Crypto;

public class Login extends Packet {
  private String _name;
  private String _pass;
  
  public int getIndex() {
    return 1;
  }
  
  public ByteBuf serialize() {
    return null;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    _name = new String(data.readBytes(data.readShort()).array());
    _pass = new String(data.readBytes(data.readShort()).array());
  }
  
  public void process() {
    Connection c = (Connection)_connection;
    if(c.isLoggedIn()) {
      c.kick("Already logged in");
      return;
    }
    
    if(!Crypto.validateText(_name) || !Crypto.validateHash(_pass)) {
      c.kick("Invalid name/pass");
      return;
    }
    
    AccountsTable tableAccount = AccountsTable.getInstance();
    PermissionsTable tablePermission = PermissionsTable.getInstance();
    CharactersTable tableChar = CharactersTable.getInstance();
    
    Response response = new Response();
    
    try {
      tableAccount.setName(_name);
      tableAccount.select();
      
      if(_pass.equals(tableAccount.getPass())) {
        tablePermission.setID(tableAccount.getPermissions());
        tablePermission.select();
        
        if(tablePermission.getPermissions().canLogin()) {
          Account a = new Account(tableAccount.getID());
          c.setPlayer(tableChar.selectFromAccount(a));
          c.setLoggedIn(true);
          c.setAccount(a);
          a.setName(_name);
          a.setPermissions(tablePermission.getPermissions());
          
          response._response = Response.RESPONSE_OKAY;
          response._player = c.getPlayer();
          
          Permissions p = new Permissions();
          p.setPermissions(a.getPermissions());
          c.send(p);
        } else {
          response._response = Response.RESPONSE_NOT_AUTHD;
        }
      } else {
        response._response = Response.RESPONSE_INVALID;
      }
    } catch(SQLException e) {
      response._response = Response.RESPONSE_SQL_EXCEPTION;
      e.printStackTrace();
    }
    
    c.send(response);
  }
  
  public static class Response extends Packet {
    public static final byte RESPONSE_OKAY = 0;
    public static final byte RESPONSE_NOT_AUTHD = 1;
    public static final byte RESPONSE_INVALID = 2;
    public static final byte RESPONSE_SQL_EXCEPTION = 3;
    
    private byte _response;
    private ArrayList<Player> _player;
    
    public int getIndex() {
      return 2;
    }
    
    public ByteBuf serialize() {
      ByteBuf b = Unpooled.buffer(4);
      b.writeByte(_response);
      
      if(_player != null) {
        b.writeInt(_player.size());
        
        for(Player p : _player) {
          b.writeShort(p.getName().length());
          b.writeBytes(p.getName().getBytes());
        }
      }
      
      return b;
    }
    
    public void deserialize(ByteBuf data) throws NotEnoughDataException {
      
    }
    
    public void process() {
      
    }
  }
}