package game.graphics.gui.editors;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.JOptionPane;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import game.Game;
import game.data.Item;
import game.data.Map;
import game.data.NPC;
import game.data.Sprite;
import game.data.util.GameData;
import game.graphics.gui.Message;
import game.network.packet.editors.EditorData;
import game.network.packet.editors.EditorSave;
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
  private Game _game = Game.getInstance();
  
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
  private Label     _lblSpriteAnim;
  private Dropdown  _drpSpriteAnim;
  private Label     _lblSpriteLoc;
  private Textbox   _txtSpriteX;
  private Textbox   _txtSpriteY;
  private Textbox   _txtSpriteZ;
  
  private ScrollPanel _splItem;
  private Label     _lblItemFile;
  private Dropdown  _drpItemFile;
  private Label     _lblItemVal;
  private Textbox   _txtItemVal;
  private Label     _lblItemLoc;
  private Textbox   _txtItemX;
  private Textbox   _txtItemY;
  private Textbox   _txtItemZ;
  
  private ScrollPanel _splNPC;
  private Label     _lblNPCFile;
  private Dropdown  _drpNPCFile;
  private Label     _lblNPCLoc;
  private Textbox   _txtNPCX;
  private Textbox   _txtNPCY;
  private Textbox   _txtNPCZ;
  
  private game.world.Sprite _pickSprite;
  private boolean   _pickLoc;
  
  private int _tab;
  private boolean _shift;
  private boolean _lock;
  private int _mouseDelta;
  
  private int _lastTileX;
  private int _lastTileY;
  
  private int _tileset;
  private int _layer;
  private int _x, _y;
  private int _w = 1;
  private int _h = 1;
  private byte _a = (byte)255;
  
  private int _attrib;
  
  private String     _file;
  private MapEditorMap.Sprite _sprite;
  private MapEditorMap.Item   _item;
  private MapEditorMap.NPC    _npc;
  
  private int _mx, _my;
  private MapEditorMap _map;
  private Region _region;
  private ArrayList<Region> _regions = new ArrayList<Region>();
  
  protected void load() {
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
      public void clickDbl() { }
      public void click() {
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
      _btnTab[i].events().addClickHandler(btnTabClick);
      _picWindow.controls().add(_btnTab[i]);
    }

    _btnTab[0].setText("Tiles");
    _btnTab[1].setText("Attribs");
    _btnTab[2].setText("Sprites");
    _btnTab[3].setText("Items");
    _btnTab[4].setText("NPCs");
    
    _picTab = new Picture[5];
    for(int i = 0; i < _picTab.length; i++) {
      _picTab[i] = new Picture(this);
      _picTab[i].setBackColour(new float[] {0.1f, 0.1f, 0.1f, 1});
      _picTab[i].setXYWH(8, _btnTab[i].getY() + _btnTab[i].getH(), 256, 256);
      _picTab[i].setVisible(false);
      _picWindow.controls().add(_picTab[i]);
    }
    
    _picTileset = new Picture(this, true);
    _picTileset.events().addMouseHandler(new Control.Events.Mouse() {
      public void down(int x, int y, int button) { handleTilesetMouseDown(x, y, button); }
      public void move(int x, int y, int button) { handleTilesetMouseMove(x, y, button); }
      public void up(int x, int y, int button) { }
    });
    
    _picSelected = new Picture(this);
    _picSelected.setWH(Settings.Map.Tile.Size, Settings.Map.Tile.Size);
    _picSelected.setBackColour(new float[] {0, 1, 0, 0.33f});
    
    _picTab[0].controls().add(_picTileset);
    _picTileset.controls().add(_picSelected);
    
    _picLayers = new Picture(this);
    _picLayers.setBackColour(new float[] {0.1f, 0.1f, 0.1f, 1});
    _picLayers.setXYWH(_picTab[0].getX() + _picTab[0].getW() + 4, _picTab[0].getY(), 100, 256);
    
    _btnLayer = new Button[Settings.Map.Depth];
    
    Control.Events.Click btnLayerClick = new Control.Events.Click() {
      public void clickDbl() { }
      public void click() {
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
      _btnLayer[i].events().addClickHandler(btnLayerClick);
      _picLayers.controls().add(_btnLayer[i]);
    }
    
    _picWindow.controls().add(_picLayers);
    
    _picTilesetList = new Picture(this);
    _picTilesetList.setBackColour(new float[] {0.33f, 0.33f, 0.33f, 0.66f});
    _picTilesetList.setBorderColour(new float[] {0, 0, 0, 1});
    _picTilesetList.setWH(_context.getW(), 144);
    _picTilesetList.setVisible(false);
    
    _picTilesetBack = new Picture(this);
    _picTilesetBack.setBackColour(new float[] {0, 0, 0, 0});
    _picTilesetBack.setWH(files.size() * 136, _picTilesetList.getH());
    _picTilesetList.controls().add(_picTilesetBack);
    
    int i = 0;
    _picTilesets = new Picture[files.size()];
    for(String file : files) {
      _picTilesets[i] = new Picture(this);
      _picTilesets[i].setTexture(_textures.getTexture(file));
      _picTilesets[i].setXYWH(i * 136 + 8, 8, 128, 128);
      _picTilesetBack.controls().add(_picTilesets[i]);
      i++;
    }
    
    // Attribs tab
    Control.Events.Click btnAttribClick = new Control.Events.Click() {
      public void clickDbl() { }
      public void click() {
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
      _btnAttrib[i].events().addClickHandler(btnAttribClick);
      _picTab[1].controls().add(_btnAttrib[i]);
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
          selSprite((MapEditorMap.Sprite)((ScrollPanelData)item)._data);
        }
      }
    });
    
    _lblSpriteFile = new Label(this);
    _lblSpriteFile.setXY(4, 4);
    _lblSpriteFile.setText("File");
    
    _drpSpriteFile = new Dropdown(this);
    _drpSpriteFile.setXY(_lblSpriteFile.getX(), _lblSpriteFile.getY() + _lblSpriteFile.getH());
    _drpSpriteFile.events().addSelectHandler(new Dropdown.Events.Select() {
      public void select(Dropdown.Item item) {
        updateSprite();
        _map.createSprites();
      }
    });
    
    _lblSpriteAnim = new Label(this);
    _lblSpriteAnim.setXY(_drpSpriteFile.getX(), _drpSpriteFile.getY() + _drpSpriteFile.getH() + 8);
    _lblSpriteAnim.setText("Animation");
    
    _drpSpriteAnim = new Dropdown(this);
    _drpSpriteAnim.setXY(_lblSpriteAnim.getX(), _lblSpriteAnim.getY() + _lblSpriteAnim.getH());
    _drpSpriteAnim.events().addSelectHandler(new Dropdown.Events.Select() {
      public void select(Dropdown.Item item) {
        updateSprite();
        _map.createSprites();
      }
    });
    
    _lblSpriteLoc = new Label(this);
    _lblSpriteLoc.setXY(_drpSpriteAnim.getX(), _drpSpriteAnim.getY() + _drpSpriteAnim.getH() + 8);
    _lblSpriteLoc.setText("Location");
    
    _txtSpriteX = new Textbox(this);
    _txtSpriteX.setXY(_lblSpriteLoc.getX(), _lblSpriteLoc.getY() + _lblSpriteLoc.getH());
    _txtSpriteX.setW(40);
    _txtSpriteX.setNumeric(true);
    _txtSpriteX.events().addChangeHandler(new Textbox.Events.Change() {
      public void change() {
        _sprite.x = Integer.parseInt(_txtSpriteX.getText());
        _map.createSprites();
      }
    });
    
    _txtSpriteY = new Textbox(this);
    _txtSpriteY.setXY(_txtSpriteX.getX() + _txtSpriteX.getW() + 4, _txtSpriteX.getY());
    _txtSpriteY.setW(40);
    _txtSpriteY.setNumeric(true);
    _txtSpriteY.events().addChangeHandler(new Textbox.Events.Change() {
      public void change() {
        _sprite.y = Integer.parseInt(_txtSpriteY.getText());
        _map.createSprites();
      }
    });
    
    _txtSpriteZ = new Textbox(this);
    _txtSpriteZ.setXY(_txtSpriteY.getX() + _txtSpriteY.getW() + 4, _txtSpriteY.getY());
    _txtSpriteZ.setW(40);
    _txtSpriteZ.setNumeric(true);
    _txtSpriteZ.events().addChangeHandler(new Textbox.Events.Change() {
      public void change() {
        _sprite.z = Byte.parseByte(_txtSpriteZ.getText());
        _map.createSprites();
      }
    });

    _splSprite.controls().add(_lblSpriteFile);
    _splSprite.controls().add(_drpSpriteFile);
    _splSprite.controls().add(_lblSpriteAnim);
    _splSprite.controls().add(_drpSpriteAnim);
    _splSprite.controls().add(_lblSpriteLoc);
    _splSprite.controls().add(_txtSpriteX);
    _splSprite.controls().add(_txtSpriteY);
    _splSprite.controls().add(_txtSpriteZ);
    
    _picTab[2].controls().add(_splSprite);
    
    // Items tab
    _splItem = new ScrollPanel(this);
    _splItem.setXY(4, 4);
    _splItem.events().onButtonAdd(new ScrollPanel.Events.Button() {
      public void event() {
        addItem();
      }
    });
    _splItem.events().onButtonDel(new ScrollPanel.Events.Button() {
      public void event() {
        delItem();
      }
    });
    _splItem.events().onSelect(new ScrollPanel.Events.Select() {
      public void event(ScrollPanel.Item item) {
        if(item != null) {
          selItem((MapEditorMap.Item)((ScrollPanelData)item)._data);
        }
      }
    });
    
    _lblItemFile = new Label(this);
    _lblItemFile.setXY(4, 4);
    _lblItemFile.setText("File");
    
    _drpItemFile = new Dropdown(this);
    _drpItemFile.setXY(_lblItemFile.getX(), _lblItemFile.getY() + _lblItemFile.getH());
    _drpItemFile.events().addSelectHandler(new Dropdown.Events.Select() {
      public void select(Dropdown.Item item) {
        updateItem();
        _map.createItems();
      }
    });
    
    _lblItemLoc = new Label(this);
    _lblItemLoc.setXY(_drpItemFile.getX(), _drpItemFile.getY() + _drpItemFile.getH() + 8);
    _lblItemLoc.setText("Location");
    
    _txtItemX = new Textbox(this);
    _txtItemX.setXY(_lblItemLoc.getX(), _lblItemLoc.getY() + _lblItemLoc.getH());
    _txtItemX.setW(40);
    _txtItemX.setNumeric(true);
    _txtItemX.events().addChangeHandler(new Textbox.Events.Change() {
      public void change() {
        _item.x = Integer.parseInt(_txtItemX.getText());
        _map.createItems();
      }
    });
    
    _txtItemY = new Textbox(this);
    _txtItemY.setXY(_txtItemX.getX() + _txtItemX.getW() + 4, _txtItemX.getY());
    _txtItemY.setW(40);
    _txtItemY.setNumeric(true);
    _txtItemY.events().addChangeHandler(new Textbox.Events.Change() {
      public void change() {
        _item.y = Integer.parseInt(_txtItemY.getText());
        _map.createItems();
      }
    });
    
    _txtItemZ = new Textbox(this);
    _txtItemZ.setXY(_txtItemY.getX() + _txtItemY.getW() + 4, _txtItemY.getY());
    _txtItemZ.setW(40);
    _txtItemZ.setNumeric(true);
    _txtItemZ.events().addChangeHandler(new Textbox.Events.Change() {
      public void change() {
        _item.z = Byte.parseByte(_txtItemZ.getText());
        _map.createItems();
      }
    });
    
    _lblItemVal = new Label(this);
    _lblItemVal.setXY(_txtItemX.getX(), _txtItemX.getY() + _txtItemX.getH() + 8);
    _lblItemVal.setText("Val");
    
    _txtItemVal = new Textbox(this);
    _txtItemVal.setXY(_lblItemVal.getX(), _lblItemVal.getY() + _lblItemVal.getH());
    _txtItemVal.setW(40);
    _txtItemVal.setNumeric(true);
    _txtItemVal.events().addChangeHandler(new Textbox.Events.Change() {
      public void change() {
        _item.val = Integer.parseInt(_txtItemVal.getText());
        _map.createItems();
      }
    });
    
    _splItem.controls().add(_lblItemFile);
    _splItem.controls().add(_drpItemFile);
    _splItem.controls().add(_lblItemLoc);
    _splItem.controls().add(_txtItemX);
    _splItem.controls().add(_txtItemY);
    _splItem.controls().add(_txtItemZ);
    _splItem.controls().add(_lblItemVal);
    _splItem.controls().add(_txtItemVal);
    
    _picTab[3].controls().add(_splItem);
    
    // NPCs tab
    _splNPC = new ScrollPanel(this);
    _splNPC.setXY(4, 4);
    _splNPC.events().onButtonAdd(new ScrollPanel.Events.Button() {
      public void event() {
        addNPC();
      }
    });
    _splNPC.events().onButtonDel(new ScrollPanel.Events.Button() {
      public void event() {
        delNPC();
      }
    });
    _splNPC.events().onSelect(new ScrollPanel.Events.Select() {
      public void event(ScrollPanel.Item item) {
        if(item != null) {
          selNPC((MapEditorMap.NPC)((ScrollPanelData)item)._data);
        }
      }
    });
    
    _lblNPCFile = new Label(this);
    _lblNPCFile.setXY(4, 4);
    _lblNPCFile.setText("File");
    
    _drpNPCFile = new Dropdown(this);
    _drpNPCFile.setXY(_lblNPCFile.getX(), _lblNPCFile.getY() + _lblNPCFile.getH());
    _drpNPCFile.events().addSelectHandler(new Dropdown.Events.Select() {
      public void select(Dropdown.Item item) {
        updateNPC();
        _map.createNPCs();
      }
    });
    
    _lblNPCLoc = new Label(this);
    _lblNPCLoc.setXY(_drpNPCFile.getX(), _drpNPCFile.getY() + _drpNPCFile.getH() + 8);
    _lblNPCLoc.setText("Location");
    
    _txtNPCX = new Textbox(this);
    _txtNPCX.setXY(_lblNPCLoc.getX(), _lblNPCLoc.getY() + _lblNPCLoc.getH());
    _txtNPCX.setW(40);
    _txtNPCX.setNumeric(true);
    _txtNPCX.events().addChangeHandler(new Textbox.Events.Change() {
      public void change() {
        _npc.x = Integer.parseInt(_txtNPCX.getText());
        _map.createNPCs();
      }
    });
    
    _txtNPCY = new Textbox(this);
    _txtNPCY.setXY(_txtNPCX.getX() + _txtNPCX.getW() + 4, _txtNPCX.getY());
    _txtNPCY.setW(40);
    _txtNPCY.setNumeric(true);
    _txtNPCY.events().addChangeHandler(new Textbox.Events.Change() {
      public void change() {
        _npc.y = Integer.parseInt(_txtNPCY.getText());
        _map.createNPCs();
      }
    });
    
    _txtNPCZ = new Textbox(this);
    _txtNPCZ.setXY(_txtNPCY.getX() + _txtNPCY.getW() + 4, _txtNPCY.getY());
    _txtNPCZ.setW(40);
    _txtNPCZ.setNumeric(true);
    _txtNPCZ.events().addChangeHandler(new Textbox.Events.Change() {
      public void change() {
        _npc.z = Byte.parseByte(_txtNPCZ.getText());
        _map.createNPCs();
      }
    });
    
    _splNPC.controls().add(_lblNPCFile);
    _splNPC.controls().add(_drpNPCFile);
    _splNPC.controls().add(_lblNPCLoc);
    _splNPC.controls().add(_txtNPCX);
    _splNPC.controls().add(_txtNPCY);
    _splNPC.controls().add(_txtNPCZ);
    
    _picTab[4].controls().add(_splNPC);
    
    controls().add(_picWindow);
    controls().add(_picTilesetList);
    
    _selected = Context.newDrawable();
    _selected.setColour(new float[] {1, 1, 1, 0.5f});
    
    _game.send(new EditorData.List(EditorData.DATA_TYPE_SPRITE), EditorData.List.class, new Game.PacketCallback<EditorData.List>() {
      public boolean recieved(EditorData.List packet) {
        if(packet.type() == EditorData.DATA_TYPE_SPRITE) {
          
          for(EditorData.List.ListData data : packet.data()) {
            _drpSpriteFile.add(new DropdownData(data.file(), data.name(), data.note()));
          }
          
          return true;
        }
        
        return false;
      }
    });
    
    _game.send(new EditorData.List(EditorData.DATA_TYPE_ITEM), EditorData.List.class, new Game.PacketCallback<EditorData.List>() {
      public boolean recieved(EditorData.List packet) {
        if(packet.type() == EditorData.DATA_TYPE_ITEM) {
          for(EditorData.List.ListData data : packet.data()) {
            _drpItemFile.add(new DropdownData(data.file(), data.name(), data.note()));
          }
          
          return true;
        }
        
        return false;
      }
    });
    
    _game.send(new EditorData.List(EditorData.DATA_TYPE_NPC), EditorData.List.class, new Game.PacketCallback<EditorData.List>() {
      public boolean recieved(EditorData.List packet) {
        if(packet.type() == EditorData.DATA_TYPE_NPC) {
          for(EditorData.List.ListData data : packet.data()) {
            _drpNPCFile.add(new DropdownData(data.file(), data.name(), data.note()));
          }
          
          return true;
        }
        
        return false;
      }
    });
    
    _attribDrawable = Context.newDrawable();
    _attribDrawable.setWH(Settings.Map.Size, Settings.Map.Size);
    _attribDrawable.setColour(new float[] {1, 1, 1, 0.5f});
    _attribDrawable.createQuad();
    
    setRegion(_game.getEntity().getRegion());
    
    setTab(_tab);
    setLayer(_layer);
    setTileset(_tileset);
    setAttrib(_attrib);
  }
  
  protected void destroy() {
    
  }
  
  protected void resize() {
    _picWindow.setXY((_context.getW() - _picWindow.getW()) / 2, (_context.getH() - _picWindow.getH()) / 2);
    _picTilesetList.setY((_context.getH() - (_picWindow.getY() + _picWindow.getH()) - _picTilesetList.getH()) / 2 + _picWindow.getY() + _picWindow.getH());
    _picTilesetList.setW(_context.getW());
    _picTilesetBack.setX((_picTilesetList.getW() - _picTilesets[_tileset].getW()) / 2 - _tileset * 136 - 54);
    
    _splSprite.setWH(_picTab[2].getW() - _splSprite.getX() * 2, _picTab[2].getH() - _splSprite.getY() * 2);
    _splItem  .setWH(_picTab[2].getW() - _splItem  .getX() * 2, _picTab[2].getH() - _splItem  .getY() * 2);
    _splNPC   .setWH(_picTab[2].getW() - _splNPC   .getX() * 2, _picTab[2].getH() - _splNPC   .getY() * 2);
  }
  
  protected void draw() {
    switch(_tab) {
      case 0:
        _selected.draw();
        break;
    }
  }
  
  protected boolean logic() {
    return false;
  }
  
  public boolean unload() {
    if(_regions.size() != 0) {
      switch(JOptionPane.showConfirmDialog(null, "Would you like to save your changes?")) {
        case JOptionPane.CANCEL_OPTION:
        case JOptionPane.CLOSED_OPTION:
          return false;
          
        case JOptionPane.YES_OPTION:
          EditorSave.Map packet = new EditorSave.Map();
          
          for(Region r : _regions) {
            MapEditorMap m = (MapEditorMap)r.getMap();
            
            System.out.println("Sending map " + m.getFile());
            packet.addData(m);
            r.setMap(m.getMap());
          }
          
          if(packet.size() != 0) {
            _game.send(packet);
          }
          
          break;
          
        case JOptionPane.NO_OPTION:
          for(Region r : _regions) {
            MapEditorMap m = (MapEditorMap)r.getMap();
            
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
  
  private void setRegion(Region region) {
    if(region != _region) {
      if(_attribDrawCallback != -1) {
        _region.events().removeDraw(_attribDrawCallback);
      }
      
      _region = region;
      
      if(!_regions.contains(_region)) {
        System.out.println("Adding region " + region.getMap().getX() + ", " + region.getMap().getY());
        _regions.add(_region);
        _region.setMap(new MapEditorMap(_region.getMap()));
      }
      
      _map = (MapEditorMap)_region.getMap();
      _map.events().addLoadHandler(new GameData.Events.Load() {
        public void load() {
          _mx = _map.getX();
          _my = _map.getY();
          System.out.println(_mx + "\t" + _my + "\t" + "Changing");
          
          _attribDrawable.setTexture(_map.getAttribMask(_layer));
          
          _splSprite.clear();
          for(MapEditorMap.Sprite s : _map._sprite) {
            _splSprite.add(new ScrollPanelData(s));
          }
          
          _splItem.clear();
          for(MapEditorMap.Item d : _map._item) {
            _splItem.add(new ScrollPanelData(d));
          }
          
          _splNPC.clear();
          for(MapEditorMap.NPC d : _map._npc) {
            _splNPC.add(new ScrollPanelData(d));
          }
          
          _attribDrawCallback = _region.events().onDraw(new Region.Events.Draw() {
            public void event(int z) {
              if(z == Settings.Map.Depth - 1) {
                switch(_tab) {
                  case 1: _attribDrawable.draw(); break;
                  case 2: _map.drawSprites(); break;
                  case 3: _map.drawItems(); break;    
                  case 4: _map.drawNPCs(); break;
                }
              }
            }
          });
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
    
    _attribDrawable.setTexture(_map.getAttribMask(_layer));
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
    _game.send(new EditorData.List(EditorData.DATA_TYPE_SPRITE), EditorData.List.class, new Game.PacketCallback<EditorData.List>() {
      public boolean recieved(EditorData.List packet) {
        final Chooser s = new Chooser(packet.data());
        s.events().addSelectHandler(new Chooser.Events.Select() {
          public void select(String file) {
            s.pop();
            
            _file = file;
            _pickLoc = true;
            _picWindow.setVisible(false);
            _pickSprite = game.world.Sprite.add(file);
            _pickSprite.setX(Mouse.getX() - _context.getCameraX());
            _pickSprite.setY(Mouse.getY() - _context.getCameraY());
            _pickSprite.setZ(_layer);
          }
        });
        
        s.push();
        
        return true;
      }
    });
  }
  
  private void delSprite() {
    if(_map._sprite.remove(_sprite)) {
      _splSprite.remove();
      _map.createSprites();
    } else {
      Message.show("Error", "Couldn't delete sprite");
    }
  }
  
  private void selSprite(MapEditorMap.Sprite sprite) {
    _drpSpriteAnim.clear();
    
    _sprite = sprite;
    
    if(_sprite.file != null) {
      int i = 0;
      for(Dropdown.Item file : _drpSpriteFile) {
        if(((DropdownData)file)._file.equals(_sprite.file)) {
          _drpSpriteFile.setSeletected(i);
        }
        i++;
      }
      
      final Sprite s = _game.getSprite(_sprite.file);
      s.events().addLoadHandler(new GameData.Events.Load() {
        public void load() {
          for(Sprite.Anim anim : s.anim) {
            _drpSpriteAnim.add(new Dropdown.Item(anim._name));
          }
        }
      });
    } else {
      _drpSpriteFile.setSeletected(-1);
    }
    
    if(_sprite.anim != null) {
      int i = 0;
      for(Dropdown.Item file : _drpSpriteAnim) {
        if(file.getText().equals(_sprite.anim)) {
          _drpSpriteAnim.setSeletected(i);
        }
        i++;
      }
    } else {
      _drpSpriteAnim.setSeletected(-1);
    }
    
    _txtSpriteX.setText(String.valueOf(_sprite.x));
    _txtSpriteY.setText(String.valueOf(_sprite.y));
    _txtSpriteZ.setText(String.valueOf(_sprite.z));
  }
  
  private void updateSprite() {
    DropdownData sprite = (DropdownData)_drpSpriteFile.get();
    Dropdown.Item anim = _drpSpriteAnim.get();
    _sprite.file = sprite != null ? sprite._file : null;
    _sprite.anim = anim != null ? anim.getText() : null;
    _sprite.x = Integer.parseInt(_txtSpriteX.getText());
    _sprite.y = Integer.parseInt(_txtSpriteY.getText());
    _sprite.z = Byte.parseByte(_txtSpriteZ.getText());
    selSprite(_sprite);
  }
  
  private void addItem() {
    _game.send(new EditorData.List(EditorData.DATA_TYPE_ITEM), EditorData.List.class, new Game.PacketCallback<EditorData.List>() {
      public boolean recieved(EditorData.List packet) {
        final Chooser s = new Chooser(packet.data());
        s.events().addSelectHandler(new Chooser.Events.Select() {
          public void select(String file) {
            s.pop();
            
            _file = file;
            _pickLoc = true;
            _picWindow.setVisible(false);
            
            final Item d = _game.getItem(file);
            d.events().addLoadHandler(new GameData.Events.Load() {
              public void load() {
                _pickSprite = game.world.Sprite.add(d.getSprite());
                _pickSprite.setX(Mouse.getX() - _context.getCameraX());
                _pickSprite.setY(Mouse.getY() - _context.getCameraY());
                _pickSprite.setZ(_layer);
              }
            });
          }
        });
        
        s.push();
        
        return true;
      }
    });
  }
  
  private void delItem() {
    if(_map._item.remove(_item)) {
      _splItem.remove();
      _map.createItems();
    } else {
      Message.show("Error", "Couldn't delete item");
    }
  }
  
  private void selItem(MapEditorMap.Item item) {
    _item = item;
    
    if(_item.file != null) {
      int i = 0;
      for(Dropdown.Item file : _drpItemFile) {
         if(((DropdownData)file)._file.equals(_item.file)) {
          _drpItemFile.setSeletected(i);
        }
        i++;
      }
    } else {
      _drpItemFile.setSeletected(-1);
    }
    
    _txtItemX.setText(String.valueOf(_item.x));
    _txtItemY.setText(String.valueOf(_item.y));
    _txtItemZ.setText(String.valueOf(_item.z));
    _txtItemVal.setText(String.valueOf(_item.val));
  }
  
  private void updateItem() {
    DropdownData item = (DropdownData)_drpItemFile.get();
    _item.file = item != null ? item._file : null;
    _item.x = Integer.parseInt(_txtItemX.getText());
    _item.y = Integer.parseInt(_txtItemY.getText());
    _item.z = Byte.parseByte(_txtItemZ.getText());
    _item.val = Integer.parseInt(_txtItemVal.getText());
    selItem(_item);
  }
  
  private void addNPC() {
    _game.send(new EditorData.List(EditorData.DATA_TYPE_NPC), EditorData.List.class, new Game.PacketCallback<EditorData.List>() {
      public boolean recieved(EditorData.List packet) {
        final Chooser s = new Chooser(packet.data());
        s.events().addSelectHandler(new Chooser.Events.Select() {
          public void select(String file) {
            s.pop();
            
            _file = file;
            _pickLoc = true;
            _picWindow.setVisible(false);
            
            final NPC d = _game.getNPC(file);
            d.events().addLoadHandler(new GameData.Events.Load() {
              public void load() {
                _pickSprite = game.world.Sprite.add(d.getSprite());
                _pickSprite.setX(Mouse.getX() - _context.getCameraX());
                _pickSprite.setY(Mouse.getY() - _context.getCameraY());
                _pickSprite.setZ(_layer);
              }
            });
          }
        });
        
        s.push();
        
        return true;
      }
    });
  }
  
  private void delNPC() {
    if(_map._npc.remove(_npc)) {
      _splNPC.remove();
      _map.createNPCs();
    } else {
      Message.show("Error", "Couldn't delete NPCs");
    }
  }
  
  private void selNPC(MapEditorMap.NPC npc) {
    _npc = npc;
    
    if(_npc.file != null) {
      int i = 0;
      for(Dropdown.Item file : _drpNPCFile) {
         if(((DropdownData)file)._file.equals(_npc.file)) {
          _drpNPCFile.setSeletected(i);
        }
        i++;
      }
    } else {
      _drpNPCFile.setSeletected(-1);
    }
    
    _txtNPCX.setText(String.valueOf(_npc.x));
    _txtNPCY.setText(String.valueOf(_npc.y));
    _txtNPCZ.setText(String.valueOf(_npc.z));
  }
  
  private void updateNPC() {
    DropdownData npc = (DropdownData)_drpNPCFile.get();
    _npc.file = npc != null ? npc._file : null;
    _npc.x = Integer.parseInt(_txtNPCX.getText());
    _npc.y = Integer.parseInt(_txtNPCY.getText());
    _npc.z = Byte.parseByte(_txtNPCZ.getText());
    selNPC(_npc);
  }
  
  private boolean mapEditorClick(int x, int y, int button) {
    x -= _context.getCameraX();
    y -= _context.getCameraY();
    
    int mx = x / Settings.Map.Size;
    int my = y / Settings.Map.Size;
    if(x < 0) mx -= 1;
    if(y < 0) my -= 1;
    
    if((_mx != mx || _my != my) || _region == null) {
      setRegion(_game.getWorld().getRegion(mx, my));
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
        
        if(x1 == _lastTileX && y1 == _lastTileY) return true;
        _lastTileX = x1;
        _lastTileY = y1;
        
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
        
        if(x1 == _lastTileX && y1 == _lastTileY) return true;
        _lastTileX = x1;
        _lastTileY = y1;
        
        Map.Attrib a = _map.getLayer(_layer).getAttrib(x1, y1);
        
        switch(button) {
          case 0:
            a._type = (byte)Map.Attrib.Type.values()[_attrib].val();
            _map.updateAttrib(_layer, x1, y1, Map.Attrib.Type.values()[_attrib].col());
            return true;
            
          case 1:
            a._type = (byte)0;
            _map.updateAttrib(_layer, x1, y1, new byte[] {0, 0, 0, 0});
            return true;
        }
        
        break;
        
      case 2:
        if(button == 0) {
          if(_pickLoc) {
            final MapEditorMap.Sprite s = new MapEditorMap.Sprite();
            
            final Sprite sprite = _game.getSprite(_file);
            sprite.events().addLoadHandler(new GameData.Events.Load() {
              public void load() {
                s.anim = sprite.anim.get(0)._name;
              }
            });
            
            s.file = _file;
            s.x = x;
            s.y = y;
            s.z = (byte)_layer;
            
            _map._sprite.add(s);
            _splSprite.add(new ScrollPanelData(s));
            _map.createSprites();
            
            _pickSprite.remove();
            _pickLoc = false;
          }
        }
        
        break;
        
      case 3:
        if(button == 0) {
          if(_pickLoc) {
            MapEditorMap.Item s = new MapEditorMap.Item();
            
            s.file = _file;
            s.x = x;
            s.y = y;
            s.z = (byte)_layer;
            
            _map._item.add(s);
            _splItem.add(new ScrollPanelData(s));
            _map.createItems();
            
            _pickSprite.remove();
            _pickLoc = false;
          }
        }
        
        break;
        
      case 4:
        if(button == 0) {
          if(_pickLoc) {
            MapEditorMap.NPC s = new MapEditorMap.NPC();
            
            s.file = _file;
            s.x = x;
            s.y = y;
            s.z = (byte)_layer;
            
            _map._npc.add(s);
            _splNPC.add(new ScrollPanelData(s));
            _map.createNPCs();
            
            _pickSprite.remove();
            _pickLoc = false;
          }
        }
        
        break;
    }
    
    return false;
  }
  
  protected boolean handleKeyDown(int key) {
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
  
  protected boolean handleKeyUp(int key) {
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
  
  protected boolean handleMouseDown(int x, int y, int button) {
    if(_picWindow.getVisible()) return true;
    return mapEditorClick(x, y, button);
  }
  
  protected boolean handleMouseUp(int x, int y, int button) {
    _lastTileX = -1;
    _lastTileY = -1;
    return _picWindow.getVisible();
  }
  
  protected boolean handleMouseMove(int x, int y, int button) {
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
        case 3:
        case 4:
          if(_pickLoc) {
            _pickSprite.setX(x - _context.getCameraX());
            _pickSprite.setY(y - _context.getCameraY());
          }
      }
    } else {
      
    }
    
    return false;
  }
  
  protected boolean handleMouseWheel(int delta) {
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
  
  private class DropdownData extends Dropdown.Item {
    private String _file;
    
    public DropdownData(String file, String name, String note) {
      super(file + ": " + name + " - " + note);
      _file = file;
    }
  }
  
  private class ScrollPanelData extends ScrollPanel.Item {
    MapEditorMap.Data _data;
    
    public ScrollPanelData(MapEditorMap.Data data) {
      _data = data;
    }
  }
  
  public static class Chooser extends GUI {
    private Events _events = new Events();
    
    private Window _wndWindow;
    private List   _lstData;
    
    private EditorData.List.ListData[] _data;
    
    public Chooser(EditorData.List.ListData[] data) {
      _data = data;
    }
    
    public void load() {
      _wndWindow = new Window(this);
      _wndWindow.setText("Choose Sprite");
      
      _lstData = new List(this);
      _lstData.setXYWH(8, 8, 400, 200);
      
      Control.Events.Click accept = new Control.Events.Click() {
        public void click() { }
        public void clickDbl() {
          _events.raiseSelect(((ListItem)_lstData.getSelected())._file);
        }
      };
      
      for(EditorData.List.ListData s : _data) {
        ListItem l = (ListItem)_lstData.addItem(new ListItem(this, s.file()));
        l.setText(s.file() + ": " + s.name() + " - " + s.note());
        l.events().addClickHandler(accept);
      }
      
      _wndWindow.setWH(_lstData.getX() + _lstData.getW() + 8, _lstData.getY() + _lstData.getH() + 28);
      _wndWindow.events().addCloseHandler(new Window.Events.Close() {
        public boolean close() {
          pop();
          return true;
        }
      });
      _wndWindow.controls().add(_lstData);
      
      controls().add(_wndWindow);
      resize();
    }
    
    public void destroy() {
      
    }
    
    public void resize() {
      _wndWindow.setXY((_context.getW() - _wndWindow.getW()) / 2, (_context.getH() - _wndWindow.getH()) / 2);
    }
    
    public void draw() {
      
    }
    
    public boolean logic() {
      return false;
    }
    
    public Events events() {
      return _events;
    }
    
    private class ListItem extends graphics.shared.gui.controls.List.ListItem {
      private String _file;
      
      protected ListItem(GUI gui, String file) {
        super(gui);
        _file = file;
      }
    }
    
    public static class Events {
      private LinkedList<Select> _select = new LinkedList<Select>();
      
      public void addSelectHandler(Select e) { _select.add(e); }
      
      public void raiseSelect(String file) {
        for(Select e : _select) {
          e.select(file);
        }
      }
      
      public static abstract class Select {
        public abstract void select(String file);
      }
    }
  }
}