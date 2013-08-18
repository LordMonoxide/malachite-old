package game.data;

import java.io.File;
import java.nio.ByteBuffer;

import game.Game;
import game.data.util.Buffer;
import game.data.util.GameData;
import game.network.packet.DataMap;
import game.settings.Settings;
import game.world.World;
import graphics.gl00.Canvas;
import graphics.gl00.Context;
import graphics.gl00.Drawable;
import graphics.shared.textures.Texture;
import graphics.shared.textures.Textures;

public class Map extends GameData {
  protected World _world;
  protected int _x, _y;
  protected int _version;
  protected Layer[] _layer  = new Layer[Settings.Map.Depth];
  
  public Map(World world, int x, int y) {
    initInternal(2, new File("../data/worlds/" + world.getName() + "/" + x + "x" + y));
    _world = world;
    _x = x;
    _y = y;
    
    for(int z = 0; z < _layer.length; z++) {
      _layer[z] = new Layer();
    }
  }
  
  public World getWorld() {
    return _world;
  }
  
  public int getX() { return _x; }
  public int getY() { return _y; }
  
  public void request() {
    DataMap.Request p = new DataMap.Request(this);
    Game.getInstance().send(p, DataMap.Response.class, new Game.PacketCallback<DataMap.Response>() {
      public boolean recieved(DataMap.Response packet) {
        if(packet.getX() == _x && packet.getY() == _y) {
          remove();
          
          if(packet.getData() != null) {
            System.out.println("Map " + _x + "x" + _y + " synced from server");
            deserialize(new Buffer(packet.getData()));
            save();
          } else {
            System.out.println("Map " + _x + "x" + _y + " loaded");
            load();
          }
          
          _loaded = true;
          _events.raiseLoad();
          
          return true;
        }
        
        return false;
      }
    });
  }
  
  public Drawable[] createDrawablesFromLayer(final int z) {
    Drawable[] d = new Drawable[Settings.Map.Tile.Count * Settings.Map.Tile.Count];
    int tiles = 0;
    
    Textures t = Context.getTextures();
    
    for(int x = 0; x < _layer[z]._tile.length; x++) {
      for(int y = 0; y < _layer[z]._tile[x].length; y++) {
        if(_layer[z]._tile[x][y]._a != 0) {
          Drawable tile = Context.newDrawable();
          tile.setTexture(t.getTexture("tiles/" + _layer[z]._tile[x][y]._tileset + ".png"));
          tile.setXYWH(x * Settings.Map.Tile.Size, y * Settings.Map.Tile.Size, Settings.Map.Tile.Size, Settings.Map.Tile.Size);
          tile.setTXYWH(_layer[z]._tile[x][y]._x * Settings.Map.Tile.Size, _layer[z]._tile[x][y]._y * Settings.Map.Tile.Size, Settings.Map.Tile.Size, Settings.Map.Tile.Size);
          tile.createQuad();
          d[tiles++] = tile;
        }
      }
    }
    
    if(tiles != 0) {
      Drawable[] d2 = new Drawable[tiles];
      System.arraycopy(d, 0, d2, 0, tiles);
      return d2;
    }
    
    return null;
  }
  
  public Texture createTextureFromLayer(int z) {
    final Drawable[] tiles = createDrawablesFromLayer(z);
    
    if(tiles != null) {
      final Canvas c = new Canvas(_x + "x" + _y + "x" + z, Settings.Map.Size, Settings.Map.Size);
      c.events().addLoadHandler(new Canvas.Events.Load() {
        public void load() {
          c.bind();
          for(Drawable d : tiles) {
            d.draw();
          }
          c.unbind();
        }
      });
      return c.getTexture();
    } else {
      return null;
    }
  }
  
  public void createAttribMaskTextureFromLayer(final int z, final Texture texture) {
    texture.events().addLoadHandler(new Texture.Events.Load() {
      public void load() {
        byte[] b = new byte[Settings.Map.Attrib.Count * Settings.Map.Attrib.Count * 4];
        int n = 0;
        
        for(int y = 0; y < Settings.Map.Attrib.Count; y++) {
          for(int x = 0; x < Settings.Map.Attrib.Count; x++) {
            if(_layer[z]._attrib[x][y]._type != 0) {
              byte[] c = Attrib.Type.fromVal(_layer[z]._attrib[x][y]._type)._col;
              b[n++] = c[0];
              b[n++] = c[1];
              b[n++] = c[2];
              b[n++] = c[3];
            } else {
              n += 4;
            }
          }
        }
        
        ByteBuffer buff = ByteBuffer.allocateDirect(b.length);
        buff.put(b);
        buff.flip();
        
        texture.update(0, 0, Settings.Map.Attrib.Count, Settings.Map.Attrib.Count, buff);
      }
    });
  }
  
  protected void serializeInternal(Buffer b) {
    b.put(_x);
    b.put(_y);
    
    b.put(_layer.length);
    b.put(_layer[0]._tile.length);
    b.put(_layer[0]._tile[0].length);
    b.put(_layer[0]._attrib.length);
    b.put(_layer[0]._attrib[0].length);
    
    for(int z = 0; z < _layer.length; z++) {
      for(int x = 0; x < _layer[z]._tile.length; x++) {
        for(int y = 0; y < _layer[z]._tile[x].length; y++) {
          b.put(_layer[z]._tile[x][y]._x);
          b.put(_layer[z]._tile[x][y]._y);
          b.put(_layer[z]._tile[x][y]._tileset);
          b.put(_layer[z]._tile[x][y]._a);
        }
      }
      
      for(int x = 0; x < _layer[z]._attrib.length; x++) {
        for(int y = 0; y < _layer[z]._attrib[x].length; y++) {
          b.put(_layer[z]._attrib[x][y]._type);
        }
      }
    }
  }
  
  protected void deserializeInternal(Buffer b) {
    switch(getVersion()) {
      case 1: deserialize01(b); break;
    }
    
    _loaded = true;
  }
  
  private void deserialize01(Buffer b) {
    _x = b.getInt();
    _y = b.getInt();
    
    int sizeZ = b.getInt();
    int sizeX = b.getInt();
    int sizeY = b.getInt();
    int sizeXA = b.getInt();
    int sizeYA = b.getInt();
    
    int maxX = sizeX > Settings.Map.Tile.Count ? Settings.Map.Tile.Count : sizeX;
    int maxY = sizeY > Settings.Map.Tile.Count ? Settings.Map.Tile.Count : sizeY;
    int maxZ = sizeZ > Settings.Map.Depth ? Settings.Map.Depth : sizeZ;
    int maxXA = sizeXA > (Settings.Map.Attrib.Count) ? (Settings.Map.Attrib.Count) : sizeXA;
    int maxYA = sizeYA > (Settings.Map.Attrib.Count) ? (Settings.Map.Attrib.Count) : sizeYA;
    
    _layer = new Layer[Settings.Map.Depth];
    
    for(int z = 0; z < maxZ; z++) {
      _layer[z] = new Layer();
      
      for(int x = 0; x < maxX; x++) {
        for(int y = 0; y < maxY; y++) {
          _layer[z]._tile[x][y]._x = b.getByte();
          _layer[z]._tile[x][y]._y = b.getByte();
          _layer[z]._tile[x][y]._tileset = b.getByte();
          _layer[z]._tile[x][y]._a = b.getByte();
        }
      }
      
      for(int x = 0; x < maxXA; x++) {
        for(int y = 0; y < maxYA; y++) {
          _layer[z]._attrib[x][y]._type = b.getByte();
        }
      }
    }
  }
  
  public class Layer {
    public Tile[][] _tile = new Tile[Settings.Map.Tile.Count][Settings.Map.Tile.Count];
    public Attrib[][] _attrib = new Attrib[Settings.Map.Attrib.Count][Settings.Map.Attrib.Count];
    
    public Layer() {
      for(int x = 0; x < _tile.length; x++) {
        for(int y = 0; y < _tile[x].length; y++) {
          _tile[x][y] = new Tile();
        }
      }
      
      for(int x = 0; x < _attrib.length; x++) {
        for(int y = 0; y < _attrib[x].length; y++) {
          _attrib[x][y] = new Attrib();
        }
      }
    }
    
    public Tile getTile(int x, int y) {
      return _tile[x][y];
    }
    
    public Attrib getAttrib(int x, int y) {
      return _attrib[x][y];
    }
  }
  
  public class Tile {
    public byte _x, _y;
    public byte _tileset;
    public byte _a;
  }
  
  public static class Attrib {
    public byte _type;
    
    public static enum Type {
      BLOCKED((byte)0x80, new byte[] {(byte)255, 0, 0, (byte)255});
      
      public static Type fromVal(int val) {
        for(Type t : Type.values()) {
          if(t._val == val) {
            return t;
          }
        }
        
        return null;
      }
      
      private final byte   _val;
      private final byte[] _col;
      
      private Type(byte val, byte[] col) {
        _val = val;
        _col = col;
      }
      
      public int val() {
        return _val;
      }
      
      public byte[] col() {
        return _col;
      }
    }
  }
}