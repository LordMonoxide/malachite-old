package game.data.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public abstract class Serializable {
  private File _f;
  private String _file;
  private int _crc;
  
  protected Serializable(File file) {
    _f = file;
    _f.getParentFile().mkdirs();
    _file = _f.getName();
  }
  
  protected Serializable(String file, int crc) {
    _f = new File("../data/" + crc);
    _f.getParentFile().mkdirs();
    _file = file;
    _crc = crc;
  }
  
  public String getFile() {
    return _file;
  }
  
  public int getCRC() {
    return _crc;
  }
  
  public boolean exists() {
    return _f.exists();
  }
  
  protected void updateCRC() {
    try {
      Buffer b = new Buffer(_f);
      _crc = b.crc();
    } catch(IOException e) {
      _crc = 0;
    }
  }
  
  public void save() {
    Buffer b = serialize();
    
    try {
      b.save(_f);
    } catch(IOException e) {
      e.printStackTrace();
    }
  }
  
  public boolean load() {
    try {
      Buffer b = new Buffer(_f);
      deserialize(b);
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