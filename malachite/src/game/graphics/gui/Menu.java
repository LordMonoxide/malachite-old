package game.graphics.gui;

import graphics.shared.gui.GUI;
import graphics.shared.gui.controls.Dropdown;
import graphics.shared.gui.controls.Dropdown.DropdownItem;

public class Menu extends GUI {
  private Dropdown drpTest;
  private Dropdown drpTest2;
  
  public void load() {
    _context.setBackColour(new float[] {1, 1, 1, 1});
    
    drpTest = new Dropdown(this);
    drpTest.add(new DropdownItem("Test"));
    drpTest.add(new DropdownItem("Test 2"));
    drpTest.add(new DropdownItem("Test 3"));
    
    drpTest2 = new Dropdown(this);
    drpTest2.setY(10);
    
    Controls().add(drpTest);
    Controls().add(drpTest2);
  }
  
  public void destroy() {
    
  }
  
  public void resize() {
    
  }
}