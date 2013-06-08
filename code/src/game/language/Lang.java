package game.language;

public enum Lang {
  VITAL("Hit Points", "Magic Points"),
  STAT ("Strength", "Intelligence", "Dexterity"),
  VITAL_ABBV("HP", "MP"),
  STAT_ABBV ("STR", "INT", "DEX");
  
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
}