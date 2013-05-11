package game.graphics.gui;

import java.nio.ByteBuffer;

import graphics.shared.gui.GUI;
import graphics.shared.gui.controls.Picture;
import graphics.shared.textures.Texture;
import graphics.shared.textures.Textures;

public class Menu extends GUI {
  private Picture _pic;
  
  public void load() {
    _context.setBackColour(new float[] {1, 1, 1, 1});
    
    ByteBuffer b = ByteBuffer.allocateDirect(32 * 32 * 4);
    for(int i = 0; i < b.capacity(); i += 4) {
      b.put((byte)255);
      b.put((byte)0);
      b.put((byte)0);
      b.put((byte)255);
    }
    b.flip();
    
    Texture t = Textures.getInstance().getTexture("mal.png", true);
    t.update(32, 32, 32, 32, b);
    
    _pic = new Picture(this);
    _pic.setTexture(t);
    
    Controls().add(_pic);
  }
  
  public void destroy() {
    
  }
  
  public void resize() {
    
  }
}