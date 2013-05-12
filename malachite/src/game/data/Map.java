package game.data;

import java.nio.ByteBuffer;
import java.util.LinkedList;

import game.Game;
import game.data.util.Buffer;
import game.data.util.Serializable;
import game.settings.Settings;
import graphics.gl00.Canvas;
import graphics.gl00.Context;
import graphics.gl00.Drawable;
import graphics.shared.textures.Texture;
import graphics.shared.textures.Textures;

public class Map extends Serializable {
  private static final int VERSION = 3;
  
  protected String _world;
  protected int _x, _y;
  protected Layer[] _layer = new Layer[Settings.Map.Depth];
  protected LinkedList<Sprite> _sprite = new LinkedList<Sprite>();
  
  public Map(String world, int x, int y) {
    super("worlds/" + world, x + "x" + y);
    _world = world;
    _x = x;
    _y = y;
    
    for(int z = 0; z < _layer.length; z++) {
      _layer[z] = new Layer();
    }
  }
  
  public String getWorld() {
    return _world;
  }
  
  public int getX() {
    return _x;
  }
  
  public int getY() {
    return _y;
  }
  
  public Drawable[] createDrawablesFromLayer(int z) {
    Drawable[] d = new Drawable[Settings.Map.Tile.Count * Settings.Map.Tile.Count];
    int tiles = 0;
    
    Textures t = Context.getTextures();
    
    for(int x = 0; x < _layer[z]._tile.length; x++) {
      for(int y = 0; y < _layer[z]._tile[x].length; y++) {
        if(_layer[z]._tile[x][y]._a != 0) {
          d[tiles] = Context.newDrawable();
          d[tiles].setTexture(t.getTexture("tiles/" + _layer[z]._tile[x][y]._tileset + ".png", true));
          d[tiles].setXYWH(x * Settings.Map.Tile.Size, y * Settings.Map.Tile.Size, Settings.Map.Tile.Size, Settings.Map.Tile.Size);
          d[tiles].setTXYWH(_layer[z]._tile[x][y]._x * Settings.Map.Tile.Size, _layer[z]._tile[x][y]._y * Settings.Map.Tile.Size, Settings.Map.Tile.Size, Settings.Map.Tile.Size);
          d[tiles].createQuad();
          tiles++;
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
    Drawable[] tiles = createDrawablesFromLayer(z);
    
    if(tiles != null) {
      Canvas c = new Canvas(_x + "x" + _y + "x" + z, Settings.Map.Size, Settings.Map.Size);
      c.bind();
      for(Drawable d : tiles) {
        d.draw();
      }
      c.unbind();
      return c.getTexture();
    } else {
      return null;
    }
  }
  
  public Texture createAttribMaskTextureFromLayer(int z) {
    Texture texture = Textures.getInstance().getTexture(_x + "x" + _y + "x" + z + " mask", Settings.Map.Size, Settings.Map.Size, null, true);
    
    ByteBuffer b = ByteBuffer.allocateDirect(Settings.Map.Attrib.Size * Settings.Map.Attrib.Size * 4);
    
    for(int x = 0; x < _layer[z]._attrib.length; x++) {
      for(int y = 0; y < _layer[z]._attrib[x].length; y++) {
        b.clear();
        
        if(_layer[z]._attrib[x][y]._type != 0) {
          Attrib.Type t = Attrib.Type.fromVal(_layer[z]._attrib[x][y]._type);
          for(int i = 0; i < b.capacity() / 4; i++) {
            b.put(t._col);
          }
        } else {
          b.put(new byte[b.capacity()]);
        }
        
        b.flip();
        texture.update(x * Settings.Map.Attrib.Size, y * Settings.Map.Attrib.Size, Settings.Map.Attrib.Size, Settings.Map.Attrib.Size, b);
      }
    }
    
    return texture;
  }
  
  public game.world.Sprite[] spawn() {
    Game g = (Game)Context.getGame();
    game.world.Sprite[] s = new game.world.Sprite[_sprite.size()];
    int i = 0;
    
    for(Map.Sprite sprite : _sprite) {
      s[i] = game.world.Sprite.add(g.getSprite(sprite._file));
      s[i].setX(sprite._x);
      s[i].setY(sprite._y);
      s[i].setZ(sprite._z);
      i++;
    }
    
    return s;
  }
  
  public Buffer serialize() {
    Buffer b = new Buffer((_layer[0]._tile.length * _layer[0]._tile[0].length * 7 + _layer[0]._attrib.length * _layer[0]._attrib[0].length) * _layer.length + 28);
    b.put(VERSION);
    
    b.put(_x);
    b.put(_y);
    
    b.put(_layer.length);
    b.put(_layer[0]._tile.length);
    b.put(_layer[0]._tile[0].length);
    b.put(_layer[0]._attrib.length);
    b.put(_layer[0]._attrib[0].length);
    
    b.put(_sprite.size());
    
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
    
    for(Sprite s : _sprite) {
      b.put(s._file);
      b.put(s._x);
      b.put(s._y);
      b.put(s._z);
    }
    
    return b;
  }
  
  public void deserialize(Buffer b) {
    switch(b.getInt()) {
      case 1: deserialize01(b); break;
      case 2: deserialize02(b); break;
      case 3: deserialize03(b); break;
    }
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
      _layer[z]._tile = new Tile[Settings.Map.Tile.Count][Settings.Map.Tile.Count];
      _layer[z]._attrib = new Attrib[Settings.Map.Attrib.Count][Settings.Map.Attrib.Count];
      
      for(int x = 0; x < maxX; x++) {
        for(int y = 0; y < maxY; y++) {
          _layer[z]._tile[x][y] = new Tile();
          _layer[z]._tile[x][y]._x = b.getByte();
          _layer[z]._tile[x][y]._y = b.getByte();
          _layer[z]._tile[x][y]._tileset = b.getByte();
          _layer[z]._tile[x][y]._a = b.getByte();
        }
      }
      
      for(int x = 0; x < maxXA; x++) {
        for(int y = 0; y < maxYA; y++) {
          _layer[z]._attrib[x][y] = new Attrib();
          _layer[z]._attrib[x][y]._type = b.getByte();
        }
      }
    }
  }
  
  private void deserialize02(Buffer b) {
    _sprite.clear();
    
    _x = b.getInt();
    _y = b.getInt();
    
    int sizeZ = b.getInt();
    int sizeX = b.getInt();
    int sizeY = b.getInt();
    int sizeXA = b.getInt();
    int sizeYA = b.getInt();
    
    int spriteSize = b.getInt();
    
    int maxX = sizeX > Settings.Map.Tile.Count ? Settings.Map.Tile.Count : sizeX;
    int maxY = sizeY > Settings.Map.Tile.Count ? Settings.Map.Tile.Count : sizeY;
    int maxZ = sizeZ > Settings.Map.Depth ? Settings.Map.Depth : sizeZ;
    int maxXA = sizeXA > (Settings.Map.Attrib.Count) ? (Settings.Map.Attrib.Count) : sizeXA;
    int maxYA = sizeYA > (Settings.Map.Attrib.Count) ? (Settings.Map.Attrib.Count) : sizeYA;
    
    _layer = new Layer[Settings.Map.Depth];
    
    for(int z = 0; z < maxZ; z++) {
      _layer[z] = new Layer();
      _layer[z]._tile = new Tile[Settings.Map.Tile.Count][Settings.Map.Tile.Count];
      _layer[z]._attrib = new Attrib[Settings.Map.Attrib.Count][Settings.Map.Attrib.Count];
      
      for(int x = 0; x < maxX; x++) {
        for(int y = 0; y < maxY; y++) {
          _layer[z]._tile[x][y] = new Tile();
          _layer[z]._tile[x][y]._x = b.getByte();
          _layer[z]._tile[x][y]._y = b.getByte();
          _layer[z]._tile[x][y]._tileset = b.getByte();
          _layer[z]._tile[x][y]._a = b.getByte();
        }
      }
      
      for(int x = 0; x < maxXA; x++) {
        for(int y = 0; y < maxYA; y++) {
          _layer[z]._attrib[x][y] = new Attrib();
          _layer[z]._attrib[x][y]._type = b.getByte();
        }
      }
    }
    
    for(int i = 0; i < spriteSize; i++) {
      Sprite s = new Sprite();
      s._file = b.getString();
      s._x = b.getInt();
      s._y = b.getInt();
      _sprite.add(s);
    }
  }
  
  private void deserialize03(Buffer b) {
    _sprite.clear();
    
    _x = b.getInt();
    _y = b.getInt();
    
    int sizeZ = b.getInt();
    int sizeX = b.getInt();
    int sizeY = b.getInt();
    int sizeXA = b.getInt();
    int sizeYA = b.getInt();
    
    int spriteSize = b.getInt();
    
    int maxX = sizeX > Settings.Map.Tile.Count ? Settings.Map.Tile.Count : sizeX;
    int maxY = sizeY > Settings.Map.Tile.Count ? Settings.Map.Tile.Count : sizeY;
    int maxZ = sizeZ > Settings.Map.Depth ? Settings.Map.Depth : sizeZ;
    int maxXA = sizeXA > (Settings.Map.Attrib.Count) ? (Settings.Map.Attrib.Count) : sizeXA;
    int maxYA = sizeYA > (Settings.Map.Attrib.Count) ? (Settings.Map.Attrib.Count) : sizeYA;
    
    _layer = new Layer[Settings.Map.Depth];
    
    for(int z = 0; z < maxZ; z++) {
      _layer[z] = new Layer();
      _layer[z]._tile = new Tile[Settings.Map.Tile.Count][Settings.Map.Tile.Count];
      _layer[z]._attrib = new Attrib[Settings.Map.Attrib.Count][Settings.Map.Attrib.Count];
      
      for(int x = 0; x < maxX; x++) {
        for(int y = 0; y < maxY; y++) {
          _layer[z]._tile[x][y] = new Tile();
          _layer[z]._tile[x][y]._x = b.getByte();
          _layer[z]._tile[x][y]._y = b.getByte();
          _layer[z]._tile[x][y]._tileset = b.getByte();
          _layer[z]._tile[x][y]._a = b.getByte();
        }
      }
      
      for(int x = 0; x < maxXA; x++) {
        for(int y = 0; y < maxYA; y++) {
          _layer[z]._attrib[x][y] = new Attrib();
          _layer[z]._attrib[x][y]._type = b.getByte();
        }
      }
    }
    
    for(int i = 0; i < spriteSize; i++) {
      Sprite s = new Sprite();
      s._file = b.getString();
      s._x = b.getInt();
      s._y = b.getInt();
      s._z = b.getByte();
      _sprite.add(s);
    }
  }
  
  public class Layer {
    protected Tile[][] _tile = new Tile[Settings.Map.Tile.Count][Settings.Map.Tile.Count];
    protected Attrib[][] _attrib = new Attrib[Settings.Map.Attrib.Count][Settings.Map.Attrib.Count];
    
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
      
      private final  byte   _val;
      private final  byte[] _col;
      
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
  
  public static class Sprite {
    public String _file;
    public int _x, _y;
    public byte _z;
  }
}