package game.graphics.gui.editors;

import game.Game;
import game.data.Item;
import game.data.util.Buffer;
import game.network.packet.editors.EditorData;
import graphics.gl00.Context;

public class ItemEditorItem extends Item {
  private int _itemCRC;
  
  public ItemEditorItem(String file) { this(file, false); }
  public ItemEditorItem(String file, boolean newData) {
    super(file);
    
    if(!newData) {
      request();
    } else {
      _loaded = true;
      _events.raiseLoad();
    }
  }
  
  public boolean isChanged() {
    return _itemCRC != serialize().crc();
  }
  
  public void setName  (String name)   { _name = name; }
  public void setNote  (String note)   { _note = note; }
  public void setSprite(String sprite) { _sprite = sprite; }
  public void setType  (int type)      { _type = type; }
  public void setDamage(int damage)    { _damage = damage; }
  public void setWeight(float weight)  { _weight = weight; }
  public void setHPHeal(int hpHeal)    { _hpHeal = hpHeal; }
  public void setMPHeal(int mpHeal)    { _mpHeal = mpHeal; }
  
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
  
  public void serializeInternal(Buffer b) {
    super.serializeInternal(b);
  }
  
  public void deserializeInternal(Buffer b) {
    super.deserializeInternal(b);
  }
}