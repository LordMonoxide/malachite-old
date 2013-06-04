package game.data.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public abstract class Serializable {
  protected String _path;
  protected String _file;
  private int _crc;
  
  protected Serializable(String path, String file) {
    _path = path;
    _file = file;
  }
  
  public String getFile() {
    return _file;
  }
  
  public int getCRC() {
    return _crc;
  }
  
  public void updateCRC() {
    try {
      Buffer b = new Buffer(new File("../data/" + _path + "/" + _file));
      _crc = b.crc();
    } catch(IOException e) {
      //e.printStackTrace();
      _crc = 0;
    }
  }
  
  public void save() {
    Buffer b = serialize();
    _crc = b.crc();
    
    try {
      b.save(new File("../data/" + _path + "/" + _file));
    } catch(IOException e) {
      e.printStackTrace();
    }
  }
  
  public boolean load() {
    try {
      Buffer b = new Buffer(new File("../data/" + _path + "/" + _file));
      deserialize(b);
      _crc = b.crc();
      return true;
    } catch(FileNotFoundException e) {
    } catch(IOException e) {
      e.printStackTrace();
    }
    
    return false;
  }
  
  public abstract Buffer serialize();
  public abstract void deserialize(Buffer b);
}