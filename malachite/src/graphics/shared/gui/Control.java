package graphics.shared.gui;

import java.util.LinkedList;

import org.lwjgl.input.Keyboard;

import graphics.gl00.Context;
import graphics.gl00.Drawable;
import graphics.gl00.Matrix;
import graphics.shared.textures.Textures;
import graphics.themes.Theme;
import graphics.util.Time;

public class Control {
  protected GUI      _gui;
  protected Matrix   _matrix   = Context.getMatrix();
  protected Textures _textures = Context.getTextures();
  
  protected Control     _controlParent;
  protected ControlList _controlList = new ControlList(this);
  protected Control     _controlNext;
  protected Control     _controlPrev;
  
  protected Drawable _border;
  protected Drawable _background;
  protected float[]  _loc          = {0, 0, 0, 0};
  protected float[]  _foreColour   = {1, 1, 1, 1};
  protected boolean  _visible      = true;
  protected boolean  _acceptsFocus = true;
  protected boolean  _focus        = false;
  
  protected Drawable _selBox;
  protected int[] _selColour;
  
  private double _lastClick;
  
  private LinkedList<ControlEventDraw>   _eventDraw        = new LinkedList<ControlEventDraw>();
  private LinkedList<ControlEventMouse>  _eventMouseDown   = new LinkedList<ControlEventMouse>();
  private LinkedList<ControlEventMouse>  _eventMouseUp     = new LinkedList<ControlEventMouse>();
  private LinkedList<ControlEventMouse>  _eventMouseMove   = new LinkedList<ControlEventMouse>();
  private LinkedList<ControlEventWheel>  _eventMouseWheel  = new LinkedList<ControlEventWheel>();
  private LinkedList<ControlEventHover>  _eventMouseEnter  = new LinkedList<ControlEventHover>();
  private LinkedList<ControlEventHover>  _eventMouseLeave  = new LinkedList<ControlEventHover>();
  private LinkedList<ControlEventKey>    _eventKeyDown     = new LinkedList<ControlEventKey>();
  private LinkedList<ControlEventKey>    _eventKeyUp       = new LinkedList<ControlEventKey>();
  private LinkedList<ControlEventChar>   _eventCharDown    = new LinkedList<ControlEventChar>();
  private LinkedList<ControlEventAxis>   _eventAxisLeft    = new LinkedList<ControlEventAxis>();
  private LinkedList<ControlEventAxis>   _eventAxisRight   = new LinkedList<ControlEventAxis>();
  private LinkedList<ControlEventButton> _eventButtonDown  = new LinkedList<ControlEventButton>();
  private LinkedList<ControlEventButton> _eventButtonUp    = new LinkedList<ControlEventButton>();
  private LinkedList<ControlEventClick>  _eventClick       = new LinkedList<ControlEventClick>();
  private LinkedList<ControlEventClick>  _eventDoubleClick = new LinkedList<ControlEventClick>();
  private LinkedList<ControlEventFocus>  _eventGotFocus    = new LinkedList<ControlEventFocus>();
  private LinkedList<ControlEventFocus>  _eventLostFocus   = new LinkedList<ControlEventFocus>();
  
  public void addEventDrawHandler       (ControlEventDraw   e) { _eventDraw       .add(e); }
  public void addEventMouseDownHandler  (ControlEventMouse  e) { _eventMouseDown  .add(e); }
  public void addEventMouseUpHandler    (ControlEventMouse  e) { _eventMouseUp    .add(e); }
  public void addEventMouseMoveHandler  (ControlEventMouse  e) { _eventMouseMove  .add(e); }
  public void addEventMouseWheelHandler (ControlEventWheel  e) { _eventMouseWheel .add(e); }
  public void addEventMouseEnterHandler (ControlEventHover  e) { _eventMouseEnter .add(e); }
  public void addEventMouseLeaveHandler (ControlEventHover  e) { _eventMouseLeave .add(e); }
  public void addEventKeyDownHandler    (ControlEventKey    e) { _eventKeyDown    .add(e); }
  public void addEventKeyUpHandler      (ControlEventKey    e) { _eventKeyUp      .add(e); }
  public void addEventCharDownHandler   (ControlEventChar   e) { _eventCharDown   .add(e); }
  public void addEventAxisLeftHandler   (ControlEventAxis   e) { _eventAxisLeft   .add(e); }
  public void addEventAxisRightHandler  (ControlEventAxis   e) { _eventAxisRight  .add(e); }
  public void addEventButtonDownHandler (ControlEventButton e) { _eventButtonDown .add(e); }
  public void addEventButtonUpHandler   (ControlEventButton e) { _eventButtonUp   .add(e); }
  public void addEventClickHandler      (ControlEventClick  e) { _eventClick      .add(e); }
  public void addEventDoubleClickHandler(ControlEventClick  e) { _eventDoubleClick.add(e); }
  public void addEventGotFocusHandler   (ControlEventFocus  e) { _eventGotFocus   .add(e); }
  public void addEventLostFocusHandler  (ControlEventFocus  e) { _eventLostFocus  .add(e); }
  
  public Control(GUI gui) {
    this(gui, Theme.getInstance(), true);
  }
  
  public Control(GUI gui, Theme theme) {
    this(gui, theme, true);
  }
  
  public Control(GUI gui, boolean register) {
    this(gui, Theme.getInstance(), true);
  }
  
  public Control(GUI gui, Theme theme, boolean register) {
    _gui = gui;
    
    if(register) {
      _selBox = Context.newDrawable();
      _selColour = Context.getContext().getNextSelectColour();
      
      float[] floatColour = new float[4];
      
      for(int i = 0; i < floatColour.length; i++) {
        floatColour[i] = _selColour[i] / 255f;
      }
      
      _selBox.setColour(floatColour);
      _selBox.createQuad();
    }
    
    _background = Context.newDrawable();
    _background.setColour(null);
    
    _border = Context.newDrawable();
    _border.setColour(null);
    _border.setXY(-1, -1);
  }
  
  public Control getParent() {
    return _controlParent;
  }
  
  protected void setParent(Control parent) {
    _controlParent = parent;
  }
  
  protected Control getRoot() {
    if(_controlParent != null) {
      return _controlParent.getRoot();
    }
    
    return this;
  }
  
  public ControlList Controls() {
    return _controlList;
  }
  
  protected final Control getControlNext() {
    return _controlNext;
  }
  
  protected final void setControlNext(Control control) {
    _controlNext = control;
  }
  
  protected final Control getControlPrev() {
    return _controlPrev;
  }
  
  protected final void setControlPrev(Control control) {
    _controlPrev = control;
  }
  
  public Drawable getBackground()  { return _background; }
  public float getX()              { return _loc[0]; }
  public float getY()              { return _loc[1]; }
  public float getW()              { return _loc[2]; }
  public float getH()              { return _loc[3]; }
  public boolean getVisible()      { return _visible; }
  public float[] getBackColour()   { return _background.getColour(); }
  public float[] getForeColour()   { return _foreColour; }
  public float[] getBorderColour() { return _border.getColour(); }
  public boolean getAcceptsFocus() { return _acceptsFocus; }
  
  public void setBackground(Drawable d)             { _background = d; }
  public void setX(float x)                         { _loc[0] = x; }
  public void setY(float y)                         { _loc[1] = y; }
  public void setBackColour(float[] c)              { _background.setColour(c); }
  public void setForeColour(float[] c)              { _foreColour = c; }
  public void setBorderColour(float[] c)            { _border.setColour(c); }
  public void setAcceptsFocus(boolean acceptsFocus) { _acceptsFocus = acceptsFocus; }
  
  public void setXY(float x, float y) {
    _loc[0] = x;
    _loc[1] = y;
  }
  
  public void setW(float w) {
    _loc[2] = w;
    updateSize();
  }
  
  public void setH(float h) {
    _loc[3] = h;
    updateSize();
  }
  
  public void setWH(float w, float h) {
    _loc[2] = w;
    _loc[3] = h;
    updateSize();
  }
  
  public void setXYWH(float x, float y, float w, float h) {
    _loc[0] = x;
    _loc[1] = y;
    _loc[2] = w;
    _loc[3] = h;
    updateSize();
  }
  
  public void setXYWH(float[] loc) {
    _loc = loc;
    updateSize();
  }
  
  private void updateSize() {
    if(_selBox != null) {
      _selBox.setWH(_loc[2], _loc[3]);
      _selBox.createQuad();
    }
    
    _background.setWH(_loc[2] + _background.getX() * 2, _loc[3] + _background.getY() * 2);
    _background.createQuad();
    
    _border.setWH(_loc[2] + _border.getX() * 2, _loc[3] + _border.getY() * 2);
    _border.createBorder();
  }
  
  public void setVisible(boolean visible) {
    _visible = visible;
    
    if(!_visible) {
      setFocus(false);
      _controlList.killFocus();
    }
  }
  
  public void setFocus(boolean focus) {
    if(_focus != focus) {
      if(focus) {
        _gui.setFocus(this);
        _focus = true;
        handleGotFocus();
      } else {
        _gui.setFocus(null);
        _focus = false;
        handleLostFocus();
      }
    }
  }
  
  public void handleKeyDown(int key) {
    if(key == Keyboard.KEY_TAB) {
      Control c = _controlNext;
      if(c == null) {
        if(_controlParent != null) {
          c = _controlParent.Controls().getLast();
        }
      }
      
      while(c != null) {
        if(c == this) break;
        
        if(c.getAcceptsFocus()) {
          c.setFocus(true);
          break;
        } else {
          c = c.getControlNext();
          if(c == null) {
            if(_controlParent != null) {
              c = _controlParent.Controls().getLast();
            }
          }
        }
      }
    }
    
    raiseKeyDown(key);
  }
  
  public void handleKeyUp(int key) {
    raiseKeyUp(key);
  }
  
  public void handleCharDown(char key) {
    raiseCharDown(key);
  }
  
  public void handleMouseDown(int x, int y, int button) {
    raiseMouseDown(x, y, button);
  }
  
  public void handleMouseUp(int x, int y, int button) {
    raiseMouseUp(x, y, button);
    
    if(Time.getTime() - _lastClick <= 250) {
      raiseDoubleClick();
    } else {
      raiseClick();
      _lastClick = Time.getTime();
    }
  }
  
  public void handleMouseMove(int x, int y, int button) {
    raiseMouseMove(x, y, button);
  }
  
  public void handleMouseWheel(int delta) {
    raiseMouseWheel(delta);
  }
  
  public void handleMouseEnter() {
    raiseMouseEnter();
  }
  
  public void handleMouseLeave() {
    raiseMouseLeave();
  }
  
  public void handleAxisLeft(double angle, float x, float y) {
    raiseAxisLeft(angle, x, y);
  }
  
  public void handleAxisRight(double angle, float x, float y) {
    raiseAxisRight(angle, x, y);
  }
  
  public void handleButtonDown(int button) {
    raiseButtonDown(button);
  }
  
  public void handleButtonUp(int button) {
    raiseButtonUp(button);
  }
  
  public void handleGotFocus() {
    raiseGotFocus();
  }
  
  public void handleLostFocus() {
    raiseLostFocus();
  }
  
  protected void raiseDraw() {
    for(ControlEventDraw e : _eventDraw) {
      e.setControl(this);
      e.event();
    }
  }
  
  protected void raiseMouseDown(int x, int y, int button) {
    for(ControlEventMouse e : _eventMouseDown) {
      e.setControl(this);
      e.event(x, y, button);
    }
  }
  
  protected void raiseMouseUp(int x, int y, int button) {
    for(ControlEventMouse e : _eventMouseUp) {
      e.setControl(this);
      e.event(x, y, button);
    }
  }
  
  protected void raiseMouseMove(int x, int y, int button) {
    for(ControlEventMouse e : _eventMouseMove) {
      e.setControl(this);
      e.event(x, y, button);
    }
  }
  
  protected void raiseMouseWheel(int delta) {
    for(ControlEventWheel e : _eventMouseWheel) {
      e.setControl(this);
      e.event(delta);
    }
  }
  
  protected void raiseMouseEnter() {
    for(ControlEventHover e : _eventMouseEnter) {
      e.setControl(this);
      e.event();
    }
  }
  
  protected void raiseMouseLeave() {
    for(ControlEventHover e : _eventMouseLeave) {
      e.setControl(this);
      e.event();
    }
  }
  
  protected void raiseKeyDown(int key) {
    for(ControlEventKey e : _eventKeyDown) {
      e.setControl(this);
      e.event(key);
    }
  }
  
  protected void raiseKeyUp(int key) {
    for(ControlEventKey e : _eventKeyUp) {
      e.setControl(this);
      e.event(key);
    }
  }
  
  protected void raiseCharDown(char key) {
    for(ControlEventChar e : _eventCharDown) {
      e.setControl(this);
      e.event(key);
    }
  }
  
  protected void raiseAxisLeft(double angle, float x, float y) {
    for(ControlEventAxis e : _eventAxisLeft) {
      e.setControl(this);
      e.event(x, y, angle);
    }
  }
  
  protected void raiseAxisRight(double angle, float x, float y) {
    for(ControlEventAxis e : _eventAxisRight) {
      e.setControl(this);
      e.event(x, y, angle);
    }
  }
  
  protected void raiseButtonDown(int button) {
    for(ControlEventButton e : _eventButtonDown) {
      e.setControl(this);
      e.event(button);
    }
  }
  
  protected void raiseButtonUp(int button) {
    for(ControlEventButton e : _eventButtonUp) {
      e.setControl(this);
      e.event(button);
    }
  }
  
  protected void raiseClick() {
    for(ControlEventClick e : _eventClick) {
      e.setControl(this);
      e.event();
    }
  }
  
  protected void raiseDoubleClick() {
    for(ControlEventClick e : _eventDoubleClick) {
      e.setControl(this);
      e.event();
    }
  }
  
  protected void raiseGotFocus() {
    for(ControlEventFocus e : _eventGotFocus) {
      e.setControl(this);
      e.event();
    }
  }
  
  protected void raiseLostFocus() {
    for(ControlEventFocus e : _eventLostFocus) {
      e.setControl(this);
      e.event();
    }
  }
  
  protected boolean drawBegin() {
    if(_visible) {
      _matrix.push();
      _matrix.translate(_loc[0], _loc[1]);
      
      if(_border.getColour() != null) {
        _border.draw();
      }
      
      if(_background.getColour() != null) {
        _background.draw();
      }
      
      return true;
    }
    
    return false;
  }
  
  protected void drawEnd() {
    if(_visible) {
      raiseDraw();
      
      _controlList.draw();
      _matrix.pop();
    }
    
    if(_controlNext != null) {
      _controlNext.draw();
    }
  }
  
  public void draw() {
    if(drawBegin()) {
      
    }
    
    drawEnd();
  }
  
  public void logic() { }
  public void logicControl() {
    logic();
    _controlList.logic();
    
    if(_controlNext != null) {
      _controlNext.logicControl();
    }
  }
  
  public void drawSelect() {
    if(drawBegin()) {
      if(_selBox != null)
        _selBox.draw();
      
      _controlList.drawSelect();
      
      _matrix.pop();
    }
    
    if(_controlNext != null) {
      _controlNext.drawSelect();
    }
  }
  
  public Control getSelectControl(int[] colour) {
    if(_selBox != null && colour[0] == _selColour[0] && colour[1] == _selColour[1] && colour[2] == _selColour[2]) {
      return this;
    } else {
      Control control = _controlList.getSelectControl(colour);
      if(control != null) {
        return control;
      } else {
        if(_controlNext != null) {
          return _controlNext.getSelectControl(colour);
        }
      }
    }
    
    return null;
  }
  
  public static class ControlEvent {
    private Control _control;
    
    public Control getControl() {
      return _control;
    }
    
    public void setControl(Control control) {
      _control = control;
    }
  }
  
  public static abstract class ControlEventDraw extends ControlEvent {
    public abstract void event();
  }
  
  public static abstract class ControlEventMouse extends ControlEvent {
    public abstract void event(int x, int y, int button);
  }
  
  public static abstract class ControlEventWheel extends ControlEvent {
    public abstract void event(int delta);
  }
  
  public static abstract class ControlEventHover extends ControlEvent {
    public abstract void event();
  }
  
  public static abstract class ControlEventKey extends ControlEvent {
    public abstract void event(int key);
  }
  
  public static abstract class ControlEventChar extends ControlEvent {
    public abstract void event(char key);
  }
  
  public static abstract class ControlEventAxis extends ControlEvent {
    public abstract void event(float x, float y, double angle);
  }
  
  public static abstract class ControlEventButton extends ControlEvent {
    public abstract void event(int button);
  }
  
  public static abstract class ControlEventClick extends ControlEvent {
    public abstract void event();
  }
  
  public static abstract class ControlEventFocus extends ControlEvent {
    public abstract void event();
  }
}