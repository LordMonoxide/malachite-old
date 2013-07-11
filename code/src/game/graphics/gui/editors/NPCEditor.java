package game.graphics.gui.editors;

import javax.swing.JOptionPane;

import game.Game;
import game.data.Item;
import game.data.util.GameData;
import game.graphics.gui.controls.Sprite;
import game.network.packet.editors.EditorData;
import game.network.packet.editors.EditorSave;
import game.settings.Settings;
import graphics.shared.fonts.Font;
import graphics.shared.fonts.Fonts;
import graphics.shared.gui.Control;
import graphics.shared.gui.GUI;
import graphics.shared.gui.controls.Button;
import graphics.shared.gui.controls.Dropdown;
import graphics.shared.gui.controls.Label;
import graphics.shared.gui.controls.Textbox;
import graphics.shared.gui.controls.compound.Window;

public class NPCEditor extends GUI implements Editor {
  private Game _game = Game.getInstance();
  private Font _font = Fonts.getInstance().getDefault();
  
  private Window   _wndEditor;
  
  private Label    _lblInv;
  private Sprite[] _sprInv;
  
  private Label    _lblInvFile;
  private Dropdown _drpInvFile;
  private Label    _lblInvVal;
  private Textbox  _txtInvVal;
  private Button   _btnInvClear;
  
  private Sprite   _sprHand1;
  private Sprite   _sprHand2;
  private Sprite[] _sprArmour;
  private Sprite[] _sprBling;
  
  private Label    _lblEquipFile;
  private Dropdown _drpEquipFile;
  private Label    _lblEquipVal;
  private Textbox  _txtEquipVal;
  private Button   _btnEquipClear;
  
  private Label    _lblName;
  private Textbox  _txtName;
  private Label    _lblNote;
  private Textbox  _txtNote;
  private Label    _lblSprite;
  private Dropdown _drpSprite;
  
  private int _selectedInv = -1;
  
  private NPCEditorNPC _npc;
  
  private float[] _fontColour = {1, 1, 1, 1};
  
  protected void load() {
    _wndEditor = new Window(this);
    _wndEditor.setWH(300, 300);
    _wndEditor.setText("NPC Editor");
    _wndEditor.addTab("Stats");
    _wndEditor.addTab("Inventory");
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
    
    _lblInv = new Label(this);
    _lblInv.setText("Inventory");
    _lblInv.setXY(8, 8);
    
    _sprInv = new Sprite[Settings.Player.Inventory.Size];
    for(int i = 0; i < _sprInv.length; i++) {
      final int n = i;
      _sprInv[i] = new Sprite(this);
      _sprInv[i].setBackColour(new float[] {0, 0, 0, 1});
      _sprInv[i].setBorderColour(new float[] {1, 1, 1, 1});
      _sprInv[i].setWH(32, 32);
      _sprInv[i].setXY(i % 8 * 34 + _lblInv.getX(), i / 8 * 34 + _lblInv.getY() + _lblInv.getH() + 4);
      _sprInv[i].events().addDrawHandler(new Control.Events.Draw() {
        public void draw() {
          if(_npc.inv(n).file != null) {
            if(_npc.inv(n).val != 1) {
              _font.draw(2, 2, String.valueOf(_npc.inv(n).val), _fontColour);
            }
          }
        }
      });
      
      _sprInv[i].events().addClickHandler(new Control.Events.Click() {
        public void clickDbl() { }
        public void click() {
          selectInv(n);
        }
      });
      
      _wndEditor.controls(1).add(_sprInv[i]);
    }
    
    _lblInvFile = new Label(this);
    _lblInvFile.setText("Item");
    _lblInvFile.setXY(_sprInv[_sprInv.length - 1].getX() + _sprInv[_sprInv.length - 1].getW() + 8, _sprInv[0].getY());
    
    _drpInvFile = new Dropdown(this);
    _drpInvFile.setEnabled(false);
    _drpInvFile.setXY(_lblInvFile.getX(), _lblInvFile.getY() + _lblInvFile.getH() + 4);
    _drpInvFile.events().addSelectHandler(new Dropdown.Events.Select() {
      public void select(Dropdown.Item item) {
        _npc.inv(_selectedInv).file = ((DropdownData)item)._file;
        _sprInv[_selectedInv].setSprite(new game.world.Sprite(_npc.inv(_selectedInv).file));
      }
    });
    
    _lblInvVal = new Label(this);
    _lblInvVal.setText("Amount");
    _lblInvVal.setXY(_drpInvFile.getX(), _drpInvFile.getY() + _drpInvFile.getH() + 8);
    
    _txtInvVal = new Textbox(this);
    _txtInvVal.setEnabled(false);
    _txtInvVal.setNumeric(true);
    _txtInvVal.setXY(_lblInvVal.getX(), _lblInvVal.getY() + _lblInvVal.getH() + 4);
    _txtInvVal.events().addChangeHandler(new Textbox.Events.Change() {
      public void change() {
        int n = Integer.parseInt(_txtInvVal.getText());
        if(n < 1) _txtInvVal.setText("1");
        _npc.inv(_selectedInv).val = Integer.parseInt(_txtInvVal.getText());
      }
    });
    
    _btnInvClear = new Button(this);
    _btnInvClear.setEnabled(false);
    _btnInvClear.setText("Clear");
    _btnInvClear.setXY(_txtInvVal.getX() + _txtInvVal.getW() - _btnInvClear.getW(), _txtInvVal.getY() + _txtInvVal.getH() + 8);
    _btnInvClear.events().addClickHandler(new Control.Events.Click() {
      public void clickDbl() { }
      public void click() {
        _npc.inv(_selectedInv).file = null;
        _npc.inv(_selectedInv).val = 0;
        
        _sprInv[_selectedInv].setSprite(null);
        _drpInvFile.setSeletected(-1);
        _txtInvVal.setText("1");
      }
    });
    
    float x = _sprInv[0].getX();
    float y = _sprInv[0].getY() + 5 * 34 + 8;
    
    _sprHand1 = new Sprite(this);
    _sprHand1.setBackColour(new float[] {0, 0, 0, 1});
    _sprHand1.setBorderColour(new float[] {1, 1, 1, 1});
    _sprHand1.setXYWH(x, y, 32, 32);
    
    x += 34;
    
    _sprHand2 = new Sprite(this);
    _sprHand2.setBackColour(new float[] {0, 0, 0, 1});
    _sprHand2.setBorderColour(new float[] {1, 1, 1, 1});
    _sprHand2.setXYWH(x, y, 32, 32);
    
    x += 68;
    
    _sprBling = new Sprite[Item.ITEM_TYPE_BLING_COUNT];
    for(int i = 0; i < Item.ITEM_TYPE_BLING_COUNT; i++) {
      _sprBling[i] = new Sprite(this);
      _sprBling[i].setBackColour(new float[] {0, 0, 0, 1});
      _sprBling[i].setBorderColour(new float[] {1, 1, 1, 1});
      _sprBling[i].setXYWH(x, y, 32, 32);
      _wndEditor.controls(1).add(_sprBling[i]);
      x += 34;
    }
    
    x = _sprInv[0].getX();
    y += 34;
    
    _sprArmour = new Sprite[Item.ITEM_TYPE_ARMOUR_COUNT];
    for(int i = 0; i < Item.ITEM_TYPE_ARMOUR_COUNT; i++) {
      _sprArmour[i] = new Sprite(this);
      _sprArmour[i].setBackColour(new float[] {0, 0, 0, 1});
      _sprArmour[i].setBorderColour(new float[] {1, 1, 1, 1});
      _sprArmour[i].setXYWH(x, y, 32, 32);
      _wndEditor.controls(1).add(_sprArmour[i]);
      x += 34;
    }
    
    _wndEditor.controls(1).add(_lblInv);
    _wndEditor.controls(1).add(_lblInvFile);
    _wndEditor.controls(1).add(_drpInvFile);
    _wndEditor.controls(1).add(_lblInvVal);
    _wndEditor.controls(1).add(_txtInvVal);
    _wndEditor.controls(1).add(_btnInvClear);
    _wndEditor.controls(1).add(_sprHand1);
    _wndEditor.controls(1).add(_sprHand2);
    
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
    
    _wndEditor.controls(2).add(_lblName);
    _wndEditor.controls(2).add(_txtName);
    _wndEditor.controls(2).add(_lblNote);
    _wndEditor.controls(2).add(_txtNote);
    _wndEditor.controls(2).add(_lblSprite);
    _wndEditor.controls(2).add(_drpSprite);
    
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
    
    _game.send(new EditorData.List(EditorData.DATA_TYPE_ITEM), EditorData.List.class, new Game.PacketCallback<EditorData.List>() {
      public boolean recieved(EditorData.List packet) {
        if(packet.type() == EditorData.DATA_TYPE_ITEM) {
          for(EditorData.List.ListData data : packet.data()) {
            _drpInvFile.add(new DropdownData(data.file(), data.name(), data.note()));
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
    _wndEditor.setW(_drpInvFile.getX() + _drpInvFile.getW() + 8);
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
    EditorSave.NPC packet = new EditorSave.NPC();
    packet.addData(_npc);
    Game.getInstance().send(packet);
  }
  
  public void editData(String file, boolean newData) {
    push();
    
    _npc = new NPCEditorNPC(file, newData);
    _npc.events().addLoadHandler(new GameData.Events.Load() {
      public void load() {
        for(int i = 0; i < Settings.Player.Inventory.Size; i++) {
          if(_npc.inv(i).file != null) {
            _sprInv[i].setSprite(new game.world.Sprite(_npc.inv(i).file));
          }
        }
        
        _txtName.setText(_npc.getName());
        _txtNote.setText(_npc.getNote());
        
        int i = 0;
        for(Dropdown.Item item : _drpSprite) {
          DropdownData s = (DropdownData)item;
          if(s._file.equals(_npc.getSprite())) {
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
    
    _npc.setName(_txtName.getText());
    _npc.setNote(_txtNote.getText());
    _npc.setSprite(sprite != null ? sprite._file : null);
  }
  
  private void selectInv(int index) {
    _drpInvFile.setEnabled(true);
    _txtInvVal.setEnabled(true);
    _btnInvClear.setEnabled(true);
    
    if(_selectedInv != -1) {
      _sprInv[_selectedInv].setBorderColour(new float[] {1, 1, 1, 1});
    }
    
    _selectedInv = index;
    _sprInv[_selectedInv].setBorderColour(new float[] {0, 1, 0, 1});
    
    if(_npc.inv(index).val < 1) _npc.inv(index).val = 1;
    _drpInvFile.setSeletected(-1);
    
    int i = 0;
    for(Dropdown.Item item : _drpInvFile) {
      DropdownData s = (DropdownData)item;
      if(s._file.equals(_npc.inv(index).file)) {
        _drpInvFile.setSeletected(i);
        break;
      }
      i++;
    }
    
    _txtInvVal.setText(String.valueOf(_npc.inv(index).val));
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