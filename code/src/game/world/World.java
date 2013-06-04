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
  
  public String getName() {
    return _name;
  }
  
  public boolean hasMap(int x, int y, int crc) {
    String name = x + "x" + y;
    Map m = _map.get(name);
    
    if(m == null) {
      m = new Map(this, x, y);
      boolean b = m.load();
      _map.put(name, m);
      if(!b) return false;
    }
    
    return m.getCRC() == crc;
  }
  
  public Region getRegion(int x, int y) {
    String name = x + "x" + y;
    Region r = _region.get(name);
    
    if(r == null) {
      Map m = _map.get(name);
      
      if(m == null) {
        m = new Map(this, x, y);
        m.request();
        
        System.out.println("Map " + name + " requested.");
        
        /*if(m.load()) {
          System.out.println("Map " + name + " loaded.");
        } else {
          System.out.println("Map " + name + " created.");
        }*/
        
        _map.put(name, m);
      }
      
      r = new Region(this);
      r.setMap(m);
      _region.put(name, r);
    }
    
    return r;
  }
  
  public Entity getEntity(int id) {
    for(Entity e : _entity) {
      if(e.getID() == id) {
        return e;
      }
    }
    
    return null;
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