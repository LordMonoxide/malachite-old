package game.graphics.gui.editors;

import java.nio.ByteBuffer;
import java.util.LinkedList;

import game.Game;
import game.data.Map;
import game.data.util.Buffer;
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
      _attribMask[z] = Textures.getInstance().getTexture(_x + "x" + _y + "x" + z + " mask", Settings.Map.Size, Settings.Map.Size, null);
    }
    
    request();
  }
  
  protected void updateAttrib(int layer, int x, int y, ByteBuffer data) {
    _attribMask[layer].update(x * Settings.Map.Attrib.Size, y * Settings.Map.Attrib.Size, Settings.Map.Attrib.Size, Settings.Map.Attrib.Size, data);
  }
  
  protected void createSprites() {
    int i = 0;
    _spritesDrawable = new EditorSprite[_sprite.size()];
    for(Sprite sprite : _sprite) {
      game.data.Sprite data = _game.getSprite(_sprite.get(i)._file);
      
      if(data != null) {
        _spritesDrawable[i] = new EditorSprite();
        _spritesDrawable[i]._sprite = data;
        _spritesDrawable[i]._drawable.setXYWH(sprite._x, sprite._y, _spritesDrawable[i]._sprite.getW(), _spritesDrawable[i]._sprite.getH());
        _spritesDrawable[i]._drawable.setColour(new float[] {1, 0, 1, 1});
        _spritesDrawable[i]._drawable.createBorder();
      }
      
      i++;
    }
  }
  
  protected void createItems() {
    int i = 0;
    _itemsDrawable = new EditorSprite[_item.size()];
    for(Item item : _item) {
      game.data.Item data = _game.getItem(_item.get(i)._file);
      
      if(data != null) {
        _itemsDrawable[i] = new EditorSprite();
        _itemsDrawable[i]._sprite = _game.getSprite(data.getSprite());
        _itemsDrawable[i]._drawable.setXYWH(item._x, item._y, _itemsDrawable[i]._sprite.getW(), _itemsDrawable[i]._sprite.getH());
        _itemsDrawable[i]._drawable.setColour(new float[] {0, 1, 0, 1});
        _itemsDrawable[i]._drawable.createBorder();
      }
      
      i++;
    }
  }
  
  protected void createNPCs() {
    int i = 0;
    _npcsDrawable = new EditorSprite[_npc.size()];
    for(NPC npc : _npc) {
      game.data.NPC data = _game.getNPC(_npc.get(i)._file);
      
      if(data != null) {
        _npcsDrawable[i] = new EditorSprite();
        _npcsDrawable[i]._sprite = _game.getSprite(data.getSprite());
        _npcsDrawable[i]._drawable.setXYWH(npc._x, npc._y, _npcsDrawable[i]._sprite.getW(), _npcsDrawable[i]._sprite.getH());
        _npcsDrawable[i]._drawable.setColour(new float[] {0, 1, 0, 1});
        _npcsDrawable[i]._drawable.createBorder();
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
        _matrix.translate(-d._sprite.getFrame(0)._fx, -d._sprite.getH() + d._sprite.getFrame(0)._fy);
        d._drawable.draw();
        _matrix.pop();
      }
    }
  }
  
  protected void drawItems() {
    for(EditorSprite d : _itemsDrawable) {
      if(d != null) {
        _matrix.push();
        _matrix.translate(-d._sprite.getFrame(0)._fx, -d._sprite.getH() + d._sprite.getFrame(0)._fy);
        d._drawable.draw();
        _matrix.pop();
      }
    }
  }
  
  protected void drawNPCs() {
    for(EditorSprite d : _npcsDrawable) {
      if(d != null) {
        _matrix.push();
        _matrix.translate(-d._sprite.getFrame(0)._fx, -d._sprite.getH() + d._sprite.getFrame(0)._fy);
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
              packet.process();
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
      b.put(s._file);
      b.put(s._x);
      b.put(s._y);
      b.put(s._z);
    }
    
    for(Item i : _item) {
      b.put(i._file);
      b.put(i._val);
      b.put(i._x);
      b.put(i._y);
      b.put(i._z);
    }
    
    for(NPC n : _npc) {
      b.put(n._file);
      b.put(n._x);
      b.put(n._y);
      b.put(n._z);
    }
  }
  
  protected void deserializeInternal(Buffer b) {
    switch(getVersion()) {
      case 1: deserialize01(b); break;
    }
  }
  
  private void deserialize01(Buffer b) {
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
      _layer[z]._tile = new Tile[sizeX][sizeY];
      _layer[z]._attrib = new Attrib[sizeXA][sizeYA];
      
      for(int x = 0; x < sizeX; x++) {
        for(int y = 0; y < sizeY; y++) {
          _layer[z]._tile[x][y] = new Tile();
          _layer[z]._tile[x][y]._x = b.getByte();
          _layer[z]._tile[x][y]._y = b.getByte();
          _layer[z]._tile[x][y]._tileset = b.getByte();
          _layer[z]._tile[x][y]._a = b.getByte();
        }
      }
      
      for(int x = 0; x < sizeXA; x++) {
        for(int y = 0; y < sizeYA; y++) {
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
    
    for(int i = 0; i < itemSize; i++) {
      Item item = new Item();
      item._file = b.getString();
      item._val = b.getInt();
      item._x = b.getInt();
      item._y = b.getInt();
      item._z = b.getByte();
      _item.add(item);
    }
    
    for(int i = 0; i < npcSize; i++) {
      NPC n = new NPC();
      n._file = b.getString();
      n._x = b.getInt();
      n._y = b.getInt();
      n._z = b.getByte();
    }
  }
  
  public static class Data {
    public String _file;
    public int _x, _y;
    public byte _z;
  }
  
  public static class Sprite extends Data {
    
  }
  
  public static class Item extends Data {
    public int _val;
  }
  
  public static class NPC extends Data {
    
  }
  
  private static class EditorSprite {
    private Drawable _drawable = Context.newDrawable();
    private game.data.Sprite _sprite;
  }
}