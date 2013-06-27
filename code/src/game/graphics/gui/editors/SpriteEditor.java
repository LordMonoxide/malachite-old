package game.graphics.gui.editors;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import game.Game;
import game.data.Sprite;
import game.data.Sprite.Anim;
import game.data.Sprite.Frame;
import game.data.Sprite.List;
import game.data.util.GameData;
import game.network.packet.editors.EditorSave;
import graphics.gl00.Context;
import graphics.gl00.Drawable;
import graphics.shared.gui.Control;
import graphics.shared.gui.GUI;
import graphics.shared.gui.controls.Button;
import graphics.shared.gui.controls.Dropdown;
import graphics.shared.gui.controls.Label;
import graphics.shared.gui.controls.Picture;
import graphics.shared.gui.controls.Scrollbar;
import graphics.shared.gui.controls.Scrollbar.Orientation;
import graphics.shared.gui.controls.Textbox;
import graphics.shared.gui.controls.compound.ScrollPanel;
import graphics.shared.gui.controls.compound.Window;

public class SpriteEditor extends GUI implements Editor {
  private Window    _wndEditor;
  
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
  
  private Button    _btnScriptCopy;
  private Button    _btnScriptPaste;
  
  private Drawable _frameLoc;
  private Drawable _frameFoot;
  
  private SpriteEditorSprite _sprite;
  
  private boolean _suspendUpdateList;
  
  private Sprite.Frame _frame;
  private int _anim;
  private int _list;
  
  protected void load() {
    _wndEditor = new Window(this);
    _wndEditor.setWH(300, 300);
    _wndEditor.setText("Sprite Editor");
    _wndEditor.addTab("Frames");
    _wndEditor.addTab("Animations");
    _wndEditor.addTab("Info");
    _wndEditor.addTab("Scripts");
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
    
    _lblFrameLoc = new Label(this);
    _lblFrameLoc.setText("Location");
    _lblFrameLoc.setXY(8, 4);
    
    Textbox.Events.Change textChange = new Textbox.Events.Change() {
      public void change() {
        updateFrame();
      }
    };
    
    _txtFrameX = new Textbox(this);
    _txtFrameX.setXY(_lblFrameLoc.getX(), _lblFrameLoc.getY() + _lblFrameLoc.getH() + 3);
    _txtFrameX.setW(40);
    _txtFrameX.events().addChangeHandler(textChange);
    _txtFrameX.setNumeric(true);
    
    _txtFrameY = new Textbox(this);
    _txtFrameY.setXY(_txtFrameX.getX() + _txtFrameX.getW() + 8, _txtFrameX.getY());
    _txtFrameY.setW(40);
    _txtFrameY.events().addChangeHandler(textChange);
    _txtFrameY.setNumeric(true);
    
    _txtFrameW = new Textbox(this);
    _txtFrameW.setXY(_txtFrameY.getX() + _txtFrameY.getW() + 8, _txtFrameY.getY());
    _txtFrameW.setW(40);
    _txtFrameW.events().addChangeHandler(textChange);
    _txtFrameW.setNumeric(true);
    
    _txtFrameH = new Textbox(this);
    _txtFrameH.setXY(_txtFrameW.getX() + _txtFrameW.getW() + 8, _txtFrameW.getY());
    _txtFrameH.setW(40);
    _txtFrameH.events().addChangeHandler(textChange);
    _txtFrameH.setNumeric(true);
    
    _lblFrameFoot = new Label(this);
    _lblFrameFoot.setText("Foot");
    _lblFrameFoot.setXY(_lblFrameLoc.getX(), _txtFrameX.getY() + _txtFrameX.getH() + 8);
    
    _txtFrameFX = new Textbox(this);
    _txtFrameFX.setXY(_lblFrameFoot.getX(), _lblFrameFoot.getY() + _lblFrameFoot.getH() + 3);
    _txtFrameFX.setW(40);
    _txtFrameFX.events().addChangeHandler(textChange);
    _txtFrameFX.setNumeric(true);
    
    _txtFrameFY = new Textbox(this);
    _txtFrameFY.setXY(_txtFrameFX.getX() + _txtFrameFX.getW() + 8, _txtFrameFX.getY());
    _txtFrameFY.setW(40);
    _txtFrameFY.events().addChangeHandler(textChange);
    _txtFrameFY.setNumeric(true);
    
    _btnFrameClone = new Button(this);
    _btnFrameClone.setText("Clone");
    _btnFrameClone.events().addClickHandler(new Control.Events.Click() {
      public void click() { cloneFrame(); }
      public void clickDbl() { click(); }
    });
    
    _splFrame = new ScrollPanel(this);
    _splFrame.setXYWH(4, 4, _txtFrameH.getX() + _txtFrameH.getW() + 8, 107);
    _splFrame.Buttons().add(_btnFrameClone);
    _splFrame.controls().add(_lblFrameLoc);
    _splFrame.controls().add(_lblFrameFoot);
    _splFrame.controls().add(_txtFrameX);
    _splFrame.controls().add(_txtFrameY);
    _splFrame.controls().add(_txtFrameW);
    _splFrame.controls().add(_txtFrameH);
    _splFrame.controls().add(_txtFrameFX);
    _splFrame.controls().add(_txtFrameFY);
    _splFrame.events().onButtonAdd(new ScrollPanel.Events.Button() {
      public void event() {
        addFrame();
      }
    });
    _splFrame.events().onButtonDel(new ScrollPanel.Events.Button() {
      public void event() {
        delFrame();
      }
    });
    _splFrame.events().onSelect(new ScrollPanel.Events.Select() {
      public void event(ScrollPanel.Item item) {
        setFrame(item != null ? ((ScrollPanelFrame)item)._frame : null);
        _btnFrameClone.setEnabled(_splFrame.size() != 0);
      }
    });
    
    _drpSprite = new Dropdown(this);
    _drpSprite.setXY(_splFrame.getX(), _splFrame.getY() + _splFrame.getH() + 4);
    _drpSprite.events().addSelectHandler(new Dropdown.Events.Select() {
      public void select(Dropdown.Item item) {
        setSprite(item.getText());
      }
    });
    
    _picFrameSprite = new Picture(this);
    _picFrameSprite.events().addDrawHandler(new Control.Events.Draw() {
      public void draw() {
        _frameLoc.draw();
        _frameFoot.draw();
      }
    });
    
    _picFrameSpriteBack = new Picture(this);
    _picFrameSpriteBack.setBackColour(new float[] {0.33f, 0.33f, 0.33f, 0.66f});
    _picFrameSpriteBack.setXY(_drpSprite.getX(), _drpSprite.getY() + _drpSprite.getH());
    _picFrameSpriteBack.controls().add(_picFrameSprite);
    
    _wndEditor.controls(0).add(_splFrame);
    _wndEditor.controls(0).add(_drpSprite);
    _wndEditor.controls(0).add(_picFrameSpriteBack);
    
    _lblAnimName = new Label(this);
    _lblAnimName.setText("Name");
    _lblAnimName.setXY(8, 4);
    
    Textbox.Events.Change animChange = new Textbox.Events.Change() {
      public void change() {
        updateAnim();
      }
    };
    
    _txtAnimName = new Textbox(this);
    _txtAnimName.setXY(_lblAnimName.getX(), _lblAnimName.getY() + _lblAnimName.getH() + 3);
    _txtAnimName.setW(160);
    _txtAnimName.events().addChangeHandler(animChange);
    
    Scrollbar.Events.Change listChange = new Scrollbar.Events.Change() {
      public void change(int delta) { updateList(); }
    };
    
    _lblListFrame = new Label(this);
    _lblListFrame.setText("Frame: 0");
    _lblListFrame.setXY(4, 4);
    
    _scrListFrame = new Scrollbar(this);
    _scrListFrame.setXYWH(_lblListFrame.getX(), _lblListFrame.getY() + _lblListFrame.getH() + 4, 100, 16);
    _scrListFrame.setOrientation(Orientation.HORIZONTAL);
    _scrListFrame.events().addChangeHandler(listChange);
    
    _lblListTime = new Label(this);
    _lblListTime.setText("Time: 0 ms");
    _lblListTime.setXY(_scrListFrame.getX() + _scrListFrame.getW() + 4, _lblListFrame.getY());
    
    _scrListTime = new Scrollbar(this);
    _scrListTime.setXYWH(_lblListTime.getX(), _lblListTime.getY() + _lblListTime.getH() + 4, 100, 16);
    _scrListTime.setMin(1);
    _scrListTime.setMax(1000);
    _scrListTime.setOrientation(Orientation.HORIZONTAL);
    _scrListTime.events().addChangeHandler(listChange);
    
    _btnListAdd = new Button(this);
    _btnListAdd.setXY(20, _txtAnimName.getY() + _txtAnimName.getH() + 18);
    _btnListAdd.setText("Add");
    _btnListAdd.events().addClickHandler(new Control.Events.Click() {
      public void click() { addList(); }
      public void clickDbl() { click(); }
    });
    
    _btnListDel = new Button(this);
    _btnListDel.setXY(_btnListAdd.getX() + _btnListAdd.getW(), _btnListAdd.getY());
    _btnListDel.setText("Del");
    _btnListDel.events().addClickHandler(new Control.Events.Click() {
      public void click() { delList(); }
      public void clickDbl() { click(); }
    });
    
    _scrList = new Scrollbar(this);
    _scrList.setXYWH(4, _btnListAdd.getY() + _btnListAdd.getH(), 16, 64);
    _scrList.events().addChangeHandler(new Scrollbar.Events.Change() {
      public void change(int delta) { setList(_list + delta); }
    });
    
    _lblListNum = new Label(this);
    _lblListNum.setXY(_scrList.getX(), _btnListAdd.getY());
    _lblListNum.setText("0");
    
    _picList = new Picture(this);
    _picList.setBackColour(new float[] {0.33f, 0.33f, 0.33f, 0.66f});
    _picList.setXY(_scrList.getX() + _scrList.getW(), _scrList.getY());
    _picList.events().addScrollHandler(new Control.Events.Scroll() {
      public void scroll(int delta) { _scrList.handleMouseWheel(delta); }
    });
    
    _picList.controls().add(_lblListFrame);
    _picList.controls().add(_scrListFrame);
    _picList.controls().add(_lblListTime);
    _picList.controls().add(_scrListTime);
    
    _btnAnimAdd = new Button(this);
    _btnAnimAdd.setXY(20, 4);
    _btnAnimAdd.setText("Add");
    _btnAnimAdd.events().addClickHandler(new Control.Events.Click() {
      public void click() { addAnim(); }
      public void clickDbl() { click(); }
    });
    
    _btnAnimDel = new Button(this);
    _btnAnimDel.setXY(_btnAnimAdd.getX() + _btnAnimAdd.getW(), _btnAnimAdd.getY());
    _btnAnimDel.setText("Del");
    _btnAnimDel.events().addClickHandler(new Control.Events.Click() {
      public void click() { delAnim(); }
      public void clickDbl() { click(); }
    });
    
    _btnAnimClone = new Button(this);
    _btnAnimClone.setXY(_btnAnimDel.getX() + _btnAnimDel.getW() + 4, _btnAnimDel.getY());
    _btnAnimClone.setText("Clone");
    _btnAnimClone.events().addClickHandler(new Control.Events.Click() {
      public void click() { cloneAnim(); }
      public void clickDbl() { click(); }
    });
    
    _scrAnim = new Scrollbar(this);
    _scrAnim.setXYWH(4, _btnAnimAdd.getY() + _btnAnimAdd.getH(), 16, 64);
    _scrAnim.events().addChangeHandler(new Scrollbar.Events.Change() {
      public void change(int delta) { setAnim(_anim + delta); }
    });
    
    _lblAnimNum = new Label(this);
    _lblAnimNum.setXY(_scrAnim.getX(), _btnAnimAdd.getY());
    _lblAnimNum.setText("0");
    
    _picAnim = new Picture(this);
    _picAnim.setBackColour(new float[] {0.33f, 0.33f, 0.33f, 0.66f});
    _picAnim.setXY(_scrAnim.getX() + _scrAnim.getW(), _scrAnim.getY());
    _picAnim.events().addScrollHandler(new Control.Events.Scroll() {
      public void scroll(int delta) { _scrAnim.handleMouseWheel(delta); }
    });
    
    _picAnim.controls().add(_lblAnimName);
    _picAnim.controls().add(_txtAnimName);
    _picAnim.controls().add(_btnListAdd);
    _picAnim.controls().add(_btnListDel);
    _picAnim.controls().add(_lblListNum);
    _picAnim.controls().add(_scrList);
    _picAnim.controls().add(_picList);
    
    _wndEditor.controls(1).add(_btnAnimAdd);
    _wndEditor.controls(1).add(_btnAnimDel);
    _wndEditor.controls(1).add(_btnAnimClone);
    _wndEditor.controls(1).add(_lblAnimNum);
    _wndEditor.controls(1).add(_scrAnim);
    _wndEditor.controls(1).add(_picAnim);
    
    Textbox.Events.Change change = new Textbox.Events.Change() {
      public void change() { update(); }
    };
    
    _lblName = new Label(this);
    _lblName.setText("Name");
    _lblName.setXY(8, 4);
    
    _txtName = new Textbox(this);
    _txtName.setXY(_lblName.getX(), _lblName.getY() + _lblName.getH() + 4);
    _txtName.events().addChangeHandler(change);
    
    _lblNote = new Label(this);
    _lblNote.setText("Notes");
    _lblNote.setXY(_txtName.getX(), _txtName.getY() + _txtName.getH() + 8);
    
    _txtNote = new Textbox(this);
    _txtNote.setXY(_lblNote.getX(), _lblNote.getY() + _lblNote.getH() + 4);
    _txtNote.events().addChangeHandler(change);
    
    _lblW = new Label(this);
    _lblW.setText("W");
    _lblW.setXY(_txtNote.getX(), _txtNote.getY() + _txtNote.getH() + 8);
    
    _txtW = new Textbox(this);
    _txtW.setXY(_lblW.getX(), _lblW.getY() + _lblW.getH() + 4);
    _txtW.setW(40);
    _txtW.events().addChangeHandler(change);
    _txtW.setNumeric(true);
    
    _lblH = new Label(this);
    _lblH.setText("H");
    _lblH.setXY(_txtW.getX() + _txtW.getW() + 8, _lblW.getY());
    
    _txtH = new Textbox(this);
    _txtH.setXY(_lblH.getX(), _txtW.getY());
    _txtH.setW(40);
    _txtH.events().addChangeHandler(change);
    _txtH.setNumeric(true);
    
    _wndEditor.controls(2).add(_lblName);
    _wndEditor.controls(2).add(_txtName);
    _wndEditor.controls(2).add(_lblNote);
    _wndEditor.controls(2).add(_txtNote);
    _wndEditor.controls(2).add(_lblW);
    _wndEditor.controls(2).add(_txtW);
    _wndEditor.controls(2).add(_lblH);
    _wndEditor.controls(2).add(_txtH);
    
    _btnScriptCopy = new Button(this);
    _btnScriptCopy.setText("Copy");
    _btnScriptCopy.setXY(4, 4);
    _btnScriptCopy.events().addClickHandler(new Control.Events.Click() {
      public void clickDbl() { }
      public void click() {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(_sprite.getScript()), null);
      }
    });
    
    _btnScriptPaste = new Button(this);
    _btnScriptPaste.setText("Paste");
    _btnScriptPaste.setXY(_btnScriptCopy.getX() + _btnScriptCopy.getW() + 4, _btnScriptCopy.getY());
    _btnScriptPaste.events().addClickHandler(new Control.Events.Click() {
      public void clickDbl() { }
      public void click() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable contents = clipboard.getContents(null);
        if(contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
          try {
            _sprite.setScript((String)contents.getTransferData(DataFlavor.stringFlavor));
          } catch(UnsupportedFlavorException ex){
            System.out.println(ex);
            ex.printStackTrace();
          } catch(IOException ex) {
            System.out.println(ex);
            ex.printStackTrace();
          }
        }
      }
    });
    
    _wndEditor.controls(3).add(_btnScriptCopy);
    _wndEditor.controls(3).add(_btnScriptPaste);
    
    controls().add(_wndEditor);
    
    _frameLoc = Context.newDrawable();
    _frameLoc.setColour(new float[] {0, 1, 0, 1});
    
    _frameFoot = Context.newDrawable();
    _frameFoot.setColour(new float[] {1, 0, 0, 1});
    _frameFoot.setWH(16, 16);
    _frameFoot.createBorder();
    
    listSprites(new File("../gfx/textures/sprites/"), "../gfx/textures/sprites/");
  }
  
  protected void destroy() {
    
  }
  
  protected void resize() {
    float w = _picFrameSprite.getW();
    float h = _picFrameSprite.getH();
    
    if(w < 400) w = 500;
    if(h < 300) h = 300;
    
    _picFrameSpriteBack.setWH(w, h);
    _splFrame.setW(w);
    
    _wndEditor.setWH(_picFrameSpriteBack.getX() + w + 4, _picFrameSpriteBack.getY() + h + 24);
    _wndEditor.setXY((_context.getW() - _wndEditor.getW()) / 2, (_context.getH() - _wndEditor.getH()) / 2);
    
    _picAnim.setWH(_wndEditor.getW() - _picAnim.getX() - 4, _wndEditor.getH() - _picAnim.getY() - 4);
    _picList.setWH(_picAnim.getW() - _picList.getX() - 4, _picAnim.getH() - _picList.getY() - 4);
  }
  
  protected void draw() {
    
  }
  
  protected boolean logic() {
    return false;
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
    EditorSave.Sprite packet = new EditorSave.Sprite();
    packet.addData(_sprite);
    Game.getInstance().send(packet);
  }
  
  private void listSprites(File dir, String path) {
    for(File f : dir.listFiles()) {
      if(f.isFile()) {
        String name = f.getPath().substring(f.getPath().indexOf(path) + path.length() + 1).replace('\\', '/');
        _drpSprite.add(new Dropdown.Item(name));
      }
    }
    
    for(File f : dir.listFiles()) {
      if(f.isDirectory()) {
        listSprites(f, path);
      }
    }
  }
  
  public void editData(String file, boolean newData) {
    push();
    
    _sprite = new SpriteEditorSprite(file, newData);
    _sprite.events().addLoadHandler(new GameData.Events.Load() {
      public void load() {
        int i = 0;
        for(Dropdown.Item item : _drpSprite) {
          if(item.getText().equals(_sprite.getTexture())) {
            _drpSprite.setSeletected(i);
            break;
          }
          i++;
        }
        
        for(Sprite.Frame f : _sprite._frame) {
          _splFrame.add(new ScrollPanelFrame(f));
        }
        
        if(_sprite._frame.size() == 0) addFrame();
        if(_sprite._anim .size() == 0) addAnim();
        
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
    });
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
  
  protected boolean handleKeyDown ( int key) { return true; }
  protected boolean handleKeyUp   ( int key) { return true; }
  protected boolean handleCharDown(char key) { return true; }
  
  public static class ScrollPanelFrame extends ScrollPanel.Item {
    Sprite.Frame _frame;
    
    public ScrollPanelFrame(Sprite.Frame frame) {
      _frame = frame;
    }
    
    public Sprite.Frame getFrame() {
      return _frame;
    }
  }
}