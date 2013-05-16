package sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class Table {
  protected SQL _sql;
  
  protected String _tableName;
  protected String _tableID;
  protected int _id;
  protected ResultSet _result;
  
  protected PreparedStatement _create;
  protected PreparedStatement _insert;
  protected PreparedStatement _update;
  
  protected PreparedStatement _drop;
  protected PreparedStatement _select;
  protected PreparedStatement _delete;
  
  protected Table() { }
  protected Table(String name, String id) {
    _sql = SQL.getInstance();
    
    _tableName = name;
    _tableID = id;
    
    _drop   = _sql.prepareStatement("DROP TABLE " + _tableName);
    _select = _sql.prepareStatement("SELECT * FROM " + _tableName + " WHERE " + _tableID + "=?");
    _delete = _sql.prepareStatement("DELETE FROM " + _tableName + " WHERE " + _tableID + "=?");
  }
  
  public void close() throws SQLException {
    if(_create != null) _create.close();
    if(_insert != null) _insert.close();
    if(_update != null) _update.close();
    if(_drop   != null) _drop  .close();
    if(_select != null) _select.close();
    if(_delete != null) _delete.close();
  }
  
  protected boolean exists() {
    return _sql.tableExists(_tableName);
  }
  
  protected void create() throws SQLException {
    _create.execute();
  }
  
  protected void drop() throws SQLException {
    _drop.execute();
  }
  
  protected void select() throws SQLException {
    _select.setInt(1, _id);
    _result = _select.executeQuery();
  }
  
  protected void delete() throws SQLException {
    _delete.setInt(1, _id);
    _delete.execute();
  }
  
  protected abstract void insert() throws SQLException;
  protected abstract void update() throws SQLException;
  
  public int getID() {
    return _id;
  }
  
  public void setID(int id) {
    _id = id;
  }
}