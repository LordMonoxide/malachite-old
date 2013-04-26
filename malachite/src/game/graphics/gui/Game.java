package game.graphics.gui;

import javax.swing.JOptionPane;

import org.lwjgl.input.Keyboard;

import game.graphics.gui.editors.DataSelection;
import game.graphics.gui.editors.MapEditor;
import game.graphics.gui.editors.SpriteEditor;
import game.settings.Settings;
import game.world.Entity;
import game.world.Region;
import game.world.Sprite;
import game.world.Entity.EntityCallback;
import graphics.gl00.Canvas;
import graphics.gl00.Context;
import graphics.gl00.Drawable;
import graphics.shared.fonts.Font;
import graphics.shared.gui.GUI;
import graphics.shared.gui.Control.ControlEventClick;
import graphics.shared.gui.Control.ControlEventKey;
import graphics.shared.gui.controls.Button;
import graphics.shared.gui.controls.Label;
import graphics.shared.gui.controls.Picture;
import graphics.shared.gui.controls.Textbox;

public class Game extends GUI {
  private game.Game _game = (game.Game)Context.getGame();
  private Font _font = Context.getFonts().getDefault();
  private float[] _fontColour = {1, 0, 1, 1};
  private Drawable _debugText;
  
  private Entity _entity = _game.getEntity();
  
  private MapEditor _editMap;
  
  private Textbox _txtChat;
  private Picture _picAdmin;
  private Label   _lblAdmin;
  private Button  _btnEdit[];
  
  private boolean[] _key = new boolean[4];
  
  public void load() {
    _entity.setEntityCallback(new EntityCallback() {
      public void move(Entity e) {
        handleEntityMove();
      }
    });
    
    _txtChat = new Textbox(this);
    _txtChat.setXY(4, _context.getH() - _txtChat.getH() - 4);
    _txtChat.setVisible(false);
    _txtChat.addEventKeyDownHandler(new ControlEventKey() {
      public void event(int key) {
        handleChatText(key);
      }
    });
    
    _picAdmin = new Picture(this, true);
    _picAdmin.setBackColour(new float[] {0.33f, 0.33f, 0.33f, 0.66f});
    _picAdmin.setBorderColour(new float[] {0, 0, 0, 1});
    _picAdmin.setWH(250, 300);
    _picAdmin.setXY((_context.getW() - _picAdmin.getW()) / 2, (_context.getH() - _picAdmin.getH()) / 2);
    _picAdmin.setVisible(false);
    
    _lblAdmin = new Label(this);
    _lblAdmin.setText("Administration");
    _lblAdmin.setXY((_picAdmin.getW() - _lblAdmin.getW()) / 2, 8);
    
    _picAdmin.Controls().add(_lblAdmin);
    
    _btnEdit = new Button[6];
    for(int i = 0; i < _btnEdit.length; i++) {
      _btnEdit[i] = new Button(this);
      _btnEdit[i].setXYWH(8, 30 + i * 19, 90, 20);
      _picAdmin.Controls().add(_btnEdit[i]);
    }
    
    _btnEdit[0].setText("Edit Maps");
    _btnEdit[0].addEventClickHandler(new ControlEventClick() {
      public void event() {
        if(_editMap == null) {
          _editMap = new MapEditor();
          _editMap.load();
          _editMap.setRegion(_entity.getRegion());
          _editMap.push();
          _picAdmin.setVisible(false);
        }
      }
    });
    _btnEdit[1].setText("Edit Sprites");
    _btnEdit[1].addEventClickHandler(new ControlEventClick() {
      public void event() {
        SpriteEditor editor = new SpriteEditor();
        editor.load();
        DataSelection dataSel = new DataSelection(editor, "sprites");
        dataSel.load();
        dataSel.push();
        _picAdmin.setVisible(false);
      }
    });
    _btnEdit[2].setText("Edit NPCs");
    _btnEdit[3].setText("Edit Items");
    _btnEdit[4].setText("Edit Spells");
    _btnEdit[5].setText("Edit Effects");
    
    Controls().add(_txtChat);
    Controls().add(_picAdmin);
    
    _font.getTexture().load();
    Canvas c = new Canvas("Debug text", 256, 256);
    c.bind();
    _font.draw(4,  4, "Graphics:", _fontColour);
    _font.draw(4, 14, "Logic:", _fontColour);
    _font.draw(4, 24, "Physics:", _fontColour);
    _font.draw(4, 34, "Map: ", _fontColour);
    _font.draw(4, 44, "Loc:", _fontColour);
    _font.draw(4, 54, "Layer:", _fontColour);
    _font.draw(4, 64, "RLoc:", _fontColour);
    _font.draw(4, 74, "XVel:", _fontColour);
    _font.draw(4, 84, "YVel:", _fontColour);
    _font.draw(4, 94, "Bearing:", _fontColour);
    c.unbind();
    
    _debugText = Context.newDrawable();
    _debugText.setTexture(c.getTexture());
    _debugText.createQuad();
    
    updateCamera();
  }
  
  public void destroy() {
    
  }
  
  public void resize() {
    _picAdmin.setXY((_context.getW() - _picAdmin.getW()) / 2, (_context.getH() - _picAdmin.getH()) / 2);
    
    updateCamera();
  }
  
  public boolean draw() {
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
    
    _debugText.draw();
    _font.draw(53,  4, _context.getFPS() + "hz", _fontColour);
    _font.draw(53, 14, _context.getLogicFPS() + "hz", _fontColour);
    _font.draw(53, 24, _game.getWorld().getFPS() + "hz", _fontColour);
    _font.draw(53, 34, _entity.getMX() + ", " + _entity.getMY(), _fontColour);
    _font.draw(53, 44, _entity.getX() + ", " + _entity.getY(), _fontColour);
    _font.draw(53, 54, String.valueOf(_entity.getZ()), _fontColour);
    _font.draw(53, 64, _entity.getRX() + ", " + _entity.getRY(), _fontColour);
    _font.draw(53, 74, String.valueOf(_entity.getVel() * _entity.getVelScaleX()), _fontColour);
    _font.draw(53, 84, String.valueOf(_entity.getVel() * _entity.getVelScaleY()), _fontColour);
    _font.draw(53, 94, String.valueOf(_entity.getBear()), _fontColour);
    
    _matrix.pop();
    
    return true;
  }
  
  private void updateCamera() {
    _context.setCameraX(-_entity.getX() + _context.getW() / 2);
    _context.setCameraY(-_entity.getY() + _context.getH() / 2);
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
      _entity.setVelScaleX(1);
      _entity.setVelScaleY(1);
      _entity.setBear(a);
      
      if(_entity.getVelTarget() == 0) {
        _entity.startMoving();
      }
    } else {
      if(_entity.getVelTarget() != 0) {
        _entity.stopMoving();
      }
    }
  }
  
  public boolean handleKeyDown(int key) {
    if(!_txtChat.getVisible()) {
      switch(key) {
        case Keyboard.KEY_W:
          _key[0] = true;
          checkMovement();
          return true;
          
        case Keyboard.KEY_A:
          _key[2] = true;
          checkMovement();
          return true;
          
        case Keyboard.KEY_S:
          _key[1] = true;
          checkMovement();
          return true;
        
        case Keyboard.KEY_D:
          _key[3] = true;
          checkMovement();
          return true;
          
        case Keyboard.KEY_T:
          _txtChat.setVisible(true);
          _txtChat.setFocus(true);
          return true;
          
        case Keyboard.KEY_SLASH:
          _txtChat.setVisible(true);
          _txtChat.setText("/");
          _txtChat.setFocus(true);
          return true;
        
        case Keyboard.KEY_F1:
          _picAdmin.setVisible(!_picAdmin.getVisible());
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
          _key[0] = false;
          checkMovement();
          return true;
          
        case Keyboard.KEY_A:
          _key[2] = false;
          checkMovement();
          return true;
          
        case Keyboard.KEY_S:
          _key[1] = false;
          checkMovement();
          return true;
          
        case Keyboard.KEY_D:
          _key[3] = false;
          checkMovement();
          return true;
      }
    }
    
    return false;
  }
  
  private void handleEntityMove() {
    updateCamera();
  }
  
  private void handleChatText(int key) {
    if(key == Keyboard.KEY_RETURN) {
      if(_txtChat.getText() == null || _txtChat.getText().length() == 0) {
        _txtChat.setVisible(false);
        return;
      }
      
      String chat = _txtChat.getText();
      String text[] = chat.split(" ");
      
      // /xyz
      if(chat.startsWith("/xyz")) {
        if(chat.matches("^\\/xyz [-+]?[0-9]*\\.?[0-9]+ [-+]?[0-9]*\\.?[0-9]+ [0-" + (Settings.Map.Depth - 1) + "]$")) {
          _entity.setX(Float.parseFloat(text[1]));
          _entity.setY(Float.parseFloat(text[2]));
          _entity.setZ(Integer.parseInt(text[3]));
          _txtChat.setText(null);
          _txtChat.setVisible(false);
        } else {
          JOptionPane.showMessageDialog(null, "Usage: /xyz # # layer");
        }
        
        return;
      }
      
      // /xy
      if(chat.startsWith("/xy")) {
        if(chat.matches("^\\/xy [-+]?[0-9]*\\.?[0-9]+ [-+]?[0-9]*\\.?[0-9]+$")) {
          _entity.setX(Float.parseFloat(text[1]));
          _entity.setY(Float.parseFloat(text[2]));
          _txtChat.setText(null);
          _txtChat.setVisible(false);
        } else {
          JOptionPane.showMessageDialog(null, "Usage: /xy # #");
        }
        
        return;
      }
      
      // /x
      if(chat.startsWith("/x")) {
        if(chat.matches("^\\/x [-+]?[0-9]*\\.?[0-9]+$")) {
          _entity.setX(Float.parseFloat(text[1]));
          _txtChat.setText(null);
          _txtChat.setVisible(false);
        } else {
          JOptionPane.showMessageDialog(null, "Usage: /x #");
        }
        
        return;
      }
      
      // /y
      if(chat.startsWith("/y")) {
        if(chat.matches("^\\/y [-+]?[0-9]*\\.?[0-9]+$")) {
          _entity.setY(Float.parseFloat(text[1]));
          _txtChat.setText(null);
          _txtChat.setVisible(false);
        } else {
          JOptionPane.showMessageDialog(null, "Usage: /y #");
        }
        
        return;
      }
      
      // /z
      if(chat.startsWith("/z")) {
        if(chat.matches("^\\/z [0-" + (Settings.Map.Depth - 1) + "]$")) {
          _entity.setZ(Integer.parseInt(text[1]));
          _txtChat.setText(null);
          _txtChat.setVisible(false);
        } else {
          JOptionPane.showMessageDialog(null, "Usage: /z layer");
        }
        
        return;
      }
      
      JOptionPane.showMessageDialog(null, "Please enter a valid command.");
    }
  }
}