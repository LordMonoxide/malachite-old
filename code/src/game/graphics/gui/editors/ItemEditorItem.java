package game.graphics.gui.editors;

import game.data.Item;
import game.data.util.Buffer;

public class ItemEditorItem extends Item {
  private Item _item;
  private int _itemCRC;
  
  public ItemEditorItem(Item item) {
    super(item.getFile(), item.getCRC());
    
    _item = item;
    
    // Deep-copy source Item into
    // this ItemEditorItem's structure
    Buffer b = _item.serialize();
    deserialize(b);
    _itemCRC = b.crc();
  }
  
  public boolean isChanged() {
    return _itemCRC != serialize().crc();
  }
  
  public void update() {
    _item.deserialize(serialize());
  }
  
  public Item getItem() {
    return _item;
  }
  
  public void setName(String name) {
    _name = name;
  }
  
  public void setNote(String note) {
    _note = note;
  }
  
  public void setSprite(String sprite) {
    _sprite = sprite;
  }
}