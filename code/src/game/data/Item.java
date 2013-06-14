package game.data;

import game.data.util.Buffer;
import game.data.util.Data;
import game.data.util.Serializable;

public class Item extends Serializable implements Data {
  private static final int VERSION = 2;
  
  protected String _name, _note;
  protected String _sprite;
  protected Type   _type = Type.None;
  protected int    _damage;
  
  public Item(String file, int crc) {
    super(file, crc);
  }
  
  public String getName()   { return _name; }
  public String getNote()   { return _note; }
  public String getSprite() { return _sprite; }
  public Type   getType()   { return _type; }
  public int    getDamage() { return _damage; }
  
  public Buffer serialize() {
    Buffer b = new Buffer();
    b.put(VERSION);
    b.put(_name);
    b.put(_note);
    b.put(_sprite);
    b.put(_type.ordinal());
    b.put(_damage);
    return b;
  }
  
  public void deserialize(Buffer b) {
    switch(b.getInt()) {
      case 1: deserialize01(b); break;
      case 2: deserialize02(b); break;
    }
  }
  
  private void deserialize01(Buffer b) {
    _name   = b.getString();
    _note   = b.getString();
    _sprite = b.getString();
  }
  
  private void deserialize02(Buffer b) {
    _name   = b.getString();
    _note   = b.getString();
    _sprite = b.getString();
    _type   = Type.valueOf(b.getInt());
    _damage = b.getInt();
  }
  
  public enum Type {
    None, Potion, Melee, Bow, Shield, Spell, Body, Head, Hand, Legs, Feet, Ring, Amulet;
    
    public static Type valueOf(int index) {
      switch(index) {
        case  0: return None;
        case  1: return Potion;
        case  2: return Melee;
        case  3: return Bow;
        case  4: return Shield;
        case  5: return Spell;
        case  6: return Body;
        case  7: return Head;
        case  8: return Hand;
        case  9: return Legs;
        case 10: return Feet;
        case 11: return Ring;
        case 12: return Amulet;
      }
      
      return null;
    }
  }
}