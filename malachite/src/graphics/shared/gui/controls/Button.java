package graphics.shared.gui.controls;

import org.lwjgl.input.Keyboard;

import graphics.gl00.Context;
import graphics.shared.fonts.Font;
import graphics.shared.fonts.Fonts;
import graphics.shared.gui.Control;
import graphics.shared.gui.GUI;
import graphics.themes.Theme;

public class Button extends Control {
  private Fonts _fonts = Context.getFonts();
  private Font _font = _fonts.getDefault();
  private String _text;
  private int[] _textLoc = {0, 0, 0, 0};
  
  private float[] _backColour = {0, 0, 0, 0};
  private float[] _glowColour = {0, 0, 0, 0};
  private float _fade;
  private boolean _hover;
  
  public Button(GUI gui) {
    this(gui, Theme.getInstance());
  }
  
  public Button(GUI gui, final Theme theme) {
    super(gui);
    
    _acceptsFocus = false;
    
    addEventMouseEnterHandler(new ControlEventHover() {
      public void event() {
        _hover = true;
      }
    });
    
    addEventMouseLeaveHandler(new ControlEventHover() {
      public void event() {
        _hover = false;
      }
    });
    
    theme.create(this);
  }
  
  public float[] getGlowColour() {
    return _glowColour;
  }
  
  public void setGlowColour(float[] c) {
    _glowColour = c;
  }
  
  public void setW(float w) {
    super.setW(w);
    setTextLoc();
  }
  
  public void setH(float h) {
    super.setH(h);
    setTextLoc();
  }
  
  public void setWH(float w, float h) {
    super.setWH(w, h);
    setTextLoc();
  }
  
  public void setXYWH(float x, float y, float w, float h) {
    super.setXYWH(x, y, w, h);
    setTextLoc();
  }
  
  public void setXYWH(float[] loc) {
    super.setXYWH(loc);
    setTextLoc();
  }
  
  public void setBackColour(float[] c) {
    _backColour = c;
    
    if(!_hover) {
      _background.setColour(c);
      _background.createQuad();
    }
  }
  
  public String getText() {
    return _text;
  }
  
  public void setText(String text) {
    _text = text;
    setTextLoc();
  }
  
  private void setTextLoc() {
    _textLoc[2] = _font.getW(_text);
    _textLoc[3] = _font.getH();
    _textLoc[0] = (int)(_loc[2] - _textLoc[2]) / 2;
    _textLoc[1] = (int)(_loc[3] - _textLoc[3]) / 2;
  }
  
  public void draw() {
    if(drawBegin()) {
      _font.draw(_textLoc[0], _textLoc[1], _text, _foreColour);
    }
    
    drawEnd();
  }
  
  public void logic() {
    if(_hover) {
      if(_fade < 1) {
        _fade += 0.1f;
        float[] c = new float[4];
        for(int i = 0; i < c.length; i++) {
          c[i] = (_glowColour[i] - _backColour[i]) * _fade + _backColour[i];
        }
        _background.setColour(c);
        _background.createQuad();
      }
    } else {
      if(_fade > 0) {
        _fade -= 0.05f;
        float[] c = new float[4];
        for(int i = 0; i < c.length; i++) {
          c[i] = (_glowColour[i] - _backColour[i]) * _fade + _backColour[i];
        }
        _background.setColour(c);
        _background.createQuad();
      }
    }
  }
  
  public void handleKeyDown(int key) {
    super.handleKeyDown(key);
    
    if(key == Keyboard.KEY_RETURN) {
      raiseClick();
    }
  }
}