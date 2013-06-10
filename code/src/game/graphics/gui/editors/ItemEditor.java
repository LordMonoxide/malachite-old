package game.graphics.gui.editors;

import javax.swing.JOptionPane;

import game.Game;
import game.data.Item;
import game.data.util.Data;
import game.network.packet.editors.Save;
import graphics.shared.gui.Control;
import graphics.shared.gui.GUI;
import graphics.shared.gui.controls.Label;
import graphics.shared.gui.controls.Textbox;
import graphics.shared.gui.controls.compound.Window;

public class ItemEditor extends GUI implements Editor {
  private Window  _wndEditor;
  
  private Label   _lblName;
  private Textbox _txtName;
  private Label   _lblNote;
  private Textbox _txtNote;
  
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
    
    Textbox.Events.Change change = new Textbox.Events.Change() {
      public void change() { update(); }
    };
    
    _lblName = new Label(this);
    _lblName.setText("Name");
    _lblName.setXY(8, 4);
    
    _txtName = new Textbox(this);
    _txtName.setXY(_lblName.getX(), _lblName.getY() + _lblName.getH() + 4);
    _txtName.events().addChangeHandler(change);
    
    _lblNote = new Label(this);
    _lblNote.setText("Notes");
    _lblNote.setXY(_txtName.getX(), _txtName.getY() + _txtName.getH() + 8);
    
    _txtNote = new Textbox(this);
    _txtNote.setXY(_lblNote.getX(), _lblNote.getY() + _lblNote.getH() + 4);
    _txtNote.events().addChangeHandler(change);
    
    _wndEditor.Controls(0).add(_lblName);
    _wndEditor.Controls(0).add(_txtName);
    _wndEditor.Controls(0).add(_lblNote);
    _wndEditor.Controls(0).add(_txtNote);
    
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
    
    _txtName.setText(_item.getName());
    _txtNote.setText(_item.getNote());
    
    resize();
  }
  
  private void update() {
    _item.setName(_txtName.getText());
    _item.setNote(_txtNote.getText());
  }
}