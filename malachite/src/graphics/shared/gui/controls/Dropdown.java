package graphics.shared.gui.controls;

import java.util.ArrayList;

import graphics.gl00.Context;
import graphics.gl00.Drawable;
import graphics.shared.fonts.Font;
import graphics.shared.fonts.Fonts;
import graphics.shared.gui.Control;
import graphics.shared.gui.GUI;
import graphics.themes.Theme;

public class Dropdown extends Control {
  private Fonts _fonts = Context.getFonts();
  private Font _font = _fonts.getDefault();
  
  private Button _btnDrop;
  private Picture _picDrop;
  
  private ArrayList<String> _text = new ArrayList<String>();
  private int _textIndex = -1;
  private int _textY = 0;
  
  private Drawable _selected;
  private int _selectedIndex;
  
  private boolean _hover;
  
  private float[] _backColour = {0, 0, 0, 0};
  private float[] _glowColour = {0, 0, 0, 0};
  private float   _fade;
  
  public Dropdown(GUI gui) {
    this(gui, Theme.getInstance());
  }
  
  public Dropdown(GUI gui, Theme theme) {
    super(gui, theme);
    
    addEventMouseEnterHandler(new ControlEventHover() {
      public void event() {
        _hover = true;
      }
    });
    
    addEventMouseLeaveHandler(new ControlEventHover() {
      public void event() {
        _hover = false;
      }
    });
    
    ControlEventClick click = new ControlEventClick() {
      public void event() {
        _btnDrop.handleMouseDown(0, 0, 0);
        _btnDrop.handleMouseUp(0, 0, 0);
      }
    };
    
    addEventClickHandler(click);
    addEventDoubleClickHandler(click);
    
    ControlEventClick btnDropClick = new ControlEventClick() {
      public void event() {
        if(_text.size() != 0) {
          _selectedIndex = _textIndex;
          _selected.setY(_selectedIndex * _font.getH());
          _picDrop.setVisible(!_picDrop.getVisible());
        }
      }
    };
    
    _btnDrop = new Button(gui, theme);
    _btnDrop.setText("\u25BC");
    _btnDrop.addEventClickHandler(btnDropClick);
    _btnDrop.addEventDoubleClickHandler(btnDropClick);
    
    _picDrop = new Picture(gui);
    _picDrop.setVisible(false);
    _picDrop.addEventDrawHandler(new ControlEventDraw() {
      public void event() {
        if(_selectedIndex != -1) {
          _selected.draw();
        }
        
        int y = 0;
        for(String s : _text) {
          _font.draw(0, y, s, _foreColour);
          y += _font.getH();
        }
      }
    });
    _picDrop.addEventMouseMoveHandler(new ControlEventMouse() {
      public void event(int x, int y, int button) {
        _selectedIndex = (y - 1) / _font.getH();
        _selected.setY(_selectedIndex * _font.getH());
      }
    });
    _picDrop.addEventClickHandler(new ControlEventClick() {
      public void event() {
        _textIndex = _selectedIndex;
        _btnDrop.handleMouseDown(0, 0, 0);
        _btnDrop.handleMouseUp(0, 0, 0);
      }
    });
    
    Controls().add(_btnDrop);
    Controls().add(_picDrop);
    
    _selected = Context.newDrawable();
    _selected.setColour(new float[] {1, 1, 1, 0.33f});
    _selected.setH(_font.getH());
    
    theme.create(this);
  }
  
  public void add(String text) {
    _text.add(text);
    _picDrop.setH(_text.size() * _font.getH());
  }
  
  public int getSize() {
    return _text.size();
  }
  
  public int getSelected() {
    return _textIndex;
  }
  
  public void setSeletected(int index) {
    _textIndex = index;
  }
  
  public void setBackColour(float[] c) {
    _backColour = c;
    
    if(!_hover) {
      _background.setColour(c);
      _background.createQuad();
    }
    
    _picDrop.setBackColour(_backColour);
  }
  
  public float[] getGlowColour() {
    return _glowColour;
  }
  
  public void setGlowColour(float[] c) {
    _glowColour = c;
  }
  
  public void setBorderColour(float[] c) {
    super.setBorderColour(c);
    _picDrop.setBorderColour(c);
  }
  
  public void setW(float w) {
    super.setW(w);
    updateSize();
  }
  
  public void setH(float h) {
    super.setH(h);
    updateSize();
  }
  
  public void setWH(float w, float h) {
    super.setWH(w, h);
    updateSize();
  }
  
  public void setXYWH(float x, float y, float w, float h) {
    super.setXYWH(x, y, w, h);
    updateSize();
  }
  
  public void setXYWH(float[] loc) {
    super.setXYWH(loc);
    updateSize();
  }
  
  private void updateSize() {
    _btnDrop.setWH(_loc[3], _loc[3]);
    _btnDrop.setX(_loc[2] - _btnDrop.getW());
    
    _picDrop.setY(_loc[3]);
    _picDrop.setW(_loc[2]);
    
    _selected.setW(_loc[2]);
    _selected.createQuad();
    
    _textY = (int)((_loc[3] - _font.getH()) / 2);
  }
  
  public void draw() {
    if(drawBegin()) {
      if(_textIndex != -1) {
        _font.draw(0, _textY, _text.get(_textIndex), _foreColour);
      }
    }
    
    drawEnd();
  }
  
  public void logic() {
    if(_hover) {
      if(_fade < 1) {
        _fade += 0.1f;
        float[] c = new float[4];
        for(int i = 0; i < c.length; i++) {
          c[i] = (_glowColour[i] - _backColour[i]) * _fade + _backColour[i];
        }
        _background.setColour(c);
        _background.createQuad();
      }
    } else {
      if(_fade > 0) {
        _fade -= 0.05f;
        float[] c = new float[4];
        for(int i = 0; i < c.length; i++) {
          c[i] = (_glowColour[i] - _backColour[i]) * _fade + _backColour[i];
        }
        _background.setColour(c);
        _background.createQuad();
      }
    }
  }
}