package graphics.shared.gui.controls;

import org.lwjgl.input.Keyboard;

import graphics.gl00.Context;
import graphics.gl00.Scalable;
import graphics.shared.fonts.Font;
import graphics.shared.fonts.Fonts;
import graphics.shared.gui.Control;
import graphics.shared.gui.GUI;

public class Button extends Control {
  private Fonts _fonts = Context.getFonts();
  private Font _font = _fonts.getDefault();
  private Scalable _background = Context.newScalable();
  private String _text = "Button";
  private int[] _textLoc = {0, 0, 0, 0};
  
  public Button(GUI gui) {
    super(gui);
    
    _acceptsFocus = false;
    
    _background.setTexture(_textures.getTexture("gui/button.png"));
    _background.setSize1(new float[] {2, 2, 2, 2});
    _background.setSize2(new float[] {2, 2, 2, 2});
    _background.setBorderS(new float[][] {
        {0, 0, 2, 2}, {2, 0, 1, 2}, {3, 0, 2, 2}, 
        {0, 2, 2, 1}, {2, 2, 1, 1}, {3, 2, 2, 1},
        {0, 3, 2, 2}, {2, 3, 1, 2}, {3, 3, 2, 2}
    });

    _background.updateVertices();
    setBackColour(new float[] {0x3F / 255f, 0xCF / 255f, 0, 1});
    setForeColour(new float[] {1, 1, 1, 1});
    setWH(90, 20);
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
    _background.setColour(c);
    _background.updateVertices();
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
  
  public void handleKeyDown(int key) {
    super.handleKeyDown(key);
    
    if(key == Keyboard.KEY_RETURN) {
      raiseClick();
    }
  }
}