package game.graphics.gui;

import graphics.gl00.Context;
import graphics.shared.gui.Control;
import graphics.shared.gui.GUI;
import graphics.shared.gui.controls.Button;
import graphics.shared.gui.controls.Label;
import graphics.shared.gui.controls.compound.Window;

public class Message extends GUI {
  private Context _context;
  private Window _window;
  private Label _text;
  private Button _okay;
  
  public static Message show(String text) {
    return show(null, text);
  }
  
  public static Message show(String title, String text) {
    Message g = new Message(title, text);
    g.push();
    return g;
  }
  
  public static Message showWait(String text) {
    Message g = show(text);
    g._okay.setVisible(false);
    return g;
  }
  
  public static Message showWait(String title, String text) {
    Message g = show(text);
    g._okay.setVisible(false);
    return g;
  }
  
  public Message() {
    this(null, null);
  }
  
  public Message(String text) {
    this(null, text);
  }
  
  public Message(String title, String text) {
    _context = Context.getContext();
    _text = new Label(this);
    
    _okay = new Button(this);
    _okay.setText("Okay");
    _okay.setFocus(true);
    _okay.events().addClickHandler(new Control.Events.Click() {
      public void clickDbl() { }
      public void click() {
        pop();
      }
    });
    
    _window = new Window(this);
    _window.setText(title);
    _window.Controls().add(_text);
    _window.Controls().add(_okay);
    _window.events().addCloseHandler(new Window.Events.Close() {
      public boolean close() {
        pop();
        return true;
      }
    });
    
    Controls().add(_window);
    
    setText(text);
  }
  
  public void setText(String text) {
    _text.setText(text);
    
    float w = _text.getW() + _window.getW() - _window.getClientW() + 30;
    if(w < 200) w = 200;
    
    _window.setWH(w, _okay.getH() + 80);
    _window.setXY((int)(_context.getW() - _window.getW()) / 2, (int)(_context.getH() - _window.getH()) / 2);
    _text.setXY((_window.getClientW() - _text.getW()) / 2, (_window.getClientH() - _text.getH() - _okay.getH()) / 2);
    _okay.setW(_window.getClientW());
    _okay.setY(_window.getClientH() - _okay.getH());
  }
  
  public void load() {
    
  }
  
  public void destroy() {
    
  }
  
  public void resize() {
    
  }
  
  public void draw() {
    
  }
  
  public boolean logic() {
    return false;
  }
  
  public boolean handleMouseDown(int x, int y, int button) {
    return true;
  }
  
  public boolean handleMouseUp(int x, int y, int button) {
    return true;
  }
  
  public boolean handleMouseMove(int x, int y, int button) {
    return true;
  }
}