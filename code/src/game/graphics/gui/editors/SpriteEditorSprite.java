package game.graphics.gui.editors;

import java.util.ArrayList;

import game.data.Sprite;
import game.data.util.Buffer;

public class SpriteEditorSprite extends Sprite {
  private Sprite _sprite;
  private int _spriteCRC;
  protected ArrayList<Frame> _frame = super._frame;
  protected ArrayList<Anim>  _anim  = super._anim;
  
  public SpriteEditorSprite(Sprite sprite) {
    super(sprite.getFile(), sprite.getCRC());
    
    _sprite = sprite;
    
    // Deep-copy source Sprite into
    // this SpriteEditorSprite's structure
    Buffer b = _sprite.serialize();
    deserialize(b);
    _spriteCRC = b.crc();
  }
  
  public boolean isChanged() {
    return _spriteCRC != serialize().crc();
  }
  
  public void update() {
    _sprite.deserialize(serialize());
  }
  
  public Sprite getSprite() {
    return _sprite;
  }
  
  public void setName(String name) {
    _name = name;
  }
  
  public void setNote(String note) {
    _note = note;
  }
  
  public void setW(int w) {
    _w = w;
  }
  
  public void setH(int h) {
    _h = h;
  }
  
  public void setScript(String script) {
    _script = script;
  }
  
  public String getTexture() {
    return _texture;
  }
  
  public void setTexture(String texture) {
    _texture = texture;
  }
}