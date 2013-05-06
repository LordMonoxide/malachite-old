package game.graphics.gui;

import graphics.shared.gui.GUI;
import graphics.shared.gui.controls.Button;
import graphics.shared.gui.controls.Picture;
import graphics.shared.gui.controls.Textbox;
import graphics.shared.gui.controls.compound.Window;
import graphics.shared.textures.Texture;

public class Menu extends GUI {
  private Picture picTest;
  private Window  wndTest;
  
  public void load() {
    _context.setBackColour(new float[] {1, 1, 1, 1});
    
    Texture t = _textures.getTexture("mal.png");
    
    picTest = new Picture(this);
    picTest.setTexture(t);
    
    wndTest = new Window(this);
    wndTest.addTab("Asdf");
    wndTest.Controls(0).add(new Button(this));
    wndTest.Controls(1).add(new Textbox(this));
    
    Controls().add(picTest);
    Controls().add(wndTest);
  }
  
  public void destroy() {
    
  }
  
  public void resize() {
    
  }
}