package game;

import java.util.Collection;
import java.util.HashMap;

import network.packet.Packet;

import game.data.Item;
import game.data.Sprite;
import game.data.account.Permissions;
import game.data.util.Serializable;
import game.graphics.gui.Menu;
import game.network.Client;
import game.network.packet.Chat;
import game.network.packet.Data;
import game.network.packet.EntityCreate;
import game.network.packet.EntityMoveStart;
import game.network.packet.EntityMoveStop;
import game.network.packet.menu.CharDel;
import game.network.packet.menu.CharNew;
import game.network.packet.menu.CharUse;
import game.network.packet.menu.Login;
import game.settings.Settings;
import game.world.Entity;
import game.world.World;
import graphics.gl00.Context;

public class Game implements graphics.gl00.Game {
  private static Game _instance = new Game();
  public static Game getInstance() { return _instance; }
  
  private Client _net;
  
  private int _id;
  private Context _context;
  private World _world;
  private Entity _entity;
  
  private Permissions _permissions = new Permissions();
  
  private Menu _menu;
  
  private MenuStateListener _menuListener;
  private GameStateListener _gameListener;
  
  private HashMap<String, Sprite> _sprite = new HashMap<String, Sprite>();
  private HashMap<String, Item>   _item   = new HashMap<String, Item>();
  
  private Entity.Events.Draw _entityDraw = new Entity.Events.Draw() {
    public void draw(Entity e) {
      _gameListener.entityDraw(e);
    }
  };
  
  public Permissions getPermissions() { return _permissions; }
  public World       getWorld()       { return _world;       }
  public Entity      getEntity()      { return _entity;      }
  
  public void loadSprites(Serializable[] data) {
    for(Serializable s : data) {
      if(!s.exists()) {
        Data.Request request = new Data.Request(s);
        _net.send(request);
        System.out.println("Requesting sprite " + s.getFile());
      } else {
        s.load();
        System.out.println("Loading sprite " + s.getFile());
      }
      
      _sprite.put(s.getFile(), (Sprite)s);
    }
  }
  
  public void loadItems(Serializable[] data) {
    for(Serializable s : data) {
      if(!s.exists()) {
        Data.Request request = new Data.Request(s);
        _net.send(request);
        System.out.println("Requesting item " + s.getFile());
      } else {
        s.load();
        System.out.println("Loading item " + s.getFile());
      }
      
      _item.put(s.getFile(), (Item)s);
    }
  }
  
  public Collection<Sprite> getSprites() { return _sprite.values(); }
  public Collection<Item>   getItems()   { return _item.values(); }
  
  public void addSprite(Sprite s) { _sprite.put(s.getFile(), s); }
  public void addItem  (Item   i) { _item  .put(i.getFile(), i); }
  
  public Sprite getSprite(String file) { return _sprite.get(file); }
  public Item   getItem  (String file) { return _item  .get(file); }
  
  public void addEntity(Entity e) {
    _world.addEntity(e);
    e.events().addDrawHandler(_entityDraw);
  }
  
  public void setMenuStateListener(MenuStateListener listener) { _menuListener = listener; }
  public void setGameStateListener(GameStateListener listener) { _gameListener = listener; }
  
  public void start() {
    Settings.load();
    
    _net = new Client();
    _net.initPackets();
    _net.connect();
    
    //_context = new graphics.gl32.Context();
    //_context.setBackColour(new float[] {0, 0, 0, 0});
    //_context.setTitle("Malachite");
    //_context.setResizable(true);
    
    if(_context == null || !_context.create(this)) {
      System.out.println("OpenGL 3.2 not supported, trying 2.1...");
      
      //_context = new graphics.gl21.Context();
      //_context.setBackColour(new float[] {0, 0, 0, 0});
      //_context.setTitle("Malachite");
      //_context.setResizable(true);
      
      if(_context == null || !_context.create(this)) {
        System.out.println("OpenGL 2.1 not supported, trying 1.4...");
        
        _context = new graphics.gl14.Context();
        _context.setBackColour(new float[] {0, 0, 0, 0});
        _context.setTitle("Malachite");
        _context.setResizable(true);
        
        if(_context == null || !_context.create(this)) {
          System.out.println("Could not create OpenGL.");
        }
      }
    }
    
    _context.run();
  }
  
  public void init() {
    _menu = new Menu();
    _menu.load();
    _menu.push();
  }
  
  public void destroy() {
    if(_world != null) {
      _world.destroy();
    }
    
    _net.shutdown();
  }
  
  public void send(Packet p) {
    _net.send(p);
  }
  
  public <T extends Packet> void send(Packet p, final Class<T> responsePacket, final PacketCallback<T> responseCallback) {
    _net.send(p);
    _net.events().onPacket(new network.Client.Events.Packet() {
      public boolean event(Packet p) {
        if(p.getClass() == responsePacket) {
          responseCallback._handler = this;
          return responseCallback.recieved((T)p);
        }
        
        return false;
      }
    }, true);
  }
  
  public void login(String name, String pass) {
    Login p = new Login(name, pass);
    _net.send(p);
    _net.events().onPacket(new network.Client.Events.Packet() {
      public boolean event(Packet p) {
        if(p instanceof Login.Response) {
          remove();
          _menuListener.loggedIn((Login.Response)p);
          return true;
        }
        
        return false;
      }
    }, true);
  }
  
  public void charDel(int index) {
    CharDel p = new CharDel(index);
    _net.send(p);
    _net.events().onPacket(new network.Client.Events.Packet() {
      public boolean event(Packet p) {
        if(p instanceof CharDel.Response) {
          remove();
          _menuListener.charDeleted((CharDel.Response)p);
          return true;
        }
        
        return false;
      }
    }, true);
  }
  
  public void charCreate(String name, String sprite) {
    CharNew p = new CharNew(name, sprite);
    _net.send(p);
    _net.events().onPacket(new network.Client.Events.Packet() {
      public boolean event(Packet p) {
        if(p instanceof CharNew.Response) {
          remove();
          _menuListener.charCreated((CharNew.Response)p);
          return true;
        }
        
        return false;
      }
    }, true);
  }
  
  public void charUse(int index) {
    CharUse p = new CharUse(index);
    _net.send(p);
    _net.events().onPacket(new network.Client.Events.Packet() {
      public boolean event(Packet p) {
        if(p instanceof CharUse.Response) {
          remove();
          _id = ((CharUse.Response)p).getID();
          _menuListener.charUsed((CharUse.Response)p);
          return true;
        }
        
        return false;
      }
    }, true);
  }
  
  public void loadWorld(String world) {
    _world = new World(world);
  }
  
  public void loadGame() {
    _net.events().onPacket(new network.Client.Events.Packet() {
      public boolean event(Packet p) {
        if(p instanceof EntityCreate) {
          if(((EntityCreate)p).getEntity().getID() == _id) {
            remove();
            
            _entity = ((EntityCreate)p).getEntity();
            _entity.events().addMoveHandler(new Entity.Events.Move() {
              public void move(Entity e) { updateCamera(); }
            });
            
            _entity.events().addStatsHandler(new Entity.Events.Stats() {
              public void vitals(Entity e) { if(_gameListener != null) _gameListener.updateVitals(e.stats()); }
              public void stats(Entity e)  { if(_gameListener != null) _gameListener.updateStats(e.stats()); }
            });
            
            _entity.events().addInvHandler(new Entity.Events.Inv() {
              public void receive(Entity e) { if(_gameListener != null) _gameListener.updateInv(e.inv()); }
            });
            
            p.process();
            _menuListener.inGame();
            
            return true;
          }
        }
        
        return false;
      }
    }, true);
  }
  
  public void updateCamera() {
    _context.setCameraX(-_entity.getX() + _context.getW() / 2);
    _context.setCameraY(-_entity.getY() + _context.getH() / 2);
  }
  
  public void startMoving(float bear) {
    _net.send(new EntityMoveStart(_entity.getX(), _entity.getY(), bear));
  }
  
  public void stopMoving() {
    _net.send(new EntityMoveStop(_entity));
  }
  
  public void sendChat(String text) {
    _net.send(new Chat(text));
  }
  
  public void gotChat(String name, String text) {
    _gameListener.gotChat(name, text);
  }
  
  public static interface MenuStateListener {
    public void loggedIn(Login.Response packet);
    public void charDeleted(CharDel.Response packet);
    public void charCreated(CharNew.Response packet);
    public void charUsed(CharUse.Response packet);
    public void inGame();
  }
  
  public static interface GameStateListener {
    public void gotChat(String name, String text);
    public void entityDraw(Entity e);
    public void updateVitals(Entity.Stats stats);
    public void updateStats(Entity.Stats stats);
    public void updateInv(Entity.Inv[] inv);
  }
  
  public static abstract class PacketCallback<T extends Packet> {
    private network.Client.Events.Packet _handler;
    
    public void remove() {
      _handler.remove();
    }
    
    public abstract boolean recieved(T packet);
  }
}