package game.graphics.gui.editors;

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
import graphics.shared.gui.controls.Label;
import graphics.shared.gui.controls.Picture;
import graphics.shared.gui.controls.Scrollbar;
import graphics.shared.gui.controls.Scrollbar.ControlEventScroll;
import graphics.shared.gui.controls.Scrollbar.Orientation;
import graphics.shared.gui.controls.Textbox;
import graphics.shared.gui.controls.Textbox.ControlEventChange;

public class SpriteEditor extends GUI implements Editor {
  private Picture   _picWindow;
  private Button[]  _btnTab;
  private Picture[] _picTab;
  
  private Picture   _picFrameLoc;
  private Label     _lblFrameNum;
  private Button    _btnFrameAdd;
  private Button    _btnFrameDel;
  private Scrollbar _scrFrame;
  private Label     _lblFrameLoc, _lblFrameFoot;
  private Textbox   _txtFrameX, _txtFrameY;
  private Textbox   _txtFrameW, _txtFrameH;
  private Textbox   _txtFrameFX, _txtFrameFY;
  private Picture   _picFrameSprite;
  private Picture   _picFrameSpriteBack;
  
  private Picture   _picAnim;
  private Label     _lblAnimNum;
  private Button    _btnAnimAdd;
  private Button    _btnAnimDel;
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
  private int _frame;
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
      _btnTab[i].setBackColour(new float[] {0.2f, 0.2f, 0.2f, 1});
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
    
    _txtFrameY = new Textbox(this);
    _txtFrameY.setXY(_txtFrameX.getX() + _txtFrameX.getW() + 8, _txtFrameX.getY());
    _txtFrameY.setW(40);
    _txtFrameY.addEventChangeHandler(textChange);
    
    _txtFrameW = new Textbox(this);
    _txtFrameW.setXY(_txtFrameY.getX() + _txtFrameY.getW() + 8, _txtFrameY.getY());
    _txtFrameW.setW(40);
    _txtFrameW.addEventChangeHandler(textChange);
    
    _txtFrameH = new Textbox(this);
    _txtFrameH.setXY(_txtFrameW.getX() + _txtFrameW.getW() + 8, _txtFrameW.getY());
    _txtFrameH.setW(40);
    _txtFrameH.addEventChangeHandler(textChange);
    
    _lblFrameFoot = new Label(this);
    _lblFrameFoot.setText("Foot");
    _lblFrameFoot.setXY(_lblFrameLoc.getX(), _txtFrameX.getY() + _txtFrameX.getH() + 8);
    
    _txtFrameFX = new Textbox(this);
    _txtFrameFX.setXY(_lblFrameFoot.getX(), _lblFrameFoot.getY() + _lblFrameFoot.getH() + 3);
    _txtFrameFX.setW(40);
    _txtFrameFX.addEventChangeHandler(textChange);
    
    _txtFrameFY = new Textbox(this);
    _txtFrameFY.setXY(_txtFrameFX.getX() + _txtFrameFX.getW() + 8, _txtFrameFX.getY());
    _txtFrameFY.setW(40);
    _txtFrameFY.addEventChangeHandler(textChange);
    
    _btnFrameAdd = new Button(this);
    _btnFrameAdd.setXY(20, 4);
    _btnFrameAdd.setText("Add");
    _btnFrameAdd.addEventClickHandler(new ControlEventClick() {
      public void event() {
        addFrame();
      }
    });
    
    _btnFrameDel = new Button(this);
    _btnFrameDel.setXY(_btnFrameAdd.getX() + _btnFrameAdd.getW(), _btnFrameAdd.getY());
    _btnFrameDel.setText("Del");
    _btnFrameDel.addEventClickHandler(new ControlEventClick() {
      public void event() {
        delFrame();
      }
    });
    
    _scrFrame = new Scrollbar(this);
    _scrFrame.setXYWH(4, _btnFrameAdd.getY() + _btnFrameAdd.getH(), 16, _txtFrameFX.getY() + _txtFrameFX.getH() + 8);
    _scrFrame.addEventScrollHandler(new ControlEventScroll() {
      public void event(int delta) {
        setFrame(_frame + delta);
      }
    });
    
    _lblFrameNum = new Label(this);
    _lblFrameNum.setXY(_scrFrame.getX(), _btnFrameAdd.getY());
    _lblFrameNum.setText("0");
    
    _picFrameLoc = new Picture(this);
    _picFrameLoc.setBackColour(new float[] {0.33f, 0.33f, 0.33f, 0.66f});
    _picFrameLoc.setXYWH(_scrFrame.getX() + _scrFrame.getW(), _scrFrame.getY(), _txtFrameH.getX() + _txtFrameH.getW() + 8, _scrFrame.getH());
    _picFrameLoc.Controls().add(_lblFrameLoc);
    _picFrameLoc.Controls().add(_lblFrameFoot);
    _picFrameLoc.Controls().add(_txtFrameX);
    _picFrameLoc.Controls().add(_txtFrameY);
    _picFrameLoc.Controls().add(_txtFrameW);
    _picFrameLoc.Controls().add(_txtFrameH);
    _picFrameLoc.Controls().add(_txtFrameFX);
    _picFrameLoc.Controls().add(_txtFrameFY);
    _picFrameLoc.addEventMouseWheelHandler(new ControlEventWheel() {
      public void event(int delta) {
        while(delta > 0) {
          delta -= 120;
          _scrFrame.setVal(_scrFrame.getVal() - 1);
        }
        
        while(delta < 0) {
          delta += 120;
          _scrFrame.setVal(_scrFrame.getVal() + 1);
        }
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
    _picFrameSpriteBack.setXY(_scrFrame.getX(), _picFrameLoc.getY() + _picFrameLoc.getH() + 4);
    _picFrameSpriteBack.Controls().add(_picFrameSprite);
    
    _picTab[0].Controls().add(_btnFrameAdd);
    _picTab[0].Controls().add(_btnFrameDel);
    _picTab[0].Controls().add(_lblFrameNum);
    _picTab[0].Controls().add(_scrFrame);
    _picTab[0].Controls().add(_picFrameLoc);
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
        while(delta > 0) {
          delta -= 120;
          _scrList.setVal(_scrList.getVal() - 1);
        }
        
        while(delta < 0) {
          delta += 120;
          _scrList.setVal(_scrList.getVal() + 1);
        }
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
        while(delta > 0) {
          delta -= 120;
          _scrAnim.setVal(_scrAnim.getVal() - 1);
        }
        
        while(delta < 0) {
          delta += 120;
          _scrAnim.setVal(_scrAnim.getVal() + 1);
        }
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
    _picTab[1].Controls().add(_lblAnimNum);
    _picTab[1].Controls().add(_scrAnim);
    _picTab[1].Controls().add(_picAnim);
    
    _lblName = new Label(this);
    _lblName.setText("Name");
    _lblName.setXY(4, 4);
    
    _txtName = new Textbox(this);
    _txtName.setXY(_lblName.getX(), _lblName.getY() + _lblName.getH() + 4);
    
    _lblNote = new Label(this);
    _lblNote.setText("Notes");
    _lblNote.setXY(_txtName.getX(), _txtName.getY() + _txtName.getH() + 8);
    
    _txtNote = new Textbox(this);
    _txtNote.setXY(_lblNote.getX(), _lblNote.getY() + _lblNote.getH() + 4);
    
    _lblW = new Label(this);
    _lblW.setText("W");
    _lblW.setXY(_txtNote.getX(), _txtNote.getY() + _txtNote.getH() + 8);
    
    _txtW = new Textbox(this);
    _txtW.setXY(_lblW.getX(), _lblW.getY() + _lblW.getH() + 4);
    _txtW.setW(40);
    
    _lblH = new Label(this);
    _lblH.setText("H");
    _lblH.setXY(_txtW.getX() + _txtW.getW() + 8, _lblW.getY());
    
    _txtH = new Textbox(this);
    _txtH.setXY(_lblH.getX(), _txtW.getY());
    _txtH.setW(40);
    
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
  }
  
  public void destroy() {
    
  }
  
  public void resize() {
    _picFrameSpriteBack.setWH(_picFrameSprite.getW(), _picFrameSprite.getH());
    _picFrameLoc.setW(_picFrameSpriteBack.getW() - _scrFrame.getW());
    _picTab[0].setWH(_picFrameSpriteBack.getX() + _picFrameSpriteBack.getW() + 4, _picFrameSpriteBack.getY() + _picFrameSpriteBack.getH() + 4);
    _picWindow.setWH(_picTab[_tab].getW() + 16, _btnTab[_tab].getH() + _picTab[_tab].getH() + 16);
    _picWindow.setXY((_context.getW() - _picWindow.getW()) / 2, (_context.getH() - _picWindow.getH()) / 2);
    
    _picTab[1].setWH(_picTab[0].getW(), _picTab[0].getH());
    _picAnim.setWH(_picTab[1].getW() - _picAnim.getX() - 4, _picTab[1].getH() - _picAnim.getY() - 4);
    _picList.setWH(_picAnim.getW() - _picList.getX() - 4, _picAnim.getH() - _picList.getY() - 4);
  }
  
  public void newData() {
    push();
    
    editData(new Sprite());
  }
  
  public void editData(Data data) {
    _sprite = new SpriteEditorSprite((Sprite)data);
    if(_sprite._frame.size() == 0) addFrame();
    if(_sprite._anim .size() == 0) addAnim();
    
    _scrFrame.setMax(_sprite._frame.size() - 1);
    _scrAnim .setMax(_sprite._anim .size() - 1);
    _scrListFrame.setMax(_scrFrame.getMax());
    _picFrameSprite.setTexture(_textures.getTexture("sprites/" + _sprite.getTexture()));
    
    _txtName.setText(_sprite.getName());
    _txtNote.setText(_sprite.getNote());
    _txtW.setText(String.valueOf(_sprite.getW()));
    _txtH.setText(String.valueOf(_sprite.getH()));
    
    setFrame(0);
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
  
  private void addFrame() {
    _sprite._frame.add(new Frame());
    _scrFrame.setMax(_sprite._frame.size() - 1);
    _scrListFrame.setMax(_scrFrame.getMax());
    setFrame(_scrFrame.getMax());
  }
  
  private void delFrame() {
    _sprite._frame.remove(_frame);
    _scrFrame.setMax(_sprite._frame.size() - 1);
    _scrListFrame.setMax(_scrFrame.getMax());
    setFrame(_frame > 0 ? _frame - 1 : _frame);
  }
  
  private void setFrame(int frame) {
    _scrFrame.setVal(frame);
    _frame = frame;
    
    Frame f = _sprite._frame.get(_frame);
    
    _lblFrameNum.setText(String.valueOf(_frame));
    _txtFrameX.setText(String.valueOf(f._x));
    _txtFrameY.setText(String.valueOf(f._y));
    _txtFrameW.setText(String.valueOf(f._w));
    _txtFrameH.setText(String.valueOf(f._h));
    _txtFrameFX.setText(String.valueOf(f._fx));
    _txtFrameFY.setText(String.valueOf(f._fy));
    
    updateFrame();
  }
  
  private void addAnim() {
    _sprite._anim.add(new Anim());
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
    _suspendUpdateList = false;
    _scrListTime.setVal(l._time / 10);
  }
  
  private void updateFrame() {
    Frame f = _sprite._frame.get(_frame);
    f._x  = Integer.parseInt(_txtFrameX.getText());
    f._y  = Integer.parseInt(_txtFrameY.getText());
    f._w  = Integer.parseInt(_txtFrameW.getText());
    f._h  = Integer.parseInt(_txtFrameH.getText());
    f._fx = Integer.parseInt(_txtFrameFX.getText());
    f._fy = Integer.parseInt(_txtFrameFY.getText());
    
    _frameLoc.setWH(f._w, f._h);
    _frameLoc.createBorder();
    _frameLoc.setXY(f._x, f._y);
    _frameFoot.setXY(f._x + f._fx - 8, f._y + f._h - f._fy - 8);
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
}