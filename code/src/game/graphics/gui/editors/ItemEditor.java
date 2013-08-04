package game.graphics.gui.editors;

import javax.swing.JOptionPane;

import game.Game;
import game.data.Item;
import game.data.util.GameData;
import game.language.Lang;
import game.network.packet.editors.EditorData;
import game.network.packet.editors.EditorSave;
import graphics.shared.gui.Control;
import graphics.shared.gui.GUI;
import graphics.shared.gui.controls.Checkbox;
import graphics.shared.gui.controls.Dropdown;
import graphics.shared.gui.controls.Label;
import graphics.shared.gui.controls.Textbox;
import graphics.shared.gui.controls.compound.Window;

public class ItemEditor extends GUI implements Editor {
  private Game _game = Game.getInstance();
  
  private Window  _wndEditor;
  
  private Label    _lblType;
  private Dropdown _drpType;
  private Label    _lblSubtype;
  private Dropdown _drpSubtype;
  
  private Label    _lblDamage;
  private Textbox  _txtDamage;
  private Label    _lblProjectile;
  private Dropdown _drpProjectile;
  private Label    _lblSpeed;
  private Textbox  _txtSpeed;
  private Label    _lblWeight;
  private Textbox  _txtWeight;
  
  private Label    _lblHPHeal;
  private Textbox  _txtHPHeal;
  private Label    _lblMPHeal;
  private Textbox  _txtMPHeal;
  private Checkbox _chkPercent;
  
  private Label    _lblBuffHP;
  private Textbox  _txtBuffHP;
  private Checkbox _chkBuffHP;
  
  private Label    _lblBuffMP;
  private Textbox  _txtBuffMP;
  private Checkbox _chkBuffMP;
  
  private Label    _lblBuffSTR;
  private Textbox  _txtBuffSTR;
  private Checkbox _chkBuffSTR;
  
  private Label    _lblBuffDEX;
  private Textbox  _txtBuffDEX;
  private Checkbox _chkBuffDEX;
  
  private Label    _lblBuffINT;
  private Textbox  _txtBuffINT;
  private Checkbox _chkBuffINT;
  
  private Label    _lblName;
  private Textbox  _txtName;
  private Label    _lblNote;
  private Textbox  _txtNote;
  private Label    _lblSprite;
  private Dropdown _drpSprite;
  
  private ItemEditorItem _item;
  
  protected void load() {
    _wndEditor = new Window(this);
    _wndEditor.setWH(300, 300);
    _wndEditor.setText("Item Editor");
    _wndEditor.addTab("Type");
    _wndEditor.addTab("Buffs");
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
    
    _lblType = new Label(this);
    _lblType.setText("Type");
    _lblType.setXY(8, 8);
    
    _drpType = new Dropdown(this);
    _drpType.setXY(_lblType.getX(), _lblType.getY() + _lblType.getH());
    for(String type : Lang.ITEM_TYPE.get()) {
      _drpType.add(new Dropdown.Item(type));
    }
    _drpType.events().addSelectHandler(new Dropdown.Events.Select() {
      public void select(Dropdown.Item item) {
        updateSubtypes();
        update();
      }
    });
    
    _lblSubtype = new Label(this);
    _lblSubtype.setText("Subtype");
    _lblSubtype.setXY(_drpType.getX(), _drpType.getY() + _drpType.getH() + 8);
    
    _drpSubtype = new Dropdown(this);
    _drpSubtype.setXY(_lblSubtype.getX(), _lblSubtype.getY() + _lblSubtype.getH());
    _drpSubtype.events().addSelectHandler(new Dropdown.Events.Select() {
      public void select(graphics.shared.gui.controls.Dropdown.Item item) {
        updateSubTypeInterface();
        update();
      }
    });
    
    _lblDamage = new Label(this);
    _lblDamage.setText("Damage");
    _lblDamage.setXY(_drpSubtype.getX(), _drpSubtype.getY() + _drpSubtype.getH() + 8);
    
    _txtDamage = new Textbox(this);
    _txtDamage.setXY(_lblDamage.getX(), _lblDamage.getY() + _lblDamage.getH());
    _txtDamage.setNumeric(true);
    _txtDamage.events().addChangeHandler(change);
    
    _lblProjectile = new Label(this);
    _lblProjectile.setText("Projectile");
    _lblProjectile.setXY(_txtDamage.getX(), _txtDamage.getY() + _txtDamage.getH() + 8);
    
    _drpProjectile = new Dropdown(this);
    _drpProjectile.setXY(_lblProjectile.getX(), _lblProjectile.getY() + _lblProjectile.getH());
    _drpProjectile.events().addSelectHandler(new Dropdown.Events.Select() {
      public void select(graphics.shared.gui.controls.Dropdown.Item item) {
        update();
      }
    });
    
    _lblSpeed = new Label(this);
    _lblSpeed.setText("Attack Speed");
    _lblSpeed.setXY(_drpProjectile.getX(), _drpProjectile.getY() + _drpProjectile.getH() + 8);
    
    _txtSpeed = new Textbox(this);
    _txtSpeed.setNumeric(true);
    _txtSpeed.setXY(_lblSpeed.getX(), _lblSpeed.getY() + _lblSpeed.getH());
    _txtSpeed.events().addChangeHandler(change);
    
    _lblWeight = new Label(this);
    _lblWeight.setText("Weight");
    _lblWeight.setXY(_txtSpeed.getX(), _txtSpeed.getY() + _txtSpeed.getH() + 8);
    
    _txtWeight = new Textbox(this);
    _txtWeight.setXY(_lblWeight.getX(), _lblWeight.getY() + _lblWeight.getH());
    _txtWeight.setNumeric(true);
    _txtWeight.events().addChangeHandler(change);
    
    _lblHPHeal = new Label(this);
    _lblHPHeal.setText("Heal HP");
    _lblHPHeal.setXY(_drpSubtype.getX(), _drpSubtype.getY() + _drpSubtype.getH() + 8);
    
    _txtHPHeal = new Textbox(this);
    _txtHPHeal.setXY(_lblHPHeal.getX(), _lblHPHeal.getY() + _lblHPHeal.getH());
    _txtHPHeal.setNumeric(true);
    _txtHPHeal.events().addChangeHandler(change);
    
    _lblMPHeal = new Label(this);
    _lblMPHeal.setText("Heal MP");
    _lblMPHeal.setXY(_txtHPHeal.getX(), _txtHPHeal.getY() + _txtHPHeal.getH() + 8);
    
    _txtMPHeal = new Textbox(this);
    _txtMPHeal.setXY(_lblMPHeal.getX(), _lblMPHeal.getY() + _lblMPHeal.getH());
    _txtMPHeal.setNumeric(true);
    _txtMPHeal.events().addChangeHandler(change);
    
    _chkPercent = new Checkbox(this);
    _chkPercent.setText("Percent");
    _chkPercent.setXY(_txtMPHeal.getX(), _txtMPHeal.getY() + _txtMPHeal.getH() + 8);
    _chkPercent.events().addCheckHandler(new Checkbox.Events.Checked() {
      public void checked() { update(); }
    });
    
    _lblBuffHP = new Label(this);
    _lblBuffHP.setText("Buff HP");
    _lblBuffHP.setXY(8, 8);
    
    _txtBuffHP = new Textbox(this);
    _txtBuffHP.setXY(_lblBuffHP.getX(), _lblBuffHP.getY() + _lblBuffHP.getH());
    _txtBuffHP.setNumeric(true);
    _txtBuffHP.events().addChangeHandler(change);
    
    _chkBuffHP = new Checkbox(this);
    _chkBuffHP.setText("%");
    _chkBuffHP.setXY(_txtBuffHP.getX() + _txtBuffHP.getW() + 4, _txtBuffHP.getY());
    _chkBuffHP.setW(34);
    _chkBuffHP.events().addCheckHandler(new Checkbox.Events.Checked() {
      public void checked() { update(); }
    });
    
    _lblBuffMP = new Label(this);
    _lblBuffMP.setText("Buff MP");
    _lblBuffMP.setXY(_txtBuffHP.getX(), _txtBuffHP.getY() + _txtBuffHP.getH() + 8);
    
    _txtBuffMP = new Textbox(this);
    _txtBuffMP.setXY(_lblBuffMP.getX(), _lblBuffMP.getY() + _lblBuffMP.getH());
    _txtBuffMP.setNumeric(true);
    _txtBuffMP.events().addChangeHandler(change);
    
    _chkBuffMP = new Checkbox(this);
    _chkBuffMP.setText("%");
    _chkBuffMP.setXY(_txtBuffMP.getX() + _txtBuffMP.getW() + 4, _txtBuffMP.getY());
    _chkBuffMP.setW(34);
    _chkBuffMP.events().addCheckHandler(new Checkbox.Events.Checked() {
      public void checked() { update(); }
    });
    
    _lblBuffSTR = new Label(this);
    _lblBuffSTR.setText("Buff STR");
    _lblBuffSTR.setXY(_txtBuffMP.getX(), _txtBuffMP.getY() + _txtBuffMP.getH() + 8);
    
    _txtBuffSTR = new Textbox(this);
    _txtBuffSTR.setXY(_lblBuffSTR.getX(), _lblBuffSTR.getY() + _lblBuffSTR.getH());
    _txtBuffSTR.setNumeric(true);
    _txtBuffSTR.events().addChangeHandler(change);
    
    _chkBuffSTR = new Checkbox(this);
    _chkBuffSTR.setText("%");
    _chkBuffSTR.setXY(_txtBuffSTR.getX() + _txtBuffSTR.getW() + 4, _txtBuffSTR.getY());
    _chkBuffSTR.setW(34);
    _chkBuffSTR.events().addCheckHandler(new Checkbox.Events.Checked() {
      public void checked() { update(); }
    });
    
    _lblBuffDEX = new Label(this);
    _lblBuffDEX.setText("Buff DEX");
    _lblBuffDEX.setXY(_txtBuffSTR.getX(), _txtBuffSTR.getY() + _txtBuffSTR.getH() + 8);
    
    _txtBuffDEX = new Textbox(this);
    _txtBuffDEX.setXY(_lblBuffDEX.getX(), _lblBuffDEX.getY() + _lblBuffDEX.getH());
    _txtBuffDEX.setNumeric(true);
    _txtBuffDEX.events().addChangeHandler(change);
    
    _chkBuffDEX = new Checkbox(this);
    _chkBuffDEX.setText("%");
    _chkBuffDEX.setXY(_txtBuffDEX.getX() + _txtBuffDEX.getW() + 4, _txtBuffDEX.getY());
    _chkBuffDEX.setW(34);
    _chkBuffDEX.events().addCheckHandler(new Checkbox.Events.Checked() {
      public void checked() { update(); }
    });
    
    _lblBuffINT = new Label(this);
    _lblBuffINT.setText("Buff INT");
    _lblBuffINT.setXY(_txtBuffDEX.getX(), _txtBuffDEX.getY() + _txtBuffDEX.getH() + 8);
    
    _txtBuffINT = new Textbox(this);
    _txtBuffINT.setXY(_lblBuffINT.getX(), _lblBuffINT.getY() + _lblBuffINT.getH());
    _txtBuffINT.setNumeric(true);
    _txtBuffINT.events().addChangeHandler(change);
    
    _chkBuffINT = new Checkbox(this);
    _chkBuffINT.setText("%");
    _chkBuffINT.setXY(_txtBuffINT.getX() + _txtBuffINT.getW() + 4, _txtBuffINT.getY());
    _chkBuffINT.setW(34);
    _chkBuffINT.events().addCheckHandler(new Checkbox.Events.Checked() {
      public void checked() { update(); }
    });
    
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
    
    _wndEditor.controls(0).add(_lblType);
    _wndEditor.controls(0).add(_drpType);
    _wndEditor.controls(0).add(_lblSubtype);
    _wndEditor.controls(0).add(_drpSubtype);
    _wndEditor.controls(0).add(_lblDamage);
    _wndEditor.controls(0).add(_txtDamage);
    _wndEditor.controls(0).add(_lblWeight);
    _wndEditor.controls(0).add(_txtWeight);
    _wndEditor.controls(0).add(_lblProjectile);
    _wndEditor.controls(0).add(_drpProjectile);
    _wndEditor.controls(0).add(_lblSpeed);
    _wndEditor.controls(0).add(_txtSpeed);
    _wndEditor.controls(0).add(_lblHPHeal);
    _wndEditor.controls(0).add(_txtHPHeal);
    _wndEditor.controls(0).add(_lblMPHeal);
    _wndEditor.controls(0).add(_txtMPHeal);
    _wndEditor.controls(0).add(_chkPercent);
    _wndEditor.controls(1).add(_lblBuffHP);
    _wndEditor.controls(1).add(_txtBuffHP);
    _wndEditor.controls(1).add(_chkBuffHP);
    _wndEditor.controls(1).add(_lblBuffMP);
    _wndEditor.controls(1).add(_txtBuffMP);
    _wndEditor.controls(1).add(_chkBuffMP);
    _wndEditor.controls(1).add(_lblBuffSTR);
    _wndEditor.controls(1).add(_txtBuffSTR);
    _wndEditor.controls(1).add(_chkBuffSTR);
    _wndEditor.controls(1).add(_lblBuffDEX);
    _wndEditor.controls(1).add(_txtBuffDEX);
    _wndEditor.controls(1).add(_chkBuffDEX);
    _wndEditor.controls(1).add(_lblBuffINT);
    _wndEditor.controls(1).add(_txtBuffINT);
    _wndEditor.controls(1).add(_chkBuffINT);
    _wndEditor.controls(2).add(_lblName);
    _wndEditor.controls(2).add(_txtName);
    _wndEditor.controls(2).add(_lblNote);
    _wndEditor.controls(2).add(_txtNote);
    _wndEditor.controls(2).add(_lblSprite);
    _wndEditor.controls(2).add(_drpSprite);
    
    _game.send(new EditorData.List(EditorData.DATA_TYPE_SPRITE), EditorData.List.class, new Game.PacketCallback<EditorData.List>() {
      public boolean recieved(EditorData.List packet) {
        if(packet.type() == EditorData.DATA_TYPE_SPRITE) {
          remove();
          for(EditorData.List.ListData data : packet.data()) {
            _drpSprite.add(new DropdownData(data.file(), data.name(), data.note()));
          }
          
          return true;
        }
        
        return false;
      }
    });
    
    /*_game.send(new EditorData.List(EditorData.DATA_TYPE_PROJECTILE), EditorData.List.class, new Game.PacketCallback<EditorData.List>() {
      public boolean recieved(EditorData.List packet) {
        if(packet.type() == EditorData.DATA_TYPE_PROJECTILE) {
          remove();
          for(EditorData.List.ListData data : packet.data()) {
            _drpProjectile.add(new DropdownData(data.file(), data.name(), data.note()));
          }
          
          return true;
        }
        
        return false;
      }
    });*/
    
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
    EditorSave.Item packet = new EditorSave.Item();
    packet.addData(_item);
    Game.getInstance().send(packet);
  }
  
  public void editData(String file, boolean newData) {
    push();
    
    _item = new ItemEditorItem(file, newData);
    _item.events().addLoadHandler(new GameData.Events.Load() {
      public void load() {
        _drpType.setSeletected((_item.getType() & Item.ITEM_TYPE_BITMASK) >> Item.ITEM_TYPE_BITSHIFT);
        
        _txtDamage.setText(String.valueOf(_item.getDamage()));
        _txtWeight.setText(String.valueOf(_item.getWeight()));
        _txtSpeed.setText(String.valueOf(_item.getSpeed()));
        
        _txtHPHeal.setText(String.valueOf(_item.getHPHeal()));
        _txtMPHeal.setText(String.valueOf(_item.getMPHeal()));
        _chkPercent.setChecked((_item.getType() & Item.ITEM_TYPE_POTION_HEAL_PERCENT) != 0);
        
        _txtBuffHP.setText(String.valueOf(_item.buffHP().val()));
        _txtBuffMP.setText(String.valueOf(_item.buffMP().val()));
        _txtBuffSTR.setText(String.valueOf(_item.buffSTR().val()));
        _txtBuffDEX.setText(String.valueOf(_item.buffDEX().val()));
        _txtBuffINT.setText(String.valueOf(_item.buffINT().val()));
        
        _chkBuffHP.setChecked(_item.buffHP().percent());
        _chkBuffMP.setChecked(_item.buffMP().percent());
        _chkBuffSTR.setChecked(_item.buffSTR().percent());
        _chkBuffDEX.setChecked(_item.buffDEX().percent());
        _chkBuffINT.setChecked(_item.buffINT().percent());
        
        _txtName.setText(_item.getName());
        _txtNote.setText(_item.getNote());
        
        int i = 0;
        for(Dropdown.Item item : _drpSprite) {
          DropdownData s = (DropdownData)item;
          if(s._file.equals(_item.getSprite())) {
            _drpSprite.setSeletected(i);
            break;
          }
          i++;
        }
        
        i = 0;
        for(Dropdown.Item item : _drpProjectile) {
          DropdownData s = (DropdownData)item;
          System.out.println(s._file + "\t" + _item.getProjectile());
          if(s._file.equals(_item.getProjectile())) {
            _drpProjectile.setSeletected(i);
            break;
          }
          i++;
        }
        
        updateSubtypes();
        resize();
      }
    });
  }
  
  private void updateSubtypes() {
    _lblDamage.setVisible(false);
    _txtDamage.setVisible(false);
    _lblWeight.setVisible(false);
    _txtWeight.setVisible(false);
    _lblProjectile.setVisible(false);
    _drpProjectile.setVisible(false);
    _lblSpeed.setVisible(false);
    _txtSpeed.setVisible(false);
    
    _lblHPHeal.setVisible(false);
    _txtHPHeal.setVisible(false);
    _lblMPHeal.setVisible(false);
    _txtMPHeal.setVisible(false);
    _chkPercent.setVisible(false);
    
    _drpSubtype.clear();
    switch(_drpType.getSelected()) {
      case Item.ITEM_TYPE_NONE:
        _drpSubtype.add(new Dropdown.Item("None"));
        break;
        
      case Item.ITEM_TYPE_WEAPON:
        _drpSubtype.add(new Dropdown.Item("None"));
        
        _lblDamage.setText("Damage");
        _lblWeight.setVisible(true);
        _txtWeight.setVisible(true);
        _lblSpeed.setVisible(true);
        _txtSpeed.setVisible(true);
        break;
        
      case Item.ITEM_TYPE_SHIELD:
        _drpSubtype.add(new Dropdown.Item("None"));
        
        _lblDamage.setText("Protection");
        _lblDamage.setVisible(true);
        _txtDamage.setVisible(true);
        _lblWeight.setVisible(true);
        _txtWeight.setVisible(true);
        break;
        
      case Item.ITEM_TYPE_ARMOUR:
        for(String type : Lang.ITEM_ARMOUR.get()) {
          _drpSubtype.add(new Dropdown.Item(type));
        }
        
        _lblDamage.setText("Protection");
        _lblDamage.setVisible(true);
        _txtDamage.setVisible(true);
        break;
        
      case Item.ITEM_TYPE_POTION:
        for(String type : Lang.ITEM_POTION.get()) {
          _drpSubtype.add(new Dropdown.Item(type));
        }
        
        _lblHPHeal.setVisible(true);
        _txtHPHeal.setVisible(true);
        _lblMPHeal.setVisible(true);
        _txtMPHeal.setVisible(true);
        _chkPercent.setVisible(true);
        break;
        
      case Item.ITEM_TYPE_SPELL:
        _drpSubtype.add(new Dropdown.Item("None"));
        break;
        
      case Item.ITEM_TYPE_BLING:
        for(String type : Lang.ITEM_BLING.get()) {
          _drpSubtype.add(new Dropdown.Item(type));
        }
        break;
        
      case Item.ITEM_TYPE_CURRENCY:
        _drpSubtype.add(new Dropdown.Item("None"));
        break;
    }
    
    if((_item.getType() & Item.ITEM_TYPE_BITMASK) == _drpType.getSelected()) {
      _drpSubtype.setSeletected((_item.getType() & Item.ITEM_SUBTYPE_BITMASK) >> Item.ITEM_SUBTYPE_BITSHIFT);
    } else {
      _drpSubtype.setSeletected(0);
    }
    
    updateSubTypeInterface();
  }
  
  private void updateSubTypeInterface() {
    switch(_drpType.getSelected() << Item.ITEM_TYPE_BITSHIFT) {
      case Item.ITEM_TYPE_WEAPON:
        _lblDamage.setVisible(true);
        _txtDamage.setVisible(true);
        _lblProjectile.setVisible(true);
        _drpProjectile.setVisible(true);
        break;
    }
  }
  
  private void update() {
    DropdownData sprite = (DropdownData)_drpSprite.get();
    
    int attribs = 0;
    switch(_drpSubtype.getSelected() << Item.ITEM_SUBTYPE_BITSHIFT) {
      case Item.ITEM_TYPE_POTION:
        if(_chkPercent.getChecked()) attribs |= Item.ITEM_TYPE_POTION_HEAL_PERCENT;
        break;
    }
    
    _item.setType((_drpType.getSelected() << Item.ITEM_TYPE_BITSHIFT) | (_drpSubtype.getSelected() << Item.ITEM_SUBTYPE_BITSHIFT) | attribs);
    
    _item.setDamage(Integer.parseInt(_txtDamage.getText()));
    _item.setWeight(Float.parseFloat(_txtWeight.getText()));
    
    if(_drpProjectile.get() != null) {
      _item.setProjectile(((DropdownData)_drpProjectile.get())._file);
    }
    
    _item.setSpeed(Integer.parseInt(_txtSpeed.getText()));
    
    _item.setHPHeal(Integer.parseInt(_txtHPHeal.getText()));
    _item.setMPHeal(Integer.parseInt(_txtMPHeal.getText()));
    
    _item.buffHP().val(Float.parseFloat(_txtBuffHP.getText()));
    _item.buffMP().val(Float.parseFloat(_txtBuffMP.getText()));
    _item.buffSTR().val(Float.parseFloat(_txtBuffSTR.getText()));
    _item.buffDEX().val(Float.parseFloat(_txtBuffDEX.getText()));
    _item.buffINT().val(Float.parseFloat(_txtBuffINT.getText()));
    
    _item.buffHP().percent(_chkBuffHP.getChecked());
    _item.buffMP().percent(_chkBuffMP.getChecked());
    _item.buffSTR().percent(_chkBuffSTR.getChecked());
    _item.buffDEX().percent(_chkBuffDEX.getChecked());
    _item.buffINT().percent(_chkBuffINT.getChecked());
    
    _item.setName(_txtName.getText());
    _item.setNote(_txtNote.getText());
    _item.setSprite(sprite != null ? sprite._file : null);
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