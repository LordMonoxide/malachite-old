package game.sql;

import game.data.account.Account;
import game.data.account.Character;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import sql.SQL;

public class CharactersTable {
  private static CharactersTable _instance = new CharactersTable();
  
  public static CharactersTable getInstance() {
    return _instance;
  }
  
  private SQL _sql;
  
  private PreparedStatement _create;
  private PreparedStatement _insert;
  private PreparedStatement _update;
  
  private PreparedStatement _drop;
  private PreparedStatement _selectAccount;
  private PreparedStatement _selectPlayer;
  private PreparedStatement _selectExist;
  private PreparedStatement _delete;
  
  public CharactersTable() {
    _sql = SQL.getInstance();
    _drop          = _sql.prepareStatement("DROP TABLE characters");
    _selectAccount = _sql.prepareStatement("SELECT c_id, c_name FROM characters WHERE c_a_id=?");
    _selectPlayer  = _sql.prepareStatement("SELECT c_name, c_world, c_x, c_y, c_z FROM characters WHERE c_id=? AND c_a_id=?");
    _selectExist   = _sql.prepareStatement("SELECT c_id FROM characters WHERE c_name=?");
    _delete        = _sql.prepareStatement("DELETE FROM characters WHERE c_id=?");
    _create        = _sql.prepareStatement("CREATE TABLE characters (c_id INT NOT NULL AUTO_INCREMENT, c_a_id INT NOT NULL, c_name VARCHAR(16) NOT NULL, c_world VARCHAR(40) NOT NULL, c_x FLOAT NOT NULL, c_y FLOAT NOT NULL, c_z INT NOT NULL, CONSTRAINT pk_c_id UNIQUE (c_id), CONSTRAINT pk_c_name UNIQUE (c_name), FOREIGN KEY (c_a_id) REFERENCES accounts(a_id))");
    _insert        = _sql.prepareStatement("INSERT INTO characters VALUES (null, ?, ?, ?, ?, ?, ?)");
    _update        = _sql.prepareStatement("UPDATE characters SET c_world=?, c_x=?, c_y=?, c_z=? WHERE c_id=?");
  }
  
  public void close() throws SQLException {
    if(_create != null) _create.close();
    if(_insert != null) _insert.close();
    if(_update != null) _update.close();
    if(_drop   != null) _drop  .close();
    if(_selectAccount != null) _selectAccount.close();
    if(_selectPlayer != null) _selectPlayer.close();
    if(_selectExist != null) _selectExist.close();
    if(_delete != null) _delete.close();
  }
  
  public boolean exists() {
    return _sql.tableExists("characters");
  }
  
  public void create() throws SQLException {
    _create.execute();
  }
  
  public void drop() throws SQLException {
    _drop.execute();
  }
  
  public void insert(Character p) throws SQLException {
    int i = 1;
    _insert.setInt(i++, p.getAccount().getID());
    _insert.setString(i++, p.getName());
    _insert.setString(i++, p.getWorld());
    _insert.setFloat(i++, p.getX());
    _insert.setFloat(i++, p.getY());
    _insert.setInt(i++, p.getZ());
    _insert.execute();
    
    _selectExist.setString(1, p.getName());
    ResultSet r = _selectExist.executeQuery();
    if(r.next()) {
      p.setID(r.getInt(1));
    }
    r.close();
  }
  
  public ArrayList<Character> selectFromAccount(Account a) throws SQLException {
    _selectAccount.setInt(1, a.getID());
    ResultSet r = _selectAccount.executeQuery();
    
    ArrayList<Character> player = new ArrayList<Character>();
    while(r.next()) {
      int i = 1;
      Character p = new Character(r.getInt(i++), a);
      p.setName(r.getString(i++));
      player.add(p);
    }
    
    r.close();
    
    return player;
  }
  
  public ArrayList<Character> selectFromAccount(Account a, int id) throws SQLException {
    _selectPlayer.setInt(1, id);
    _selectPlayer.setInt(2, a.getID());
    ResultSet r = _selectPlayer.executeQuery();
    
    ArrayList<Character> player = new ArrayList<Character>();
    while(r.next()) {
      int i = 1;
      Character p = new Character(id, a);
      p.setName(r.getString(i++));
      p.setWorld(r.getString(i++));
      p.setX(r.getFloat(i++));
      p.setY(r.getFloat(i++));
      p.setZ(r.getInt(i++));
      player.add(p);
    }
    
    r.close();
    
    return player;
  }
  
  public int find(String name) throws SQLException {
    _selectExist.setString(1, name);
    _selectExist.execute();
    
    ResultSet r = _selectExist.getResultSet();
    if(r.next()) {
      int i = r.getInt(1);
      r.close();
      return i;
    }
    
    r.close();
    return -1;
  }
  
  public void update(Character p) throws SQLException {
    int i = 1;
    _update.setString(i++, p.getWorld());
    _update.setFloat(i++, p.getX());
    _update.setFloat(i++, p.getY());
    _update.setInt(i++, p.getZ());
    _update.setInt(i++, p.getID());
    _update.execute();
  }
  
  public void delete(Character p) throws SQLException {
    _delete.setInt(1, p.getID());
    _delete.execute();
  }
}