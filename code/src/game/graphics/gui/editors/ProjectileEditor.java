package game.graphics.gui.editors;

import javax.swing.JOptionPane;

import game.Game;
import game.data.util.GameData;
import game.network.packet.editors.EditorData;
import game.network.packet.editors.EditorSave;
import graphics.shared.gui.Control;
import graphics.shared.gui.GUI;
import graphics.shared.gui.controls.Dropdown;
import graphics.shared.gui.controls.Label;
import graphics.shared.gui.controls.Textbox;
import graphics.shared.gui.controls.compound.Window;

public class ProjectileEditor extends GUI implements Editor {
  private Game _game = Game.getInstance();
  
  private Window   _wndEditor;
  
  private Label    _lblDamage;
  private Textbox  _txtDamage;
  private Label    _lblLife;
  private Textbox  _txtLife;
  private Label    _lblVel;
  private Textbox  _txtVel;
  private Label    _lblDec;
  private Textbox  _txtDec;
  
  private Label    _lblName;
  private Textbox  _txtName;
  private Label    _lblNote;
  private Textbox  _txtNote;
  private Label    _lblSprite;
  private Dropdown _drpSprite;
  
  private ProjectileEditorProjectile _projectile;
  
  protected void load() {
    _wndEditor = new Window(this);
    _wndEditor.setWH(300, 300);
    _wndEditor.setText("Projectile Editor");
    _wndEditor.addTab("Projectile");
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
    
    _lblDamage = new Label(this);
    _lblDamage.setText("Damage");
    _lblDamage.setXY(8, 8);
    
    _txtDamage = new Textbox(this);
    _txtDamage.setNumeric(true);
    _txtDamage.setXY(_lblDamage.getX(), _lblDamage.getY() + _lblDamage.getH());
    _txtDamage.events().addChangeHandler(change);
    
    _lblLife = new Label(this);
    _lblLife.setText("Life");
    _lblLife.setXY(_txtDamage.getX(), _txtDamage.getY() + _txtDamage.getH() + 8);
    
    _txtLife = new Textbox(this);
    _txtLife.setNumeric(true);
    _txtLife.setXY(_lblLife.getX(), _lblLife.getY() + _lblLife.getH());
    _txtLife.events().addChangeHandler(change);
    
    _lblVel = new Label(this);
    _lblVel.setText("Start Velocity");
    _lblVel.setXY(_txtLife.getX(), _txtLife.getY() + _txtLife.getH() + 8);
    
    _txtVel = new Textbox(this);
    _txtVel.setNumeric(true);
    _txtVel.setXY(_lblVel.getX(), _lblVel.getY() + _lblVel.getH());
    _txtVel.events().addChangeHandler(change);
    
    _lblDec = new Label(this);
    _lblDec.setText("Deceleration");
    _lblDec.setXY(_txtVel.getX(), _txtVel.getY() + _txtVel.getH() + 8);
    
    _txtDec = new Textbox(this);
    _txtDec.setNumeric(true);
    _txtDec.setXY(_lblDec.getX(), _lblDec.getY() + _lblDec.getH());
    _txtDec.events().addChangeHandler(change);

    _wndEditor.controls(0).add(_lblDamage);
    _wndEditor.controls(0).add(_txtDamage);
    _wndEditor.controls(0).add(_lblLife);
    _wndEditor.controls(0).add(_txtLife);
    _wndEditor.controls(0).add(_lblVel);
    _wndEditor.controls(0).add(_txtVel);
    _wndEditor.controls(0).add(_lblDec);
    _wndEditor.controls(0).add(_txtDec);
    
    _lblName = new Label(this);
    _lblName.setText("Name");
    _lblName.setXY(8, 8);
    
    _txtName = new Textbox(this);
    _txtName.setXY(_lblName.getX(), _lblName.getY() + _lblName.getH());
    _txtName.events().addChangeHandler(change);
    
    _lblNote = new Label(this);
    _lblNote.setText("Notes");
    _lblNote.setXY(_txtName.getX(), _txtName.getY() + _txtName.getH() + 8);
    
    _txtNote = new Textbox(this);
    _txtNote.setXY(_lblNote.getX(), _lblNote.getY() + _lblNote.getH());
    _txtNote.events().addChangeHandler(change);
    
    _lblSprite = new Label(this);
    _lblSprite.setXY(_txtNote.getX(), _txtNote.getY() + _txtNote.getH() + 8);
    _lblSprite.setText("Sprite");
    
    _drpSprite = new Dropdown(this);
    _drpSprite.setXY(_lblSprite.getX(), _lblSprite.getY() + _lblSprite.getH());
    _drpSprite.events().addSelectHandler(new Dropdown.Events.Select() {
      public void select(Dropdown.Item item) { update(); }
    });
    
    _wndEditor.controls(1).add(_lblName);
    _wndEditor.controls(1).add(_txtName);
    _wndEditor.controls(1).add(_lblNote);
    _wndEditor.controls(1).add(_txtNote);
    _wndEditor.controls(1).add(_lblSprite);
    _wndEditor.controls(1).add(_drpSprite);
    
    _game.send(new EditorData.List(EditorData.DATA_TYPE_SPRITE), EditorData.List.class, new Game.PacketCallback<EditorData.List>() {
      public boolean recieved(EditorData.List packet) {
        if(packet.type() == EditorData.DATA_TYPE_SPRITE) {
          for(EditorData.List.ListData data : packet.data()) {
            _drpSprite.add(new DropdownData(data.file(), data.name(), data.note()));
          }
          
          return true;
        }
        
        return false;
      }
    });
    
    controls().add(_wndEditor);
  }
  
  protected void destroy() {
    
  }
  
  protected void resize() {
    _wndEditor.setXY((_context.getW() - _wndEditor.getW()) / 2, (_context.getH() - _wndEditor.getH()) / 2);
  }
  
  protected void draw() {
    
  }
  
  protected boolean logic() {
    return false;
  }
  
  private void unload() {
    switch(JOptionPane.showConfirmDialog(null, "Would you like to save your changes?")) {
      case JOptionPane.CANCEL_OPTION:
      case JOptionPane.CLOSED_OPTION:
        return;
        
      case JOptionPane.YES_OPTION:
        save();
    }
    
    pop();
  }
  
  private void save() {
    EditorSave.Projectile packet = new EditorSave.Projectile();
    packet.addData(_projectile);
    Game.getInstance().send(packet);
  }
  
  public void editData(String file, boolean newData) {
    push();
    
    _projectile = new ProjectileEditorProjectile(file, newData);
    _projectile.events().addLoadHandler(new GameData.Events.Load() {
      public void load() {
        _txtDamage.setText(String.valueOf(_projectile.getDamage()));
        _txtLife.setText(String.valueOf(_projectile.getLife()));
        _txtVel.setText(String.valueOf(_projectile.getVel()));
        _txtDec.setText(String.valueOf(_projectile.getDec()));
        _txtName.setText(_projectile.getName());
        _txtNote.setText(_projectile.getNote());
        
        int i = 0;
        for(Dropdown.Item item : _drpSprite) {
          DropdownData s = (DropdownData)item;
          if(s._file.equals(_projectile.getSprite())) {
            _drpSprite.setSeletected(i);
            break;
          }
          i++;
        }
        
        resize();
      }
    });
  }
  
  private void update() {
    DropdownData sprite = (DropdownData)_drpSprite.get();
    
    _projectile.setDamage(Integer.valueOf(_txtDamage.getText()));
    _projectile.setLife(Integer.valueOf(_txtLife.getText()));
    _projectile.setVel(Float.valueOf(_txtVel.getText()));
    _projectile.setDec(Float.valueOf(_txtDec.getText()));
    _projectile.setName(_txtName.getText());
    _projectile.setNote(_txtNote.getText());
    _projectile.setSprite(sprite != null ? sprite._file : null);
  }
  
  protected boolean handleKeyDown ( int key) { return true; }
  protected boolean handleKeyUp   ( int key) { return true; }
  protected boolean handleCharDown(char key) { return true; }
  
  private class DropdownData extends Dropdown.Item {
    private String _file;
    
    public DropdownData(String file, String name, String note) {
      super(file + ": " + name + " - " + note);
      _file = file;
    }
  }
}