package graphics.shared.gui.controls.compound;

import java.util.LinkedList;

import graphics.shared.gui.Control;
import graphics.shared.gui.ControlList;
import graphics.shared.gui.GUI;
import graphics.shared.gui.controls.Button;
import graphics.shared.gui.controls.Label;
import graphics.shared.gui.controls.Picture;
import graphics.themes.Theme;

public class Window extends Control {
  private Theme _theme;
  
  private Picture _title;
  private Label   _text;
  private Button  _close;
  private Picture _panelBack;
  
  private LinkedList<Button>  _tab   = new LinkedList<Button>();
  private LinkedList<Picture> _panel = new LinkedList<Picture>();
  
  private int _index;
  
  private int _mouseDownX;
  private int _mouseDownY;
  
  private ControlEventClick _tabClick;
  
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
    
    _panelBack = new Picture(gui);
    
    super.Controls().add(_title);
    super.Controls().add(_panelBack);
    
    _theme = theme;
    _theme.create(this, _title, _text, _close);
    
    _tabClick = new ControlEventClick() {
      public void event() {
        for(int i = 0; i < _tab.size(); i++) {
          if(getControl() == _tab.get(i)) {
            setTab(i);
            break;
          }
        }
      }
    };
  }
  
  public String getText() {
    return _text.getText();
  }
  
  public void setText(String text) {
    _text.setText(text);
    resize();
  }
  
  public ControlList Controls() {
    return Controls(_index);
  }
  
  public ControlList Controls(int index) {
    if(_panel.size() != 0) {
      return _panel.get(index).Controls();
    } else {
      return super._controlList;
    }
  }
  
  public void addTab(String text) {
    Button tab = new Button(_gui);
    Picture panel = new Picture(_gui);
    
    _theme.createWindowTab(tab, panel);
    
    tab.setX(_tab.size() * tab.getW());
    tab.setText(text);
    tab.addEventClickHandler(_tabClick);
    
    panel.setVisible(false);
    
    _tab.add(tab);
    _panel.add(panel);
    
    _title.Controls().add(tab);
    _panelBack.Controls().add(panel);
    
    resize();
    
    if(_panel.size() == 1) {
      setTab(0);
    }
  }
  
  public void setTab(int index) {
    _panel.get(_index).setVisible(false);
    
    _index = index;
    
    _panel.get(_index).setVisible(true);
  }
  
  protected void resize() {
    _title.setW(_loc[2]);
    _close.setX(_title.getW() - _close.getW());
    
    int x = 0;
    if(_tab.size() != 0) {
      x = (int)(_tab.getLast().getX() + _tab.getLast().getW());
    }
    
    int w = (int)(_title.getW() - _close.getW()) - x;
    _text.setXY((w - _text.getW()) / 2 + x, (_title.getH() - _text.getH()) / 2);
    
    _panelBack.setWH(_loc[2], _loc[3] - _title.getH());
    
    for(Picture p : _panel) {
      p.setWH(_panelBack.getW(), _panelBack.getH());
    }
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