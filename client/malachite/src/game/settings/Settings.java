package game.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Settings {
  public static final Map Map = new Map();
  
  private static Properties _settings = new Properties();
  private static File _file = new File("../settings.conf");
  
  public static void load() {
    if(!_file.exists()) save();
    
    try {
      _settings.load(new FileInputStream(_file));
      Map.load();
      Map.init();
      
    } catch(IOException e) {
      e.printStackTrace();
    }
  }
  
  public static void save() {
    Map.save();
    
    try {
      _settings.store(new FileOutputStream(_file), null);
    } catch(IOException e) {
      e.printStackTrace();
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
      Size = Integer.parseInt(_settings.getProperty("MapSize"));
      Depth = Integer.parseInt(_settings.getProperty("MapDepth"));
      
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
        Size = Integer.parseInt(_settings.getProperty("MapTileSize"));
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
        Size = Integer.parseInt(_settings.getProperty("MapAttribSize"));
      }
      
      private void save() {
        _settings.setProperty("MapAttribSize", Integer.toString(Size));
      }
    }
  }
}