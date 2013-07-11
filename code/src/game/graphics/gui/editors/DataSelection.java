package game.graphics.gui.editors;

import javax.swing.JOptionPane;

import game.Game;
import game.network.packet.editors.EditorData;
import graphics.shared.gui.Control;
import graphics.shared.gui.GUI;
import graphics.shared.gui.controls.Button;
import graphics.shared.gui.controls.List;
import graphics.shared.gui.controls.compound.Window;

public class DataSelection extends GUI {
  private DataSelection _this = this;
  
  private Window _window;
  private List   _data;
  private Button _new;
  
  private Editor _editor;
  private int _type;
  
  public DataSelection(Editor editor, int type) {
    _editor = editor;
    _type = type;
  }
  
  protected void load() {
    _window = new Window(this);
    _window.setText("Choose What to Edit");
    
    _data = new List(this);
    _data.setXYWH(8, 8, 400, 200);
    
    final Control.Events.Click accept = new Control.Events.Click() {
      public void click() { }
      public void clickDbl() {
        editData(((ListItem)getControl()).file());
      }
    };
    
    Game.getInstance().send(new EditorData.List(_type), EditorData.List.class, new Game.PacketCallback<EditorData.List>() {
      public boolean recieved(game.network.packet.editors.EditorData.List packet) {
        if(packet.type() == _type) {
          remove();
          
          for(EditorData.List.ListData data : packet.data()) {
            ListItem l = (ListItem)_data.addItem(new ListItem(_this, data.file()));
            l.setText(data.file() + ": " + data.name() + " - " + data.note());
            l.events().addClickHandler(accept);
          }
          
          return true;
        }
        
        return false;
      }
    });
    
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
      
      for(List.ListItem l : _data.items()) {
        if(((ListItem)l).file().equals(s)) {
          switch(JOptionPane.showConfirmDialog(null, s + " already exists.  Would you like to overwrite it?", null, JOptionPane.YES_NO_CANCEL_OPTION)) {
            case JOptionPane.CANCEL_OPTION: return;
            case JOptionPane.NO_OPTION:     continue;
            default: break;
          }
        }
      }
      
      break;
    }
    
    _editor.editData(s, true);
    pop();
  }
  
  private void editData(String file) {
    System.out.println("Editing " + file);
    _editor.editData(file, false);
    pop();
  }
  
  public static class ListItem extends graphics.shared.gui.controls.List.ListItem {
    private String _file;
    
    protected ListItem(GUI gui, String file) {
      super(gui);
      _file = file;
    }
    
    public String file() {
      return _file;
    }
  }
}