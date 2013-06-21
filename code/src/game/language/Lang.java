package game.language;

public enum Lang {
  VITAL     ("Hit Points", "Magic Points"),
  STAT      ("Strength", "Intelligence", "Dexterity"),
  VITAL_ABBV("HP", "MP"),
  STAT_ABBV ("STR", "INT", "DEX"),
  ITEM_TYPE ("None", "Weapon", "Shield", "Armour", "Potion", "Spell", "Bling", "Currency"),
  ITEM_WEAPON("Melee", "Bow"),
  ITEM_ARMOUR("Body", "Head", "Hands", "Legs", "Feet"),
  ITEM_POTION("Heal", "Buff"),
  ITEM_BLING ("Ring", "Amulet"),
  ITEM_CURRENCY("Currency"),
  CURRENCY("Currency");
  
  private String[] _text;
  
  private Lang(String text) {
    _text = new String[] { text };
  }
  
  private Lang(String... text) {
    _text = text;
  }
  
  public String text() {
    return _text[0];
  }
  
  public String text(int index) {
    return _text[index];
  }
  
  public String[] get() {
    return _text;
  }
}