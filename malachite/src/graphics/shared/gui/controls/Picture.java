package graphics.shared.gui.controls;

import graphics.gl00.Context;
import graphics.gl00.Drawable;
import graphics.shared.gui.Control;
import graphics.shared.gui.GUI;
import graphics.shared.textures.Texture;

public class Picture extends Control {
  private Drawable _background = Context.newDrawable();
  private Drawable _override;

  public Picture(GUI gui) {
    this(gui, false);
  }
  
  public Picture(GUI gui, boolean register) {
    super(gui, register);
    _acceptsFocus = false;
  }
  
  public void setW(float w) {
    super.setW(w);
    _background.setW(w);
    _background.createQuad();
  }
  
  public void setH(float h) {
    super.setH(h);
    _background.setH(h);
    _background.createQuad();
  }
  
  public void setWH(float w, float h) {
    super.setWH(w, h);
    _background.setWH(w, h);
    _background.createQuad();
  }
  
  public void setXYWH(float x, float y, float w, float h) {
    super.setXYWH(x, y, w, h);
    _background.setWH(w, h);
    _background.createQuad();
  }
  
  public void setXYWH(float[] loc) {
    super.setXYWH(loc);
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
    setWH(_background.getW(), _background.getH());
  }
  
  public Drawable getDrawableOverride() {
    return _override;
  }
  
  public void setDrawableOverride(Drawable drawable) {
    _override = drawable;
  }
  
  public void draw() {
    if(drawBegin()) {
      if(_override == null) {
        _background.draw();
      } else { 
        _override.draw();
      }
    }
    
    drawEnd();
  }
}