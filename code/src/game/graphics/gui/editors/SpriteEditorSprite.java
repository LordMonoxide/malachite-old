package game.graphics.gui.editors;

import java.util.ArrayList;

import game.Game;
import game.data.Sprite;
import game.data.util.Buffer;
import game.network.packet.editors.EditorData;
import graphics.gl00.Context;

public class SpriteEditorSprite extends Sprite {
  protected ArrayList<Frame> _frame = super._frame;
  protected ArrayList<Anim>  _anim  = super._anim;
  
  protected SpriteEditorSprite(String file) { this(file, false); }
  protected SpriteEditorSprite(String file, boolean newData) {
    super(file);
    
    if(!newData) {
      request();
    } else {
      _loaded = true;
      _events.raiseLoad();
    }
  }
  
  protected String getTexture() { return _texture; }
  
  protected void setName(String name) { _name = name; }
  protected void setNote(String note) { _note = note; }
  protected void setW(int w) { _w = w; }
  protected void setH(int h) { _h = h; }
  protected void setScript(String script) { _script = script; }
  protected void setTexture(String texture) { _texture = texture; }
  
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
  
  protected void serializeInternal(Buffer b) {
    super.serializeInternal(b);
  }
  
  protected void deserializeInternal(Buffer b) {
    super.deserializeInternal(b);
  }
}