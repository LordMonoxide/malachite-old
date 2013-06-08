package game.world;

import java.util.LinkedList;

import game.settings.Settings;
import physics.Movable;

public class Entity extends Movable {
  private Events _events;
  
  private int _id;
  
  private String _name;
  private game.data.Sprite _spriteFile;
  
  private World _world;
  private float _rx, _ry;
  private int _mx, _my;
  
  private Region _region;
  private int _z;
  
  private Stats _stats;
  
  private Sprite _sprite;
  
  public Entity() {
    setAcc(0.148f);
    setDec(0.361f);
    setVelTerm(1.75f);
    
    _events = new Events(this);
    _stats = new Stats(this);
  }
  
  public Events events() {
    return _events;
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
      _sprite.events().addDrawHandler(new Sprite.Events.Draw() {
        public void draw() {
          _events.raiseDraw();
        }
      });
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
    
    _events.raiseMove();
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
    
    _events.raiseMove();
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
  
  public Stats stats() {
    return _stats;
  }
  
  public static class Stats {
    public static final int VITALS = 2;
    public static final int VITAL_HP = 0;
    public static final int VITAL_MP = 1;
    public static final int STATS = 3;
    public static final int STAT_STR = 0;
    public static final int STAT_INT = 1;
    public static final int STAT_DEX = 2;
    
    private Entity _entity;
    
    private Vital[] _vital;
    private Stat[]  _stat;
    
    private Stats(Entity entity) {
      _entity = entity;
      
      _vital = new Vital[VITALS];
      _stat  = new Stat [STATS];
      
      for(int i = 0; i < VITALS; i++) _vital[i] = new Vital(_entity);
      for(int i = 0; i < STATS;  i++) _stat [i] = new Stat(_entity);
    }
    
    public Vital vital(int index) { return _vital[index]; }
    public Stat  stat (int index) { return _stat [index]; }
    
    public static class Vital {
      private Entity _entity;
      
      private int _max;
      private int _val;
      
      private Vital(Entity entity) {
        _entity = entity;
      }
      
      public int max() { return _max; }
      public int val() { return _val; }
      
      public void max(int max) { set(max, _val); }
      public void val(int val) { set(_max, val); }
      
      public void set(int max, int val) {
        _max = max;
        _val = val;
        
        _entity.events().raiseVitals();
      }
    }
    
    public static class Stat {
      private Entity _entity;
      
      private int _val;
      private float _exp;
      
      private Stat(Entity entity) {
        _entity = entity;
      }
      
      public   int val() { return _val; }
      public float exp() { return _exp; }
      
      public void val(int val)   { set(val, _exp); }
      public void exp(float exp) { set(_val, exp); }
      
      public void set(int val, float exp) {
        _val = val;
        _exp = exp;
        
        _entity.events().raiseStats();
      }
    }
  }
  
  public static class Events {
    private LinkedList<Draw>  _draw  = new LinkedList<Draw>();
    private LinkedList<Move>  _move  = new LinkedList<Move>();
    private LinkedList<Stats> _stats = new LinkedList<Stats>();
    
    public void addDrawHandler (Draw e)  { _draw.add(e); }
    public void addMoveHandler (Move e)  { _move.add(e); }
    public void addStatsHandler(Stats e) { _stats.add(e); }
    
    private Entity _entity;
    
    public Events(Entity entity) {
      _entity = entity;
    }
    
    public void raiseDraw() {
      for(Draw e : _draw) {
        e.draw(_entity);
      }
    }
    
    public void raiseMove() {
      for(Move e : _move) {
        e.move(_entity);
      }
    }
    
    public void raiseVitals() {
      for(Stats e : _stats) {
        e.vitals(_entity);
      }
    }
    
    public void raiseStats() {
      for(Stats e : _stats) {
        e.stats(_entity);
      }
    }
    
    public static abstract class Draw  { public abstract void draw  (Entity e); }
    public static abstract class Move  { public abstract void move  (Entity e); }
    
    public static abstract class Stats {
      public abstract void vitals(Entity e);
      public abstract void stats (Entity e);
    }
  }

}