package game;

import game.settings.Settings;
import game.world.Entity;
import game.world.World;
import graphics.gl00.Context;

public class Game implements graphics.gl00.Game {
  private Context _context;
  private World _world;
  private Entity _entity;
  
  public World  getWorld()  { return _world;  }
  public Entity getEntity() { return _entity; }
  
  public void start() {
    Settings.load();
    
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
    _entity = new Entity();
    _entity.setX(Settings.Map.Size / 2);
    _entity.setY(Settings.Map.Size / 2);
    _entity.setZ(2);
    
    _world = new World("default");
    _world.addEntity(_entity);
    
    //Menu g = new Menu();
    game.graphics.gui.Game g = new game.graphics.gui.Game();
    g.load();
    g.push();
  }
  
  public void destroy() {
    _world.destroy();
  }
}