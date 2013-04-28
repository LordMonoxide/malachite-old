package graphics.shared.gui.controls;

import java.util.LinkedList;

import graphics.shared.gui.Control;
import graphics.shared.gui.GUI;
import graphics.themes.Theme;

public class Scrollbar extends Control {
  private Button _up, _down;
  private int _min, _max, _val;
  private Orientation _orientation;
  
  private LinkedList<ControlEventScroll> _eventScroll = new LinkedList<ControlEventScroll>();

  public void addEventScrollHandler(ControlEventScroll e) { _eventScroll.add(e); }
  
  public Scrollbar(GUI gui) {
    this(gui, Theme.getInstance());
  }
  
  public Scrollbar(GUI gui, Theme theme) {
    super(gui);
    
    ControlEventClick up = new ControlEventClick() {
      public void event() {
        if(_val > _min) setVal(_val - 1);
      }
    };
    
    ControlEventClick down = new ControlEventClick() {
      public void event() {
        if(_val < _max) setVal(_val + 1);
      }
    };
    
    ControlEventWheel wheel = new ControlEventWheel() {
      public void event(int delta) {
        while(delta > 0) {
          delta -= 120;
          _up.handleMouseUp(0, 0, 0);
        }
        
        while(delta < 0) {
          delta += 120;
          _down.handleMouseUp(0, 0, 0);
        }
      }
    };
    
    addEventMouseWheelHandler(wheel);
    
    _up = new Button(gui, theme);
    _up.addEventClickHandler(up);
    _up.addEventDoubleClickHandler(up);
    _up.addEventMouseWheelHandler(wheel);
    
    _down = new Button(gui, theme);
    _down.addEventClickHandler(down);
    _down.addEventDoubleClickHandler(down);
    _down.addEventMouseWheelHandler(wheel);
    
    Controls().add(_up);
    Controls().add(_down);
    
    theme.create(this);
  }
  
  public int getMin() { return _min; }
  public int getMax() { return _max; }
  public int getVal() { return _val; }
  
  public void setMin(int min) { 
    _min = min;
    if(_min > _max) _min = _max;
    if(_val < _min) setVal(_min);
  }
  
  public void setMax(int max) {
    _max = max;
    if(_max < _min) _max = _min;
    if(_val > _max) setVal(_max);
  }
  
  public void setVal(int val) {
    if(val > _max) val = _max;
    if(val < _min) val = _min;
    if(_val != val) {
      int delta = val - _val;
      _val = val;
      raiseScroll(delta);
    }
  }
  
  public Orientation getOrientation() {
    return _orientation;
  }
  
  public void setOrientation(Orientation orientation) {
    _orientation = orientation;
    resize();
  }
  
  protected void resize() {
    switch(_orientation) {
      case VERTICAL:
        _up  .setText("\u25B2");
        _down.setText("\u25BC");
        _up.setWH(_loc[2], _loc[3] / 2);
        _down.setWH(_loc[2], _loc[3] / 2);
        _down.setXY(0, _up.getH());
        break;
        
      case HORIZONTAL:
        _up  .setText("\u25C4");
        _down.setText("\u25BA");
        _up.setWH(_loc[2] / 2, _loc[3]);
        _down.setWH(_loc[2] / 2, _loc[3]);
        _down.setXY(_up.getW(), 0);
    }
  }
  
  protected void raiseScroll(int delta) {
    for(ControlEventScroll e : _eventScroll) {
      e.setControl(this);
      e.event(delta);
    }
  }
  
  public static abstract class ControlEventScroll extends ControlEvent {
    public abstract void event(int delta);
  }
  
  public static enum Orientation {
    HORIZONTAL, VERTICAL;
  }
}