package game.graphics.gui;

import org.lwjgl.input.Keyboard;

import game.data.Item;
import game.data.util.Data;
import game.graphics.gui.editors.DataSelection;
import game.graphics.gui.editors.ItemEditor;
import game.graphics.gui.editors.MapEditor;
import game.graphics.gui.editors.SpriteEditor;
import game.language.Lang;
import game.network.packet.Chat;
import game.network.packet.EntityInteract;
import game.network.packet.InvUse;
import game.network.packet.InvSwap;
import game.settings.Settings;
import game.world.Entity;
import game.world.Region;
import game.world.Sprite;
import graphics.gl00.Canvas;
import graphics.gl00.Context;
import graphics.gl00.Drawable;
import graphics.shared.fonts.Font;
import graphics.shared.gui.Control;
import graphics.shared.gui.GUI;
import graphics.shared.gui.controls.Button;
import graphics.shared.gui.controls.Label;
import graphics.shared.gui.controls.Menu;
import graphics.shared.gui.controls.Picture;
import graphics.shared.gui.controls.Textbox;
import graphics.shared.gui.controls.compound.Window;

public class Game extends GUI {
  private game.Game _game = (game.Game)Context.getGame();
  private Font _font = Context.getFonts().getDefault();
  private float[] _debugColour = {1, 0, 1, 1};
  private Drawable _debugText;
  
  private boolean[] _key = new boolean[4];
  private boolean _showChat;
  
  private Listener _listener = new Listener(this);
  
  private Entity _entity = _game.getEntity();
  
  private MapEditor _editMap;
  
  private Textbox  _txtChat;
  private Window   _wndAdmin;
  private Button[] _btnEdit;
  
  private Picture[] _picVitalBack;
  private Picture[] _picVital;
  private Label[]   _lblVital;
  private Label[]   _lblStat;
  private Label[]   _lblStatVal;
  private Label     _lblWeight;
  private Label     _lblWeightVal;
  
  private Window    _wndInv;
  private game.graphics.gui.controls.Sprite[] _sprInv;
  private Picture   _picItemDesc;
  private game.graphics.gui.controls.Sprite _sprInvHover;
  private Label     _lblInvName;
  private Label     _lblInvType;
  private Label     _lblInvVal;
  
  private game.graphics.gui.controls.Sprite _sprSelectedInv;
  private Label _lblSelectedInvVal;
  private Entity.Inv _selectedInv;
  
  
  private game.graphics.gui.controls.Sprite   _sprHand1;
  private game.graphics.gui.controls.Sprite   _sprHand2;
  private game.graphics.gui.controls.Sprite[] _sprArmour;
  private game.graphics.gui.controls.Sprite[] _sprBling;
  
  private Menu _mnuItem;
  
  private Entity _selectedEntity;
  
  private String[] _chat = new String[256];
  private float[]  _chatColour = new float[] {1, 1, 1, 1};
  
  private boolean _loaded;
  
  public void load() {
    _game.setGameStateListener(_listener);
    
    _context.setBackColour(new float[] {0, 0, 0, 0});
    
    _txtChat = new Textbox(this);
    _txtChat.setX(4);
    _txtChat.setVisible(false);
    _txtChat.events().addKeyHandler(new Control.Events.Key() {
      public void down(int key) {
        handleChatText(key);
      }
      
      public void up(int key) { }
      public void text(char key) { }
    });
    
    _wndAdmin = new Window(this);
    _wndAdmin.setWH(250, 300);
    _wndAdmin.setXY((_context.getW() - _wndAdmin.getW()) / 2, (_context.getH() - _wndAdmin.getH()) / 2);
    _wndAdmin.addTab("Mods");
    _wndAdmin.addTab("Editors");
    _wndAdmin.setTab(1);
    _wndAdmin.setText("Administration");
    _wndAdmin.setVisible(false);
    
    _btnEdit = new Button[6];
    for(int i = 0; i < _btnEdit.length; i++) {
      _btnEdit[i] = new Button(this);
      _btnEdit[i].setXYWH(8, 8 + i * 19, 90, 20);
      _wndAdmin.Controls().add(_btnEdit[i]);
    }
    
    _btnEdit[0].setText("Edit Maps");
    _btnEdit[0].events().addClickHandler(new Control.Events.Click() {
      public void clickDbl() { }
      public void click() {
        if(_editMap == null) {
          _editMap = new MapEditor();
          _editMap.load();
          _editMap.setRegion(_entity.getRegion());
          _editMap.push();
          _wndAdmin.setVisible(false);
        }
      }
    });
    _btnEdit[1].setText("Edit Sprites");
    _btnEdit[1].events().addClickHandler(new Control.Events.Click() {
      public void clickDbl() { }
      public void click() {
        SpriteEditor editor = new SpriteEditor();
        editor.load();
        DataSelection dataSel = new DataSelection(editor, "sprites", _game.getSprites().toArray(new Data[0]));
        dataSel.load();
        dataSel.push();
        _wndAdmin.setVisible(false);
      }
    });
    _btnEdit[2].setText("Edit Items");
    _btnEdit[2].events().addClickHandler(new Control.Events.Click() {
      public void clickDbl() { }
      public void click() {
        ItemEditor editor = new ItemEditor();
        editor.load();
        DataSelection dataSel = new DataSelection(editor, "items", _game.getItems().toArray(new Data[0]));
        dataSel.load();
        dataSel.push();
        _wndAdmin.setVisible(false);
      }
    });
    _btnEdit[3].setText("Edit NPCs");
    _btnEdit[4].setText("Edit Spells");
    _btnEdit[5].setText("Edit Effects");
    
    _picVitalBack = new Picture[2];
    _picVital     = new Picture[2];
    _lblVital     = new Label  [2];
    _lblStat      = new Label  [3];
    _lblStatVal   = new Label  [3];
    
    for(int i = 0; i < _lblVital.length; i++) {
      _picVitalBack[i] = new Picture(this);
      _picVitalBack[i].setBackColour(new float[] {0, 0, 0, 1});
      _picVitalBack[i].setBorderColour(new float[] {1, 1, 1, 1});
      _picVitalBack[i].setWH(100, 10);
      _picVitalBack[i].setY((_picVitalBack[i].getH() + 2) * i + 2);
      
      _picVital[i] = new Picture(this);
      _picVital[i].setBackColour(new float[] {0, 1, 0, 1});
      _picVital[i].setWH(_picVitalBack[i].getW(), _picVitalBack[i].getH() - 1);
      _picVital[i].setY(1);
      _picVitalBack[i].Controls().add(_picVital[i]);
      
      _lblVital[i] = new Label(this);
      _lblVital[i].setText(Lang.VITAL_ABBV.text(i) + ":");
      _lblVital[i].setY(_picVitalBack[i].getY());
      
      Controls().add(_picVitalBack[i]);
      Controls().add(_lblVital[i]);
    }
    
    for(int i = 0; i < _lblStat.length; i++) {
      _lblStat[i] = new Label(this);
      _lblStat[i].setText(Lang.STAT_ABBV.text(i) + ":");
      _lblStat[i].setY(_lblVital[_lblVital.length - 1].getY() + _lblVital[_lblVital.length - 1].getH() * (i + 1) + 2);
      
      _lblStatVal[i] = new Label(this);
      _lblStatVal[i].setY(_lblStat[i].getY());
      
      Controls().add(_lblStat[i]);
      Controls().add(_lblStatVal[i]);
    }
    
    _lblWeight = new Label(this);
    _lblWeight.setText("Weight:");
    _lblWeight.setY(_lblStat[_lblStat.length - 1].getY() + _lblStat[_lblStat.length - 1].getH());
    
    _lblWeightVal = new Label(this);
    _lblWeightVal.setY(_lblWeight.getY());
    
    Controls().add(_lblWeight);
    Controls().add(_lblWeightVal);
    
    _wndInv = new Window(this);
    _sprInv = new game.graphics.gui.controls.Sprite[Settings.Player.Inventory.Size];
    for(int i = 0; i < _sprInv.length; i++) {
      final int n = i;
      _sprInv[i] = new game.graphics.gui.controls.Sprite(this);
      _sprInv[i].setBackColour(new float[] {0, 0, 0, 1});
      _sprInv[i].setBorderColour(new float[] {1, 1, 1, 1});
      _sprInv[i].setWH(32, 32);
      _sprInv[i].setXY(i % 8 * 34 + 5, i / 8 * 34 + 5);
      _sprInv[i].events().addDrawHandler(new Control.Events.Draw() {
        public void draw() {
          if(_entity.inv(n) != null) {
            if(_entity.inv(n).val() != 1) {
              _font.draw(2, 2, String.valueOf(_entity.inv(n).val()), _chatColour);
            }
          }
        }
      });
      
      _sprInv[i].events().addHoverHandler(new Control.Events.Hover() {
        public void enter() {
          if(_entity.inv(n) != null) {
            _sprInvHover.setSprite(_sprInv[n].getSprite());
            _lblInvName.setText(_entity.inv(n).item().getName());
            
            _lblInvVal.setText(String.valueOf(_entity.inv(n).val()));
            _lblInvVal.setVisible(_entity.inv(n).val() != 0);
            
            if(_entity.inv(n).item().getType() != Item.ITEM_TYPE_NONE) {
              _lblInvType.setText(Lang.ITEM_TYPE.text(_entity.inv(n).item().getType() & Item.ITEM_TYPE_BITMASK));
            } else {
              _lblInvType.setText(null);
            }
            
            _picItemDesc.setXY(getControl().getX() - 3, getControl().getY() - 3);
            _picItemDesc.setVisible(true);
          }
        }
        
        public void leave() {
          _picItemDesc.setVisible(false);
        }
      });
      
      _sprInv[i].events().addClickHandler(new Control.Events.Click() {
        public void click() {
          
        }
        
        public void clickDbl() {
          if(_entity.inv(n) != null) {
            _picItemDesc.setVisible(false);
            useInv(_entity.inv(n));
          }
        }
      });
      
      _sprInv[i].events().addMouseHandler(new Control.Events.Mouse() {
        public void up(int x, int y, int button) {
          if(button == 0) {
            if(!_sprSelectedInv.getVisible()) {
              _sprSelectedInv.setSprite(new Sprite(_sprInv[n].getSprite().getSource()));
              _sprSelectedInv.setVisible(true);
              _lblSelectedInvVal.setText(String.valueOf(_entity.inv(n).val()));
              _lblSelectedInvVal.setVisible(_entity.inv(n).val() != 0);
              
              _selectedInv = _entity.inv(n);
              updateInv(_entity.inv(n), null);
              _entity.inv(n, null);
              
              _picItemDesc.setVisible(false);
              
              _context.setCursor(new Context.CursorCallback() {
                public void draw() {
                  _sprSelectedInv.setX(_context.getMouseX() - _sprSelectedInv.getW() / 2);
                  _sprSelectedInv.setY(_context.getMouseY() - _sprSelectedInv.getH() / 2);
                  _sprSelectedInv.draw();
                }
              }, (int)(_sprInv[n].getAllX() + _sprInv[n].getW() / 2), (int)(_sprInv[n].getAllY() + _sprInv[n].getH() / 2));
            } else {
              if(n == _selectedInv.index()) {
                updateInv(null, _selectedInv);
                _entity.inv(n, _selectedInv);
                _context.setCursor(null, (int)(_sprInv[n].getAllX() + _sprInv[n].getW() / 2), (int)(_sprInv[n].getAllY() + _sprInv[n].getH() / 2));
                _sprSelectedInv.setVisible(false);
                _selectedInv = null;
                return;
              }
              
              if(_entity.inv(n) != null) {
                if(_entity.inv(n).item() != _selectedInv.item()) {
                  return;
                }
              }
              
              _game.send(new InvSwap(_selectedInv.index(), n, _selectedInv.val()));
              _context.setCursor(null, (int)(_sprInv[n].getAllX() + _sprInv[n].getW() / 2), (int)(_sprInv[n].getAllY() + _sprInv[n].getH() / 2));
              _sprSelectedInv.setVisible(false);
              _selectedInv = null;
            }
          }
        }
        
        public void down(int x, int y, int button) {
          
        }
        
        public void move(int x, int y, int button) {
          
        }
      });
      
      _wndInv.Controls().add(_sprInv[i]);
    }
    
    _picItemDesc = new Picture(this);
    _picItemDesc.setBorderColour(new float[] {0, 0, 0, 1});
    _picItemDesc.setBackColour(new float[] {0.3f, 0.3f, 0.3f, 1});
    _picItemDesc.setWH(138, 38);
    _picItemDesc.setVisible(false);
    _wndInv.Controls().add(_picItemDesc);
    
    _sprInvHover = new game.graphics.gui.controls.Sprite(this, false);
    _sprInvHover.setBackColour(new float[] {0, 0, 0, 1});
    _sprInvHover.setBorderColour(new float[] {1, 1, 1, 1});
    _sprInvHover.setXYWH(3, 3, 32, 32);
    
    _lblInvVal = new Label(this);
    _lblInvVal.setXY(2, 2);
    _sprInvHover.Controls().add(_lblInvVal);
    
    _lblInvName = new Label(this);
    _lblInvName.setXY(_sprInvHover.getX() + _sprInvHover.getW() + 4, _sprInvHover.getY());
    
    _lblInvType = new Label(this);
    _lblInvType.setXY(_lblInvName.getX(), _lblInvName.getY() + _lblInvName.getH());
    
    _picItemDesc.Controls().add(_sprInvHover);
    _picItemDesc.Controls().add(_lblInvName);
    _picItemDesc.Controls().add(_lblInvType);
    
    _lblSelectedInvVal = new Label(this);
    _lblSelectedInvVal.setXY(2, 2);
    
    _sprSelectedInv = new game.graphics.gui.controls.Sprite(this);
    _sprSelectedInv.setWH(32, 32);
    _sprSelectedInv.setVisible(false);
    _sprSelectedInv.Controls().add(_lblSelectedInvVal);
    
    float x = _sprInv[0].getX();
    float y = 5 * 34 + 8;
    
    _sprHand1 = new game.graphics.gui.controls.Sprite(this);
    _sprHand1.setBackColour(new float[] {0, 0, 0, 1});
    _sprHand1.setBorderColour(new float[] {1, 1, 1, 1});
    _sprHand1.setXYWH(x, y, 32, 32);
    _sprHand1.events().addMouseHandler(new Control.Events.Mouse() {
      public void move(int x, int y, int button) { }
      public void down(int x, int y, int button) { }
      public void up(int x, int y, int button) {
        if(button == 0) {
          if(_sprSelectedInv.getVisible()) {
            switch(_selectedInv.item().getType() & Item.ITEM_TYPE_BITMASK) {
              case Item.ITEM_TYPE_WEAPON:
              case Item.ITEM_TYPE_SHIELD:
                break;
                
              default:
                return;
            }
            
            if(_entity.equip().hand1() != null) return;
            
            _game.send(new InvUse(_selectedInv, 0));
            _context.setCursor(null, (int)(_sprHand1.getAllX() + _sprHand1.getW() / 2), (int)(_sprHand1.getAllY() + _sprHand1.getH() / 2));
            _sprSelectedInv.setVisible(false);
            _selectedInv = null;
          }
        }
      }
    });
    _wndInv.Controls().add(_sprHand1);
    x += 34;
    
    _sprHand2 = new game.graphics.gui.controls.Sprite(this);
    _sprHand2.setBackColour(new float[] {0, 0, 0, 1});
    _sprHand2.setBorderColour(new float[] {1, 1, 1, 1});
    _sprHand2.setXYWH(x, y, 32, 32);
    _sprHand2.events().addMouseHandler(new Control.Events.Mouse() {
      public void move(int x, int y, int button) { }
      public void down(int x, int y, int button) { }
      public void up(int x, int y, int button) {
        if(button == 0) {
          if(_sprSelectedInv.getVisible()) {
            switch(_selectedInv.item().getType() & Item.ITEM_TYPE_BITMASK) {
              case Item.ITEM_TYPE_WEAPON:
              case Item.ITEM_TYPE_SHIELD:
                break;
                
              default:
                return;
            }
            
            if(_entity.equip().hand2() != null) return;
            
            _game.send(new InvUse(_selectedInv, 1));
            _context.setCursor(null, (int)(_sprHand2.getAllX() + _sprHand2.getW() / 2), (int)(_sprHand2.getAllY() + _sprHand2.getH() / 2));
            _sprSelectedInv.setVisible(false);
            _selectedInv = null;
          }
        }
      }
    });
    _wndInv.Controls().add(_sprHand2);
    x += 68;
    
    _sprBling = new game.graphics.gui.controls.Sprite[Item.ITEM_TYPE_BLING_COUNT];
    for(int i = 0; i < Item.ITEM_TYPE_BLING_COUNT; i++) {
      final int n = i;
      _sprBling[i] = new game.graphics.gui.controls.Sprite(this);
      _sprBling[i].setBackColour(new float[] {0, 0, 0, 1});
      _sprBling[i].setBorderColour(new float[] {1, 1, 1, 1});
      _sprBling[i].setXYWH(x, y, 32, 32);
      _sprBling[i].events().addMouseHandler(new Control.Events.Mouse() {
        public void move(int x, int y, int button) { }
        public void down(int x, int y, int button) { }
        public void up(int x, int y, int button) {
          if(button == 0) {
            if(_sprSelectedInv.getVisible()) {
              if((_selectedInv.item().getType() & Item.ITEM_TYPE_BITMASK) != Item.ITEM_TYPE_BLING) return;
              if((_selectedInv.item().getType() & Item.ITEM_SUBTYPE_BITMASK) != n << Item.ITEM_SUBTYPE_BITSHIFT) return;
              
              if(_entity.equip().bling(n) != null) return;
              
              _game.send(new InvUse(_selectedInv));
              _context.setCursor(null, (int)(_sprBling[n].getAllX() + _sprBling[n].getW() / 2), (int)(_sprBling[n].getAllY() + _sprBling[n].getH() / 2));
              _sprSelectedInv.setVisible(false);
              _selectedInv = null;
            }
          }
        }
      });
      _wndInv.Controls().add(_sprBling[i]);
      x += 34;
    }
    
    x = _sprInv[0].getX();
    y += 34;
    
    _sprArmour = new game.graphics.gui.controls.Sprite[Item.ITEM_TYPE_ARMOUR_COUNT];
    for(int i = 0; i < Item.ITEM_TYPE_ARMOUR_COUNT; i++) {
      final int n = i;
      _sprArmour[i] = new game.graphics.gui.controls.Sprite(this);
      _sprArmour[i].setBackColour(new float[] {0, 0, 0, 1});
      _sprArmour[i].setBorderColour(new float[] {1, 1, 1, 1});
      _sprArmour[i].setXYWH(x, y, 32, 32);
      _sprArmour[i].events().addMouseHandler(new Control.Events.Mouse() {
        public void move(int x, int y, int button) { }
        public void down(int x, int y, int button) { }
        public void up(int x, int y, int button) {
          if(button == 0) {
            if(_sprSelectedInv.getVisible()) {
              if((_selectedInv.item().getType() & Item.ITEM_TYPE_BITMASK) != Item.ITEM_TYPE_ARMOUR) return;
              if((_selectedInv.item().getType() & Item.ITEM_SUBTYPE_BITMASK) != n << Item.ITEM_SUBTYPE_BITSHIFT) return;
              if(_entity.equip().armour(n) != null) return;
              
              _game.send(new InvUse(_selectedInv, 0));
              _context.setCursor(null, (int)(_sprArmour[n].getAllX() + _sprArmour[n].getW() / 2), (int)(_sprArmour[n].getAllY() + _sprArmour[n].getH() / 2));
              _sprSelectedInv.setVisible(false);
              _selectedInv = null;
            }
          }
        }
      });
      _wndInv.Controls().add(_sprArmour[i]);
      x += 34;
    }
    
    _wndInv.setText("Inventory");
    _wndInv.setClientWH(8 * 34 + 8, y + 38);
    _wndInv.setVisible(false);
    
    _mnuItem = new Menu(this);
    _mnuItem.setW(100);
    _mnuItem.add("Pick up");
    _mnuItem.add("View details");
    _mnuItem.events().addSelectHandler(new Menu.Events.Select() {
      public void select(int index) {
        switch(index) {
          case 0:
            _game.send(new EntityInteract(_selectedEntity));
            break;
            
          case 1:
            Message.show("Not yet implemented");
            break;
        }
      }
    });
    
    Controls().add(_txtChat);
    Controls().add(_mnuItem);
    Controls().add(_wndInv);
    Controls().add(_wndAdmin);
    
    Canvas c = new Canvas("Debug text", 256, 256);
    c.bind();
    _font.draw(4,   4, "Graphics:", _debugColour);
    _font.draw(4,  14, "Logic:", _debugColour);
    _font.draw(4,  24, "Physics:", _debugColour);
    _font.draw(4,  34, "Map:", _debugColour);
    _font.draw(4,  44, "Loc:", _debugColour);
    _font.draw(4,  54, "Layer:", _debugColour);
    _font.draw(4,  64, "RLoc:", _debugColour);
    _font.draw(4,  74, "XVel:", _debugColour);
    _font.draw(4,  84, "YVel:", _debugColour);
    _font.draw(4,  94, "Bearing:", _debugColour);
    _font.draw(4, 104, "Sprites:", _debugColour);
    _font.draw(4, 114, "Textures:", _debugColour);
    _font.draw(4, 124, "Entity ID:", _debugColour);
    c.unbind();
    
    _debugText = Context.newDrawable();
    _debugText.setTexture(c.getTexture());
    _debugText.createQuad();
    
    resize();
    updateStats(_entity.stats());
    updateInv(_entity.inv());
    updateEquip(_entity.equip());
    
    _loaded = true;
  }
  
  public void destroy() {
    
  }
  
  public void resize() {
    _wndAdmin.setXY((_context.getW() - _wndAdmin.getW()) / 2, (_context.getH() - _wndAdmin.getH()) / 2);
    _wndInv.setXY((_context.getW() - _wndInv.getW()) / 2, (_context.getH() - _wndInv.getH()) / 2);
    _txtChat.setY(_context.getH() - _txtChat.getH() - 4);
    
    for(int i = 0; i < _lblVital.length; i++) {
      _picVitalBack[i].setX(_context.getW() - _picVitalBack[i].getW() - 2);
      _lblVital[i].setX(_picVitalBack[i].getX() - _lblVital[i].getW() - 4);
    }
    
    for(int i = 0; i < _lblStat.length; i++) {
      _lblStat[i].setX(_picVitalBack[0].getX() - _lblStat[i].getW() - 4);
      _lblStatVal[i].setX(_picVitalBack[0].getX());
    }
    
    _lblWeight.setX(_picVitalBack[0].getX() - _lblWeight.getW() - 4);
    _lblWeightVal.setX(_picVitalBack[0].getX());
    
    _game.updateCamera();
  }
  
  public void updateStats(Entity.Stats stats) {
    _picVital[0].setW((float)stats.vitalHP().val() / stats.vitalHP().max() * _picVitalBack[0].getW());
    _picVital[1].setW((float)stats.vitalMP().val() / stats.vitalMP().max() * _picVitalBack[1].getW());
    _lblStatVal[0].setText(String.valueOf(stats.statSTR().val()));
    _lblStatVal[1].setText(String.valueOf(stats.statINT().val()));
    _lblStatVal[2].setText(String.valueOf(stats.statDEX().val()));
    _lblWeightVal.setText(String.valueOf(stats.weight()));
  }
  
  public void updateInv(Entity.Inv[] inv) {
    for(int i = 0; i < Settings.Player.Inventory.Size; i++) {
      if(_sprInv[i].getSprite() != null) {
        _sprInv[i].getSprite().remove();
        _sprInv[i].setSprite(null);
      }
      
      if(inv[i] != null) {
        _sprInv[i].setSprite(new Sprite(_game.getSprite(inv[i].item().getSprite())));
      }
    }
  }
  
  public void updateInv(Entity.Inv oldInv, Entity.Inv newInv) {
    int index = -1;
    if(oldInv != null) index = oldInv.index();
    if(newInv != null) index = newInv.index();
    if(index == -1) return;
    
    if(_sprInv[index].getSprite() != null) {
      _sprInv[index].setSprite(null);
    }
    
    if(newInv != null) {
      _sprInv[index].setSprite(new Sprite(_game.getSprite(newInv.item().getSprite())));
    }
  }
  
  public void updateEquip(Entity.Equip equip) {
    if(_sprHand1.getSprite() != null) _sprHand1.setSprite(null);
    if(_sprHand2.getSprite() != null) _sprHand2.setSprite(null);
    
    if(equip.hand1() != null) {
      _sprHand1.setSprite(new Sprite(_game.getSprite(equip.hand1().getSprite())));
    }
    
    if(equip.hand2() != null) {
      _sprHand2.setSprite(new Sprite(_game.getSprite(equip.hand2().getSprite())));
    }
    
    for(int i = 0; i < Item.ITEM_TYPE_ARMOUR_COUNT; i++) {
      if(_sprArmour[i].getSprite() != null) _sprArmour[i].setSprite(null);
      if(equip.armour(i) != null) {
        _sprArmour[i].setSprite(new Sprite(_game.getSprite(equip.armour(i).getSprite())));
      }
    }
    
    for(int i = 0; i < Item.ITEM_TYPE_BLING_COUNT; i++) {
      if(_sprBling[i].getSprite() != null) _sprBling[i].setSprite(null);
      if(equip.bling(i) != null) {
        _sprBling[i].setSprite(new Sprite(_game.getSprite(equip.bling(i).getSprite())));
      }
    }
  }
  
  private void useInv(Entity.Inv inv) {
    _game.send(new InvUse(inv));
  }
  
  public void draw() {
    if(!_loaded) load();
    
    double w = Math.floor(_context.getW() / Settings.Map.Size) + 1;
    double h = Math.floor(_context.getH() / Settings.Map.Size) + 1;
    int x1 = (int)Math.ceil(w / 2);
    int y1 = (int)Math.ceil(h / 2);
    
    Region r = _entity.getRegion();
    for(int z = 0; z < Settings.Map.Depth; z++) {
      for(int x = -x1; x <= x1; x++) {
        for(int y = -y1; y <= y1; y++) {
          r.getRelativeRegion(x, y).draw(z);
        }
      }
      
      Sprite.draw(z);
    }
    
    _matrix.push();
    _matrix.reset();
    
    int textHeight = _txtChat.getVisible() ? _context.getH() - (int)_txtChat.getY() : 4;
    
    for(int i = 0; i < 5; i++) {
      _font.draw(4, _context.getH() - (i + 1) * _font.getH() - textHeight, _chat[i], _chatColour);
    }
    
    _debugText.draw();
    _font.draw(53,   4, _context.getFPS() + "hz", _debugColour);
    _font.draw(53,  14, _context.getLogicFPS() + "hz", _debugColour);
    _font.draw(53,  24, _game.getWorld().getFPS() + "hz", _debugColour);
    _font.draw(53,  34, _entity.getMX() + ", " + _entity.getMY(), _debugColour);
    _font.draw(53,  44, _entity.getX() + ", " + _entity.getY(), _debugColour);
    _font.draw(53,  54, String.valueOf(_entity.getZ()), _debugColour);
    _font.draw(53,  64, _entity.getRX() + ", " + _entity.getRY(), _debugColour);
    _font.draw(53,  74, String.valueOf(_entity.getVel() * _entity.getVelScaleX()), _debugColour);
    _font.draw(53,  84, String.valueOf(_entity.getVel() * _entity.getVelScaleY()), _debugColour);
    _font.draw(53,  94, String.valueOf(_entity.getBear()), _debugColour);
    _font.draw(53, 104, String.valueOf(Sprite.count()), _debugColour);
    _font.draw(53, 114, _textures.loaded() + " (" + _textures.loading() + ")", _debugColour);
    _font.draw(53, 124, String.valueOf(_entity.getID()), _debugColour);
    
    _matrix.pop();
  }
  
  public void entityDraw(Entity e) {
    if(e.getName() != null) {
      _font.draw(-_font.getW(e.getName()) / 2, (int)(-e.getSprite().getFrameH() + e.getSprite().getFrameFY()) - _font.getH(), e.getName(), _chatColour);
    }
  }
  
  public boolean logic() {
    return false;
  }
  
  private void checkMovement() {
    float a = -1;
    if(_key[0] && !_key[1]) {
      if(_key[2] && !_key[3]) {
        a = 225;
      } else if(_key[3] && !_key[2]) {
        a = 315;
      } else {
        a = 270;
      }
    } else if(_key[1] && !_key[0]) {
      if(_key[2] && !_key[3]) {
        a = 135;
      } else if(_key[3] && !_key[2]) {
        a = 45;
      } else {
        a = 90;
      }
    } else {
      if(_key[2] && !_key[3]) {
        a = 180;
      } else if(_key[3] && !_key[2]) {
        a = 0;
      }
    }
    
    if(a != -1) {
      if(_entity.getVelTarget() == 0) {
        _game.startMoving(a);
      } else {
        if(_entity.getBear() != a) {
          _game.startMoving(a);
        }
      }
    } else {
      if(_entity.getVelTarget() != 0) {
        _game.stopMoving();
      }
    }
  }
  
  public boolean handleMouseUp(int x, int y, int button) {
    _selectedEntity = _game.interact(x, y);
    
    if(_selectedEntity != null) {
      if(_selectedEntity.getType() == Entity.Type.Item) {
        _mnuItem.show((int)(_selectedEntity.getX() + _context.getCameraX() - _mnuItem.getW() / 2), (int)(_selectedEntity.getY() + _context.getCameraY()) + 16);
      }
      
      return true;
    }
    
    return false;
  }
  
  public boolean handleKeyDown(int key) {
    if(!_txtChat.getVisible()) {
      switch(key) {
        case Keyboard.KEY_W:
        case Keyboard.KEY_UP:
          _key[0] = true;
          checkMovement();
          return true;
          
        case Keyboard.KEY_A:
        case Keyboard.KEY_LEFT:
          _key[2] = true;
          checkMovement();
          return true;
          
        case Keyboard.KEY_S:
        case Keyboard.KEY_DOWN:
          _key[1] = true;
          checkMovement();
          return true;
        
        case Keyboard.KEY_D:
        case Keyboard.KEY_RIGHT:
          _key[3] = true;
          checkMovement();
          return true;
          
        case Keyboard.KEY_T:
          _txtChat.setVisible(true);
          _showChat = true;
          return true;
          
        case Keyboard.KEY_E:
        case Keyboard.KEY_I:
          _wndInv.setVisible(!_wndInv.getVisible());
          return true;
          
        case Keyboard.KEY_SLASH:
          _txtChat.setVisible(true);
          _txtChat.setFocus(true);
          return true;
        
        case Keyboard.KEY_F1:
          _wndAdmin.setVisible(!_wndAdmin.getVisible());
          return true;
          
        case Keyboard.KEY_ESCAPE:
          if(_editMap != null) {
            if(_editMap.unload()) {
              _editMap.pop();
              _editMap = null;
            }
            
            return true;
          }
      }
    }
    
    return false;
  }
  
  public boolean handleKeyUp(int key) {
    if(!_txtChat.getVisible()) {
      switch(key) {
        case Keyboard.KEY_W:
        case Keyboard.KEY_UP:
          _key[0] = false;
          checkMovement();
          return true;
          
        case Keyboard.KEY_A:
        case Keyboard.KEY_LEFT:
          _key[2] = false;
          checkMovement();
          return true;
          
        case Keyboard.KEY_S:
        case Keyboard.KEY_DOWN:
          _key[1] = false;
          checkMovement();
          return true;
          
        case Keyboard.KEY_D:
        case Keyboard.KEY_RIGHT:
          _key[3] = false;
          checkMovement();
          return true;
      }
    } else {
      if(_showChat) {
        _txtChat.setFocus(true);
        _showChat = false;
      }
    }
    
    return false;
  }
  
  private void handleChatText(int key) {
    if(key == Keyboard.KEY_RETURN) {
      if(_txtChat.getText() == null || _txtChat.getText().length() == 0) {
        _txtChat.setVisible(false);
        return;
      }
      
      String chat = _txtChat.getText();
      _txtChat.setText(null);
      _txtChat.setVisible(false);
      
      _game.send(new Chat(chat));
    }
  }
  
  public class Listener implements game.Game.GameStateListener {
    private Game _game;
    
    public Listener(Game game) {
      _game = game;
    }
    
    public void gotChat(String name, String text) {
      for(int i = _game._chat.length - 1; i >= 1; i--) {
        _game._chat[i] = _game._chat[i - 1];
      }
      
      _game._chat[0] = name + ": " + text;
    }
    
    public void entityDraw(Entity e) {
      _game.entityDraw(e);
    }
    
    public void updateVitals(Entity.Stats stats) {
      _game.updateStats(stats);
    }
    
    public void updateStats(Entity.Stats stats) {
      _game.updateStats(stats);
    }
    
    public void updateInv(Entity.Inv[] inv) {
      _game.updateInv(inv);
    }

    public void updateInv(Entity.Inv oldInv, Entity.Inv newInv) {
      _game.updateInv(oldInv, newInv);
    }
    
    public void updateEquip(Entity.Equip equip) {
      _game.updateEquip(equip);
    }
  }
}