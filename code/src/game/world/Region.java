package game.world;

import java.util.LinkedList;

import game.data.Map;
import game.settings.Settings;
import graphics.gl00.Context;
import graphics.gl00.Drawable;
import graphics.gl00.Matrix;

public class Region {
  private Matrix _matrix = Context.getMatrix();
  
  private Events _events;
  
  private World _world;
  private Drawable[] _layer;
  private Map _map;
  private int _x, _y;
  //private Entity[] _entity;
  
  private boolean _loaded;
  
  public Region(World world) {
    _events = new Events();
    _world = world;
  }
  
  public Events events() {
    return _events;
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
    
    _loaded = true;
  }
  
  /*public void spawn() {
    despawn();
    _entity = _map.spawn();
  }
  
  public void despawn() {
    if(_entity != null) {
      for(Entity e : _entity) {
        e.getSprite().remove();
      }
      
      _entity = null;
    }
  }*/
  
  public void draw(int z) {
    if(!_loaded) calc();
    
    if(_layer == null) return;
    
    _matrix.push();
    _matrix.translate(_x, _y);
    
    if(_layer[z] != null) {
      _layer[z].draw();
    }
    
    _events.raiseDraw(z);
    
    _matrix.pop();
  }
  
  public static class Events {
    private LinkedList<Draw> _draw = new LinkedList<Draw>();
    
    public int onDraw(Draw e) { _draw.add(e); return _draw.size() - 1; }
    public void removeDraw(int index) { _draw.remove(index); }
    
    public void raiseDraw(int z) {
      for(Draw e : _draw) {
        e.event(z);
      }
    }
    
    public static abstract class Draw {
      public abstract void event(int z);
    }
  }
}