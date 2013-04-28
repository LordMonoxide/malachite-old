package game.graphics.gui.editors;

import java.io.File;

import javax.swing.JOptionPane;

import game.data.Sprite;
import game.data.util.Data;
import game.data.util.Serializable;
import graphics.shared.gui.Control.ControlEventClick;
import graphics.shared.gui.GUI;
import graphics.shared.gui.controls.Button;
import graphics.shared.gui.controls.List;
import graphics.shared.gui.controls.Picture;

public class DataSelection extends GUI {
  private Picture _picWindow;
  private List _data;
  private Button _new;
  
  private Editor _editor;
  private String _dir;
  private String[] _name;
  
  public DataSelection(Editor editor, String dir) {
    _editor = editor;
    _dir = dir;
    
    File[] d = new File("../data/" + _dir).listFiles();
    
    if(d != null) {
      _name = new String[d.length];
      
      int i = 0;
      for(File f : d) {
        _name[i++] = f.getName();
      }
    }
  }
  
  public void load() {
    _picWindow = new Picture(this, true);
    _picWindow.setBackColour(new float[] {0.33f, 0.33f, 0.33f, 0.66f});
    _picWindow.setBorderColour(new float[] {0, 0, 0, 1});
    
    _data = new List(this);
    _data.setXYWH(8, 8, 400, 200);
    
    ControlEventClick accept = new ControlEventClick() {
      public void event() {
        editData((Data)((ListItem)getControl()).getData());
      }
    };
    
    if(_name != null) {
      for(String n : _name) {
        Sprite s = new Sprite(n);
        if(s.load()) {
          ListItem l = (ListItem)_data.addItem(new ListItem(this, s));
          l.setText(n + ": " + s.getName() + " - " + s.getNote());
          l.addEventDoubleClickHandler(accept);
        }
      }
    }
    
    _new = new Button(this);
    _new.setText("New");
    _new.setXY(_data.getX() + _data.getW() - _new.getW(), _data.getY() + _data.getH() + 7);
    _new.addEventClickHandler(new ControlEventClick() {
      public void event() {
        newData();
      }
    });
    
    _picWindow.setWH(_new.getX() + _new.getW() + 8, _new.getY() + _new.getH() + 4);
    _picWindow.Controls().add(_data);
    _picWindow.Controls().add(_new);
    
    Controls().add(_picWindow);
    resize();
  }
  
  public void destroy() {
    
  }
  
  public void resize() {
    _picWindow.setXY((_context.getW() - _picWindow.getW()) / 2, (_context.getH() - _picWindow.getH()) / 2);
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
  
  private void editData(Data data) {
    _editor.editData(data);
    pop();
  }
  
  public static class ListItem extends graphics.shared.gui.controls.List.ListItem {
    private Serializable _data;
    
    protected ListItem(GUI gui, Serializable data) {
      super(gui);
      _data = data;
    }
    
    public Serializable getData() {
      return _data;
    }
  }
}