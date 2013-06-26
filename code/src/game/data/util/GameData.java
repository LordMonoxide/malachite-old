package game.data.util;

import game.Game;
import game.network.packet.Data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;

public abstract class GameData {
  private int _version;
  private File _file;
  private int _rev;
  protected String _name;
  protected String _note;
  protected boolean _loaded;
  protected Events _events = new Events(this);
  
  protected GameData(int version, File file) {
    _version = version;
    _file = file;
  }
  
  public    int getVersion() { return _version; }
  public String getFile() { return _file.getName();  }
  public    int getRev () { return _rev;  }
  public String getName() { return _name; }
  public String getNote() { return _note; }
  public boolean loaded() { return _loaded; }
  
  public Events events() { return _events; }
  
  public void save() {
    Buffer b = serialize();
    
    try {
      b.save(_file);
    } catch(IOException e) {
      e.printStackTrace();
    }
  }
  
  public boolean load() {
    try {
      deserialize(new Buffer(_file));
      _loaded = true;
      return true;
    } catch(FileNotFoundException e) {
    } catch(IOException e) {
      e.printStackTrace();
    }
    
    return false;
  }
  
  public Buffer serialize() {
    Buffer b = new Buffer();
    b.put(_version);
    b.put(_rev);
    b.put(_name);
    b.put(_note);
    serializeInternal(b);
    return b;
  }
  
  public void deserialize(Buffer b) {
    _version = b.getInt();
    _rev = b.getInt();
    _name = b.getString();
    _note = b.getString();
    deserializeInternal(b);
  }
  
  public void request() {
    Data.Request p = new Data.Request(this);
    Game.getInstance().send(p, Data.Response.class, new Game.PacketCallback<Data.Response>() {
      public boolean recieved(Data.Response packet) {
        if(packet.getFile().equals(getFile())) {
          remove();
          
          packet.process();
          System.out.println("GameData " + getFile() + " synced from server");
          _loaded = true;
          _events.raiseLoad();
          
          return true;
        }
        
        return false;
      }
    });
  }
  
  protected abstract void serializeInternal(Buffer b);
  protected abstract void deserializeInternal(Buffer b);
  
  public static class Events {
    private LinkedList<Load> _load = new LinkedList<Load>();
    
    private GameData _this;
    
    public Events(GameData data) {
      _this = data;
    }
    
    public void addLoadHandler(Load e) {
      e._events = this;
      _load.add(e);
      
      if(_this._loaded) {
        raiseLoad();
      }
    }
    
    public void raiseLoad() {
      for(Load e : _load) {
        e.load();
      }
    }
    
    public static abstract class Load {
      private Events _events;
      
      public abstract void load();
      public void remove() {
        _events._load.remove(this);
      }
    }
  }
}