package game.graphics.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;

import network.util.Crypto;

import org.lwjgl.input.Keyboard;

import game.data.util.Properties;
import game.network.packet.Login;
import graphics.shared.gui.Control;
import graphics.shared.gui.GUI;
import graphics.shared.gui.controls.Button;
import graphics.shared.gui.controls.Label;
import graphics.shared.gui.controls.List;
import graphics.shared.gui.controls.Picture;
import graphics.shared.gui.controls.Textbox;
import graphics.shared.gui.controls.compound.Window;

public class Menu extends GUI {
  private Events _events = new Events(this);
  
  private Picture[] _background = new Picture[15];

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
  
  private String _savedPass;
  
  private Message _wait;
  
  public Events events() { return _events; }
  
  public void load() {
    _context.setBackColour(new float[] {1, 1, 1, 1});
    
    for(int i = 0; i < _background.length; i++) {
      _background[i] = new Picture(this);
      _background[i].setXY((i % 5) * 256, (i / 5) * 256);
      _background[i].setTexture(_textures.getTexture("gui/menu/" + i + ".png"));
      Controls().add(_background[i]);
    }
    
    _wndLogin = new Window(this);
    _wndLogin.setWH(272, 178);
    _wndLogin.setXY((_context.getW() - _wndLogin.getW()) / 2, (_context.getH() - _wndLogin.getH()) / 2);
    _wndLogin.setText("Login");
    _wndLogin.events().onClose(new Window.Events.Close() {
      public boolean event() {
        return true;
      }
    });
    
    _txtName = new Textbox(this);
    _txtName.setX((_wndLogin.getClientW() - _txtName.getW()) / 2);
    _txtName.setY(_txtName.getX() + 4);
    _txtName.events().onKeyDown(new Control.Events.Key() {
      public void event(int key) {
        if(key == Keyboard.KEY_RETURN) {
          login();
        } else {
          if(_savedPass != null && _savedPass.length() != 0) {
            _savedPass = null;
            _txtPass.setText(null);
          }
        }
      }
    });
    
    _txtPass = new Textbox(this);
    _txtPass.setXY(_txtName.getX(), _txtName.getY() + _txtName.getH() + 9);
    _txtPass.events().onKeyDown(new Control.Events.Key() {
      public void event(int key) {
        if(key == Keyboard.KEY_RETURN) {
          login();
        } else {
          _savedPass = null;
        }
      }
    });
    _txtPass.events().onGotFocus(new Control.Events.Focus() {
      public void event() {
        if(_savedPass != null) {
          _savedPass = null;
          _txtPass.setText(null);
        }
      }
    });
    
    _btnLogin = new Button(this);
    _btnLogin.setXY(_txtName.getX() + (_txtName.getW() - _btnLogin.getW()) - 3, _txtPass.getY() + _txtPass.getH() + 15);
    _btnLogin.setText("Login");
    _btnLogin.events().onClick(new Control.Events.Click() {
      public void event() {
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
    
    _btnCharDel = new Button(this);
    _btnCharDel.setText("Del");
    _btnCharDel.setXYWH(_btnCharNew.getX() + _btnCharNew.getW() + 10, _btnCharNew.getY(), 60, 20);
    
    _btnCharUse = new Button(this);
    _btnCharUse.setText("Use");
    _btnCharUse.setXYWH(_btnCharDel.getX() + _btnCharDel.getW() + 10, _btnCharNew.getY(), 60, 20);
    
    _wndChar.Controls().add(_lstChar);
    _wndChar.Controls().add(_btnCharNew);
    _wndChar.Controls().add(_btnCharDel);
    _wndChar.Controls().add(_btnCharUse);
    
    Controls().add(_wndLogin);
    Controls().add(_wndChar);
    
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
    
    _events.raiseLogin(name, pass);
  }
  
  private void loginResponse(game.network.packet.Login.Response packet) {
    _wait.pop();
    
    switch(packet.getResponse()) {
      case Login.Response.RESPONSE_OKAY:
        _wndLogin.setVisible(false);
        _wndChar.setVisible(true);
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
  
  public static class Events {
    private static Menu _menu;
    
    protected Events(Menu m) {
      _menu = m;
    }
    
    private LinkedList<Login> _login = new LinkedList<Login>();
    
    public void onLogin(Login e) {
      _login.add(e);
    }
    
    public void raiseLogin(String name, String pass) {
      for(Login e : _login) {
        e.event(name, pass);
      }
    }
    
    public static abstract class Login {
      public abstract void event(String name, String pass);
      
      public final void loggedIn(game.network.packet.Login.Response packet) {
        _menu.loginResponse(packet);
      }
    }
  }
}