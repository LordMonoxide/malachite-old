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
  private String[] _charName = new String[3];
  
  private AccountsTable() {
    super("accounts", "a_name");
    _create = _sql.prepareStatement("CREATE TABLE accounts (a_name VARCHAR(40) NOT NULL, a_pass CHAR(64) NOT NULL, a_p_id INT NOT NULL, a_c_name1 VARCHAR(16), a_c_name2 VARCHAR(16), a_c_name3 VARCHAR(16), CONSTRAINT pk_a_name UNIQUE (a_name), FOREIGN KEY (a_p_id) REFERENCES permissions(p_id), FOREIGN KEY (a_c_name1) REFERENCES characters(c_name), FOREIGN KEY (a_c_name2) REFERENCES characters(c_name), FOREIGN KEY (a_c_name3) REFERENCES characters(c_name))");
    _insert = _sql.prepareStatement("INSERT INTO accounts VALUES (?, ?, ?, ?, ?, ?)");
    _update = _sql.prepareStatement("UPDATE accounts SET a_p_id=?, a_c_name1=?, a_c_name2=?, a_c_name3=? WHERE a_name=?");
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
    int i = 1;
    _insert.setString(i++, _name);
    _insert.setString(i++, _pass);
    _insert.setInt(i++, _permissions);
    
    if(_charName[0] != null) _insert.setString(i++, _charName[0]);
    else                     _insert.setNull(i++, java.sql.Types.VARCHAR);
    if(_charName[1] != null) _insert.setString(i++, _charName[1]);
    else                     _insert.setNull(i++, java.sql.Types.VARCHAR);
    if(_charName[2] != null) _insert.setString(i++, _charName[2]);
    else                     _insert.setNull(i++, java.sql.Types.VARCHAR);
    
    _insert.execute();
  }
  
  public void select() throws SQLException {
    _select.setString(1, _name);
    _result = _select.executeQuery();
    
    if(_result.next()) {
      int i = 1;
      _name = _result.getString(i++);
      _pass = _result.getString(i++);
      _permissions = _result.getInt(i++);
      _charName[0] = _result.getString(i++);
      _charName[1] = _result.getString(i++);
      _charName[2] = _result.getString(i++);
    }
  }
  
  public void update() throws SQLException {
    int i = 1;
    _update.setInt(i++, _permissions);
    
    if(_charName[0] != null) _update.setString(i++, _charName[0]);
    else                     _update.setNull(i++, java.sql.Types.VARCHAR);
    if(_charName[1] != null) _update.setString(i++, _charName[1]);
    else                     _update.setNull(i++, java.sql.Types.VARCHAR);
    if(_charName[2] != null) _update.setString(i++, _charName[2]);
    else                     _update.setNull(i++, java.sql.Types.VARCHAR);
    
    _update.setString(i++, _name);
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
  
  public String getCharName(int index) {
    return _charName[index];
  }
  
  public void setCharName(int index, String charName) {
    _charName[index] = charName;
  }
}