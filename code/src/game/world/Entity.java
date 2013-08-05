package game.world;

import java.util.LinkedList;

import game.data.Item;
import game.settings.Settings;
import graphics.util.Time;
import physics.Movable;

public class Entity extends Movable {
  public String toString() {
    return "Entity '" + _name + "' on " + _world + " at (" + _x + ", " + _y + ", " + _z + ")";
  }
  
  private Events _events;
  
  private int _id;
  
  private String _name;
  private String _spriteFile;
  private Type   _type;
  
  private World _world;
  private float _rx, _ry;
  private int _mx, _my;
  
  private Region _region;
  private int _z;
  
  private Stats _stats;
  private Inv[] _inv;
  private Equip _equip;
  private long _curr;
  
  private Sprite _sprite;
  
  private double _lastVitals;
  
  public Entity() {
    setAcc(0.148f);
    setDec(0.361f);
    setVelTerm(1.75f);
    
    _events = new Events(this);
    _stats  = new Stats(this);
    _inv    = new Inv[Settings.Player.Inventory.Size];
    _equip  = new Equip(this);
  }
  
  public Events events() {
    return _events;
  }
  
  public Sprite getSprite() {
    return _sprite;
  }
  
  public void setSprite(String sprite) {
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
  
  public Type getType() {
    return _type;
  }
  
  public void setType(Type type) {
    _type = type;
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
  
  public boolean drawVitals() {
    return _lastVitals > Time.getTime();
  }
  
  public Stats stats() {
    return _stats;
  }
  
  public Inv inv(int index) {
    return _inv[index];
  }
  
  public void inv(int index, Inv inv) {
    Inv oldInv = _inv[index];
    _inv[index] = inv;
    _events.raiseInvUpdate(oldInv, inv);
  }
  
  public Inv[] inv() {
    return _inv;
  }
  
  public void inv(Inv[] inv) {
    _inv = inv;
    _events.raiseInvReceive();
  }
  
  public Equip equip() {
    return _equip;
  }
  
  public long currency() {
    return _curr;
  }
  
  public void currency(long curr) {
    _curr = curr;
    _events.raiseInvCurrency();
  }
  
  public static class Stats {
    private Entity _entity;
    
    private Vital _hp, _mp;
    private Stat  _str, _int, _dex;
    private float _weight;
    
    private Stats(Entity entity) {
      _entity = entity;
      
      _hp  = new Vital(_entity);
      _mp  = new Vital(_entity);
      _str = new Stat(_entity);
      _int = new Stat(_entity);
      _dex = new Stat(_entity);
    }
    
    public Vital vitalHP() { return _hp; }
    public Vital vitalMP() { return _mp; }
    public Stat  statSTR() { return _str; }
    public Stat  statINT() { return _int; }
    public Stat  statDEX() { return _dex; }
    
    public float weight()             { return _weight; }
    public void  weight(float weight) { _weight = weight; }
    
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
      public void val(int val) { set(_max, val); _entity._lastVitals = Time.getTime() + 5000; }
      
      public void set(int max, int val) {
        _max = max;
        _val = val;
        
        _entity._lastVitals = Time.getTime() + 5000;
        _entity.events().raiseVitals();
      }
    }
    
    public static class Stat {
      private Entity _entity;
      
      private int _val;
      
      private Stat(Entity entity) {
        _entity = entity;
      }
      
      public  int val() { return _val; }
      public void val(int val) {
        _val = val;
        _entity.events().raiseStats();
      }
    }
    
    public static class Buffs {
      private LinkedList<Buff> _buff = new LinkedList<Buff>();
      
      private int _fixed;
      private float _percent;
      
      public int fixed() { return _fixed; }
      public float percent() { return _percent; }
      
      public int add(Buff buff) {
        if(_buff.add(buff)) {
          if(buff._percent) {
            _percent += buff._val;
          } else {
            _fixed += buff._val;
          }
          
          buff._buffs = this;
          buff._index = _buff.size();
          
          return buff._index;
        }
        
        return 0;
      }
      
      public boolean remove(int buff) {
        return _buff.remove(buff) != null;
      }
      
      public static class Buff {
        private Buffs _buffs;
        private int _index;
        
        private float _val;
        private boolean _percent;
        
        public float   val() { return _val; }
        public void    val(float val) { _val = val; }
        public boolean percent() { return _percent; }
        public void    percent(boolean percent) { _percent = percent; }
        
        public void remove() {
          _buffs.remove(_index);
        }
      }
    }
  }
  
  public static class Inv {
    private int _index;
    
    public Inv(int index) {
      _index = index;
    }
    
    private Item _item;
    private  int _val;
    
    public  int index() { return _index; }
    public Item item()  { return _item; }
    public  int val ()  { return _val;  }
    public void item(Item item) { _item = item; }
    public void val ( int val)  { _val  = val;  }
  }
  
  public static class Equip {
    private Entity _entity;
    
    private Equip(Entity entity) {
      _entity = entity;
    }
    
    private Item   _hand1;
    private Item   _hand2;
    private Item[] _armour = new Item[Item.ITEM_TYPE_ARMOUR_COUNT];
    private Item[] _bling  = new Item[Item.ITEM_TYPE_BLING_COUNT];
    
    public Item hand1()          { return _hand1; }
    public Item hand2()          { return _hand2; }
    public Item armour(int type) { return _armour[type]; }
    public Item bling (int type) { return _bling [type]; }
    
    public void hand1 (Item item)           { _hand1        = item; _entity._events.raiseInvEquip(this); }
    public void hand2 (Item item)           { _hand2        = item; _entity._events.raiseInvEquip(this); }
    public void armour(int type, Item item) { _armour[type] = item; _entity._events.raiseInvEquip(this); }
    public void bling (int type, Item item) { _bling [type] = item; _entity._events.raiseInvEquip(this); }
  }
  
  public enum Type {
    Player, Sprite, Item, NPC;
    
    public static Type valueOf(int index) {
      switch(index) {
        case 0: return Player;
        case 1: return Sprite;
        case 2: return Item;
        case 3: return NPC;
      }
      
      return null;
    }
  }
  
  public static class Events {
    private LinkedList<Draw>  _draw  = new LinkedList<Draw>();
    private LinkedList<Move>  _move  = new LinkedList<Move>();
    private LinkedList<Stats> _stats = new LinkedList<Stats>();
    private LinkedList<Inv>   _inv   = new LinkedList<Inv>();
    
    public void addDrawHandler (Draw e)  { _draw.add(e); }
    public void addMoveHandler (Move e)  { _move.add(e); }
    public void addStatsHandler(Stats e) { _stats.add(e); }
    public void addInvHandler  (Inv e)   { _inv.add(e); }
    
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
    
    public void raiseInvReceive() {
      for(Inv e : _inv) {
        e.update(_entity);
      }
    }
    
    public void raiseInvUpdate(Entity.Inv oldInv, Entity.Inv newInv) {
      for(Inv e : _inv) {
        e.update(_entity, oldInv, newInv);
      }
    }
    
    public void raiseInvEquip(Entity.Equip equip) {
      for(Inv e : _inv) {
        e.equip(_entity, equip);
      }
    }
    
    public void raiseInvCurrency() {
      for(Inv e : _inv) {
        e.currency(_entity);
      }
    }
    
    public static abstract class Draw { public abstract void draw(Entity e); }
    public static abstract class Move { public abstract void move(Entity e); }
    
    public static abstract class Stats {
      public abstract void vitals(Entity e);
      public abstract void stats (Entity e);
    }
    
    public static abstract class Inv {
      public abstract void update(Entity e);
      public abstract void update(Entity e, Entity.Inv oldInv, Entity.Inv newInv);
      public abstract void equip (Entity e, Entity.Equip equip);
      public abstract void currency(Entity e);
    }
  }
}