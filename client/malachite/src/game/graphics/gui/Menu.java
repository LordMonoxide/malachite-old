package game.graphics.gui;

import graphics.shared.gui.GUI;
import graphics.shared.gui.controls.Picture;

public class Menu extends GUI {
  private Picture[] _background = new Picture[15];
  
  public void load() {
    _context.setBackColour(new float[] {1, 1, 1, 1});
    
    for(int i = 0; i < _background.length; i++) {
      _background[i] = new Picture(this);
      _background[i].setXY((i % 5) * 256, (i / 5) * 256);
      _background[i].setTexture(_textures.getTexture("gui/menu/" + i + ".png"));
      Controls().add(_background[i]);
    }
  }
  
  public void destroy() {
    
  }
  
  public void resize() {
    
  }
}