package graphics.shared.gui.controls.compound;

import java.util.LinkedList;

import graphics.shared.gui.Control;
import graphics.shared.gui.GUI;
import graphics.shared.gui.controls.Button;
import graphics.shared.gui.controls.Label;
import graphics.shared.gui.controls.Picture;
import graphics.themes.Theme;

public class Window extends Control {
  private Picture _title;
  private Label   _text;
  private Button  _close;
  private Picture _panel;
  
  private int _mouseDownX;
  private int _mouseDownY;
  
  private LinkedList<ControlEventClose> _eventClose = new LinkedList<ControlEventClose>();
  
  public void addEventCloseHandler(ControlEventClose e) { _eventClose.add(e); }
  
  public Window(GUI gui) {
    this(gui, Theme.getInstance());
  }
  
  public Window(GUI gui, Theme theme) {
    super(gui);
    
    _title = new Picture(gui, true);
    _title.addEventMouseDownHandler(new ControlEventMouse() {
      public void event(int x, int y, int button) {
        _mouseDownX = x;
        _mouseDownY = y;
      }
    });
    _title.addEventMouseMoveHandler(new ControlEventMouse() {
      public void event(int x, int y, int button) {
        if(button == 0) {
          setXY(_loc[0] + x - _mouseDownX, _loc[1] + y - _mouseDownY);
        }
      }
    });
    
    _text = new Label(gui);
    
    ControlEventClick closeClick = new ControlEventClick() {
      public void event() {
        raiseClose();
      }
    };
    
    _close = new Button(gui);
    _close.addEventClickHandler(closeClick);
    _close.addEventDoubleClickHandler(closeClick);
    
    _title.Controls().add(_text);
    _title.Controls().add(_close);
    
    _panel = new Picture(gui);
    
    Controls().add(_title);
    Controls().add(_panel);
    
    theme.create(this, _title, _text, _close, _panel);
  }
  
  public String getTitle() {
    return _text.getText();
  }
  
  public void setTitle(String title) {
    _text.setText(title);
  }
  
  protected void resize() {
    _title.setW(_loc[2]);
    _text.setW(_title.getW());
    _close.setX(_title.getW() - _close.getW());
    _panel.setWH(_loc[2], _loc[3] - _title.getH());
  }
  
  protected void raiseClose() {
    for(ControlEventClose e : _eventClose) {
      e.setControl(this);
      e.event();
    }
  }
  
  public static abstract class ControlEventClose extends ControlEvent {
    public abstract void event();
  }
}