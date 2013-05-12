package game.graphics.gui.editors;

import java.nio.ByteBuffer;
import java.util.LinkedList;

import game.data.Map;
import game.data.util.Buffer;
import game.settings.Settings;
import graphics.gl00.Context;
import graphics.gl00.Drawable;
import graphics.gl00.Matrix;
import graphics.shared.textures.Texture;

public class MapEditorMap extends Map {
  private Matrix _matrix = Context.getMatrix();
  
  private Map _map;
  private int _mapCRC;
  protected LinkedList<Sprite> _sprite = super._sprite;
  
  private Texture[] _attribMask;
  private game.world.Sprite[] _sprites;
  private Drawable[] _spritesDrawable;
  
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
    deleteSprites();
    
    _sprites = spawn();
    _spritesDrawable = new Drawable[_sprites.length];
    
    for(int i = 0; i < _spritesDrawable.length; i++) {
      _spritesDrawable[i] = Context.newDrawable();
      _spritesDrawable[i].setXYWH(_sprite.get(i)._x, _sprite.get(i)._y, _sprites[i].getW(), _sprites[i].getH());
      _spritesDrawable[i].setColour(new float[] {1, 0, 1, 1});
      _spritesDrawable[i].createBorder();
    }
  }
  
  public void deleteSprites() {
    if(_sprites != null) {
      for(game.world.Sprite s : _sprites) {
        s.remove();
      }
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
    int i = 0;
    for(Drawable d : _spritesDrawable) {
      _matrix.push();
      _matrix.translate(_sprites[i].getFrameX(), _sprites[i].getFrameY());
      d.draw();
      _matrix.pop();
    }
  }
}