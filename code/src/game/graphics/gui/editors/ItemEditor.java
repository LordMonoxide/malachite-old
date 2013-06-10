package game.graphics.gui.editors;

import javax.swing.JOptionPane;

import game.Game;
import game.data.Item;
import game.data.util.Data;
import game.network.packet.editors.Save;
import graphics.shared.gui.Control;
import graphics.shared.gui.GUI;
import graphics.shared.gui.controls.compound.Window;

public class ItemEditor extends GUI implements Editor {
  private Window _wndEditor;
  
  private ItemEditorItem _item;
  
  public void load() {
    _wndEditor = new Window(this);
    _wndEditor.setWH(300, 300);
    _wndEditor.setText("Item Editor");
    _wndEditor.addTab("Info");
    _wndEditor.events().addCloseHandler(new Window.Events.Close() {
      public boolean close() {
        unload();
        return true;
      }
    });
    _wndEditor.addButton("Save").events().addClickHandler(new Control.Events.Click() {
      public void clickDbl() { }
      public void click() {
        save();
      }
    });
    
    Controls().add(_wndEditor);
  }
  
  public void destroy() {
    
  }
  
  public void resize() {
    _wndEditor.setXY((_context.getW() - _wndEditor.getW()) / 2, (_context.getH() - _wndEditor.getH()) / 2);
  }
  
  public void draw() {
    
  }
  
  public boolean logic() {
    return false;
  }
  
  public void unload() {
    if(_item.isChanged()) {
      switch(JOptionPane.showConfirmDialog(null, "Would you like to save your changes?")) {
        case JOptionPane.CANCEL_OPTION:
        case JOptionPane.CLOSED_OPTION:
          return;
          
        case JOptionPane.YES_OPTION:
          save();
      }
    }
    
    pop();
  }
  
  private void save() {
    System.out.println("Updating item " + _item.getFile());
    _item.update();
    
    Save.Item packet = new Save.Item();
    packet.addData(_item);
    Game.getInstance().send(packet);
  }
  
  public void newData(String file) {
    editData(new Item(file, 0));
  }
  
  public void editData(Data data) {
    push();
    
    _item = new ItemEditorItem((Item)data);
    
    resize();
  }
}