package graphics.shared.gui.controls;

import graphics.shared.gui.Control;
import graphics.shared.gui.GUI;
import graphics.shared.textures.Texture;

public class Picture extends Control {
  public Picture(GUI gui) {
    this(gui, false);
  }
  
  public Picture(GUI gui, boolean register) {
    super(gui, register);
    _acceptsFocus = false;
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
  
  public void updateSize() {
    _background.setWH(_loc[2], _loc[3]);
    _background.createQuad();
  }
  
  public void setBackColour(float[] c) {
    super.setBackColour(c);
    _background.setColour(c);
    _background.createQuad();
  }
  
  public Texture getTexture() {
    return _background.getTexture();
  }
  
  public void setTexture(Texture texture) {
    _background.setTexture(texture);
    
    if(texture != null) {
      _background.setColour(new float[] {1, 1, 1, 1});
      setWH(_background.getW(), _background.getH());
    } else {
      _background.setColour(null);
      _background.createQuad();
    }
  }
}