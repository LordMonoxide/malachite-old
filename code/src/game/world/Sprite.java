package game.world;

import game.data.Sprite.Anim;
import graphics.gl00.Context;
import graphics.gl00.Drawable;
import graphics.gl00.Matrix;
import graphics.util.Time;

import java.util.ArrayList;
import java.util.LinkedList;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class Sprite {
  private static ArrayList<Sprite> _sprite = new ArrayList<Sprite>();
  private static Matrix _matrix = Context.getMatrix();
  
  private static Context _context = Context.getContext();
  
  private static int _tallestSprite;
  
  public static int count() {
    return _sprite.size();
  }
  
  public static Sprite add(game.data.Sprite sprite) { return add(sprite, true); }
  public static Sprite add(game.data.Sprite sprite, boolean visible) {
    Sprite s = new Sprite(sprite);
    s.setVisible(visible);
    
    synchronized(_sprite) {
      _sprite.add(s);
    }
    
    if(s._h > _tallestSprite) {
      _tallestSprite = s._h;
    }
    
    return s;
  }
  
  private static void remove(Sprite sprite) {
    synchronized(_sprite) {
      _sprite.remove(sprite);
    }
  }
  
  public static void draw(int z) {
    int y1 = (int)_context.getCameraY();
    int y2 = _context.getH() - y1 + _tallestSprite;
    
    for(int y = -y1; y <= y2; y++) {
      synchronized(_sprite) {
        for(Sprite sprite : _sprite) {
          if(sprite._visible) {
            if((int)sprite._y == y && sprite._z == z) {
              sprite.draw();
            }
          }
        }
      }
    }
  }
  
  private ScriptEngineManager _manager = new ScriptEngineManager();
  private ScriptEngine _engine = _manager.getEngineByName("JavaScript");
  private Invocable _script;
  
  private Events _events;
  
  private game.data.Sprite _source;
  
  private int _w, _h;
  private Drawable[] _frame;
  private Anim[] _anim;
  
  private boolean _visible;
  
  private float _x, _y;
  private int _z;
  private int _animNum;
  private int _listNum;
  private int _frameNum;
  private double _timer;
  
  public Sprite(game.data.Sprite sprite) {
    _events = new Events();
    _source = sprite;
    _visible = true;
    _w = sprite.getW();
    _h = sprite.getH();
    _frame = sprite.createDrawables();
    _anim = sprite.createAnimList();
    setAnim(sprite.getDefault());
    
    if(sprite.getScript() != null) {
      try {
        _engine.put("sprite", this);
        _engine.eval(sprite.getScript());
        _script = (Invocable)_engine;
        _script.invokeFunction("init");
      } catch(ScriptException e) {
        e.printStackTrace();
      } catch(NoSuchMethodException e) {
      }
    }
  }
  
  public Events events() {
    return _events;
  }
  
  public game.data.Sprite getSource() {
    return _source;
  }
  
  public boolean getVisible() {
    return _visible;
  }
  
  public void setVisible(boolean visible) {
    _visible = visible;
  }
  
  public float getX() {
    return _x;
  }
  
  public void setX(float x) {
    _x = x;
  }
  
  public float getY() {
    return _y;
  }
  
  public void setY(float y) {
    _y = y;
  }
  
  public int getZ() {
    return _z;
  }
  
  public void setZ(int z) {
    _z = z;
  }
  
  public int getW() {
    return _w;
  }
  
  public int getH() {
    return _h;
  }
  
  public void setVel(float vel) {
    if(_script != null) {
      try {
        _script.invokeFunction("setVelocity", vel);
      } catch(NoSuchMethodException | ScriptException e) {
        e.printStackTrace();
      }
    }
  }
  
  public void setBear(float bear) {
    if(_script != null) {
      try {
        _script.invokeFunction("setBearing", bear);
      } catch(NoSuchMethodException | ScriptException e) {
        e.printStackTrace();
      }
    }
  }
  
  public void remove() {
    remove(this);
  }
  
  public void setAnim(String anim) {
    for(int i = 0; i < _anim.length; i++) {
      if(_anim[i]._name.equals(anim)) {
        setAnim(i);
        break;
      }
    }
  }
  
  public void setAnim(int anim) {
    _animNum = anim;
    setList(_anim[_animNum].getDefault());
  }
  
  private void setList(int list) {
    _listNum = list;
    if(_listNum > _anim[_animNum].getListSize() - 1) _listNum = 0;
    setFrame(_anim[_animNum].getList(_listNum).getFrame());
    _timer = _anim[_animNum].getList(_listNum).getTime() + Time.getTime();
  }
  
  public float getFrameX() { return _frame[_frameNum].getX(); }
  public float getFrameY() { return _frame[_frameNum].getY(); }
  public float getFrameFX() { return _source.getFrame(_frameNum)._fx; }
  public float getFrameFY() { return _source.getFrame(_frameNum)._fy; }
  public float getFrameW() { return _source.getFrame(_frameNum)._w; }
  public float getFrameH() { return _source.getFrame(_frameNum)._h; }
  
  private void setFrame(int frame) {
    _frameNum = frame;
  }
  
  public void draw() {
    _matrix.push();
    _matrix.translate(_x, _y);
    _frame[_frameNum].draw();
    _events.raiseDraw();
    _matrix.pop();
    
    if(_anim[_animNum].getListSize() > 1) {
      if(_timer <= Time.getTime()) {
        setList(_listNum + 1);
      }
    }
  }
  
  public static class Events {
    private LinkedList<Draw> _draw = new LinkedList<Draw>();
    public void addDrawHandler(Draw e) { _draw.add(e); }
    
    public void raiseDraw() {
      for(Draw e : _draw) {
        e.draw();
      }
    }
    
    public static abstract class Draw {
      public abstract void draw();
    }
  }
}