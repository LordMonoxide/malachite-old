package game.graphics.gui.editors;

import java.io.File;

import javax.swing.JOptionPane;

import game.data.Sprite;
import game.data.Sprite.Anim;
import game.data.Sprite.Frame;
import game.data.Sprite.List;
import game.data.util.Data;
import graphics.gl00.Context;
import graphics.gl00.Drawable;
import graphics.shared.gui.Control.ControlEventDraw;
import graphics.shared.gui.Control.ControlEventWheel;
import graphics.shared.gui.GUI;
import graphics.shared.gui.Control.ControlEventClick;
import graphics.shared.gui.controls.Button;
import graphics.shared.gui.controls.Dropdown;
import graphics.shared.gui.controls.Dropdown.DropdownItem;
import graphics.shared.gui.controls.Label;
import graphics.shared.gui.controls.Picture;
import graphics.shared.gui.controls.Scrollbar;
import graphics.shared.gui.controls.Scrollbar.ControlEventScroll;
import graphics.shared.gui.controls.Scrollbar.Orientation;
import graphics.shared.gui.controls.Textbox;
import graphics.shared.gui.controls.Textbox.ControlEventChange;
import graphics.shared.gui.controls.compound.ScrollPanel;
import graphics.shared.gui.controls.compound.ScrollPanel.ControlEventButton;
import graphics.shared.gui.controls.compound.ScrollPanel.ControlEventSelect;
import graphics.shared.gui.controls.compound.ScrollPanel.ScrollPanelItem;

public class SpriteEditor extends GUI implements Editor {
  private Picture   _picWindow;
  private Button[]  _btnTab;
  private Picture[] _picTab;
  
  private Button    _btnClose;
  private Button    _btnSave;
  
  private ScrollPanel _splFrame;
  private Button      _btnFrameClone;
  private Label     _lblFrameLoc, _lblFrameFoot;
  private Textbox   _txtFrameX, _txtFrameY;
  private Textbox   _txtFrameW, _txtFrameH;
  private Textbox   _txtFrameFX, _txtFrameFY;
  private Dropdown  _drpSprite;
  private Picture   _picFrameSprite;
  private Picture   _picFrameSpriteBack;
  
  private Picture   _picAnim;
  private Label     _lblAnimNum;
  private Button    _btnAnimAdd;
  private Button    _btnAnimDel;
  private Button    _btnAnimClone;
  private Scrollbar _scrAnim;
  private Label     _lblAnimName;
  private Textbox   _txtAnimName;
  
  private Picture   _picList;
  private Label     _lblListNum;
  private Button    _btnListAdd;
  private Button    _btnListDel;
  private Scrollbar _scrList;
  private Label     _lblListFrame;
  private Scrollbar _scrListFrame;
  private Label     _lblListTime;
  private Scrollbar _scrListTime;
  
  private Label     _lblName;
  private Textbox   _txtName;
  private Label     _lblNote;
  private Textbox   _txtNote;
  private Label     _lblW, _lblH;
  private Textbox   _txtW, _txtH;
  
  private Drawable _frameLoc;
  private Drawable _frameFoot;
  
  private SpriteEditorSprite _sprite;
  
  private boolean _suspendUpdateList;
  
  private int _tab;
  private Sprite.Frame _frame;
  private int _anim;
  private int _list;
  
  public void load() {
    _picWindow = new Picture(this, true);
    _picWindow.setBackColour(new float[] {0.33f, 0.33f, 0.33f, 0.66f});
    _picWindow.setBorderColour(new float[] {0, 0, 0, 1});
    _picWindow.setWH(300, 300);
    
    _btnTab = new Button[3];
    
    ControlEventClick btnTabClick = new ControlEventClick() {
      public void event() {
        for(int i = 0; i < _btnTab.length; i++) {
          if(_btnTab[i] == getControl()) {
            setTab(i);
            return;
          }
        }
      }
    };
    
    for(int i = 0; i < _btnTab.length; i++) {
      _btnTab[i] = new Button(this);
      _btnTab[i].setXYWH(8 + i * 59, 8, 60, 20);
      _btnTab[i].addEventClickHandler(btnTabClick);
      _picWindow.Controls().add(_btnTab[i]);
    }
    
    _btnTab[0].setText("Frames");
    _btnTab[1].setText("Animations");
    _btnTab[2].setText("Settings");
    
    _picTab = new Picture[3];
    for(int i = 0; i < _picTab.length; i++) {
      _picTab[i] = new Picture(this);
      _picTab[i].setBackColour(new float[] {0.1f, 0.1f, 0.1f, 1});
      _picTab[i].setXYWH(8, _btnTab[i].getY() + _btnTab[i].getH(), 256, 256);
      _picTab[i].setVisible(false);
      _picWindow.Controls().add(_picTab[i]);
    }
    
    _btnClose = new Button(this);
    _btnClose.setText("Close");
    _btnClose.setY((_picTab[0].getY() - _btnClose.getH()) / 2);
    _btnClose.addEventClickHandler(new ControlEventClick() {
      public void event() {
        unload();
      }
    });
    
    _btnSave = new Button(this);
    _btnSave.setText("Save");
    _btnSave.setY(_btnClose.getY());
    _btnSave.addEventClickHandler(new ControlEventClick() {
      public void event() {
        save();
      }
    });
    
    _picWindow.Controls().add(_btnClose);
    _picWindow.Controls().add(_btnSave);
    
    _lblFrameLoc = new Label(this);
    _lblFrameLoc.setText("Location");
    _lblFrameLoc.setXY(8, 4);
    
    ControlEventChange textChange = new ControlEventChange() {
      public void event() {
        updateFrame();
      }
    };
    
    _txtFrameX = new Textbox(this);
    _txtFrameX.setXY(_lblFrameLoc.getX(), _lblFrameLoc.getY() + _lblFrameLoc.getH() + 3);
    _txtFrameX.setW(40);
    _txtFrameX.addEventChangeHandler(textChange);
    _txtFrameX.setNumeric(true);
    
    _txtFrameY = new Textbox(this);
    _txtFrameY.setXY(_txtFrameX.getX() + _txtFrameX.getW() + 8, _txtFrameX.getY());
    _txtFrameY.setW(40);
    _txtFrameY.addEventChangeHandler(textChange);
    _txtFrameY.setNumeric(true);
    
    _txtFrameW = new Textbox(this);
    _txtFrameW.setXY(_txtFrameY.getX() + _txtFrameY.getW() + 8, _txtFrameY.getY());
    _txtFrameW.setW(40);
    _txtFrameW.addEventChangeHandler(textChange);
    _txtFrameW.setNumeric(true);
    
    _txtFrameH = new Textbox(this);
    _txtFrameH.setXY(_txtFrameW.getX() + _txtFrameW.getW() + 8, _txtFrameW.getY());
    _txtFrameH.setW(40);
    _txtFrameH.addEventChangeHandler(textChange);
    _txtFrameH.setNumeric(true);
    
    _lblFrameFoot = new Label(this);
    _lblFrameFoot.setText("Foot");
    _lblFrameFoot.setXY(_lblFrameLoc.getX(), _txtFrameX.getY() + _txtFrameX.getH() + 8);
    
    _txtFrameFX = new Textbox(this);
    _txtFrameFX.setXY(_lblFrameFoot.getX(), _lblFrameFoot.getY() + _lblFrameFoot.getH() + 3);
    _txtFrameFX.setW(40);
    _txtFrameFX.addEventChangeHandler(textChange);
    _txtFrameFX.setNumeric(true);
    
    _txtFrameFY = new Textbox(this);
    _txtFrameFY.setXY(_txtFrameFX.getX() + _txtFrameFX.getW() + 8, _txtFrameFX.getY());
    _txtFrameFY.setW(40);
    _txtFrameFY.addEventChangeHandler(textChange);
    _txtFrameFY.setNumeric(true);
    
    _btnFrameClone = new Button(this);
    _btnFrameClone.setText("Clone");
    _btnFrameClone.addEventClickHandler(new ControlEventClick() {
      public void event() {
        cloneFrame();
      }
    });
    
    _splFrame = new ScrollPanel(this);
    _splFrame.setXYWH(4, 4, _txtFrameH.getX() + _txtFrameH.getW() + 8, 107);
    _splFrame.Buttons().add(_btnFrameClone);
    _splFrame.Controls().add(_lblFrameLoc);
    _splFrame.Controls().add(_lblFrameFoot);
    _splFrame.Controls().add(_txtFrameX);
    _splFrame.Controls().add(_txtFrameY);
    _splFrame.Controls().add(_txtFrameW);
    _splFrame.Controls().add(_txtFrameH);
    _splFrame.Controls().add(_txtFrameFX);
    _splFrame.Controls().add(_txtFrameFY);
    _splFrame.addEventButtonAddHandler(new ControlEventButton() {
      public void event() {
        addFrame();
      }
    });
    _splFrame.addEventButtonDelHandler(new ControlEventButton() {
      public void event() {
        delFrame();
      }
    });
    _splFrame.addEventSelect(new ControlEventSelect() {
      public void event(ScrollPanelItem item) {
        setFrame(item != null ? ((ScrollPanelFrame)item)._frame : null);
        _btnFrameClone.setEnabled(_splFrame.size() != 0);
      }
    });
    
    _drpSprite = new Dropdown(this);
    _drpSprite.setXY(_splFrame.getX(), _splFrame.getY() + _splFrame.getH() + 4);
    _drpSprite.addEventSelectHandler(new Dropdown.ControlEventSelect() {
      public void event(DropdownItem item) {
        setSprite(item.getText());
      }
    });
    
    _picFrameSprite = new Picture(this);
    _picFrameSprite.addEventDrawHandler(new ControlEventDraw() {
      public void event() {
        _frameLoc.draw();
        _frameFoot.draw();
      }
    });
    
    _picFrameSpriteBack = new Picture(this);
    _picFrameSpriteBack.setBackColour(new float[] {0.33f, 0.33f, 0.33f, 0.66f});
    _picFrameSpriteBack.setXY(_drpSprite.getX(), _drpSprite.getY() + _drpSprite.getH());
    _picFrameSpriteBack.Controls().add(_picFrameSprite);
    
    _picTab[0].Controls().add(_splFrame);
    _picTab[0].Controls().add(_drpSprite);
    _picTab[0].Controls().add(_picFrameSpriteBack);
    
    _lblAnimName = new Label(this);
    _lblAnimName.setText("Name");
    _lblAnimName.setXY(8, 4);
    
    ControlEventChange animChange = new ControlEventChange() {
      public void event() {
        updateAnim();
      }
    };
    
    _txtAnimName = new Textbox(this);
    _txtAnimName.setXY(_lblAnimName.getX(), _lblAnimName.getY() + _lblAnimName.getH() + 3);
    _txtAnimName.setW(160);
    _txtAnimName.addEventChangeHandler(animChange);
    
    ControlEventScroll listChange = new ControlEventScroll() {
      public void event(int delta) {
        updateList();
      }
    };
    
    _lblListFrame = new Label(this);
    _lblListFrame.setText("Frame: 0");
    _lblListFrame.setXY(4, 4);
    
    _scrListFrame = new Scrollbar(this);
    _scrListFrame.setXYWH(_lblListFrame.getX(), _lblListFrame.getY() + _lblListFrame.getH() + 4, 100, 16);
    _scrListFrame.setOrientation(Orientation.HORIZONTAL);
    _scrListFrame.addEventScrollHandler(listChange);
    
    _lblListTime = new Label(this);
    _lblListTime.setText("Time: 0 ms");
    _lblListTime.setXY(_scrListFrame.getX() + _scrListFrame.getW() + 4, _lblListFrame.getY());
    
    _scrListTime = new Scrollbar(this);
    _scrListTime.setXYWH(_lblListTime.getX(), _lblListTime.getY() + _lblListTime.getH() + 4, 100, 16);
    _scrListTime.setMin(1);
    _scrListTime.setMax(1000);
    _scrListTime.setOrientation(Orientation.HORIZONTAL);
    _scrListTime.addEventScrollHandler(listChange);
    
    _btnListAdd = new Button(this);
    _btnListAdd.setXY(20, _txtAnimName.getY() + _txtAnimName.getH() + 18);
    _btnListAdd.setText("Add");
    _btnListAdd.addEventClickHandler(new ControlEventClick() {
      public void event() {
        addList();
      }
    });
    
    _btnListDel = new Button(this);
    _btnListDel.setXY(_btnListAdd.getX() + _btnListAdd.getW(), _btnListAdd.getY());
    _btnListDel.setText("Del");
    _btnListDel.addEventClickHandler(new ControlEventClick() {
      public void event() {
        delList();
      }
    });
    
    _scrList = new Scrollbar(this);
    _scrList.setXYWH(4, _btnListAdd.getY() + _btnListAdd.getH(), 16, 64);
    _scrList.addEventScrollHandler(new ControlEventScroll() {
      public void event(int delta) {
        setList(_list + delta);
      }
    });
    
    _lblListNum = new Label(this);
    _lblListNum.setXY(_scrList.getX(), _btnListAdd.getY());
    _lblListNum.setText("0");
    
    _picList = new Picture(this);
    _picList.setBackColour(new float[] {0.33f, 0.33f, 0.33f, 0.66f});
    _picList.setXY(_scrList.getX() + _scrList.getW(), _scrList.getY());
    _picList.addEventMouseWheelHandler(new ControlEventWheel() {
      public void event(int delta) {
        _scrList.handleMouseWheel(delta);
      }
    });
    
    _picList.Controls().add(_lblListFrame);
    _picList.Controls().add(_scrListFrame);
    _picList.Controls().add(_lblListTime);
    _picList.Controls().add(_scrListTime);
    
    _btnAnimAdd = new Button(this);
    _btnAnimAdd.setXY(20, 4);
    _btnAnimAdd.setText("Add");
    _btnAnimAdd.addEventClickHandler(new ControlEventClick() {
      public void event() {
        addAnim();
      }
    });
    
    _btnAnimDel = new Button(this);
    _btnAnimDel.setXY(_btnAnimAdd.getX() + _btnAnimAdd.getW(), _btnAnimAdd.getY());
    _btnAnimDel.setText("Del");
    _btnAnimDel.addEventClickHandler(new ControlEventClick() {
      public void event() {
        delAnim();
      }
    });
    
    _btnAnimClone = new Button(this);
    _btnAnimClone.setXY(_btnAnimDel.getX() + _btnAnimDel.getW() + 4, _btnAnimDel.getY());
    _btnAnimClone.setText("Clone");
    _btnAnimClone.addEventClickHandler(new ControlEventClick() {
      public void event() {
        cloneAnim();
      }
    });
    
    _scrAnim = new Scrollbar(this);
    _scrAnim.setXYWH(4, _btnAnimAdd.getY() + _btnAnimAdd.getH(), 16, 64);
    _scrAnim.addEventScrollHandler(new ControlEventScroll() {
      public void event(int delta) {
        setAnim(_anim + delta);
      }
    });
    
    _lblAnimNum = new Label(this);
    _lblAnimNum.setXY(_scrAnim.getX(), _btnAnimAdd.getY());
    _lblAnimNum.setText("0");
    
    _picAnim = new Picture(this);
    _picAnim.setBackColour(new float[] {0.33f, 0.33f, 0.33f, 0.66f});
    _picAnim.setXY(_scrAnim.getX() + _scrAnim.getW(), _scrAnim.getY());
    _picAnim.addEventMouseWheelHandler(new ControlEventWheel() {
      public void event(int delta) {
        _scrAnim.handleMouseWheel(delta);
      }
    });
    
    _picAnim.Controls().add(_lblAnimName);
    _picAnim.Controls().add(_txtAnimName);
    _picAnim.Controls().add(_btnListAdd);
    _picAnim.Controls().add(_btnListDel);
    _picAnim.Controls().add(_lblListNum);
    _picAnim.Controls().add(_scrList);
    _picAnim.Controls().add(_picList);
    
    _picTab[1].Controls().add(_btnAnimAdd);
    _picTab[1].Controls().add(_btnAnimDel);
    _picTab[1].Controls().add(_btnAnimClone);
    _picTab[1].Controls().add(_lblAnimNum);
    _picTab[1].Controls().add(_scrAnim);
    _picTab[1].Controls().add(_picAnim);
    
    ControlEventChange change = new ControlEventChange() {
      public void event() {
        update();
      }
    };
    
    _lblName = new Label(this);
    _lblName.setText("Name");
    _lblName.setXY(8, 4);
    
    _txtName = new Textbox(this);
    _txtName.setXY(_lblName.getX(), _lblName.getY() + _lblName.getH() + 4);
    _txtName.addEventChangeHandler(change);
    
    _lblNote = new Label(this);
    _lblNote.setText("Notes");
    _lblNote.setXY(_txtName.getX(), _txtName.getY() + _txtName.getH() + 8);
    
    _txtNote = new Textbox(this);
    _txtNote.setXY(_lblNote.getX(), _lblNote.getY() + _lblNote.getH() + 4);
    _txtNote.addEventChangeHandler(change);
    
    _lblW = new Label(this);
    _lblW.setText("W");
    _lblW.setXY(_txtNote.getX(), _txtNote.getY() + _txtNote.getH() + 8);
    
    _txtW = new Textbox(this);
    _txtW.setXY(_lblW.getX(), _lblW.getY() + _lblW.getH() + 4);
    _txtW.setW(40);
    _txtW.addEventChangeHandler(change);
    _txtW.setNumeric(true);
    
    _lblH = new Label(this);
    _lblH.setText("H");
    _lblH.setXY(_txtW.getX() + _txtW.getW() + 8, _lblW.getY());
    
    _txtH = new Textbox(this);
    _txtH.setXY(_lblH.getX(), _txtW.getY());
    _txtH.setW(40);
    _txtH.addEventChangeHandler(change);
    _txtH.setNumeric(true);
    
    _picTab[2].Controls().add(_lblName);
    _picTab[2].Controls().add(_txtName);
    _picTab[2].Controls().add(_lblNote);
    _picTab[2].Controls().add(_txtNote);
    _picTab[2].Controls().add(_lblW);
    _picTab[2].Controls().add(_txtW);
    _picTab[2].Controls().add(_lblH);
    _picTab[2].Controls().add(_txtH);
    
    Controls().add(_picWindow);
    
    _frameLoc = Context.newDrawable();
    _frameLoc.setColour(new float[] {0, 1, 0, 1});
    
    _frameFoot = Context.newDrawable();
    _frameFoot.setColour(new float[] {1, 0, 0, 1});
    _frameFoot.setWH(16, 16);
    _frameFoot.createBorder();
    
    setTab(_tab);
    listSprites(new File("../gfx/textures/sprites/"), "../gfx/textures/sprites/");
  }
  
  public void destroy() {
    
  }
  
  public void resize() {
    _picFrameSpriteBack.setWH(_picFrameSprite.getW(), _picFrameSprite.getH());
    _splFrame.setW(_picFrameSpriteBack.getW());
    _picTab[0].setWH(_picFrameSpriteBack.getX() + _picFrameSpriteBack.getW() + 4, _picFrameSpriteBack.getY() + _picFrameSpriteBack.getH() + 4);
    _picWindow.setWH(_picTab[_tab].getW() + 16, _btnTab[_tab].getH() + _picTab[_tab].getH() + 16);
    _picWindow.setXY((_context.getW() - _picWindow.getW()) / 2, (_context.getH() - _picWindow.getH()) / 2);
    
    _btnClose.setX(_picWindow.getW() - _btnClose.getW() - 4);
    _btnSave.setX(_btnClose.getX() - _btnSave.getW() - 4);
    
    for(int i = 1; i < _picTab.length; i++) {
      _picTab[i].setWH(_picTab[0].getW(), _picTab[0].getH());
    }
    
    _picAnim.setWH(_picTab[1].getW() - _picAnim.getX() - 4, _picTab[1].getH() - _picAnim.getY() - 4);
    _picList.setWH(_picAnim.getW() - _picList.getX() - 4, _picAnim.getH() - _picList.getY() - 4);
  }
  
  public void unload() {
    if(_sprite.isChanged()) {
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
    System.out.println("Updating sprite " + _sprite.getFile());
    _sprite.update();
    _sprite.save();
  }
  
  private void listSprites(File dir, String path) {
    for(File f : dir.listFiles()) {
      if(f.isFile()) {
        String name = f.getPath().substring(f.getPath().indexOf(path) + path.length() + 1).replace('\\', '/');
        _drpSprite.add(new DropdownItem(name));
      }
    }
    
    for(File f : dir.listFiles()) {
      if(f.isDirectory()) {
        listSprites(f, path);
      }
    }
  }
  
  public void newData(String file) {
    editData(new Sprite(file));
  }
  
  public void editData(Data data) {
    push();
    
    _sprite = new SpriteEditorSprite((Sprite)data);
    if(_sprite._frame.size() == 0) addFrame();
    if(_sprite._anim .size() == 0) addAnim();
    
    int i = 0;
    for(DropdownItem item : _drpSprite) {
      if(item.getText().equals(_sprite.getTexture())) {
        _drpSprite.setSeletected(i);
        break;
      }
      i++;
    }
    
    for(Sprite.Frame f : _sprite._frame) {
      _splFrame.add(new ScrollPanelFrame(f));
    }
    
    _scrAnim .setMax(_sprite._anim .size() - 1);
    _scrListFrame.setMax(_splFrame.size());
    _picFrameSprite.setTexture(_textures.getTexture("sprites/" + _sprite.getTexture()));
    
    _txtName.setText(_sprite.getName());
    _txtNote.setText(_sprite.getNote());
    _txtW.setText(String.valueOf(_sprite.getW()));
    _txtH.setText(String.valueOf(_sprite.getH()));
    
    setAnim(0);
    
    resize();
  }
  
  private void setTab(int index) {
    _btnTab[_tab].setBackColour(new float[] {0.2f, 0.2f, 0.2f, 1});
    _picTab[_tab].setVisible(false);
    _btnTab[index].setBackColour(new float[] {0, 0, 0.8f, 1});
    _picTab[index].setVisible(true);
    _tab = index;
    
    resize();
  }
  
  private void setSprite(String sprite) {
    _sprite.setTexture(sprite);
    _picFrameSprite.setTexture(_textures.getTexture("sprites/" + _sprite.getTexture()));
    resize();
  }
  
  private void cloneFrame() {
    addFrame(_frame);
  }
  
  private void addFrame() { addFrame(null); }
  private void addFrame(Frame f) {
    f = f == null ? new Frame() : new Frame(f);
    _sprite._frame.add(f);
    _splFrame.add(new ScrollPanelFrame(f));
  }
  
  private void delFrame() {
    _sprite._frame.remove(((ScrollPanelFrame)_splFrame.getItem())._frame);
    _splFrame.remove();
  }
  
  private void setFrame(Sprite.Frame frame) {
    _frame = frame;
    
    if(_frame != null) {
      _txtFrameX.setText(String.valueOf(_frame._x));
      _txtFrameY.setText(String.valueOf(_frame._y));
      _txtFrameW.setText(String.valueOf(_frame._w));
      _txtFrameH.setText(String.valueOf(_frame._h));
      _txtFrameFX.setText(String.valueOf(_frame._fx));
      _txtFrameFY.setText(String.valueOf(_frame._fy));
      
      updateFrame();
    }
  }
  
  private void cloneAnim() {
    addAnim(_sprite._anim.get(_anim));
  }
  
  private void addAnim() { addAnim(null); }
  private void addAnim(Anim a) {
    a = a == null ? new Anim() : new Anim(a);
    _sprite._anim.add(a);
    _scrAnim.setMax(_sprite._anim.size() - 1);
    setAnim(_scrAnim.getMax());
  }
  
  private void delAnim() {
    _sprite._anim.remove(_anim);
    _scrAnim.setMax(_sprite._anim.size() - 1);
    setAnim(_anim > 0 ? _anim - 1 : _anim);
  }
  
  private void setAnim(int anim) {
    _scrAnim.setVal(anim);
    _anim = anim;
    
    Anim a = _sprite._anim.get(_anim);
    
    _lblAnimNum.setText(String.valueOf(_anim));
    _txtAnimName.setText(a._name);
    
    _scrList.setMax(a._list.size() - 1);
    if(a._list.size() == 0) addList();
    setList(0);
  }
  
  private void addList() {
    _sprite._anim.get(_anim)._list.add(new List());
    _scrList.setMax(_sprite._anim.get(_anim)._list.size() - 1);
    setList(_scrList.getMax());
  }
  
  private void delList() {
    _sprite._anim.get(_anim)._list.remove(_list);
    _scrList.setMax(_sprite._anim.get(_anim)._list.size() - 1);
    setList(_list > 0 ? _list - 1 : _list);
  }
  
  private void setList(int list) {
    _scrList.setVal(list);
    _list = list;
    
    List l = _sprite._anim.get(_anim)._list.get(_list);
    
    _lblListNum.setText(String.valueOf(_list));
    
    _suspendUpdateList = true;
    _scrListFrame.setVal(l._frame);
    _scrListTime.setVal(l._time / 10);
    _suspendUpdateList = false;
    updateList();
  }
  
  private void update() {
    _sprite.setName(_txtName.getText());
    _sprite.setNote(_txtNote.getText());
    _sprite.setW(Integer.parseInt(_txtW.getText()));
    _sprite.setH(Integer.parseInt(_txtH.getText()));
  }
  
  private void updateFrame() {
    _frame._x  = Integer.parseInt(_txtFrameX.getText());
    _frame._y  = Integer.parseInt(_txtFrameY.getText());
    _frame._w  = Integer.parseInt(_txtFrameW.getText());
    _frame._h  = Integer.parseInt(_txtFrameH.getText());
    _frame._fx = Integer.parseInt(_txtFrameFX.getText());
    _frame._fy = Integer.parseInt(_txtFrameFY.getText());
    
    _frameLoc.setWH(_frame._w, _frame._h);
    _frameLoc.createBorder();
    _frameLoc.setXY(_frame._x, _frame._y);
    _frameFoot.setXY(_frame._x + _frame._fx - 8, _frame._y + _frame._h - _frame._fy - 8);
  }
  
  private void updateAnim() {
    Anim a = _sprite._anim.get(_anim);
    a._name = _txtAnimName.getText();
  }
  
  private void updateList() {
    if(_suspendUpdateList) return;
    
    List l = _sprite._anim.get(_anim)._list.get(_list);
    l._frame = _scrListFrame.getVal();
    l._time  = _scrListTime.getVal() * 10;
    
    _lblListFrame.setText("Frame: " + l._frame);
    _lblListTime.setText("Time: " + l._time + " ms");
  }
  
  public boolean handleKeyDown ( int key) { return true; }
  public boolean handleKeyUp   ( int key) { return true; }
  public boolean handleCharDown(char key) { return true; }
  
  public static class ScrollPanelFrame extends ScrollPanelItem {
    Sprite.Frame _frame;
    
    public ScrollPanelFrame(Sprite.Frame frame) {
      _frame = frame;
    }
    
    public Sprite.Frame getFrame() {
      return _frame;
    }
  }
}