package game.world;

import game.data.Map;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;

import physics.Sandbox;

public class World extends Sandbox {
  private HashMap<String, Region> _region = new HashMap<String, Region>();
  private HashMap<String, Map> _map = new HashMap<String, Map>();
  
  private LinkedList<Entity> _entity = new LinkedList<Entity>();
  
  private String _name;
  
  public World(String name) {
    _name = name;
    
    File d = new File("../data/worlds/" + _name + "/");
    d.mkdirs();
    
    startSandbox();
  }
  
  public void destroy() {
    stopSandbox();
  }
  
  public Region getRegion(int x, int y) {
    String name = x + "x" + y;
    Region r = _region.get(name);
    
    if(r == null) {
      Map m = _map.get(name);
      
      if(m == null) {
        m = new Map(_name, x, y);
        
        if(m.load()) {
          System.out.println("Map " + name + " loaded.");
        } else {
          System.out.println("Map " + name + " created.");
        }
        
        _map.put(name, m);
      }
      
      r = new Region(this);
      r.setMap(m);
      //r.calc();
      _region.put(name, r);
    }
    
    return r;
  }
  
  public void addEntity(Entity e) {
    e.setWorld(this);
    e.setRegion(getRegion(e.getMX(), e.getMY()));
    _entity.add(e);
    addToSandbox(e);
  }
  
  public void removeEntity(Entity e) {
    removeFromSandbox(e);
    _entity.remove(e);
    e.setWorld(null);
  }
}