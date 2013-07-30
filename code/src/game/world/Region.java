package game.world;

import java.util.LinkedList;

import game.data.Map;
import game.data.util.GameData;
import game.settings.Settings;
import graphics.gl00.Context;
import graphics.gl00.Drawable;
import graphics.gl00.Matrix;
import graphics.shared.textures.Texture;

public class Region {
  private Matrix _matrix = Context.getMatrix();
  
  private Events _events;
  
  private World _world;
  private Drawable[] _layer = new Drawable[Settings.Map.Depth];
  private Map _map;
  private int _x, _y;
  
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
    
    _map.events().addLoadHandler(new GameData.Events.Load() {
      public void load() {
        Context.getContext().addLoadCallback(new Context.Loader.Callback() {
          public void load() {
            calc();
          }
        }, true);
      }
    });
  }
  
  public void calc() {
    for(int z = 0; z < Settings.Map.Depth; z++) {
      Texture texture = _map.createTextureFromLayer(z);
      Drawable layer = null;
      
      if(texture != null) {
        layer = Context.newDrawable();
        layer.setTexture(texture);
        layer.createQuad();
      }
      
      _layer[z] = layer;
    }
  }
  
  public void draw(int z) {
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