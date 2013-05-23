package game.world;

import game.settings.Settings;
import physics.Movable;

public class Entity extends Movable {
  private EntityCallback _callback;
  
  private int _id;
  
  private String _name;
  private game.data.Sprite _spriteFile;
  
  private World _world;
  private float _rx, _ry;
  private int _mx, _my;
  
  private Region _region;
  private int _z;
  
  private Sprite _sprite;
  
  public Entity() {
    setAcc(0.148f);
    setDec(0.361f);
    setVelTerm(1.75f);
  }
  
  public void setEntityCallback(EntityCallback callback) {
    _callback = callback;
  }
  
  public Sprite getSprite() {
    return _sprite;
  }
  
  public void setSprite(game.data.Sprite sprite) {
    _spriteFile = sprite;
  }
  
  public int getID() {
    return _id;
  }
  
  public void setID(int id) {
    _id = id;
  }
  
  public String getName() {
    return _name;
  }
  
  public void setName(String name) {
    _name = name;
  }
  
  public World getWorld() {
    return _world;
  }
  
  public void setWorld(World world) {
    _world = world;
    
    if(_sprite != null) {
      _sprite.remove();
      _sprite = null;
    }
    
    if(_world != null) {
      _sprite = Sprite.add(_spriteFile);
      _sprite.setX(_x);
      _sprite.setY(_y);
      _sprite.setZ(_z);
    }
  }
  
  public void setInitialXY(float x, float y) {
    _x = x;
    _y = y;
    _rx = (_x % Settings.Map.Size);
    _ry = (_y % Settings.Map.Size);
    _mx = (int)_x / Settings.Map.Size;
    _my = (int)_y / Settings.Map.Size;
    
    if(_x < 0) {
      _rx += Settings.Map.Size;
      _mx -= 1;
    }
    
    if(_y < 0) {
      _ry += Settings.Map.Size;
      _my -= 1;
    }
  }
  
  public void setX(float x) {
    _x = x;
    _rx = (_x % Settings.Map.Size);
    if(_rx < 0) _rx += Settings.Map.Size;
    
    int mx = (int)_x / Settings.Map.Size;
    if(_x < 0) mx -= 1;
    if(mx != _mx) {
      _mx = mx;
      setRegion(_world.getRegion(_mx, _my));
    }
    
    if(_sprite != null) {
      _sprite.setX(_x);
    }
    
    if(_callback != null) {
      _callback.move(this);
    }
  }
  
  public void setY(float y) {
    _y = y;
    _ry = (_y % Settings.Map.Size);
    if(_ry < 0) _ry += Settings.Map.Size;
    
    int my = (int)_y / Settings.Map.Size;
    if(_y < 0) my -= 1;
    if(my != _my) {
      _my = my;
      setRegion(_world.getRegion(_mx, _my));
    }
    
    if(_sprite != null) {
      _sprite.setY(_y);
    }
    
    if(_callback != null) {
      _callback.move(this);
    }
  }
  
  public int getZ() {
    return _z;
  }
  
  public void setZ(int z) {
    _z = z;
    
    if(_sprite != null) {
      _sprite.setZ(_z);
    }
  }
  
  public float getRX() {
    return _rx;
  }
  
  public float getRY() {
    return _ry;
  }
  
  public int getMX() {
    return _mx;
  }
  
  public int getMY() {
    return _my;
  }
  
  public void setVel(float vel) {
    _vel = vel;
    _sprite.setVel(vel);
  }
  
  public void setBear(float bear) {
    _bear = bear;
    _sprite.setBear(bear);
  }
  
  public Region getRegion() {
    return _region;
  }
  
  public void setRegion(Region r) {
    _region = r;
  }
  
  public interface EntityCallback {
    public void move(Entity e);
  }
}