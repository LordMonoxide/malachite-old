package game;

import java.util.HashMap;

import network.packet.Packet;

import game.data.Sprite;
import game.graphics.gui.Menu;
import game.network.Client;
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
  
  private Menu _menu;
  
  private Menu.Events.Login _eventLogin;
  
  private HashMap<String, Sprite> _sprite = new HashMap<String, Sprite>();
  
  public World  getWorld()  { return _world;  }
  public Entity getEntity() { return _entity; }
  
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
    /*_entity = new Entity();
    _entity.setX(Settings.Map.Size / 2);
    _entity.setY(Settings.Map.Size / 2);
    _entity.setZ(2);
    
    _world = new World("default");
    _world.addEntity(_entity);*/
    
    _eventLogin = new Menu.Events.Login() {
      public void event(String name, String pass) {
        login(name, pass);
      }
    };
    
    _menu = new game.graphics.gui.Menu();
    _menu.events().onLogin(_eventLogin);
    
    _menu.load();
    _menu.push();
  }
  
  public void destroy() {
    //_world.destroy();
    _net.shutdown();
  }
  
  private void login(String name, String pass) {
    Login p = new Login(name, pass);
    _net.send(p);
    
    _net.events().onPacket(new network.Client.Events.Packet() {
      public boolean event(Packet p) {
        if(p instanceof Login.Response) {
          _eventLogin.loggedIn((Login.Response)p);
          remove();
          return true;
        }
        
        return false;
      }
    }, true);
  }
}