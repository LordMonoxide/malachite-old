package sql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class SQL {
  private static SQL _instance;
  
  public static void create(Class<? extends SQL> sql) {
    try {
      _instance = sql.newInstance();
    } catch(InstantiationException | IllegalAccessException e) {
      e.printStackTrace();
    }
  }
  
  public static SQL getInstance() {
    return _instance;
  }
  
  private Connection _connection;
  
  protected abstract Connection create(String url, String db, String name, String pass) throws SQLException, ClassNotFoundException;
  public boolean connect(String url, String db, String name, String pass) {
    try {
      _connection = create(url, db, name, pass);
      return true;
    } catch(SQLException e) {
      System.out.println("Exception: "   + e.getMessage());
      System.out.println("State: "       + e.getSQLState());
      System.out.println("VendorError: " + e.getErrorCode());
      e.printStackTrace();
    } catch(ClassNotFoundException e) {
      e.printStackTrace();
    }
    
    return false;
  }
  
  public void close() {
    try {
      if(_connection != null) _connection.close();
    } catch(SQLException e) {
      e.printStackTrace();
    }
  }
  
  public boolean tableExists(String name) {
    try {
      DatabaseMetaData dbm = _connection.getMetaData();
      return dbm.getTables(null, null, name, null).next();
    } catch(SQLException e) {
      e.printStackTrace();
    }
    
    return false;
  }
  
  public PreparedStatement prepareStatement(String sql) {
    try {
      return _connection.prepareStatement(sql);
    } catch(SQLException e) {
      e.printStackTrace();
    }
    
    return null;
  }
}