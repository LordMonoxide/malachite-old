package game.graphics.gui.editors;

import game.data.Item;
import game.data.util.Buffer;

public class ItemEditorItem extends Item {
  private Item _item;
  private int _itemCRC;
  
  public ItemEditorItem(Item item) {
    super(item.getFile());
    
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
  
  public void setName  (String name)   { _name = name; }
  public void setNote  (String note)   { _note = note; }
  public void setSprite(String sprite) { _sprite = sprite; }
  public void setType  (int type)      { _type = type; }
  public void setDamage(int damage)    { _damage = damage; }
  public void setWeight(float weight)  { _weight = weight; }
  public void setHPHeal(int hpHeal)    { _hpHeal = hpHeal; }
  public void setMPHeal(int mpHeal)    { _mpHeal = mpHeal; }
}