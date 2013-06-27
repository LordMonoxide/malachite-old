package game.data;

import java.io.File;

import game.data.util.Buffer;
import game.data.util.GameData;

public class NPC extends GameData {
  protected String _sprite;
  
  public NPC() { }
  public NPC(String file) {
    init(file);
  }
  
  public void init(String file) {
    super.initInternal(1, new File("../data/NPCs/" + file));
  }
  
  public String getSprite() { return _sprite; }
  
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