package game.graphics.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import network.util.Crypto;

import org.lwjgl.input.Keyboard;

import game.Game;
import game.data.util.Properties;
import game.network.packet.CharDel;
import game.network.packet.CharNew;
import game.network.packet.CharUse;
import game.network.packet.CharUse.Response;
import game.network.packet.Login;
import graphics.gl00.Context;
import graphics.shared.gui.Control;
import graphics.shared.gui.GUI;
import graphics.shared.gui.controls.Button;
import graphics.shared.gui.controls.Label;
import graphics.shared.gui.controls.List;
import graphics.shared.gui.controls.Textbox;
import graphics.shared.gui.controls.compound.Window;

public class Menu extends GUI {
  private Game _game = (Game)Context.getGame();
  
  private Listener _listener = new Listener(this);
  
  //private Picture[] _background = new Picture[15];

  private Window  _wndLogin;
  private Textbox _txtName;
  private Textbox _txtPass;
  private Button  _btnLogin;
  private Label   _lblUser;
  
  private Window  _wndChar;
  private List    _lstChar;
  private Button  _btnCharNew;
  private Button  _btnCharDel;
  private Button  _btnCharUse;
  
  private Window _wndNewChar;
  private Textbox _txtNewCharName;
  private Label _lblNewCharName;
  private Button _btnNewCharCreate;
  
  private String _savedPass;
  
  private Message _wait;
  
  public void load() {
    //_context.setBackColour(new float[] {1, 1, 1, 1});
    
    /*for(int i = 0; i < _background.length; i++) {
      _background[i] = new Picture(this);
      _background[i].setXY((i % 5) * 256, (i / 5) * 256);
      _background[i].setTexture(_textures.getTexture("gui/menu/" + i + ".png"));
      Controls().add(_background[i]);
    }*/
    
    _wndLogin = new Window(this);
    _wndLogin.setWH(272, 178);
    _wndLogin.setXY((_context.getW() - _wndLogin.getW()) / 2, (_context.getH() - _wndLogin.getH()) / 2);
    _wndLogin.setText("Login");
    _wndLogin.events().addCloseHandler(new Window.Events.Close() {
      public boolean close() {
        _context.destroy();
        return true;
      }
    });
    
    _txtName = new Textbox(this);
    _txtName.setX((_wndLogin.getClientW() - _txtName.getW()) / 2);
    _txtName.setY(_txtName.getX() + 4);
    _txtName.events().addKeyHandler(new Control.Events.Key() {
      public void down(int key) {
        if(key == Keyboard.KEY_RETURN) {
          login();
        } else {
          if(_savedPass != null && _savedPass.length() != 0) {
            _savedPass = null;
            _txtPass.setText(null);
          }
        }
      }
      
      public void up(int key) { }
      public void text(char key) { }
    });
    
    _txtPass = new Textbox(this);
    _txtPass.setXY(_txtName.getX(), _txtName.getY() + _txtName.getH() + 9);
    _txtPass.events().addKeyHandler(new Control.Events.Key() {
      public void down(int key) {
        if(key == Keyboard.KEY_RETURN) {
          login();
        } else {
          _savedPass = null;
        }
      }
      
      public void up(int key) { }
      public void text(char key) { }
    });
    
    _txtPass.events().addFocusHandler(new Control.Events.Focus() {
      public void got() {
        if(_savedPass != null) {
          _savedPass = null;
          _txtPass.setText(null);
        }
      }
      
      public void lost() { }
    });
    
    _btnLogin = new Button(this);
    _btnLogin.setXY(_txtName.getX() + (_txtName.getW() - _btnLogin.getW()) - 3, _txtPass.getY() + _txtPass.getH() + 15);
    _btnLogin.setText("Login");
    _btnLogin.events().addClickHandler(new Control.Events.Click() {
      public void clickDbl() { }
      public void click() {
        login();
      }
    });
    
    _lblUser = new Label(this);
    _lblUser.setText("Username and password:");
    _lblUser.setXY(_txtName.getX(), _txtName.getY() - _lblUser.getH() - 4);
    
    _wndLogin.Controls().add(_txtName);
    _wndLogin.Controls().add(_txtPass);
    _wndLogin.Controls().add(_btnLogin);
    _wndLogin.Controls().add(_lblUser);
    
    _wndChar = new Window(this);
    _wndChar.setWH(272, 178);
    _wndChar.setXY((_context.getW() - _wndChar.getW()) / 2, (_context.getH() - _wndChar.getH()) / 2);
    _wndChar.setText("Characters");
    _wndChar.setVisible(false);
    
    _lstChar = new List(this);
    _lstChar.setX((_wndChar.getClientW() - _lstChar.getW()) / 2);
    _lstChar.setY(22);
    
    _btnCharNew = new Button(this);
    _btnCharNew.setText("New");
    _btnCharNew.setXYWH(_lstChar.getX(), _lstChar.getY() + _lstChar.getH() + 15, 60, 20);
    _btnCharNew.events().addClickHandler(new Control.Events.Click() {
      public void clickDbl() { }
      public void click() {
        showNewChar();
      }
    });
    
    _btnCharDel = new Button(this);
    _btnCharDel.setText("Del");
    _btnCharDel.setXYWH(_btnCharNew.getX() + _btnCharNew.getW() + 10, _btnCharNew.getY(), 60, 20);
    _btnCharDel.events().addClickHandler(new Control.Events.Click() {
      public void clickDbl() { }
      public void click() {
        if(_lstChar.getSelected() != null) {
          charDel(_lstChar.getSelected().getIndex());
        }
      }
    });
    
    _btnCharUse = new Button(this);
    _btnCharUse.setText("Use");
    _btnCharUse.setXYWH(_btnCharDel.getX() + _btnCharDel.getW() + 10, _btnCharNew.getY(), 60, 20);
    _btnCharUse.events().addClickHandler(new Button.Events.Click() {
      public void clickDbl() { }
      public void click() {
        if(_lstChar.getSelected() != null) {
          charUse(_lstChar.getSelected().getIndex());
        }
      }
    });
    
    _wndChar.Controls().add(_lstChar);
    _wndChar.Controls().add(_btnCharNew);
    _wndChar.Controls().add(_btnCharDel);
    _wndChar.Controls().add(_btnCharUse);
    
    _wndNewChar = new Window(this);
    _wndNewChar.setWH(272, 178);
    _wndNewChar.setXY((_context.getW() - _wndNewChar.getW()) / 2, (_context.getH() - _wndNewChar.getH()) / 2);
    _wndNewChar.setText("New Character");
    _wndNewChar.setVisible(false);
    
    _txtNewCharName = new Textbox(this);
    _txtNewCharName.setX((_wndNewChar.getClientW() - _txtNewCharName.getW()) / 2);
    _txtNewCharName.setY(_txtNewCharName.getX() + 4);
    
    _lblNewCharName = new Label(this);
    _lblNewCharName.setText("Character Name:");
    _lblNewCharName.setXY(_txtNewCharName.getX(), _txtNewCharName.getY() - _lblNewCharName.getH() - 4);
    
    _btnNewCharCreate = new Button(this);
    _btnNewCharCreate.setText("Create");
    _btnNewCharCreate.setXYWH(_lstChar.getX(), _lstChar.getY() + _lstChar.getH() + 15, 60, 20);
    _btnNewCharCreate.events().addClickHandler(new Button.Events.Click() {
      public void clickDbl() { }
      public void click() {
        charCreate(_txtNewCharName.getText());
      }
    });
    
    _wndNewChar.Controls().add(_txtNewCharName);
    _wndNewChar.Controls().add(_lblNewCharName);
    _wndNewChar.Controls().add(_btnNewCharCreate);
    
    Controls().add(_wndLogin);
    Controls().add(_wndChar);
    Controls().add(_wndNewChar);
    
    Properties login = new Properties();
    try {
      File f = new File("../login.conf");
      
      f.createNewFile();
      
      FileInputStream fs = new FileInputStream(f);
      login.load(fs);
      _txtName.setText(login.getProperty("username"));
      _savedPass = login.getProperty("password");
      
      if(_savedPass != null && _savedPass.length() != 0) {
        if(!Crypto.validateHash(_savedPass)) {
          _savedPass = null;
        } else {
          _txtPass.setText("***");
        }
      }
      
      fs.close();
    } catch(IOException e) {
      e.printStackTrace();
    }
    
    if(_txtName.getText() != null && _txtName.getText().length() != 0) {
      _txtName.setFocus(true);
    }
  }
  
  public void destroy() {
    
  }
  
  public void resize() {
    
  }
  
  public void draw() {
    
  }
  
  public boolean logic() {
    return false;
  }
  
  private void login() {
    String name = _txtName.getText();
    String pass = _savedPass;
    
    if(name == null || _txtPass.getText() == null || name.length() == 0 || _txtPass.getText().length() == 0) {
      Message.show("You must enter a name and password.");
      return;
    }
    
    if(!Crypto.validateText(name)) {
      Message.show("Sorry, you've entered invalid characters.");
      return;
    }
    
    if(pass == null) {
      if(!Crypto.validateText(_txtPass.getText())) {
        Message.show("Sorry, you've entered invalid characters.");
        return;
      }
      
      pass = Crypto.sha256(_txtPass.getText());
    }
    
    _wait = Message.showWait("Loading...");
    
    Properties login = new Properties();
    login.setProperty("username", name);
    login.setProperty("password", pass);
    
    File f = new File("../login.conf");
    
    try {
      f.createNewFile();
      
      FileOutputStream fs = new FileOutputStream(f);
      login.store(fs, null);
      fs.close();
    } catch(IOException e) {
      e.printStackTrace();
    }
    
    _game.login(name, pass, _listener);
  }
  
  private void loggedIn(game.network.packet.Login.Response packet) {
    _wait.pop();
    
    switch(packet.getResponse()) {
      case Login.Response.RESPONSE_OKAY:
        if(packet.getName().length != 0) {
          for(String s : packet.getName()) {
            _lstChar.addItem(s, null);
          }
          
          _wndChar.setVisible(true);
        } else {
          showNewChar();
        }
        
        _wndLogin.setVisible(false);
        break;
      
      case Login.Response.RESPONSE_NOT_AUTHD:
        Message.show("Sorry, you aren't authorised to log in to this account.");
        break;
        
      case Login.Response.RESPONSE_INVALID:
        Message.show("Your username or password is incorrect.");
        break;
        
      case Login.Response.RESPONSE_SQL_EXCEPTION:
        Message.show("A server error occurred.");
        break;
    }
  }
  
  private void showNewChar() {
    if(!_game.getPermissions().canAlterChars()) {
      Message.show("Sorry, your account isn't authorised to create or delete characters.");
      return;
    }
    
    _txtNewCharName.setText(null);
    _txtNewCharName.setFocus(true);
    _wndNewChar.setVisible(true);
    _wndChar.setVisible(false);
  }
  
  private void charDel(int index) {
    if(!_game.getPermissions().canAlterChars()) {
      Message.show("Sorry, your account isn't authorised to create or delete characters.");
      return;
    }
    
    _game.charDel(index, _listener);
  }
  
  private void charDeleted(CharDel.Response packet) {
    switch(packet.getResponse()) {
      case CharDel.Response.RESPONSE_OKAY:
        _lstChar.removeItem(_lstChar.getSelected());
        Message.show("Your character has been deleted.");
        break;
        
      case CharDel.Response.RESPONSE_SQL_EXCEPTION:
        Message.show("A server error occurred.");
        break;
    }
  }
  
  private void charCreate(String name) {
    if(!_game.getPermissions().canAlterChars()) {
      Message.show("Sorry, your account isn't authorised to create or delete characters.");
      return;
    }
    
    if(!Crypto.validateText(name)) {
      Message.show("Sorry, you've entered invalid characters.");
      return;
    }
    
    //TODO: This needs to be not hardcoded
    _game.charCreate(name, "Isaac", _listener);
  }
  
  private void charCreated(CharNew.Response packet) {
    switch(packet.getResponse()) {
      case CharNew.Response.RESPONSE_OKAY:
        _lstChar.addItem(_txtNewCharName.getText(), null);
        _wndChar.setVisible(true);
        _wndNewChar.setVisible(false);
        break;
        
      case CharNew.Response.RESPONSE_EXISTS:
        Message.show("Sorry, that name has already been taken.");
        break;
        
      case CharNew.Response.RESPONSE_SQL_EXCEPTION:
        Message.show("A server error occurred.");
        break;
    }
  }
  
  private void charUse(int index) {
    _game.charUse(index, _listener);
  }
  
  private void charUsed(CharUse.Response packet) {
    switch(packet.getResponse()) {
      case CharUse.Response.RESPONSE_OKAY:
        _game.loadWorld(packet.getWorld());
        _game.loadGame(_listener);
        break;
        
      case CharUse.Response.RESPONSE_SQL_ERROR:
        Message.show("A server error occurred.");
        break;
    }
  }
  
  private void inGame() {
    game.graphics.gui.Game g = new game.graphics.gui.Game();
    g.push();
    pop();
  }
  
  public static class Listener implements Game.StateListener {
    private Menu _menu;
    
    public Listener(Menu menu) {
      _menu = menu;
    }
    
    public void loggedIn(Login.Response packet) {
      _menu.loggedIn(packet);
    }
    
    public void charDeleted(CharDel.Response packet) {
      _menu.charDeleted(packet);
    }
    
    public void charCreated(CharNew.Response packet) {
      _menu.charCreated(packet);
    }
    
    public void charUsed(Response packet) {
      _menu.charUsed(packet);
    }
    
    public void inGame() {
      _menu.inGame();
    }
  }
}