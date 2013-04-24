package game.graphics.themes;

import graphics.themes.Theme;

public class Malachite extends Theme {
  private static Theme _instance = new Malachite();
  public static Theme getInstance() { return _instance; }
  
  private Malachite() {
    super();
    _buttonBackColour = new float[] {0x3F / 255f, 0xCF / 255f, 0, 1};
    _buttonGlowColour = new float[] {_buttonBackColour[0] + 0.1f, _buttonBackColour[1] + 0.1f, _buttonBackColour[2] + 0.1f, 1};
  }
}