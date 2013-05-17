package game.sql;

import java.sql.SQLException;

import sql.Table;

public class AccountsTable extends Table {
  private static AccountsTable _instance = new AccountsTable();
  
  public static AccountsTable getInstance() {
    return _instance;
  }
  
  private String _name;
  private String _pass;
  private int _permissions;
  
  private AccountsTable() {
    super("accounts", "a_name");
    _create = _sql.prepareStatement("CREATE TABLE accounts (a_id INT NOT NULL AUTO_INCREMENT, a_name VARCHAR(40) NOT NULL, a_pass CHAR(64) NOT NULL, a_p_id INT NOT NULL, CONSTRAINT pk_a_id UNIQUE (a_id), CONSTRAINT pk_a_name UNIQUE (a_name), FOREIGN KEY (a_p_id) REFERENCES permissions(p_id))");
    _insert = _sql.prepareStatement("INSERT INTO accounts VALUES (null, ?, ?, ?)");
    _update = _sql.prepareStatement("UPDATE accounts SET a_name=?, a_pass=?, a_p_id=? WHERE a_id=?");
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
    _insert.setString(i++, _name);
    _insert.setString(i++, _pass);
    _insert.setInt(i++, _permissions);
    _insert.execute();
  }
  
  public void select() throws SQLException {
    _select.setString(1, _name);
    _result = _select.executeQuery();
    
    if(_result.next()) {
      int i = 1;
      _id = _result.getInt(i++);
      _name = _result.getString(i++);
      _pass = _result.getString(i++);
      _permissions = _result.getInt(i++);
    }
  }
  
  public void update() throws SQLException {
    int i = 1;
    _update.setString(i++, _name);
    _update.setString(i++, _pass);
    _update.setInt(i++, _permissions);
    _update.setInt(i++, _id);
    _update.execute();
  }
  
  public void delete() throws SQLException {
    _delete.setString(1, _name);
    _delete.execute();
  }
  
  public String getName() {
    return _name;
  }
  
  public void setName(String name) {
    _name = name;
  }
  
  public String getPass() {
    return _pass;
  }
  
  public void setPass(String pass) {
    _pass = pass;
  }
  
  public int getPermissions() {
    return _permissions;
  }
  
  public void setPermissions(int permissions) {
    _permissions = permissions;
  }
}