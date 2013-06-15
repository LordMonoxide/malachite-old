package game.data;

import game.data.util.Buffer;
import game.data.util.Data;
import game.data.util.Serializable;

public class Item extends Serializable implements Data {
  private static final int VERSION = 1;
  
  protected String _name, _note;
  protected String _sprite;
  protected int    _type;
  protected int    _damage;
  
  public Item(String file, int crc) {
    super(file, crc);
  }
  
  public String getName()   { return _name; }
  public String getNote()   { return _note; }
  public String getSprite() { return _sprite; }
  public int    getType()   { return _type; }
  public int    getDamage() { return _damage; }
  
  public Buffer serialize() {
    Buffer b = new Buffer();
    b.put(VERSION);
    b.put(_name);
    b.put(_note);
    b.put(_sprite);
    b.put(_type);
    b.put(_damage);
    return b;
  }
  
  public void deserialize(Buffer b) {
    switch(b.getInt()) {
      case 1: deserialize01(b); break;
    }
  }
  
  private void deserialize01(Buffer b) {
    _name   = b.getString();
    _note   = b.getString();
    _sprite = b.getString();
    _type   = b.getInt();
    _damage = b.getInt();
  }
  
  /*  0000 0000 0000 0000 0000 0000 0000 0000
   *  ^     ATTRIBS     ^ ^  SUB  ^ ^ TYPES ^
   */
  public static final int ITEM_TYPE_MASK                = 0xFF;
  public static final int ITEM_TYPE_NONE                = 0x00;
  public static final int ITEM_TYPE_NONE_NONE           = 0x00 * 0x100;
  
  public static final int ITEM_TYPE_WEAPON              = 0x01;
  public static final int ITEM_TYPE_WEAPON_MELEE        = 0x00 * 0x100;
  public static final int ITEM_TYPE_WEAPON_BOW          = 0x01 * 0x100;
  public static final int ITEM_TYPE_WEAPON_SHIELD       = 0x02 * 0x100;
  
  public static final int ITEM_TYPE_ARMOUR              = 0x02;
  public static final int ITEM_TYPE_ARMOUR_BODY         = 0x00 * 0x100;
  public static final int ITEM_TYPE_ARMOUR_HEAD         = 0x01 * 0x100;
  public static final int ITEM_TYPE_ARMOUR_HAND         = 0x02 * 0x100;
  public static final int ITEM_TYPE_ARMOUR_LEGS         = 0x03 * 0x100;
  public static final int ITEM_TYPE_ARMOUR_FEET         = 0x04 * 0x100;
  
  public static final int ITEM_TYPE_POTION              = 0x03;
  public static final int ITEM_TYPE_POTION_HEAL         = 0x00 * 0x100;
  public static final int ITEM_TYPE_POTION_HEAL_FIXED   = 0x00 * 0x10000;
  public static final int ITEM_TYPE_POTION_HEAL_PERCENT = 0x00 * 0x10000;
  public static final int ITEM_TYPE_POTION_BUFF         = 0x01 * 0x100;
  
  public static final int ITEM_TYPE_SPELL               = 0x04;
  public static final int ITEM_TYPE_SPELL_SPELL         = 0x00 * 0x100;
  
  public static final int ITEM_TYPE_BLING               = 0x05;
  public static final int ITEM_TYPE_BLING_RING          = 0x00 * 0x100;
  public static final int ITEM_TYPE_BLING_AMULET        = 0x01 * 0x100;
}