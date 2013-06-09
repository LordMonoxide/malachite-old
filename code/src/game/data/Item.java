package game.data;

import game.data.util.Buffer;
import game.data.util.Data;
import game.data.util.Serializable;

public class Item extends Serializable implements Data {
  private static final int VERSION = 1;
  
  private String _name, _note;
  private String _sprite;
  
  public Item(String file, int crc) {
    super(file, crc);
    System.out.println("Test");
  }
  
  public String getName()   { return _name; }
  public String getNote()   { return _note; }
  public String getSprite() { return _sprite; }
  
  public Buffer serialize() {
    Buffer b = new Buffer();
    b.put(VERSION);
    b.put(_name);
    b.put(_note);
    b.put(_sprite);
    return b;
  }
  
  public void deserialize(Buffer b) {
    switch(b.getInt()) {
      case 1: deserialize01(b);
    }
  }
  
  private void deserialize01(Buffer b) {
    _name = b.getString();
    _note = b.getString();
    _sprite = b.getString();
  }
}