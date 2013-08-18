package game.graphics.gui.editors;

import java.nio.ByteBuffer;
import java.util.LinkedList;

import game.Game;
import game.data.Map;
import game.data.util.Buffer;
import game.data.util.GameData;
import game.network.packet.editors.EditorDataMap;
import game.settings.Settings;
import graphics.gl00.Context;
import graphics.gl00.Drawable;
import graphics.gl00.Matrix;
import graphics.shared.textures.Texture;
import graphics.shared.textures.Textures;

public class MapEditorMap extends Map {
  private Game _game = Game.getInstance();
  private Matrix _matrix = Context.getMatrix();
  
  private Map _map;
  protected LinkedList<Sprite> _sprite = new LinkedList<Sprite>();
  protected LinkedList<Item>   _item   = new LinkedList<Item>();
  protected LinkedList<NPC>    _npc    = new LinkedList<NPC>();
  
  private Texture[] _attribMask;
  private EditorSprite[] _spritesDrawable;
  private EditorSprite[] _itemsDrawable;
  private EditorSprite[] _npcsDrawable;
  
  protected MapEditorMap(Map map) {
    super(map.getWorld(), map.getX(), map.getY());
    
    _map = map;
    
    _attribMask = new Texture[Settings.Map.Depth];
    for(int z = 0; z < Settings.Map.Depth; z++) {
      _attribMask[z] = Textures.getInstance().getTexture(_x + "x" + _y + "x" + z + " mask", Settings.Map.Attrib.Count, Settings.Map.Attrib.Count, null);
    }
    
    request();
  }
  
  protected void updateAttrib(int layer, int x, int y, byte[] col) {
    ByteBuffer b;
    b = ByteBuffer.allocateDirect(4);
    b.put(col);
    b.flip();
    _attribMask[layer].update(x, y, 1, 1, b);
  }
  
  protected void createSprites() {
    int i = 0;
    _spritesDrawable = new EditorSprite[_sprite.size()];
    for(final Sprite sprite : _sprite) {
      final game.data.Sprite data = _game.getSprite(_sprite.get(i).file);
      
      if(data != null) {
        final int n = i;
        data.events().addLoadHandler(new GameData.Events.Load() {
          public void load() {
            _spritesDrawable[n] = new EditorSprite();
            _spritesDrawable[n]._sprite = data;
            _spritesDrawable[n]._drawable.setXYWH(sprite.x, sprite.y, _spritesDrawable[n]._sprite.getW(), _spritesDrawable[n]._sprite.getH());
            _spritesDrawable[n]._drawable.setColour(new float[] {1, 0, 1, 1});
            _spritesDrawable[n]._drawable.createBorder();
          }
        });
      }
      
      i++;
    }
  }
  
  protected void createItems() {
    int i = 0;
    _itemsDrawable = new EditorSprite[_item.size()];
    for(final Item item : _item) {
      final game.data.Item data = _game.getItem(_item.get(i).file);
      
      if(data != null) {
        final int n = i;
        data.events().addLoadHandler(new GameData.Events.Load() {
          public void load() {
            _itemsDrawable[n] = new EditorSprite();
            _itemsDrawable[n]._sprite = _game.getSprite(data.getSprite());
            _itemsDrawable[n]._drawable.setXYWH(item.x, item.y, _itemsDrawable[n]._sprite.getW(), _itemsDrawable[n]._sprite.getH());
            _itemsDrawable[n]._drawable.setColour(new float[] {0, 1, 0, 1});
            _itemsDrawable[n]._drawable.createBorder();
          }
        });
      }
      
      i++;
    }
  }
  
  protected void createNPCs() {
    int i = 0;
    _npcsDrawable = new EditorSprite[_npc.size()];
    for(final NPC npc : _npc) {
      final game.data.NPC data = _game.getNPC(_npc.get(i).file);
      
      if(data != null) {
        final int n = i;
        data.events().addLoadHandler(new GameData.Events.Load() {
          public void load() {
            _npcsDrawable[n] = new EditorSprite();
            _npcsDrawable[n]._sprite = _game.getSprite(data.getSprite());
            _npcsDrawable[n]._drawable.setXYWH(npc.x, npc.y, _npcsDrawable[n]._sprite.getW(), _npcsDrawable[n]._sprite.getH());
            _npcsDrawable[n]._drawable.setColour(new float[] {0, 1, 0, 1});
            _npcsDrawable[n]._drawable.createBorder();
          }
        });
      }
      
      i++;
    }
  }
  
  protected Map getMap() {
    return _map;
  }
  
  protected Layer getLayer(int z) {
    return _layer[z];
  }
  
  protected Texture getAttribMask(int layer) {
    return _attribMask[layer];
  }
  
  protected void drawSprites() {
    for(EditorSprite d : _spritesDrawable) {
      if(d != null) {
        _matrix.push();
        _matrix.translate(-d._sprite.frame.get(0)._fx, -d._sprite.getH() + d._sprite.frame.get(0)._fy);
        d._drawable.draw();
        _matrix.pop();
      }
    }
  }
  
  protected void drawItems() {
    for(EditorSprite d : _itemsDrawable) {
      if(d != null) {
        _matrix.push();
        _matrix.translate(-d._sprite.frame.get(0)._fx, -d._sprite.getH() + d._sprite.frame.get(0)._fy);
        d._drawable.draw();
        _matrix.pop();
      }
    }
  }
  
  protected void drawNPCs() {
    for(EditorSprite d : _npcsDrawable) {
      if(d != null) {
        _matrix.push();
        _matrix.translate(-d._sprite.frame.get(0)._fx, -d._sprite.getH() + d._sprite.frame.get(0)._fy);
        d._drawable.draw();
        _matrix.pop();
      }
    }
  }
  
  public void request() {
    EditorDataMap.Request p = new EditorDataMap.Request(this);
    Game.getInstance().send(p, EditorDataMap.Response.class, new Game.PacketCallback<EditorDataMap.Response>() {
      public boolean recieved(final EditorDataMap.Response packet) {
        if(packet.getX() == _x && packet.getY() == _y) {
          remove();
          
          Context.getContext().addLoadCallback(new Context.Loader.Callback() {
            public void load() {
              deserialize(new Buffer(packet.getData()));
              System.out.println("MapEditorMap " + getFile() + " synced from server");
              
              for(int z = 0; z < _attribMask.length; z++) {
                createAttribMaskTextureFromLayer(z, _attribMask[z]);
              }
              
              createSprites();
              createItems();
              createNPCs();
              
              _loaded = true;
              _events.raiseLoad();
            }
          }, false);
          
          return true;
        }
        
        return false;
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
    
    b.put(_sprite.size());
    b.put(_item.size());
    b.put(_npc.size());
    
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
      b.put(s.file);
      b.put(s.anim);
      b.put(s.x);
      b.put(s.y);
      b.put(s.z);
    }
    
    for(Item i : _item) {
      b.put(i.file);
      b.put(i.val);
      b.put(i.x);
      b.put(i.y);
      b.put(i.z);
    }
    
    for(NPC n : _npc) {
      b.put(n.file);
      b.put(n.x);
      b.put(n.y);
      b.put(n.z);
    }
  }
  
  protected void deserializeInternal(Buffer b) {
    _sprite.clear();
    _item.clear();
    _npc.clear();
    
    _x = b.getInt();
    _y = b.getInt();
    
    int sizeZ = b.getInt();
    int sizeX = b.getInt();
    int sizeY = b.getInt();
    int sizeXA = b.getInt();
    int sizeYA = b.getInt();
    
    int spriteSize = b.getInt();
    int itemSize = b.getInt();
    int npcSize = b.getInt();
    
    _layer = new Layer[sizeZ];
    
    for(int z = 0; z < sizeZ; z++) {
      _layer[z] = new Layer();
      
      for(int x = 0; x < sizeX; x++) {
        for(int y = 0; y < sizeY; y++) {
          _layer[z]._tile[x][y]._x = b.getByte();
          _layer[z]._tile[x][y]._y = b.getByte();
          _layer[z]._tile[x][y]._tileset = b.getByte();
          _layer[z]._tile[x][y]._a = b.getByte();
        }
      }
      
      for(int x = 0; x < sizeXA; x++) {
        for(int y = 0; y < sizeYA; y++) {
          _layer[z]._attrib[x][y]._type = b.getByte();
        }
      }
    }
    
    for(int i = 0; i < spriteSize; i++) {
      Sprite s = new Sprite();
      s.file = b.getString();
      s.anim = b.getString();
      s.x = b.getInt();
      s.y = b.getInt();
      s.z = b.getByte();
      _sprite.add(s);
    }
    
    for(int i = 0; i < itemSize; i++) {
      Item item = new Item();
      item.file = b.getString();
      item.val = b.getInt();
      item.x = b.getInt();
      item.y = b.getInt();
      item.z = b.getByte();
      _item.add(item);
    }
    
    for(int i = 0; i < npcSize; i++) {
      NPC n = new NPC();
      n.file = b.getString();
      n.x = b.getInt();
      n.y = b.getInt();
      n.z = b.getByte();
      _npc.add(n);
    }
  }
  
  public static class Data {
    public String file;
    public int x, y;
    public byte z;
  }
  
  public static class Sprite extends Data {
    public String anim;
  }
  
  public static class Item extends Data {
    public int val;
  }
  
  public static class NPC extends Data {
    
  }
  
  private static class EditorSprite {
    private Drawable _drawable = Context.newDrawable();
    private game.data.Sprite _sprite;
  }
}