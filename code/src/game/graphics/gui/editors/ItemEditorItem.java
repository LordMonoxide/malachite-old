package game.graphics.gui.editors;

import game.Game;
import game.data.Item;
import game.data.util.Buffer;
import game.network.packet.editors.EditorData;
import graphics.gl00.Context;

public class ItemEditorItem extends Item {
  protected ItemEditorItem(String file) { this(file, false); }
  protected ItemEditorItem(String file, boolean newData) {
    super(file);
    
    if(!newData) {
      request();
    } else {
      _loaded = true;
      _events.raiseLoad();
    }
  }
  
  protected void setName  (String name)   { _name = name; }
  protected void setNote  (String note)   { _note = note; }
  protected void setSprite(String sprite) { _sprite = sprite; }
  protected void setType  (int type)      { _type = type; }
  protected void setDamage(int damage)    { _damage = damage; }
  protected void setProjectile(String projectile) { _projectile = projectile; }
  protected void setSpeed (int speed)     { _speed = speed; }
  protected void setWeight(float weight)  { _weight = weight; }
  protected void setHPHeal(int hpHeal)    { _hpHeal = hpHeal; }
  protected void setMPHeal(int mpHeal)    { _mpHeal = mpHeal; }
  
  public void request() {
    EditorData.Request p = new EditorData.Request(this);
    Game.getInstance().send(p, EditorData.Response.class, new Game.PacketCallback<EditorData.Response>() {
      public boolean recieved(final EditorData.Response packet) {
        if(packet.getType() == EditorData.DATA_TYPE_ITEM && packet.getFile().equals(getFile())) {
          remove();
          
          Context.getContext().addLoadCallback(new Context.Loader.Callback() {
            public void load() {
              deserialize(new Buffer(packet.getData()));
              
              System.out.println("Full item " + getFile() + " synced from server");
              _loaded = true;
              _events.raiseLoad();
            }
          }, false, "itemeditoritem");
          
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