package game.graphics.gui;

import graphics.shared.gui.GUI;
import graphics.shared.gui.controls.Dropdown;
import graphics.shared.gui.controls.Picture;
import graphics.shared.gui.controls.compound.ScrollPanel;
import graphics.shared.textures.Texture;

public class Menu extends GUI {
  private Picture  picTest;
  private Dropdown drpTest;
  private ScrollPanel splTest;
  
  public void load() {
    _context.setBackColour(new float[] {1, 1, 1, 1});
    
    Texture t = _textures.getTexture("mal.png");
    
    picTest = new Picture(this);
    picTest.setTexture(t);
    
    drpTest = new Dropdown(this);
    drpTest.setXY(400, 400);
    drpTest.add("Test");
    drpTest.add("Test 2");
    drpTest.setSeletected(0);
    
    splTest = new ScrollPanel(this);
    
    Controls().add(picTest);
    Controls().add(drpTest);
    Controls().add(splTest);
  }
  
  public void destroy() {
    
  }
  
  public void resize() {
    
  }
  
  public boolean draw() {
    return true;
  }
}