package game.graphics.themes;

import graphics.gl00.Context;
import graphics.gl00.Scalable;
import graphics.shared.gui.controls.Button;
import graphics.shared.gui.controls.List;
import graphics.shared.gui.controls.Textbox;
import graphics.themes.Theme;

public class Malachite extends Theme {
  private static Theme _instance = new Malachite();
  public static Theme getInstance() { return _instance; }
  
  public void create(Button c) {
    super.create(c);
    c.setBackColour(new float[] {0x3F / 255f, 0xCF / 255f, 0, 1});
    c.setGlowColour(new float[] {0x3F / 255f + 0.1f, 0xCF / 255f + 0.1f, 0.1f, 1});
  }
  
  public void create(List c) {
    Scalable s = Context.newScalable();
    s.setTexture(_textures.getTexture("gui/textbox.png"));
    s.setXY(-5, -5);
    s.setSize(
        new float[] {12, 12, 12, 12},
        new float[] {12, 12, 12, 12},
        25, 25, 1
    );
    
    c.setBackground(s);
    c.setWH(200, 80);
  }
  
  public void create(Textbox c) {
    Scalable s = Context.newScalable();
    s.setTexture(_textures.getTexture("gui/textbox.png"));
    s.setXY(-5, -5);
    s.setSize(
        new float[] {12, 12, 12, 12},
        new float[] {12, 12, 12, 12},
        25, 25, 1
    );
    
    c.setBackground(s);
    c.setForeColour(new float[] {0, 0, 0, 1});
    c.setWH(200, 17);
  }
}