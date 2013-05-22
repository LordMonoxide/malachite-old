package game.world;

import game.settings.Settings;
import physics.Movable;

public class Entity extends Movable {
  private EntityCallback _callback;
  
  private String _name;
  
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
    _sprite = Sprite.add(sprite);
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
    
    _sprite.setX(_x);
    
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
    
    _sprite.setY(_y);
    
    if(_callback != null) {
      _callback.move(this);
    }
  }
  
  public int getZ() {
    return _z;
  }
  
  public void setZ(int z) {
    _z = z;
    _sprite.setZ(_z);
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