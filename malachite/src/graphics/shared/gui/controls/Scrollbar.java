package graphics.shared.gui.controls;

import java.util.LinkedList;

import graphics.shared.gui.Control;
import graphics.shared.gui.GUI;

public class Scrollbar extends Control {
  private Button _up, _down;
  private int _min, _max = 99, _val;
  
  private LinkedList<ControlEventScroll> _eventScroll = new LinkedList<ControlEventScroll>();

  public void addEventScrollHandler (ControlEventScroll e) { _eventScroll.add(e); }
  
  public Scrollbar(GUI gui) {
    super(gui);
    
    ControlEventClick up = new ControlEventClick() {
      public void event() {
        if(_val < _max) setVal(_val + 1);
      }
    };
    
    ControlEventClick down = new ControlEventClick() {
      public void event() {
        if(_val > _min) setVal(_val - 1);
      }
    };
    
    _up = new Button(gui);
    _up.setText(null);
    _up.addEventClickHandler(up);
    _up.addEventDoubleClickHandler(up);
    
    _down = new Button(gui);
    _down.setText(null);
    _down.addEventClickHandler(down);
    _down.addEventDoubleClickHandler(down);
    
    Controls().add(_up);
    Controls().add(_down);
    
    setWH(16, 100);
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
    if(_val != val) {
      int delta = val - _val;
      _val = val;
      raiseScroll(delta);
    }
  }
  
  public void setW(float w) {
    super.setW(w);
    updateSize();
  }
  
  public void setH(float h) {
    super.setH(h);
    updateSize();
  }
  
  public void setWH(float w, float h) {
    super.setWH(w, h);
    updateSize();
  }
  
  public void setXYWH(float x, float y, float w, float h) {
    super.setXYWH(x, y, w, h);
    updateSize();
  }
  
  public void setXYWH(float[] loc) {
    super.setXYWH(loc);
    updateSize();
  }
  
  private void updateSize() {
    _up.setWH(_loc[2], _loc[3] / 2);
    _down.setWH(_loc[2], _loc[3] / 2);
    _down.setY(_up.getH());
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
}