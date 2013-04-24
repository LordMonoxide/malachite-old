package game.graphics.gui.editors;

import java.io.File;

import game.data.Sprite;
import graphics.shared.gui.Control.ControlEventClick;
import graphics.shared.gui.GUI;
import graphics.shared.gui.controls.Button;
import graphics.shared.gui.controls.List;
import graphics.shared.gui.controls.List.ListItem.ControlEventSelect;
import graphics.shared.gui.controls.Picture;

public class DataSelection extends GUI {
  private Picture _picWindow;
  private List _data;
  private Button _new;
  
  private Editor _editor;
  private String[] _name;
  
  public DataSelection(Editor editor, String dir) {
    _editor = editor;
    
    File[] d = new File("../data/" + dir).listFiles();
    
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
    _data.addEventSelectHandler(new ControlEventSelect() {
      public void event() {
        System.out.println(getControl());
      }
    });
    
    if(_name != null) {
      for(String n : _name) {
        Sprite s = new Sprite();
        if(s.load(n)) {
          _data.addItem(n + ": " + s.getName() + " - " + s.getNote(), null);
        }
      }
    }
    
    _data.addItem("Test", null);
    _data.addItem("Test2", null);
    
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
    _editor.newData();
    pop();
  }
  
  private void editData() {
    
  }
}