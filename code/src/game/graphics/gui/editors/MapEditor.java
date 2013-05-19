package game.graphics.gui.editors;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.JOptionPane;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import game.Game;
import game.data.Map;
import game.data.Sprite;
import game.data.util.Serializable;
import game.settings.Settings;
import game.world.Region;
import graphics.gl00.Context;
import graphics.gl00.Drawable;
import graphics.shared.gui.Control;
import graphics.shared.gui.GUI;
import graphics.shared.gui.controls.Button;
import graphics.shared.gui.controls.Dropdown;
import graphics.shared.gui.controls.Label;
import graphics.shared.gui.controls.List;
import graphics.shared.gui.controls.Picture;
import graphics.shared.gui.controls.Textbox;
import graphics.shared.gui.controls.compound.ScrollPanel;
import graphics.shared.gui.controls.compound.Window;

public class MapEditor extends GUI {
  private Game _game = (Game)Context.getGame();
  
  private Picture   _picWindow;
  private Button[]  _btnTab;
  private Picture[] _picTab;
  private Picture   _picTileset;
  private Picture   _picSelected;
  
  private Picture   _picLayers;
  private Button[]  _btnLayer;
  
  private Picture   _picTilesetList;
  private Picture   _picTilesetBack;
  private Picture[] _picTilesets;
  
  private Drawable  _selected;
  
  private Button[]  _btnAttrib;
  
  private Drawable  _attribDrawable;
  private int _attribDrawCallback = -1;
  
  private ScrollPanel _splSprite;
  private Label     _lblSpriteFile;
  private Dropdown  _drpSpriteFile;
  private Label     _lblSpriteLoc;
  private Textbox   _txtSpriteX;
  private Textbox   _txtSpriteY;
  private Textbox   _txtSpriteZ;
  
  private game.world.Sprite _pickSprite;
  private boolean   _pickLoc;
  
  private int _tab;
  private boolean _shift;
  private boolean _lock;
  private int _mouseDelta;
  
  private int _tileset;
  private int _layer;
  private int _x, _y;
  private int _w = 1;
  private int _h = 1;
  private byte _a = (byte)255;
  
  private int _attrib;
  
  private Map.Sprite _sprite;
  
  private int _mx, _my;
  private MapEditorMap _map;
  private Region _region;
  private ArrayList<Region> _regions = new ArrayList<Region>();
  
  public void load() {
    ArrayList<String> files = new ArrayList<String>();
    for(int i = 0;; i++) {
      File f = new File("../gfx/textures/tiles/" + i + ".png");
      if(f.exists()) {
        files.add("tiles/" + i + ".png");
      } else {
        break;
      }
    }
    
    _picWindow = new Picture(this, true);
    _picWindow.setBackColour(new float[] {0.33f, 0.33f, 0.33f, 0.66f});
    _picWindow.setBorderColour(new float[] {0, 0, 0, 1});
    _picWindow.setVisible(false);
    
    _btnTab = new Button[5];
    
    Control.Events.Click btnTabClick = new Control.Events.Click() {
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
      _btnTab[i].setXYWH(8 + i * 49, 8, 50, 20);
      _btnTab[i].events().onClick(btnTabClick);
      _picWindow.Controls().add(_btnTab[i]);
    }

    _btnTab[0].setText("Tiles");
    _btnTab[1].setText("Attribs");
    _btnTab[2].setText("Sprites");
    _btnTab[3].setText("NPCs");
    _btnTab[4].setText("Items");
    
    _picTab = new Picture[5];
    for(int i = 0; i < _picTab.length; i++) {
      _picTab[i] = new Picture(this);
      _picTab[i].setBackColour(new float[] {0.1f, 0.1f, 0.1f, 1});
      _picTab[i].setXYWH(8, _btnTab[i].getY() + _btnTab[i].getH(), 256, 256);
      _picTab[i].setVisible(false);
      _picWindow.Controls().add(_picTab[i]);
    }
    
    _picTileset = new Picture(this, true);
    _picTileset.events().onMouseDown(new Control.Events.Mouse() {
      public void event(int x, int y, int button) {
        handleTilesetMouseDown(x, y, button);
      }
    });
    _picTileset.events().onMouseMove(new Control.Events.Mouse() {
      public void event(int x, int y, int button) {
        handleTilesetMouseMove(x, y, button);
      }
    });
    
    _picSelected = new Picture(this);
    _picSelected.setWH(Settings.Map.Tile.Size, Settings.Map.Tile.Size);
    _picSelected.setBackColour(new float[] {0, 1, 0, 0.33f});
    
    _picTab[0].Controls().add(_picTileset);
    _picTileset.Controls().add(_picSelected);
    
    _picLayers = new Picture(this);
    _picLayers.setBackColour(new float[] {0.1f, 0.1f, 0.1f, 1});
    _picLayers.setXYWH(_picTab[0].getX() + _picTab[0].getW() + 4, _picTab[0].getY(), 100, 256);
    
    _btnLayer = new Button[Settings.Map.Depth];
    
    Control.Events.Click btnLayerClick = new Control.Events.Click() {
      public void event() {
        for(int i = 0; i < _btnLayer.length; i++) {
          if(_btnLayer[i] == getControl()) {
            setLayer(i);
            return;
          }
        }
      }
    };
    
    for(int i = 0; i < Settings.Map.Depth; i++) {
      _btnLayer[i] = new Button(this);
      _btnLayer[i].setBackColour(new float[] {0.2f, 0.2f, 0.2f, 1});
      _btnLayer[i].setXYWH(2, 2 + i * 16, 96, 16);
      _btnLayer[i].setText("Layer " + i);
      _btnLayer[i].events().onClick(btnLayerClick);
      _picLayers.Controls().add(_btnLayer[i]);
    }
    
    _picWindow.Controls().add(_picLayers);
    
    _picTilesetList = new Picture(this);
    _picTilesetList.setBackColour(new float[] {0.33f, 0.33f, 0.33f, 0.66f});
    _picTilesetList.setBorderColour(new float[] {0, 0, 0, 1});
    _picTilesetList.setWH(_context.getW(), 144);
    _picTilesetList.setVisible(false);
    
    _picTilesetBack = new Picture(this);
    _picTilesetBack.setBackColour(new float[] {0, 0, 0, 0});
    _picTilesetBack.setWH(files.size() * 136, _picTilesetList.getH());
    _picTilesetList.Controls().add(_picTilesetBack);
    
    int i = 0;
    _picTilesets = new Picture[files.size()];
    for(String file : files) {
      _picTilesets[i] = new Picture(this);
      _picTilesets[i].setTexture(_textures.getTexture(file));
      _picTilesets[i].setXYWH(i * 136 + 8, 8, 128, 128);
      _picTilesetBack.Controls().add(_picTilesets[i]);
      i++;
    }
    
    // Attribs tab
    Control.Events.Click btnAttribClick = new Control.Events.Click() {
      public void event() {
        for(int i = 0; i < _btnAttrib.length; i++) {
          if(_btnAttrib[i] == getControl()) {
            setAttrib(i);
            return;
          }
        }
      }
    };
    
    i = 0;
    _btnAttrib = new Button[Map.Attrib.Type.values().length];
    for(Map.Attrib.Type attrib : Map.Attrib.Type.values()) {
      _btnAttrib[i] = new Button(this);
      _btnAttrib[i].setText(attrib.toString());
      _btnAttrib[i].setXY(4, 4 + i * _btnAttrib[i].getH());
      _btnAttrib[i].events().onClick(btnAttribClick);
      _picTab[1].Controls().add(_btnAttrib[i]);
      i++;
    }
    
    // Sprites tab
    _splSprite = new ScrollPanel(this);
    _splSprite.setXY(4, 4);
    _splSprite.events().onButtonAdd(new ScrollPanel.Events.Button() {
      public void event() {
        addSprite();
      }
    });
    _splSprite.events().onButtonDel(new ScrollPanel.Events.Button() {
      public void event() {
        delSprite();
      }
    });
    _splSprite.events().onSelect(new ScrollPanel.Events.Select() {
      public void event(ScrollPanel.Item item) {
        if(item != null) {
          selSprite(((ScrollPanelSprite)item)._sprite);
        }
      }
    });
    
    _lblSpriteFile = new Label(this);
    _lblSpriteFile.setXY(4, 4);
    _lblSpriteFile.setText("File");
    
    _drpSpriteFile = new Dropdown(this);
    _drpSpriteFile.setXY(_lblSpriteFile.getX(), _lblSpriteFile.getY() + _lblSpriteFile.getH());
    _drpSpriteFile.events().onSelect(new Dropdown.Events.Select() {
      public void event(Dropdown.Item item) {
        updateSprite();
        _map.createSprites();
      }
    });
    
    _lblSpriteLoc = new Label(this);
    _lblSpriteLoc.setXY(_drpSpriteFile.getX(), _drpSpriteFile.getY() + _drpSpriteFile.getH() + 8);
    _lblSpriteLoc.setText("Location");
    
    _txtSpriteX = new Textbox(this);
    _txtSpriteX.setXY(_lblSpriteLoc.getX(), _lblSpriteLoc.getY() + _lblSpriteLoc.getH());
    _txtSpriteX.setW(40);
    _txtSpriteX.setNumeric(true);
    _txtSpriteX.events().onChange(new Textbox.Events.Change() {
      public void event() {
        _sprite._x = Integer.parseInt(_txtSpriteX.getText());
        _map.createSprites();
      }
    });
    
    _txtSpriteY = new Textbox(this);
    _txtSpriteY.setXY(_txtSpriteX.getX() + _txtSpriteX.getW() + 4, _txtSpriteX.getY());
    _txtSpriteY.setW(40);
    _txtSpriteY.setNumeric(true);
    _txtSpriteY.events().onChange(new Textbox.Events.Change() {
      public void event() {
        _sprite._y = Integer.parseInt(_txtSpriteY.getText());
        _map.createSprites();
      }
    });
    
    _txtSpriteZ = new Textbox(this);
    _txtSpriteZ.setXY(_txtSpriteY.getX() + _txtSpriteY.getW() + 4, _txtSpriteY.getY());
    _txtSpriteZ.setW(40);
    _txtSpriteZ.setNumeric(true);
    _txtSpriteZ.events().onChange(new Textbox.Events.Change() {
      public void event() {
        _sprite._z = Byte.parseByte(_txtSpriteZ.getText());
        _map.createSprites();
      }
    });
    
    _splSprite.Controls().add(_lblSpriteFile);
    _splSprite.Controls().add(_drpSpriteFile);
    _splSprite.Controls().add(_lblSpriteLoc);
    _splSprite.Controls().add(_txtSpriteX);
    _splSprite.Controls().add(_txtSpriteY);
    _splSprite.Controls().add(_txtSpriteZ);
    
    _picTab[2].Controls().add(_splSprite);
    
    Controls().add(_picWindow);
    Controls().add(_picTilesetList);
    
    _selected = Context.newDrawable();
    _selected.setColour(new float[] {1, 1, 1, 0.5f});
    
    File d = new File("../data/sprites/");
    if(d.isDirectory()) {
      for(File f : d.listFiles()) {
        Sprite s = new Sprite(f.getName());
        if(s.load()) {
          _drpSpriteFile.add(new DropdownSprite(s));
        }
      }
    }
    
    _attribDrawable = Context.newDrawable();
    _attribDrawable.setWH(Settings.Map.Size, Settings.Map.Size);
    _attribDrawable.setColour(new float[] {1, 1, 1, 0.5f});
    _attribDrawable.createQuad();
    
    setTab(_tab);
    setLayer(_layer);
    setTileset(_tileset);
    setAttrib(_attrib);
  }
  
  public void destroy() {
    
  }
  
  public void resize() {
    _picWindow.setXY((_context.getW() - _picWindow.getW()) / 2, (_context.getH() - _picWindow.getH()) / 2);
    _picTilesetList.setY((_context.getH() - (_picWindow.getY() + _picWindow.getH()) - _picTilesetList.getH()) / 2 + _picWindow.getY() + _picWindow.getH());
    _picTilesetList.setW(_context.getW());
    _picTilesetBack.setX((_picTilesetList.getW() - _picTilesets[_tileset].getW()) / 2 - _tileset * 136 - 54);
    
    _splSprite.setWH(_picTab[2].getW() - _splSprite.getX() * 2, _picTab[2].getH() - _splSprite.getY() * 2);
  }
  
  public boolean unload() {
    if(_regions.size() != 0) {
      switch(JOptionPane.showConfirmDialog(null, "Would you like to save your changes?")) {
        case JOptionPane.CANCEL_OPTION:
        case JOptionPane.CLOSED_OPTION:
          return false;
          
        case JOptionPane.YES_OPTION:
          for(Region r : _regions) {
            MapEditorMap m = (MapEditorMap)r.getMap();
            m.deleteSprites();
            
            if(m.isChanged()) {
              System.out.println("Updating map " + m.getFile());
              m.update();
              
              r.setMap(m.getMap());
              r.getMap().save();
            }
          }
          
          break;
          
        case JOptionPane.NO_OPTION:
          for(Region r : _regions) {
            MapEditorMap m = (MapEditorMap)r.getMap();
            m.deleteSprites();
            
            r.setMap(m.getMap());
          }
          
          break;
      }
    }
    
    if(_attribDrawCallback != -1) {
      _region.events().removeDraw(_attribDrawCallback);
    }
    
    return true;
  }
  
  public void setRegion(Region region) {
    if(region != _region) {
      if(_attribDrawCallback != -1) {
        _region.events().removeDraw(_attribDrawCallback);
      }
      
      _region = region;
      _region.despawn();
      _region.setMap(new MapEditorMap(_region.getMap()));
      _map = (MapEditorMap)_region.getMap();
      _mx = _map.getX();
      _my = _map.getY();
      System.out.println(_mx + "\t" + _my + "\t" + "Changing");
      
      if(!_regions.contains(_region)) {
        System.out.println("Adding region " + region.getMap().getX() + ", " + region.getMap().getY());
        _regions.add(_region);
      }
      
      _attribDrawable.setTexture(_map.getAttribMask(_layer));
      
      _splSprite.clear();
      for(Map.Sprite s : _map._sprite) {
        _splSprite.add(new ScrollPanelSprite(s));
      }
      
      _attribDrawCallback = _region.events().onDraw(new Region.Events.Draw() {
        public void event(int z) {
          if(z == Settings.Map.Depth - 1) {
            switch(_tab) {
              case 1:
                _attribDrawable.draw();
                
              case 2:
                _map.drawSprites();
            }
          }
        }
      });
    }
  }
  
  private void setTab(int index) {
    _btnTab[_tab].setBackColour(new float[] {0.2f, 0.2f, 0.2f, 1});
    _picTab[_tab].setVisible(false);
    _btnTab[index].setBackColour(new float[] {0, 0, 0.8f, 1});
    _picTab[index].setVisible(true);
    _tab = index;
    
    _picTilesetList.setVisible(_tab == 0 && _picWindow.getVisible());
  }
  
  private void setLayer(int index) {
    _btnLayer[_layer].setBackColour(new float[] {0.2f, 0.2f, 0.2f, 1});
    _btnLayer[index].setBackColour(new float[] {0, 0, 0.8f, 1});
    _layer = index;
    
    if(_map != null) {
      _attribDrawable.setTexture(_map.getAttribMask(_layer));
    }
  }
  
  private void setTileset(int index) {
    _tileset = index;
    _picTileset.setTexture(_picTilesets[_tileset].getTexture());
    _picWindow.setWH(_picTileset.getW() + 124, _picTileset.getH() + _btnTab[0].getH() + _btnTab[0].getY() * 2);
    _picLayers.setX(_picTab[0].getX() + _picTab[0].getW() + 8);
    _selected.setTexture(_picTileset.getTexture());
    _selected.setWH(_w * Settings.Map.Tile.Size, _h * Settings.Map.Tile.Size);
    _selected.setTWH(_w * Settings.Map.Tile.Size, _h * Settings.Map.Tile.Size);
    _selected.createQuad();
    resize();
  }
  
  private void setAttrib(int index) {
    _btnAttrib[_attrib].setBackColour(new float[] {0.2f, 0.2f, 0.2f, 1});
    _btnAttrib[index].setBackColour(new float[] {0, 0, 0.8f, 1});
    _attrib = index;
  }
  
  private void addSprite() {
    final SpriteChooser s = new SpriteChooser();
    s.load();
    s.events().onSelect(new SpriteChooser.Events.Select() {
      public void event(String file) {
        s.pop();
        
        _pickLoc = true;
        _picWindow.setVisible(false);
        _pickSprite = game.world.Sprite.add(_game.getSprite(file));
        _pickSprite.setX(Mouse.getX() - _context.getCameraX());
        _pickSprite.setY(Mouse.getY() - _context.getCameraY());
        _pickSprite.setZ(_layer);
      }
    });
    
    s.push();
  }
  
  private void delSprite() {
    _map._sprite.remove(_sprite);
    _splSprite.remove();
    _map.createSprites();
  }
  
  private void selSprite(Map.Sprite sprite) {
    _sprite = sprite;
    
    if(_sprite._file != null) {
      int i = 0;
      for(Dropdown.Item item : _drpSpriteFile) {
        DropdownSprite s = (DropdownSprite)item;
        if(s._sprite.getFile().equals(_sprite._file)) {
          _drpSpriteFile.setSeletected(i);
        }
        i++;
      }
    } else {
      _drpSpriteFile.setSeletected(-1);
    }
    
    _txtSpriteX.setText(String.valueOf(_sprite._x));
    _txtSpriteY.setText(String.valueOf(_sprite._y));
    _txtSpriteZ.setText(String.valueOf(_sprite._z));
  }
  
  private void updateSprite() {
    DropdownSprite sprite = (DropdownSprite)_drpSpriteFile.get();
    _sprite._file = sprite != null ? sprite._sprite.getFile() : null;
    _sprite._x = Integer.parseInt(_txtSpriteX.getText());
    _sprite._y = Integer.parseInt(_txtSpriteY.getText());
    _sprite._z = Byte.parseByte(_txtSpriteZ.getText());
    selSprite(_sprite);
  }
  
  public boolean draw() {
    switch(_tab) {
      case 0:
        _selected.draw();
        break;
    }
    
    return false;
  }
  
  private boolean mapEditorClick(int x, int y, int button) {
    x -= _context.getCameraX();
    y -= _context.getCameraY();
    
    if(button != -1) {
      int mx = x / Settings.Map.Size;
      int my = y / Settings.Map.Size;
      if(x < 0) mx -= 1;
      if(y < 0) my -= 1;
      
      if((_mx != mx || _my != my) || _region == null) {
        setRegion(_game.getWorld().getRegion(mx, my));
      }
    }
    
    x %= Settings.Map.Size;
    y %= Settings.Map.Size;
    if(x < 0) x += Settings.Map.Size;
    if(y < 0) y += Settings.Map.Size;
    
    int x1, y1;
    switch(_tab) {
      case 0:
        x1 = (int)x / Settings.Map.Tile.Size;
        y1 = (int)y / Settings.Map.Tile.Size;
        
        Map.Tile t;
        
        switch(button) {
          case 0:
            int x4 = 0, y4 = 0;
            for(int x2 = 0; x2 < _w; x2++) {
              for(int y2 = 0; y2 < _h; y2++) {
                int x3 = _mx, y3 = _my;
                
                if(x1 + x2 - x4 < 0) {
                  x3--;
                  x4 -= Settings.Map.Tile.Count;
                }
                
                if(y1 + y2 - y4 < 0) {
                  y3--;
                  y4 -= Settings.Map.Tile.Count;
                }
                
                if(x1 + x2 - x4 >= Settings.Map.Tile.Count) {
                  x3++;
                  x4 += Settings.Map.Tile.Count;
                }
                
                if(y1 + y2 - y4 >= Settings.Map.Tile.Count) {
                  y3++;
                  y4 += Settings.Map.Tile.Count;
                }
                
                if(x3 != _mx || y3 != _my) {
                  _region.calc();
                  setRegion(_game.getWorld().getRegion(x3, y3));
                }
                
                t = _map.getLayer(_layer).getTile(x1 + x2 - x4, y1 + y2 - y4);
                t._tileset = (byte)_tileset;
                t._x = (byte)(_x + x2);
                t._y = (byte)(_y + y2);
                t._a = _a;
              }
            }
            
            _region.calc();
            return true;
            
          case 1:
            t = _map.getLayer(_layer).getTile(x1, y1);
            t._tileset = (byte)0;
            t._x = (byte)0;
            t._y = (byte)0;
            t._a = (byte)0;
            _region.calc();
            return true;
        }
        
        break;
        
      case 1:
        x1 = (int)x / Settings.Map.Attrib.Size;
        y1 = (int)y / Settings.Map.Attrib.Size;
        
        Map.Attrib a = _map.getLayer(_layer).getAttrib(x1, y1);
        
        ByteBuffer b;
        switch(button) {
          case 0:
            a._type = (byte)Map.Attrib.Type.values()[_attrib].val();
            
            b = ByteBuffer.allocateDirect(Settings.Map.Attrib.Size * Settings.Map.Attrib.Size * 4);
            for(int i = 0; i < b.capacity() / 4; i++) {
              b.put(Map.Attrib.Type.values()[_attrib].col());
            }
            b.flip();
            
            _map.updateAttrib(_layer, x1, y1, b);
            return true;
            
          case 1:
            a._type = (byte)0;
            
            b = ByteBuffer.allocateDirect(Settings.Map.Attrib.Size * Settings.Map.Attrib.Size * 4);
            b.put(new byte[b.capacity()]);
            b.flip();
            
            _map.updateAttrib(_layer, x1, y1, b);
            return true;
        }
        
        break;
        
      case 2:
        if(button == 0) {
          if(_pickLoc) {
            Map.Sprite s = new Map.Sprite();
            
            s._file = _pickSprite.getSource().getFile();
            s._x = x;
            s._y = y;
            s._z = (byte)_layer;
            
            _map._sprite.add(s);
            _splSprite.add(new ScrollPanelSprite(s));
            _map.createSprites();
            
            _pickSprite.remove();
            _pickLoc = false;
          }
        }
        
        break;
    }
    
    return false;
  }
  
  public boolean handleKeyDown(int key) {
    switch(key) {
      case Keyboard.KEY_LSHIFT:
      case Keyboard.KEY_RSHIFT:
        _shift = true;
        return true;
      
      case Keyboard.KEY_TAB:
        _picWindow.setVisible(true);
        _picTilesetList.setVisible(_tab == 0);
        _lock = _shift;
        return true;
        
      case Keyboard.KEY_ESCAPE:
        return _picWindow.getVisible() && !_lock;
    }
    
    return false;
  }
  
  public boolean handleKeyUp(int key) {
    switch(key) {
      case Keyboard.KEY_LSHIFT:
      case Keyboard.KEY_RSHIFT:
        _shift = false;
        return true;
      
      case Keyboard.KEY_TAB:
        if(!_lock) {
          _picWindow.setVisible(false);
          _picTilesetList.setVisible(false);
          return true;
        }
    }
    
    return false;
  }
  
  public boolean handleMouseDown(int x, int y, int button) {
    if(_picWindow.getVisible()) return true;
    return mapEditorClick(x, y, button);
  }
  
  public boolean handleMouseUp(int x, int y, int button) {
    return _picWindow.getVisible();
  }
  
  public boolean handleMouseMove(int x, int y, int button) {
    if(!_picWindow.getVisible()) {
      switch(_tab) {
        case 0:
          int x1 = x - (int)_context.getCameraX();
          int y1 = y - (int)_context.getCameraY();
          if(x1 < 0) x1 -= Settings.Map.Tile.Size;
          if(y1 < 0) y1 -= Settings.Map.Tile.Size;
          x1 /= Settings.Map.Tile.Size;
          y1 /= Settings.Map.Tile.Size;
          
          _selected.setXY(x1 * Settings.Map.Tile.Size, y1 * Settings.Map.Tile.Size);
          
          if(button != -1) {
            return mapEditorClick(x, y, button);
          }
          
          break;
          
        case 1:
          if(button != -1) {
            return mapEditorClick(x, y, button);
          }
          
        case 2:
          if(_pickLoc) {
            _pickSprite.setX(x - _context.getCameraX());
            _pickSprite.setY(y - _context.getCameraY());
          }
      }
    } else {
      
    }
    
    return false;
  }
  
  public boolean handleMouseWheel(int delta) {
    if(_picTilesetList.getVisible()) {
      _mouseDelta += delta;
      
      while(_mouseDelta < 0) {
        _mouseDelta += 120;
        
        if(_tileset < _picTilesets.length - 1) {
          setTileset(_tileset + 1);
        }
      }
      
      while(_mouseDelta > 0) {
        _mouseDelta -= 120;
        
        if(_tileset > 0) {
          setTileset(_tileset - 1);
        }
      }
      
      return true;
    }
    
    return false;
  }
  
  public boolean handleButtonDown(int button) {
    if(_picWindow.getVisible()) {
      switch(button) {
        case 4:
          if(_tab > 0) setTab(_tab - 1);
          return true;
          
        case 5:
          if(_tab < _picTab.length - 1) setTab(_tab + 1);
          return true;
          
        case 1000:
          if(_y > 0) _y -= 1;
          _picSelected.setXYWH(_x * Settings.Map.Tile.Size, _y * Settings.Map.Tile.Size, Settings.Map.Tile.Size, Settings.Map.Tile.Size);
          return true;
          
        case 1001:
          if(_y < _picTileset.getH() / Settings.Map.Tile.Size - 1) _y += 1;
          _picSelected.setXYWH(_x * Settings.Map.Tile.Size, _y * Settings.Map.Tile.Size, Settings.Map.Tile.Size, Settings.Map.Tile.Size);
          return true;
          
        case 1002:
          if(_x > 0) _x -= 1;
          _picSelected.setXYWH(_x * Settings.Map.Tile.Size, _y * Settings.Map.Tile.Size, Settings.Map.Tile.Size, Settings.Map.Tile.Size);
          return true;
          
        case 1003:
          if(_x < _picTileset.getW() / Settings.Map.Tile.Size - 1) _x += 1;
          _picSelected.setXYWH(_x * Settings.Map.Tile.Size, _y * Settings.Map.Tile.Size, Settings.Map.Tile.Size, Settings.Map.Tile.Size);
          return true;
      }
    }
    
    switch(button) {
      case 11:
        _picWindow.setVisible(!_picWindow.getVisible());
        _picTilesetList.setVisible(_picWindow.getVisible());
    }
    
    return false;
  }
  
  private void handleTilesetMouseDown(int x, int y, int button) {
    _x = (int)(x / Settings.Map.Tile.Size);
    _y = (int)(y / Settings.Map.Tile.Size);
    _w = 1;
    _h = 1;
    
    _picSelected.setXYWH(_x * Settings.Map.Tile.Size, _y * Settings.Map.Tile.Size, Settings.Map.Tile.Size, Settings.Map.Tile.Size);
    _selected.setWH(_w * Settings.Map.Tile.Size, _h * Settings.Map.Tile.Size);
    _selected.setTXYWH(_x * Settings.Map.Tile.Size, _y * Settings.Map.Tile.Size, _w * Settings.Map.Tile.Size, _h * Settings.Map.Tile.Size);
    _selected.createQuad();
  }
  
  private void handleTilesetMouseMove(int x, int y, int button) {
    if(button == 0) {
      if(x < 0) x = 0;
      if(y < 0) y = 0;
      if(x > _picTileset.getW()) x = (int)_picTileset.getW() - 1;
      if(y > _picTileset.getH()) y = (int)_picTileset.getH() - 1;
      
      x = (int)(x / Settings.Map.Tile.Size);
      y = (int)(y / Settings.Map.Tile.Size);
      
      if(x < _x || y < _y) return;
      
      _w = x - _x + 1;
      _h = y - _y + 1;
      
      _picSelected.setWH(_w * Settings.Map.Tile.Size, _h * Settings.Map.Tile.Size);
      _selected.setWH(_w * Settings.Map.Tile.Size, _h * Settings.Map.Tile.Size);
      _selected.setTWH(_w * Settings.Map.Tile.Size, _h * Settings.Map.Tile.Size);
      _selected.createQuad();
    }
  }
  
  public static class DropdownSprite extends Dropdown.Item {
    private Sprite _sprite;
    
    public DropdownSprite(Sprite sprite) {
      super(sprite.getName() + " - " + sprite.getNote());
      _sprite = sprite;
    }
    
    public Sprite getSprite() {
      return _sprite;
    }
  }
  
  public static class ScrollPanelSprite extends ScrollPanel.Item {
    Map.Sprite _sprite;
    
    public ScrollPanelSprite(Map.Sprite sprite) {
      _sprite = sprite;
    }
    
    public Map.Sprite getSprite() {
      return _sprite;
    }
  }
  
  public static class SpriteChooser extends GUI {
    private Events _events;
    
    private Window _window;
    private List   _data;
    
    public void load() {
      _events = new Events();
      
      _window = new Window(this);
      _window.setText("Choose Sprite");
      
      _data = new List(this);
      _data.setXYWH(8, 8, 400, 200);
      
      Control.Events.Click accept = new Control.Events.Click() {
        public void event() {
          _events.raiseSelect(((ListItem)_data.getSelected()).getData().getFile());
        }
      };
      
      File d = new File("../data/sprites/");
      if(d.isDirectory()) {
        for(File f : d.listFiles()) {
          Sprite s = new Sprite(f.getName());
          if(s.load()) {
            ListItem l = (ListItem)_data.addItem(new ListItem(this, s));
            l.setText(s.getFile() + ": " + s.getName() + " - " + s.getNote());
            l.events().onDoubleClick(accept);
          }
        }
      }
      
      _window.setWH(_data.getX() + _data.getW() + 8, _data.getY() + _data.getH() + 28);
      _window.events().onClose(new Window.Events.Close() {
        public boolean event() {
          pop();
          return true;
        }
      });
      _window.Controls().add(_data);
      
      Controls().add(_window);
      resize();
    }
    
    public void destroy() {
      
    }
    
    public void resize() {
      _window.setXY((_context.getW() - _window.getW()) / 2, (_context.getH() - _window.getH()) / 2);
    }
    
    public Events events() {
      return _events;
    }
    
    public static class ListItem extends graphics.shared.gui.controls.List.ListItem {
      private Serializable _data;
      
      protected ListItem(GUI gui, Serializable data) {
        super(gui);
        _data = data;
      }
      
      public Serializable getData() {
        return _data;
      }
    }
    
    public static class Events {
      private LinkedList<Select> _select = new LinkedList<Select>();
      
      public void onSelect(Select e) { _select.add(e); }
      
      public void raiseSelect(String file) {
        for(Select e : _select) {
          e.event(file);
        }
      }
      
      public static abstract class Select {
        public abstract void event(String file);
      }
    }
  }
}