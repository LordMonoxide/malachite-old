package game.graphics.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;

import org.lwjgl.input.Keyboard;

import game.data.util.Crypto;
import game.data.util.Properties;
import graphics.shared.gui.Control;
import graphics.shared.gui.GUI;
import graphics.shared.gui.controls.Button;
import graphics.shared.gui.controls.Label;
import graphics.shared.gui.controls.Picture;
import graphics.shared.gui.controls.Textbox;
import graphics.shared.gui.controls.compound.Window;

public class Menu extends GUI {
  private Events _events = new Events();
  
  private Picture[] _background = new Picture[15];

  private Window _wndLogin;
  private Textbox _txtName;
  private Textbox _txtPass;
  private Button _btnLogin;
  private Label _lblUser;
  
  private String _savedPass;
  
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
    
    Control.Events.Key loginKey = new Control.Events.Key() {
      public void event(int key) {
        if(key == Keyboard.KEY_RETURN) {
          login();
        }
      }
    };
    
    _txtName = new Textbox(this);
    _txtName.setX((_wndLogin.getClientW() - _txtName.getW()) / 2);
    _txtName.setY(_txtName.getX() + 4);
    _txtName.events().onKeyDown(loginKey);
    
    _txtPass = new Textbox(this);
    _txtPass.setXY(_txtName.getX(), _txtName.getY() + _txtName.getH() + 9);
    _txtPass.events().onKeyDown(loginKey);
    
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
    
    Controls().add(_wndLogin);
    
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
      _txtPass.setFocus(true);
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
    
    //_wait = Message.showWait("Loading...");
    
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
  
  public static class Events {
    private LinkedList<Login> _login = new LinkedList<Login>();
    
    public void onLogin(Login e) { _login.add(e); }
    
    public void raiseLogin(String name, String pass) {
      for(Login e : _login) {
        e.event(name, pass);
      }
    }
    
    public static abstract class Login {
      public abstract void event(String name, String pass);
    }
  }
}