package game.gui;

import graphics.shared.gui.GUI;
import graphics.shared.gui.controls.List;
import graphics.shared.gui.controls.Picture;
import graphics.shared.gui.controls.List.ListItem.ControlEventSelect;
import graphics.shared.textures.Texture;

public class Menu extends GUI {
  private Picture picTest;
  private List    lstTest;
  
  public void load() {
    _context.setBackColour(new float[] {1, 1, 1, 1});
    
    Texture t = _textures.getTexture("mal.png");
    
    picTest = new Picture(this);
    picTest.setTexture(t);
    
    ControlEventSelect eventSelect = new ControlEventSelect() {
      public void event() {
        System.out.println(getControl());
      }
    };
    
    lstTest = new List(this);
    lstTest.addItem("Item 1", t).addEventSelectHandler(eventSelect);
    lstTest.addItem("Item 2", t).addEventSelectHandler(eventSelect);
    lstTest.addItem("Item 3", t).addEventSelectHandler(eventSelect);
    lstTest.addItem("Item 4", t).addEventSelectHandler(eventSelect);
    
    Controls().add(picTest);
    Controls().add(lstTest);
  }
  
  public void destroy() {
    
  }
  
  public void resize() {
    
  }
  
  public boolean draw() {
    return true;
  }
}