package game.graphics.gui.controls;

import graphics.shared.gui.GUI;
import graphics.shared.gui.controls.Picture;

public class Sprite extends Picture {
  private game.world.Sprite _sprite;
  
  public Sprite(GUI gui) {
    this(gui, true);
  }
  
  public Sprite(GUI gui, boolean register) {
    super(gui, register);
  }
  
  public game.world.Sprite getSprite() {
    return _sprite;
  }
  
  public void setSprite(game.world.Sprite sprite) {
    _sprite = sprite;
  }
  
  public void draw() {
    if(drawBegin()) {
      if(_sprite != null) {
        float scale;
        if(_sprite.getFrameW() > _sprite.getFrameH()) {
          scale = _loc[2] / _sprite.getFrameW();
        } else {
          scale = _loc[3] / _sprite.getFrameH();
        }
        
        _matrix.push();
        _matrix.translate(_loc[2] / 2, 0);
        _matrix.scale(scale, scale);
        _matrix.translate(0, _sprite.getH() - _sprite.getFrameFY());
        _sprite.draw();
        _matrix.pop();
      }
    }
    
    drawEnd();
  }
}