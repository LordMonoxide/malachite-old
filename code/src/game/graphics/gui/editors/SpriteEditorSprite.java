package game.graphics.gui.editors;

import java.util.ArrayList;

import game.Game;
import game.data.Sprite;
import game.data.util.Buffer;
import game.network.packet.editors.EditorData;
import graphics.gl00.Context;

public class SpriteEditorSprite extends Sprite {
  private int _spriteCRC;
  protected ArrayList<Frame> _frame = super._frame;
  protected ArrayList<Anim>  _anim  = super._anim;
  
  public SpriteEditorSprite(String file) { this(file, false); }
  public SpriteEditorSprite(String file, boolean newData) {
    super(file);
    
    if(!newData) {
      request();
    } else {
      _loaded = true;
      _events.raiseLoad();
    }
  }
  
  public boolean isChanged() {
    return _spriteCRC != serialize().crc();
  }
  
  public String getTexture() { return _texture; }
  
  public void setName(String name) { _name = name; }
  public void setNote(String note) { _note = note; }
  public void setW(int w) { _w = w; }
  public void setH(int h) { _h = h; }
  public void setScript(String script) { _script = script; }
  public void setTexture(String texture) { _texture = texture; }
  
  public void request() {
    EditorData.Request p = new EditorData.Request(this);
    Game.getInstance().send(p, EditorData.Response.class, new Game.PacketCallback<EditorData.Response>() {
      public boolean recieved(final EditorData.Response packet) {
        if(packet.getType() == EditorData.DATA_TYPE_SPRITE && packet.getFile().equals(getFile())) {
          remove();
          
          Context.getContext().addLoadCallback(new Context.Loader.Callback() {
            public void load() {
              deserialize(new Buffer(packet.getData()));
              
              System.out.println("Full sprite " + getFile() + " synced from server");
              _loaded = true;
              _events.raiseLoad();
            }
          }, false, "spriteeditorsprite");
          
          return true;
        }
        
        return false;
      }
    });
  }
  
  public void serializeInternal(Buffer b) {
    super.serializeInternal(b);
  }
  
  public void deserializeInternal(Buffer b) {
    super.deserializeInternal(b);
  }
}