package game.gui.editors;

import game.data.Sprite;
import game.data.Sprite.Frame;
import game.data.util.Data;
import graphics.gl00.Context;
import graphics.gl00.Drawable;
import graphics.shared.gui.Control.ControlEventDraw;
import graphics.shared.gui.GUI;
import graphics.shared.gui.Control.ControlEventClick;
import graphics.shared.gui.Control.ControlEventHover;
import graphics.shared.gui.controls.Button;
import graphics.shared.gui.controls.Label;
import graphics.shared.gui.controls.Picture;
import graphics.shared.gui.controls.Scrollbar;
import graphics.shared.gui.controls.Scrollbar.ControlEventScroll;
import graphics.shared.gui.controls.Textbox;
import graphics.shared.gui.controls.Textbox.ControlEventChange;

public class SpriteEditor extends GUI implements Editor {
  private Picture _picWindow;
  private Button[]  _btnTab;
  private Button    _btnTabSel;
  private Picture[] _picTab;
  
  private Picture _picFrameLoc;
  private Label _lblFrameNum;
  private Button _btnFrameAdd;
  private Button _btnFrameDel;
  private Scrollbar _scrFrame;
  private Label _lblFrameLoc, _lblFrameFoot;
  private Textbox _txtFrameX, _txtFrameY;
  private Textbox _txtFrameW, _txtFrameH;
  private Textbox _txtFrameFX, _txtFrameFY;
  private Picture _picFrameSprite;
  private Picture _picFrameSpriteBack;
  
  private Drawable _frameLoc;
  private Drawable _frameFoot;
  
  private SpriteEditorSprite _sprite;
  
  private int _tab;
  private int _frame;
  
  public void load() {
    _picWindow = new Picture(this, true);
    _picWindow.setBackColour(new float[] {0.33f, 0.33f, 0.33f, 0.66f});
    _picWindow.setBorderColour(new float[] {0, 0, 0, 1});
    _picWindow.setWH(300, 300);
    
    _btnTab = new Button[2];
    
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
    
    ControlEventHover btnTabEnter = new ControlEventHover() {
      public void event() {
        if(getControl() != _btnTabSel) {
          getControl().setBackColour(new float[] {0.3f, 0.3f, 0.3f, 1});
        }
      }
    };
    
    ControlEventHover btnTabLeave = new ControlEventHover() {
      public void event() {
        if(getControl() != _btnTabSel) {
          getControl().setBackColour(new float[] {0.2f, 0.2f, 0.2f, 1});
        }
      }
    };
    
    for(int i = 0; i < _btnTab.length; i++) {
      _btnTab[i] = new Button(this);
      _btnTab[i].setBackColour(new float[] {0.2f, 0.2f, 0.2f, 1});
      _btnTab[i].setXYWH(8 + i * 59, 8, 60, 20);
      _btnTab[i].addEventClickHandler(btnTabClick);
      _btnTab[i].addEventMouseEnterHandler(btnTabEnter);
      _btnTab[i].addEventMouseLeaveHandler(btnTabLeave);
      _picWindow.Controls().add(_btnTab[i]);
    }
    
    _btnTab[0].setText("Frames");
    _btnTab[1].setText("Animations");
    
    _picTab = new Picture[2];
    for(int i = 0; i < _picTab.length; i++) {
      _picTab[i] = new Picture(this);
      _picTab[i].setBackColour(new float[] {0.1f, 0.1f, 0.1f, 1});
      _picTab[i].setXYWH(8, _btnTab[i].getY() + _btnTab[i].getH(), 256, 256);
      _picTab[i].setVisible(false);
      _picWindow.Controls().add(_picTab[i]);
    }
    
    _lblFrameLoc = new Label(this);
    _lblFrameLoc.setText("Location");
    _lblFrameLoc.setForeColour(new float[] {1, 1, 1, 1});
    _lblFrameLoc.setXY(8, 4);
    
    ControlEventChange textChange = new ControlEventChange() {
      public void event() {
        updateLoc();
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
    _lblFrameFoot.setForeColour(new float[] {1, 1, 1, 1});
    _lblFrameFoot.setXY(_lblFrameLoc.getX(), _txtFrameX.getY() + _txtFrameX.getH() + 8);
    
    _txtFrameFX = new Textbox(this);
    _txtFrameFX.setXY(_lblFrameFoot.getX(), _lblFrameFoot.getY() + _lblFrameFoot.getH() + 3);
    _txtFrameFX.setW(40);
    _txtFrameFX.addEventChangeHandler(textChange);
    
    _txtFrameFY = new Textbox(this);
    _txtFrameFY.setXY(_txtFrameFX.getX() + _txtFrameFX.getW() + 8, _txtFrameFX.getY());
    _txtFrameFY.setW(40);
    _txtFrameFY.addEventChangeHandler(textChange);
    
    _lblFrameNum = new Label(this);
    _lblFrameNum.setForeColour(new float[] {1, 1, 1, 1});
    _lblFrameNum.setXY(4, 4);
    
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
        setFrame(_scrFrame.getMax() - _scrFrame.getVal());
      }
    });
    
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
    
    _picFrameSprite = new Picture(this);
    _picFrameSprite.addEventDrawHandler(new ControlEventDraw() {
      public void event() {
        _frameLoc.draw();
        _frameFoot.draw();
      }
    });
    
    _picFrameSpriteBack = new Picture(this);
    _picFrameSpriteBack.setBackColour(new float[] {0.33f, 0.33f, 0.33f, 0.66f});
    _picFrameSpriteBack.setXY(_picFrameLoc.getX(), _picFrameLoc.getY() + _picFrameLoc.getH() + 4);
    _picFrameSpriteBack.Controls().add(_picFrameSprite);

    _picTab[0].Controls().add(_btnFrameAdd);
    _picTab[0].Controls().add(_btnFrameDel);
    _picTab[0].Controls().add(_lblFrameNum);
    _picTab[0].Controls().add(_scrFrame);
    _picTab[0].Controls().add(_picFrameLoc);
    _picTab[0].Controls().add(_picFrameSpriteBack);
    
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
    _picFrameLoc.setW(_picFrameSpriteBack.getW());
    _picTab[0].setWH(_picFrameSpriteBack.getX() + _picFrameSpriteBack.getW() + 4, _picFrameSpriteBack.getY() + _picFrameSpriteBack.getH() + 4);
    _picWindow.setWH(_picTab[_tab].getW() + 16, _btnTab[_tab].getH() + _picTab[_tab].getH() + 16);
    _picWindow.setXY((_context.getW() - _picWindow.getW()) / 2, (_context.getH() - _picWindow.getH()) / 2);
  }
  
  public void newData() {
    push();
    
    editData(new Sprite());
  }
  
  public void editData(Data data) {
    _sprite = new SpriteEditorSprite((Sprite)data);
    if(_sprite._frame.size() == 0) addFrame();
    
    _scrFrame.setMax(_sprite._frame.size() - 1);
    _scrFrame.setVal(_scrFrame.getMax());
    
    _picFrameSprite.setTexture(_textures.getTexture("sprites/" + _sprite.getTexture()));
    
    setFrame(0);
    
    resize();
  }
  
  private void setTab(int index) {
    _btnTabSel = _btnTab[index];
    _btnTab[_tab].setBackColour(new float[] {0.2f, 0.2f, 0.2f, 1});
    _picTab[_tab].setVisible(false);
    _btnTab[index].setBackColour(new float[] {0, 0, 0.8f, 1});
    _picTab[index].setVisible(true);
    _tab = index;
    
    resize();
  }
  
  private void addFrame() {
    _sprite._frame.add(new Frame());
    _scrFrame.setMax(_scrFrame.getMax() + 1);
    setFrame(_scrFrame.getMax());
  }
  
  private void delFrame() {
    _sprite._frame.remove(_frame);
    _scrFrame.setMax(_scrFrame.getMax() - 1);
    
    setFrame(_frame > 0 ? _frame - 1 : _frame);
  }
  
  private void setFrame(int frame) {
    _scrFrame.setVal(_scrFrame.getMax() - frame);
    
    _frame = frame;
    _lblFrameNum.setText(String.valueOf(frame));
    _txtFrameX.setText(String.valueOf(_sprite._frame.get(frame)._x));
    _txtFrameY.setText(String.valueOf(_sprite._frame.get(frame)._y));
    _txtFrameW.setText(String.valueOf(_sprite._frame.get(frame)._w));
    _txtFrameH.setText(String.valueOf(_sprite._frame.get(frame)._h));
    _txtFrameFX.setText(String.valueOf(_sprite._frame.get(frame)._fx));
    _txtFrameFY.setText(String.valueOf(_sprite._frame.get(frame)._fy));
    
    updateLoc();
  }
  
  private void updateLoc() {
    _frameLoc.setWH(_sprite._frame.get(_frame)._w, _sprite._frame.get(_frame)._h);
    _frameLoc.createBorder();
    _frameLoc.setXY(_sprite._frame.get(_frame)._x, _sprite._frame.get(_frame)._y);
    _frameFoot.setXY(_sprite._frame.get(_frame)._x + _sprite._frame.get(_frame)._fx - 8, _sprite._frame.get(_frame)._y + _sprite._frame.get(_frame)._h - _sprite._frame.get(_frame)._fy - 8);
  }
}