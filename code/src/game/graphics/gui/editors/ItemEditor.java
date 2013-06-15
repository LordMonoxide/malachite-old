package game.graphics.gui.editors;

import javax.swing.JOptionPane;

import game.Game;
import game.data.Item;
import game.data.Sprite;
import game.data.util.Data;
import game.language.Lang;
import game.network.packet.editors.Save;
import graphics.gl00.Context;
import graphics.shared.gui.Control;
import graphics.shared.gui.GUI;
import graphics.shared.gui.controls.Checkbox;
import graphics.shared.gui.controls.Dropdown;
import graphics.shared.gui.controls.Label;
import graphics.shared.gui.controls.Textbox;
import graphics.shared.gui.controls.compound.Window;

public class ItemEditor extends GUI implements Editor {
  private Game _game = (Game)Context.getGame();
  
  private Window  _wndEditor;
  
  private Label    _lblType;
  private Dropdown _drpType;
  private Label    _lblSubtype;
  private Dropdown _drpSubtype;
  
  private Label    _lblDamage;
  private Textbox  _txtDamage;
  
  private Label    _lblHPHeal;
  private Textbox  _txtHPHeal;
  private Label    _lblMPHeal;
  private Textbox  _txtMPHeal;
  private Checkbox _chkPercent;
  
  private Label    _lblName;
  private Textbox  _txtName;
  private Label    _lblNote;
  private Textbox  _txtNote;
  private Label    _lblSprite;
  private Dropdown _drpSprite;
  
  private ItemEditorItem _item;
  
  public void load() {
    _wndEditor = new Window(this);
    _wndEditor.setWH(300, 300);
    _wndEditor.setText("Item Editor");
    _wndEditor.addTab("Type");
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
      public void checked() {
        update();
      }
    });
    
    _lblName = new Label(this);
    _lblName.setText("Name");
    _lblName.setXY(8, 4);
    
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
    
    _wndEditor.Controls(0).add(_lblType);
    _wndEditor.Controls(0).add(_drpType);
    _wndEditor.Controls(0).add(_lblSubtype);
    _wndEditor.Controls(0).add(_drpSubtype);
    _wndEditor.Controls(0).add(_lblDamage);
    _wndEditor.Controls(0).add(_txtDamage);
    _wndEditor.Controls(0).add(_lblHPHeal);
    _wndEditor.Controls(0).add(_txtHPHeal);
    _wndEditor.Controls(0).add(_lblMPHeal);
    _wndEditor.Controls(0).add(_txtMPHeal);
    _wndEditor.Controls(0).add(_chkPercent);
    _wndEditor.Controls(1).add(_lblName);
    _wndEditor.Controls(1).add(_txtName);
    _wndEditor.Controls(1).add(_lblNote);
    _wndEditor.Controls(1).add(_txtNote);
    _wndEditor.Controls(1).add(_lblSprite);
    _wndEditor.Controls(1).add(_drpSprite);
    
    for(Sprite s : _game.getSprites()) {
      _drpSprite.add(new DropdownSprite(s));
    }
    
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
    
    _drpType.setSeletected((_item.getType() & Item.ITEM_TYPE_BITMASK) >> Item.ITEM_TYPE_BITSHIFT);
    _txtDamage.setText(String.valueOf(_item.getDamage()));
    _txtHPHeal.setText(String.valueOf(_item.getHPHeal()));
    _txtMPHeal.setText(String.valueOf(_item.getMPHeal()));
    _chkPercent.setChecked((_item.getType() & Item.ITEM_TYPE_POTION_HEAL_PERCENT) != 0);
    
    _txtName.setText(_item.getName());
    _txtNote.setText(_item.getNote());
    
    int i = 0;
    for(Dropdown.Item item : _drpSprite) {
      DropdownSprite s = (DropdownSprite)item;
      if(s._sprite.getFile().equals(_item.getSprite())) {
        _drpSprite.setSeletected(i);
        break;
      }
      i++;
    }
    
    updateSubtypes();
    resize();
  }
  
  private void updateSubtypes() {
    _lblDamage.setVisible(false);
    _txtDamage.setVisible(false);
    
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
        for(String type : Lang.ITEM_WEAPON.get()) {
          _drpSubtype.add(new Dropdown.Item(type));
        }
        
        _lblDamage.setVisible(true);
        _txtDamage.setVisible(true);
        break;
        
      case Item.ITEM_TYPE_ARMOUR:
        for(String type : Lang.ITEM_ARMOUR.get()) {
          _drpSubtype.add(new Dropdown.Item(type));
        }
        
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
    }
    
    if((_item.getType() & Item.ITEM_TYPE_BITMASK) == _drpType.getSelected()) {
      _drpSubtype.setSeletected((_item.getType() & Item.ITEM_SUBTYPE_BITMASK) >> Item.ITEM_SUBTYPE_BITSHIFT);
    } else {
      _drpSubtype.setSeletected(0);
    }
  }
  
  private void update() {
    DropdownSprite sprite = (DropdownSprite)_drpSprite.get();
    
    int attribs = 0;
    if(_chkPercent.getChecked()) attribs |= Item.ITEM_TYPE_POTION_HEAL_PERCENT;
    
    _item.setType((_drpType.getSelected() << Item.ITEM_TYPE_BITSHIFT) | (_drpSubtype.getSelected() << Item.ITEM_SUBTYPE_BITSHIFT) | attribs);
    _item.setDamage(Integer.parseInt(_txtDamage.getText()));
    _item.setHPHeal(Integer.parseInt(_txtHPHeal.getText()));
    _item.setMPHeal(Integer.parseInt(_txtMPHeal.getText()));
    
    _item.setName(_txtName.getText());
    _item.setNote(_txtNote.getText());
    _item.setSprite(sprite != null ? sprite._sprite.getFile() : null);
  }
  
  private class DropdownSprite extends Dropdown.Item {
    private Sprite _sprite;
    
    public DropdownSprite(Sprite sprite) {
      super(sprite.getName() + " - " + sprite.getNote());
      _sprite = sprite;
    }
  }
}