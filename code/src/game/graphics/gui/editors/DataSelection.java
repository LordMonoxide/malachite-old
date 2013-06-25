package game.graphics.gui.editors;

import java.io.File;

import javax.swing.JOptionPane;

import game.data.util.GameData;
import graphics.shared.gui.Control;
import graphics.shared.gui.GUI;
import graphics.shared.gui.controls.Button;
import graphics.shared.gui.controls.List;
import graphics.shared.gui.controls.compound.Window;

public class DataSelection extends GUI {
  private Window _window;
  private List   _data;
  private Button _new;
  
  private Editor _editor;
  private String _dir;
  private GameData[] _edit;
  
  public DataSelection(Editor editor, String dir, GameData[] data) {
    _editor = editor;
    _dir = dir;
    _edit = data;
  }
  
  protected void load() {
    _window = new Window(this);
    _window.setText("Choose What to Edit");
    
    _data = new List(this);
    _data.setXYWH(8, 8, 400, 200);
    
    Control.Events.Click accept = new Control.Events.Click() {
      public void click() { }
      public void clickDbl() {
        editData((GameData)((ListItem)getControl()).getData());
      }
    };
    
    for(GameData s : _edit) {
      ListItem l = (ListItem)_data.addItem(new ListItem(this, s));
      l.setText(s.getFile() + ": " + s.getName() + " - " + s.getNote());
      l.events().addClickHandler(accept);
    }
    
    _new = new Button(this);
    _new.setText("New");
    _new.setXY(_data.getX() + _data.getW() - _new.getW(), _data.getY() + _data.getH() + 7);
    _new.events().addClickHandler(new Control.Events.Click() {
      public void clickDbl() { }
      public void click() {
        newData();
      }
    });
    
    _window.setWH(_new.getX() + _new.getW() + 8, _new.getY() + _new.getH() + 28);
    _window.events().addCloseHandler(new Window.Events.Close() {
      public boolean close() {
        pop();
        return true;
      }
    });
    _window.controls().add(_data);
    _window.controls().add(_new);
    
    controls().add(_window);
    resize();
  }
  
  protected void destroy() {
    
  }
  
  protected void resize() {
    _window.setXY((_context.getW() - _window.getW()) / 2, (_context.getH() - _window.getH()) / 2);
  }
  
  protected void draw() {
    
  }
  
  protected boolean logic() {
    return false;
  }
  
  private void newData() {
    String s;
    
    for(;;) {
      s = JOptionPane.showInputDialog("Please enter the file name:");
      if(s == null || s.length() == 0) return;
      
      File f = new File("../data/" + _dir + "/" + s);
      if(f.exists()) {
        switch(JOptionPane.showConfirmDialog(null, s + " already exists.  Would you like to overwrite it?", null, JOptionPane.YES_NO_CANCEL_OPTION)) {
          case JOptionPane.CANCEL_OPTION: return;
          case JOptionPane.NO_OPTION:     continue;
        }
      }
      
      break;
    }
    
    _editor.newData(s);
    pop();
  }
  
  private void editData(GameData data) {
    _editor.editData(data);
    pop();
  }
  
  public static class ListItem extends graphics.shared.gui.controls.List.ListItem {
    private GameData _data;
    
    protected ListItem(GUI gui, GameData data) {
      super(gui);
      _data = data;
    }
    
    public GameData getData() {
      return _data;
    }
  }
}