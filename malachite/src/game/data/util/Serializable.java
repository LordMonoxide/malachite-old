package game.data.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public abstract class Serializable {
  protected String _path;
  protected String _file;
  
  protected Serializable(String path, String file) {
    _path = path;
    _file = file;
  }
  
  public String getFile() {
    return _file;
  }
  
  public void save() {
    Buffer b = serialize();
    
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