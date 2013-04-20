package game.world;

import game.data.Map;
import game.settings.Settings;
import graphics.gl00.Context;
import graphics.gl00.Drawable;
import graphics.gl00.Matrix;

public class Region {
  private Matrix _matrix = Context.getMatrix();
  
  private World _world;
  private Drawable[] _layer;
  private Map _map;
  private int _x, _y;
  
  public Region(World world) {
    _world = world;
  }
  
  public Region getRelativeRegion(int x, int y) {
    return _world.getRegion(_map.getX() + x, _map.getY() + y);
  }
  
  public Map getMap() {
    return _map;
  }
  
  public int getX() {
    return _x;
  }
  
  public int getY() {
    return _y;
  }
  
  public void setMap(Map map) {
    _map = map;
    _x = _map.getX() * Settings.Map.Size;
    _y = _map.getY() * Settings.Map.Size;
  }
  
  public void calc() {
    _layer = new Drawable[Settings.Map.Depth];
    for(int z = 0; z < _layer.length; z++) {
      _layer[z] = Context.newDrawable();
      _layer[z].setTexture(_map.createTextureFromLayer(z));
      
      if(_layer[z].getTexture() == null) {
        _layer[z].setColour(new float[] {0, 0, 0, 0});
      }
      
      _layer[z].createQuad();
    }
  }
  
  public void draw(int z) {
    if(_layer == null) return;
    
    _matrix.push();
    _matrix.translate(_x, _y);
    
    if(_layer[z] != null) {
      _layer[z].draw();
    }
    
    _matrix.pop();
  }
}