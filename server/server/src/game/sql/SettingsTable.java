package game.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

import sql.Table;

public class SettingsTable extends Table {
  private static SettingsTable _instance = new SettingsTable();
  
  public static SettingsTable getInstance() {
    return _instance;
  }
  
  private double _version;
  
  public SettingsTable() {
    super("settings", "s_id");
    _create = _sql.prepareStatement("CREATE TABLE " + _tableName + " (" + _tableID + " INT NOT NULL AUTO_INCREMENT, s_client_version DOUBLE NOT NULL, CONSTRAINT pk_" + _tableID + " UNIQUE (" + _tableID + "))");
    _insert = _sql.prepareStatement("INSERT INTO " + _tableName + " VALUES (null, ?)");
    _update = _sql.prepareStatement("UPDATE " + _tableName + " SET s_client_version=? WHERE " + _tableID + "=?");
    _id = 1;
  }
  
  public double getVersion() {
    return _version;
  }
  
  public void setVersion(double version) {
    _version = version;
  }
  
  public boolean exists() {
    return super.exists();
  }
  
  public void create() throws SQLException {
    super.create();
    insert();
  }
  
  protected void insert() throws SQLException {
    int i = 1;
    _insert.setDouble(i++, _version);
    _insert.execute();
  }
  
  public void select() throws SQLException {
    super.select();
    ResultSet r = _result;
    
    r.next();
    int i = 2;
    _version = r.getDouble(i++);
  }
  
  public void update() throws SQLException {
    int i = 1;
    _update.setDouble(i++, _version);
    _update.setInt(i++, _id);
    _update.execute();
  }
}