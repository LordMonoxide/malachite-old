package game.data;

import java.io.File;

import game.data.util.Buffer;
import game.data.util.GameData;

public class NPC extends GameData {
  public String toString() {
    return "NPC '" + getFile() + "' (" + super.toString() + ")";
  }
  
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
    b.put(_sprite);
  }
  
  protected void deserializeInternal(Buffer b) {
    switch(getVersion()) {
      case 1: deserialize01(b); break;
    }
  }
  
  private void deserialize01(Buffer b) {
    _sprite = b.getString();
  }
}