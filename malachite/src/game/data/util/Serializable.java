package game.data.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public abstract class Serializable {
  protected String _path;
  
  protected Serializable(String path) {
    _path = path;
  }
  
  public void save(String name) {
    Buffer b = serialize();
    
    try {
      b.save(new File("../data/" + _path + "/" + name));
    } catch(IOException e) {
      e.printStackTrace();
    }
  }
  
  public boolean load(String name) {
    try {
      Buffer b = new Buffer(new File("../data/" + _path + "/" + name));
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