package game.graphics.gui.editors;

import java.io.File;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.lwjgl.input.Keyboard;

import game.Game;
import game.data.Map.Tile;
import game.settings.Settings;
import game.world.Region;
import graphics.gl00.Context;
import graphics.gl00.Drawable;
import graphics.shared.gui.Control.ControlEventClick;
import graphics.shared.gui.Control.ControlEventMouse;
import graphics.shared.gui.GUI;
import graphics.shared.gui.controls.Button;
import graphics.shared.gui.controls.Picture;

public class MapEditor extends GUI {
  private Game _game = (Game)Context.getGame();
  
  private Picture _picWindow;
  private Button[] _btnTab;
  private Picture[] _picTab;
  private Picture _picTileset;
  private Picture _picSelected;
  
  private Picture _picLayers;
  private Button[] _btnLayer;
  
  private Picture _picTilesetList;
  private Picture _picTilesetBack;
  private Picture[] _picTilesets;
  
  private Drawable _selected = Context.newDrawable();
  
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
      _btnTab[i].setXYWH(8 + i * 49, 8, 50, 20);
      _btnTab[i].addEventClickHandler(btnTabClick);
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
    _picTileset.addEventMouseDownHandler(new ControlEventMouse() {
      public void event(int x, int y, int button) {
        handleTilesetMouseDown(x, y, button);
      }
    });
    _picTileset.addEventMouseMoveHandler(new ControlEventMouse() {
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
    
    ControlEventClick btnLayerClick = new ControlEventClick() {
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
      _btnLayer[i].addEventClickHandler(btnLayerClick);
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
    
    Controls().add(_picWindow);
    Controls().add(_picTilesetList);
    
    _selected.setColour(new float[] {1, 1, 1, 0.5f});
    
    setTab(_tab);
    setLayer(_layer);
    setTileset(_tileset);
  }
  
  public void destroy() {
    
  }
  
  public void resize() {
    _picWindow.setXY((_context.getW() - _picWindow.getW()) / 2, (_context.getH() - _picWindow.getH()) / 2);
    _picTilesetList.setY((_context.getH() - (_picWindow.getY() + _picWindow.getH()) - _picTilesetList.getH()) / 2 + _picWindow.getY() + _picWindow.getH());
    _picTilesetList.setW(_context.getW());
    _picTilesetBack.setX((_picTilesetList.getW() - _picTilesets[_tileset].getW()) / 2 - _tileset * 136 - 54);
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
            
            if(m.isChanged()) {
              System.out.println("Updating map " + m.getX() + ", " + m.getY());
              m.update();
              
              r.setMap(m.getMap());
              r.getMap().save();
            }
          }
          
          return true;
          
        case JOptionPane.NO_OPTION:
          for(Region r : _regions) {
            r.setMap(((MapEditorMap)r.getMap()).getMap());
          }
          
          return true;
      }
    }
    
    return true;
  }
  
  public void setRegion(Region region) {
    if(region != _region) {
      _region = region;
      _region.setMap(new MapEditorMap(_region.getMap()));
      _map = (MapEditorMap)_region.getMap();
      
      if(!_regions.contains(_region)) {
        System.out.println("Adding region " + region.getMap().getX() + ", " + region.getMap().getY());
        _regions.add(_region);
      }
    }
  }
  
  private void setTab(int index) {
    _btnTab[_tab].setBackColour(new float[] {0.2f, 0.2f, 0.2f, 1});
    _picTab[_tab].setVisible(false);
    _btnTab[index].setBackColour(new float[] {0, 0, 0.8f, 1});
    _picTab[index].setVisible(true);
    _tab = index;
  }
  
  private void setLayer(int index) {
    _btnLayer[_layer].setBackColour(new float[] {0.2f, 0.2f, 0.2f, 1});
    _btnLayer[index].setBackColour(new float[] {0, 0, 0.8f, 1});
    _layer = index;
  }
  
  private void setTileset(int tileset) {
    _tileset = tileset;
    _picTileset.setTexture(_picTilesets[_tileset].getTexture());
    _picWindow.setWH(_picTileset.getW() + 124, _picTileset.getH() + _btnTab[0].getH() + _btnTab[0].getY() * 2);
    _picLayers.setX(_picTab[0].getX() + _picTab[0].getW() + 8);
    _selected.setTexture(_picTileset.getTexture());
    _selected.setWH(_w * Settings.Map.Tile.Size, _h * Settings.Map.Tile.Size);
    _selected.setTWH(_w * Settings.Map.Tile.Size, _h * Settings.Map.Tile.Size);
    _selected.createQuad();
    resize();
  }
  
  public boolean draw() {
    _selected.draw();
    
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
        _mx = mx;
        _my = my;
        setRegion(_game.getWorld().getRegion(mx, my));
      }
    }
    
    x %= Settings.Map.Size;
    y %= Settings.Map.Size;
    if(x < 0) x += Settings.Map.Size;
    if(y < 0) y += Settings.Map.Size;
    
    switch(_tab) {
      case 0:
        int x1 = (int)x / Settings.Map.Tile.Size;
        int y1 = (int)y / Settings.Map.Tile.Size;
        
        Tile t;
        
        switch(button) {
          case 0:
            for(int x2 = 0; x2 < _w; x2++) {
              for(int y2 = 0; y2 < _h; y2++) {
                t = _map.getLayer(_layer).getTile(x1 + x2, y1 + y2);
                t.setTileset((byte)_tileset);
                t.setX((byte)(_x + x2));
                t.setY((byte)(_y + y2));
                t.setA(_a);
              }
            }
            
            _region.calc();
            return true;
            
          case 1:
            t = _map.getLayer(_layer).getTile(x1, y1);
            t.setTileset((byte)0);
            t.setX((byte)0);
            t.setY((byte)0);
            t.setA((byte)0);
            _region.calc();
            return true;
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
        _picTilesetList.setVisible(true);
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
      _selected.setXY((int)((x - _context.getCameraX()) / Settings.Map.Tile.Size) * Settings.Map.Tile.Size, (int)((y - _context.getCameraY()) / Settings.Map.Tile.Size) * Settings.Map.Tile.Size);
      
      if(button != -1) {
        return mapEditorClick(x, y, button);
      }
    } else {
      
    }
    
    return false;
  }
  
  public boolean handleMouseWheel(int delta) {
    if(_picWindow.getVisible()) {
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
  
  public void handleTilesetMouseDown(int x, int y, int button) {
    _x = (int)(x / Settings.Map.Tile.Size);
    _y = (int)(y / Settings.Map.Tile.Size);
    _w = 1;
    _h = 1;
    
    _picSelected.setXYWH(_x * Settings.Map.Tile.Size, _y * Settings.Map.Tile.Size, Settings.Map.Tile.Size, Settings.Map.Tile.Size);
    _selected.setWH(_w * Settings.Map.Tile.Size, _h * Settings.Map.Tile.Size);
    _selected.setTXYWH(_x * Settings.Map.Tile.Size, _y * Settings.Map.Tile.Size, _w * Settings.Map.Tile.Size, _h * Settings.Map.Tile.Size);
    _selected.createQuad();
  }
  
  public void handleTilesetMouseMove(int x, int y, int button) {
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
}