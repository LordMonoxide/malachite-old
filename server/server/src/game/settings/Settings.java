package game.settings;

import game.data.util.Properties;
import game.data.util.Properties.InvalidDataException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Settings {
  public static final Net Net = new Net();
  
  private static Properties _settings = new Properties();
  private static File _file = new File("../settings.conf");
  
  public static void load() {
    if(!_file.exists()) save();
    
    try {
      _settings.load(new FileInputStream(_file));
      Net.load();
    } catch(IOException e) {
      e.printStackTrace();
    }
  }
  
  public static void save() {
    Net.save();
    
    try {
      _settings.store(new FileOutputStream(_file), null);
    } catch(IOException e) {
      e.printStackTrace();
    }
  }
  
  public static class Net {
    public double Version = 0.01;
    public int Port = 4000;
    
    private void load() {
      try {
        Version = _settings.getDouble("NetVersion");
      } catch(InvalidDataException e) {
        e.printStackTrace();
      }
      
      try {
        Port = _settings.getInt("NetPort");
      } catch(InvalidDataException e) {
        e.printStackTrace();
      }
    }
    
    private void save() {
      _settings.setProperty("NetVersion", Double.toString(Version));
      _settings.setProperty("NetPort", Integer.toString(Port));
    }
  }
}