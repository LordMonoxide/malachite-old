package game.sql;

import java.sql.SQLException;

import sql.Table;

public class CharactersTable extends Table {
  private static CharactersTable _instance = new CharactersTable();
  
  public static CharactersTable getInstance() {
    return _instance;
  }
  
  private int _account;
  private String _name;
  private String _world;
  private float _x, _y;
  private int _z;
  
  public CharactersTable() {
    super("characters", "c_name");
    _create = _sql.prepareStatement("CREATE TABLE characters (c_id INT NOT NULL AUTO_INCREMENT, c_a_id INT NOT NULL, c_name VARCHAR(16) NOT NULL, c_world VARCHAR(40) NOT NULL, c_x FLOAT NOT NULL, c_y FLOAT NOT NULL, c_z INT NOT NULL, CONSTRAINT pk_c_id UNIQUE (c_id), CONSTRAINT pk_c_name UNIQUE (c_name), FOREIGN KEY (c_a_id) REFERENCES accounts(a_id))");
    _insert = _sql.prepareStatement("INSERT INTO characters VALUES (?, ?, ?, ?, ?, ?)");
    _update = _sql.prepareStatement("UPDATE characters SET c_world=?, c_x=?, c_y=?, c_z=? WHERE c_id=?");
  }
  
  public boolean exists() {
    return super.exists();
  }
  
  public void create() throws SQLException {
    super.create();
  }
  
  public void drop() throws SQLException {
    super.drop();
  }
  
  public void insert() throws SQLException {
    int i = 2;
    _insert.setInt(i++, _account);
    _insert.setString(i++, _name);
    _insert.setString(i++, _world);
    _insert.setFloat(i++, _x);
    _insert.setFloat(i++, _y);
    _insert.setInt(i++, _z);
    _insert.execute();
  }
  
  public void select() throws SQLException {
    _select.setString(1, _name);
    _result = _select.executeQuery();
    
    if(_result.next()) {
      int i = 1;
      _id      = _result.getInt(i++);
      _account = _result.getInt(i++);
      _name    = _result.getString(i++);
      _world   = _result.getString(i++);
      _x       = _result.getFloat(i++);
      _y       = _result.getFloat(i++);
      _z       = _result.getInt(i++);
    }
  }
  
  public void update() throws SQLException {
    int i = 1;
    _update.setString(i++, _world);
    _update.setFloat(i++, _x);
    _update.setFloat(i++, _y);
    _update.setInt(i++, _z);
    _update.setInt(i++, _id);
    _update.execute();
  }
  
  public void delete() throws SQLException {
    _delete.setString(1, _name);
    _delete.execute();
  }
  
  public int getAccount() {
    return _account;
  }
  
  public void setAccount(int account) {
    _account = account;
  }
  
  public String getName() {
    return _name;
  }
  
  public void setName(String name) {
    _name = name;
  }
  
  public String getWorld() {
    return _world;
  }
  
  public void setWorld(String world) {
    _world = world;
  }
  
  public float getX() {
    return _x;
  }
  
  public void setX(float x) {
    _x = x;
  }
  
  public float getY() {
    return _y;
  }
  
  public void setY(float y) {
    _y = y;
  }
  
  public int getZ() {
    return _z;
  }
  
  public void setZ(int z) {
    _z = z;
  }
}