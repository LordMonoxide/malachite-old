package game.graphics.gui.editors;

import java.nio.ByteBuffer;
import java.util.LinkedList;

import game.Game;
import game.data.Map;
import game.data.util.Buffer;
import game.settings.Settings;
import graphics.gl00.Context;
import graphics.gl00.Drawable;
import graphics.gl00.Matrix;
import graphics.shared.textures.Texture;

public class MapEditorMap extends Map {
  private Game _game = Game.getInstance();
  private Matrix _matrix = Context.getMatrix();
  
  private Map _map;
  private int _mapCRC;
  protected LinkedList<Sprite> _sprite = super._sprite;
  
  private Texture[] _attribMask;
  private EditorSprite[] _spritesDrawable;
  
  public MapEditorMap(Map map) {
    super(map.getWorld(), map.getX(), map.getY());
    
    _map = map;
    
    // Deep-copy source Map into
    // this MapEditorMap's structure
    Buffer b = _map.serialize();
    deserialize(b);
    _mapCRC = b.crc();
    
    _attribMask = new Texture[Settings.Map.Depth];
    
    for(int z = 0; z < _attribMask.length; z++) {
      _attribMask[z] = createAttribMaskTextureFromLayer(z);
    }
    
    createSprites();
  }
  
  public boolean isChanged() {
    return _mapCRC != serialize().crc();
  }
  
  public void update() {
    _map.deserialize(serialize());
  }
  
  public void updateAttrib(int layer, int x, int y, ByteBuffer data) {
    _attribMask[layer].update(x * Settings.Map.Attrib.Size, y * Settings.Map.Attrib.Size, Settings.Map.Attrib.Size, Settings.Map.Attrib.Size, data);
  }
  
  public void createSprites() {
    int i = 0;
    _spritesDrawable = new EditorSprite[_sprite.size()];
    for(Sprite sprite : _sprite) {
      _spritesDrawable[i] = new EditorSprite();
      _spritesDrawable[i]._sprite = _game.getSprite(_sprite.get(i)._file);
      _spritesDrawable[i]._drawable.setXYWH(sprite._x, sprite._y, _spritesDrawable[i]._sprite.getW(), _spritesDrawable[i]._sprite.getH());
      _spritesDrawable[i]._drawable.setColour(new float[] {1, 0, 1, 1});
      _spritesDrawable[i]._drawable.createBorder();
      i++;
    }
  }
  
  public Map getMap() {
    return _map;
  }
  
  public Layer getLayer(int z) {
    return _layer[z];
  }
  
  public Texture getAttribMask(int layer) {
    return _attribMask[layer];
  }
  
  public void drawSprites() {
    for(EditorSprite d : _spritesDrawable) {
      _matrix.push();
      _matrix.translate(-d._sprite.getFrame(0)._fx, -d._sprite.getH() + d._sprite.getFrame(0)._fy);
      d._drawable.draw();
      _matrix.pop();
    }
  }
  
  private static class EditorSprite {
    private Drawable _drawable = Context.newDrawable();
    private game.data.Sprite _sprite;
  }
}