package game.settings;

import game.data.util.Properties;
import game.data.util.Properties.InvalidDataException;
import game.sql.AccountsTable;
import game.sql.CharactersTable;
import game.sql.PermissionsTable;
import game.sql.SettingsTable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;

import sql.MySQL;

public class Settings {
  private static Properties _settings = new Properties();
  private static File _file = new File("../sql.conf");
  
  public static void load() {
    if(!_file.exists()) save();
    
    FileInputStream fs = null;
    
    try {
      fs = new FileInputStream(_file);
      _settings.load(fs);
      SQL._instance.load(_settings.getString("host"), _settings.getString("db"), _settings.getString("user"), _settings.getString("pass"));
      sql.SQL.create(MySQL.class);
      sql.SQL.getInstance().connect(Settings.SQL.Host(), Settings.SQL.DB(), Settings.SQL.User(), Settings.SQL.Pass());
      
      SettingsTable settingsTable = SettingsTable.getInstance();
      PermissionsTable permissionsTable = PermissionsTable.getInstance();
      AccountsTable accountTable = AccountsTable.getInstance();
      CharactersTable charTable = CharactersTable.getInstance();
      
      try {
        if(!settingsTable.exists()) {
          System.out.println("Creating settings table...");
          settingsTable.setVersion(Settings.Net.Version());
          settingsTable.create();
        } else {
          settingsTable.select();
          
          try {
            Net._instance.load(settingsTable.getVersion(), _settings.getInt("port"));
          } catch(InvalidDataException e) { }
        }
        
        if(!permissionsTable.exists()) {
          System.out.println("Creating permissions table...");
          permissionsTable.create();
        }
        
        if(!charTable.exists()) {
          System.out.println("Creating character table...");
          charTable.create();
        }
        
        if(!accountTable.exists()) {
          System.out.println("Creating account table...");
          accountTable.create();
        }
      } catch(SQLException e) {
        e.printStackTrace();
      }
    } catch(IOException e) {
      e.printStackTrace();
    } finally {
      try {
        fs.close();
      } catch(IOException e) {
        e.printStackTrace();
      }
    }
  }
  
  public static void save() {
    _settings.setProperty("host", Settings.SQL.Host());
    _settings.setProperty("db",   Settings.SQL.DB());
    _settings.setProperty("user", Settings.SQL.User());
    _settings.setProperty("pass", Settings.SQL.Pass());
    _settings.setProperty("port", String.valueOf(Settings.Net.Port()));
    
    try {
      _settings.store(new FileOutputStream(_file), null);
    } catch(IOException e) {
      e.printStackTrace();
    }
  }
  
  public static class SQL {
    private static SQL _instance = new SQL();
    
    private String _host = "127.0.0.1";
    private String _db   = "m8";
    private String _user = "root";
    private String _pass = "";
    
    public static String Host() { return _instance._host; }
    public static String DB()   { return _instance._db; }
    public static String User() { return _instance._user; }
    public static String Pass() { return _instance._pass; }
    
    public void load(String host, String db, String user, String pass) {
      _host = host;
      _db = db;
      _user = user;
      _pass = pass;
    }
  }
  
  public static class Net {
    private static Net _instance = new Net();
    
    private double _version = 0.01;
    private int _port = 4000;
    
    public static double Version() { return _instance._version; }
    public static    int Port()    { return _instance._port; }
    
    private void load(double version, int port) {
      _version = version;
      _port = port;
    }
  }
}