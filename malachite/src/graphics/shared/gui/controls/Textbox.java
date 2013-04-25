package graphics.shared.gui.controls;

import java.util.LinkedList;

import org.lwjgl.input.Keyboard;

import graphics.gl00.Context;
import graphics.gl00.Drawable;
import graphics.shared.fonts.Font;
import graphics.shared.fonts.Fonts;
import graphics.shared.gui.Control;
import graphics.shared.gui.GUI;
import graphics.themes.Theme;

public class Textbox extends Control {
  private Fonts _fonts = Context.getFonts();
  private Font _font = _fonts.getDefault();
  
  private Drawable _caret;
  private String _text;
  private int _textY = 0;
  
  private int _selStart = 0;
  
  private LinkedList<ControlEventChange> _eventChange = new LinkedList<ControlEventChange>();
  
  public void addEventChangeHandler (ControlEventChange e) { _eventChange.add(e); }
  
  public Textbox(GUI gui) {
    this(gui, Theme.getInstance());
  }
  
  public Textbox(GUI gui, Theme theme) {
    super(gui, true);
    
    _caret = Context.newDrawable();
    _caret.setWH(1, _font.getH());
    
    theme.create(this);
  }
  
  public void setForeColour(float[] c) {
    super.setForeColour(c);
    _caret.setColour(c);
    _caret.createQuad();
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
    _caret.setY((_loc[3] - _caret.getH()) / 2);
    _caret.createQuad();
    _textY = (int)_caret.getY();
  }
  
  public int getSelStart() {
    return _selStart;
  }
  
  public void setSelStart(int selStart) {
    _selStart = selStart;
    
    if(_text != null) {
      _caret.setX(_font.getW(_text.substring(0, _selStart)));
    } else {
      _caret.setX(0);
    }
  }
  
  public String getText() {
    return _text;
  }
  
  public void setText(String text) {
    _text = text;
    
    if(_text != null) {
      setSelStart(_text.length());
    } else {
      setSelStart(0);
    }
  }
  
  public void handleKeyDown(int key) {
    switch(key) {
      case Keyboard.KEY_LEFT:
        if(_selStart != 0) {
          setSelStart(_selStart - 1);
          raiseChange();
        }
        break;
        
      case Keyboard.KEY_RIGHT:
        if(_selStart != _text.length()) {
          setSelStart(_selStart + 1);
          raiseChange();
        }
        break;
        
      case Keyboard.KEY_BACK:
        if(_selStart != 0) {
          String temp = _text.substring(0, _selStart - 1) + _text.substring(_selStart, _text.length());
          _text = temp;
          setSelStart(_selStart - 1);
          raiseChange();
        }
        break;
        
      case Keyboard.KEY_DELETE:
        if(_selStart != _text.length()) {
          String temp = _text.substring(0, _selStart) + _text.substring(_selStart + 1, _text.length());
          _text = temp;
          raiseChange();
        }
        break;
    }
    
    super.handleKeyDown(key);
  }
  
  public void handleCharDown(char key) {
    if(_text != null) {
      String temp = _text.substring(0, _selStart) + key + _text.substring(_selStart);
      _text = temp;
    } else {
      _text = Character.toString(key);
    }
    
    raiseChange();
    
    setSelStart(_selStart + 1);
    
    super.handleCharDown(key);
  }
  
  public void draw() {
    if(drawBegin()) {
      _font.draw(0, _textY, _text, _foreColour);
      
      if(_focus)
        _caret.draw();
    }
    
    drawEnd();
  }
  
  private void raiseChange() {
    for(ControlEventChange e : _eventChange) {
      e.setControl(this);
      e.event();
    }
  }
  
  public static abstract class ControlEventChange extends ControlEvent {
    public abstract void event();
  }
}