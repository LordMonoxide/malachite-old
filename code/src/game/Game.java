package game;

import java.util.HashMap;

import network.packet.Packet;

import game.data.Sprite;
import game.data.account.Permissions;
import game.graphics.gui.Menu;
import game.network.Client;
import game.network.packet.CharDel;
import game.network.packet.CharNew;
import game.network.packet.CharUse;
import game.network.packet.Login;
import game.settings.Settings;
import game.world.Entity;
import game.world.World;
import graphics.gl00.Context;

public class Game implements graphics.gl00.Game {
  private Client _net;
  
  private Context _context;
  private World _world;
  private Entity _entity;
  
  private Permissions _permissions = new Permissions();
  
  private Menu _menu;
  
  private HashMap<String, Sprite> _sprite = new HashMap<String, Sprite>();
  
  public Permissions getPermissions() { return _permissions; }
  public World       getWorld()       { return _world;       }
  public Entity      getEntity()      { return _entity;      }
  
  public Sprite getSprite(String file) {
    Sprite s = _sprite.get(file);
    
    if(s == null) {
      if((s = new Sprite(file)).load()) {
        System.out.println("Sprite " + file + " loaded.");
      } else {
        System.err.println("Couldn't load sprite " + file);
      }
      
      _sprite.put(file, s);
    }
    
    return s;
  }
  
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
    _menu = new game.graphics.gui.Menu();
    _menu.load();
    _menu.push();
  }
  
  public void destroy() {
    if(_world != null) {
      _world.destroy();
    }
    
    _net.shutdown();
  }
  
  public void login(String name, String pass, final StateListener s) {
    Login p = new Login(name, pass);
    _net.send(p);
    _net.events().onPacket(new network.Client.Events.Packet() {
      public boolean event(Packet p) {
        if(p instanceof Login.Response) {
          remove();
          s.loggedIn((Login.Response)p);
          return true;
        }
        
        return false;
      }
    }, true);
  }
  
  public void charDel(int index, final StateListener s) {
    CharDel p = new CharDel(index);
    _net.send(p);
    _net.events().onPacket(new network.Client.Events.Packet() {
      public boolean event(Packet p) {
        if(p instanceof CharDel.Response) {
          remove();
          s.charDeleted((CharDel.Response)p);
          return true;
        }
        
        return false;
      }
    }, true);
  }
  
  public void charNew(String name, final StateListener s) {
    CharNew p = new CharNew(name);
    _net.send(p);
    _net.events().onPacket(new network.Client.Events.Packet() {
      public boolean event(Packet p) {
        if(p instanceof CharNew.Response) {
          remove();
          s.charCreated((CharNew.Response)p);
          return true;
        }
        
        return false;
      }
    }, true);
  }
  
  public void charUse(int index, final StateListener s) {
    CharUse p = new CharUse(index);
    _net.send(p);
    _net.events().onPacket(new network.Client.Events.Packet() {
      public boolean event(Packet p) {
        if(p instanceof CharUse.Response) {
          remove();
          loadGame();
          s.charUsed((CharUse.Response)p);
          return true;
        }
        
        return false;
      }
    }, true);
  }
  
  public void loadGame() {
    _entity = new Entity();
    _entity.setSprite(getSprite("Isaac"));
    _entity.setWorld(_world);
    _entity.setX(Settings.Map.Size / 2);
    _entity.setY(Settings.Map.Size / 2);
    _entity.setZ(2);
    
    _world = new World("default");
    _world.addEntity(_entity);
  }
  
  public static interface StateListener {
    public void loggedIn(Login.Response packet);
    public void charDeleted(CharDel.Response packet);
    public void charCreated(CharNew.Response packet);
    public void charUsed(CharUse.Response packet);
  }
}