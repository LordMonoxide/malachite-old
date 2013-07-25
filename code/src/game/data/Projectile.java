package game.data;

import game.data.util.Buffer;
import game.data.util.GameData;

import java.io.File;

public class Projectile extends GameData {
  public String toString() {
    return "Projectile '" + getFile() + "' (" + super.toString() + ")";
  }
  
  public Projectile() { }
  public Projectile(String file) {
    init(file);
  }
  
  public void init(String file) {
    super.initInternal(1, new File("../data/projectiles/" + file));
  }
  
  protected void serializeInternal(Buffer b) {
    
  }
  
  protected void deserializeInternal(Buffer b) {
    switch(getVersion()) {
      case 1: deserialize01(b); break;
    }
  }
  
  private void deserialize01(Buffer b) {
    
  }
}