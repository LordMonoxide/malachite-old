package game.settings;

import game.data.util.Properties;
import game.data.util.Properties.InvalidDataException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Settings {
  public static final Net Net = new Net();
  public static final Map Map = new Map();
  
  private static Properties _settings = new Properties();
  private static File _file = new File("../settings.conf");
  
  public static void load() {
    if(!_file.exists()) save();
    
    try {
      _settings.load(new FileInputStream(_file));
      Net.load();
      Map.load();
      Map.init();
    } catch(IOException e) {
      e.printStackTrace();
    }
  }
  
  public static void save() {
    Net.save();
    Map.save();
    
    try {
      _settings.store(new FileOutputStream(_file), null);
    } catch(IOException e) {
      e.printStackTrace();
    }
  }
  
  public static class Net {
    public final double Version = 0.01;
    public String IP = "home.monoxidedesign.com";
    public int Port = 4000;
    
    private void load() {
      IP = _settings.getString("NetIP");
      
      try {
        Port = _settings.getInt("NetPort");
      } catch(InvalidDataException e) {
        e.printStackTrace();
      }
    }
    
    private void save() {
      _settings.setProperty("NetIP", IP);
      _settings.setProperty("NetPort", Integer.toString(Port));
    }
  }
  
  public static class Map {
    public final Tile Tile;
    public final Attrib Attrib;
    
    public int Size = 512;
    public int Depth = 5;
    
    private Map() {
      Tile = new Tile();
      Attrib = new Attrib();
    }
    
    private void init() {
      Tile.init();
      Attrib.init();
    }
    
    private void load() {
      try {
        Size = _settings.getInt("MapSize");
      } catch(InvalidDataException e) {
        e.printStackTrace();
      }
      
      try {
        Depth = _settings.getInt("MapDepth");
      } catch(InvalidDataException e) {
        e.printStackTrace();
      }
      
      Tile.load();
      Attrib.load();
    }
    
    private void save() {
      _settings.setProperty("MapSize", Integer.toString(Size));
      _settings.setProperty("MapDepth", Integer.toString(Depth));
      
      Tile.save();
      Attrib.save();
    }
    
    public static class Tile {
      public int Size = 32;
      public int Count;
      
      private void init() {
        Count = Settings.Map.Size / Size;
      }
      
      private void load() {
        try {
          Size = _settings.getInt("MapTileSize");
        } catch(InvalidDataException e) {
          e.printStackTrace();
        }
      }
      
      private void save() {
        _settings.setProperty("MapTileSize", Integer.toString(Size));
      }
    }
    
    public static class Attrib {
      public int Size = 16;
      public int Count;
      
      private void init() {
        Count = Settings.Map.Size / Size;
      }
      
      private void load() {
        try {
          Size = _settings.getInt("MapAttribSize");
        } catch(InvalidDataException e) {
          e.printStackTrace();
        }
      }
      
      private void save() {
        _settings.setProperty("MapAttribSize", Integer.toString(Size));
      }
    }
  }
}