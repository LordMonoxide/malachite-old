package graphics.shared.gui.controls;

import org.lwjgl.input.Keyboard;

import graphics.gl00.Context;
import graphics.gl00.Scalable;
import graphics.shared.fonts.Font;
import graphics.shared.fonts.Fonts;
import graphics.shared.gui.Control;
import graphics.shared.gui.GUI;
import graphics.themes.Theme;

public class Button extends Control {
  private Fonts _fonts = Context.getFonts();
  private Font _font = _fonts.getDefault();
  private Scalable _background = Context.newScalable();
  private String _text;
  private int[] _textLoc = {0, 0, 0, 0};
  
  private float[] _glowColour = {0, 0, 0, 0};
  private float _fade;
  private boolean _hover;
  
  public Button(GUI gui) {
    this(gui, Theme.getInstance());
  }
  
  public Button(GUI gui, final Theme theme) {
    super(gui);
    
    _acceptsFocus = false;
    
    _background.setTexture(_textures.getTexture(theme.getButtonBackgroundTexture()));
    _background.setSize(
        theme.getButtonBackgroundSize1(),
        theme.getButtonBackgroundSize2(),
        theme.getButtonBackgroundTW(),
        theme.getButtonBackgroundTH(),
        theme.getButtonBackgroundTS()
    );
    _background.updateVertices();
    
    setBackColour(theme.getButtonBackColour());
    setForeColour(theme.getButtonForeColour());
    setGlowColour(theme.getButtonGlowColour());
    setWH(theme.getButtonWidth(), theme.getButtonHeight());
    setText(theme.getButtonText());
    
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
  }
  
  public float[] getGlowColour() {
    return _glowColour;
  }
  
  public void setGlowColour(float[] c) {
    _glowColour = c;
  }
  
  public void setW(float w) {
    super.setW(w);
    _background.setW(w);
    _background.updateVertices();
    setTextLoc();
  }
  
  public void setH(float h) {
    super.setH(h);
    _background.setH(h);
    _background.updateVertices();
    setTextLoc();
  }
  
  public void setWH(float w, float h) {
    super.setWH(w, h);
    _background.setWH(w, h);
    _background.updateVertices();
    setTextLoc();
  }
  
  public void setXYWH(float x, float y, float w, float h) {
    super.setXYWH(x, y, w, h);
    _background.setWH(w, h);
    _background.updateVertices();
    setTextLoc();
  }
  
  public void setXYWH(float[] loc) {
    super.setXYWH(loc);
    _background.setWH(_loc[2], _loc[3]);
    _background.updateVertices();
    setTextLoc();
  }
  
  public void setBackColour(float[] c) {
    super.setBackColour(c);
    
    if(!_hover) {
      _background.setColour(c);
      _background.updateVertices();
    }
  }
  
  public void setForeColour(float[] c) {
    super.setForeColour(c);
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
      _background.draw();
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
        _background.updateVertices();
      }
    } else {
      if(_fade > 0) {
        _fade -= 0.05f;
        float[] c = new float[4];
        for(int i = 0; i < c.length; i++) {
          c[i] = (_glowColour[i] - _backColour[i]) * _fade + _backColour[i];
        }
        _background.setColour(c);
        _background.updateVertices();
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