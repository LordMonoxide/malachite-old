package game.data;

import java.io.File;

import game.data.util.Buffer;
import game.data.util.GameData;
import game.world.Entity;

public class Item extends GameData {
  protected String _sprite;
  protected int    _type;
  
  protected int    _damage;
  protected float  _weight;
  
  protected int    _hpHeal, _mpHeal;
  
  private Entity.Stats.Buffs.Buff _buffHP = new Entity.Stats.Buffs.Buff();
  private Entity.Stats.Buffs.Buff _buffMP = new Entity.Stats.Buffs.Buff();
  private Entity.Stats.Buffs.Buff _buffSTR = new Entity.Stats.Buffs.Buff();
  private Entity.Stats.Buffs.Buff _buffDEX = new Entity.Stats.Buffs.Buff();
  private Entity.Stats.Buffs.Buff _buffINT = new Entity.Stats.Buffs.Buff();
  
  public Item() { }
  public Item(String file) {
    init(file);
  }
  
  public void init(String file) {
    super.initInternal(1, new File("../data/item/" + file));
  }
  
  public String getSprite() { return _sprite; }
  public int    getType()   { return _type; }
  public int    getDamage() { return _damage; }
  public float  getWeight() { return _weight; }
  public int    getHPHeal() { return _hpHeal; }
  public int    getMPHeal() { return _mpHeal; }
  public Entity.Stats.Buffs.Buff buffHP() { return _buffHP; }
  public Entity.Stats.Buffs.Buff buffMP() { return _buffMP; }
  public Entity.Stats.Buffs.Buff buffSTR() { return _buffSTR; }
  public Entity.Stats.Buffs.Buff buffDEX() { return _buffDEX; }
  public Entity.Stats.Buffs.Buff buffINT() { return _buffINT; }
  
  protected void serializeInternal(Buffer b) {
    b.put(_sprite);
    b.put(_type);
    b.put(_damage);
    b.put(_weight);
    b.put(_hpHeal);
    b.put(_mpHeal);
    b.put(_buffHP.val());
    b.put(_buffHP.percent());
    b.put(_buffMP.val());
    b.put(_buffMP.percent());
    b.put(_buffSTR.val());
    b.put(_buffSTR.percent());
    b.put(_buffDEX.val());
    b.put(_buffDEX.percent());
    b.put(_buffINT.val());
    b.put(_buffINT.percent());
  }
  
  protected void deserializeInternal(Buffer b) {
    switch(getVersion()) {
      case 1: deserialize01(b); break;
    }
  }
  
  private void deserialize01(Buffer b) {
    _sprite = b.getString();
    _type   = b.getInt();
    _damage = b.getInt();
    _weight = b.getFloat();
    _hpHeal = b.getInt();
    _mpHeal = b.getInt();
    _buffHP.val(b.getFloat());
    _buffHP.percent(b.getBool());
    _buffMP.val(b.getFloat());
    _buffMP.percent(b.getBool());
    _buffSTR.val(b.getFloat());
    _buffSTR.percent(b.getBool());
    _buffDEX.val(b.getFloat());
    _buffDEX.percent(b.getBool());
    _buffINT.val(b.getFloat());
    _buffINT.percent(b.getBool());
  }
  
  /*  0000 0000 0000 0000 0000 0000 0000 0000
   *  ^     ATTRIBS     ^ ^  SUB  ^ ^ TYPES ^
   */
  public static final int ITEM_TYPE_BITMASK             = 0x0000000F;
  public static final int ITEM_TYPE_BITSHIFT            = 0;
  
  public static final int ITEM_SUBTYPE_BITMASK          = 0x000000F0;
  public static final int ITEM_SUBTYPE_BITSHIFT         = 4;
  
  public static final int ITEM_ATTRIBS_BITMASK          = 0xFFFFFF00;
  public static final int ITEM_ATTRIBS_BITSHIFT         = 8;
  
  public static final int ITEM_TYPE_NONE                = 0x00;
  
  public static final int ITEM_TYPE_WEAPON              = 0x01;
  public static final int ITEM_TYPE_WEAPON_MELEE        = 0x00 * 0x10;
  public static final int ITEM_TYPE_WEAPON_BOW          = 0x01 * 0x10;
  
  public static final int ITEM_TYPE_SHIELD              = 0x02;
  
  public static final int ITEM_TYPE_ARMOUR              = 0x03;
  public static final int ITEM_TYPE_ARMOUR_BODY         = 0x00 * 0x10;
  public static final int ITEM_TYPE_ARMOUR_HEAD         = 0x01 * 0x10;
  public static final int ITEM_TYPE_ARMOUR_HAND         = 0x02 * 0x10;
  public static final int ITEM_TYPE_ARMOUR_LEGS         = 0x03 * 0x10;
  public static final int ITEM_TYPE_ARMOUR_FEET         = 0x04 * 0x10;
  public static final int ITEM_TYPE_ARMOUR_COUNT        = 5;
  
  public static final int ITEM_TYPE_POTION              = 0x04;
  public static final int ITEM_TYPE_POTION_HEAL         = 0x00 * 0x10;
  public static final int ITEM_TYPE_POTION_HEAL_PERCENT = 0x01 * 0x100;
  public static final int ITEM_TYPE_POTION_BUFF         = 0x01 * 0x10;
  
  public static final int ITEM_TYPE_SPELL               = 0x05;
  
  public static final int ITEM_TYPE_BLING               = 0x06;
  public static final int ITEM_TYPE_BLING_RING          = 0x00 * 0x10;
  public static final int ITEM_TYPE_BLING_AMULET        = 0x01 * 0x10;
  public static final int ITEM_TYPE_BLING_COUNT         = 2;
  
  public static final int ITEM_TYPE_CURRENCY            = 0x07;
}