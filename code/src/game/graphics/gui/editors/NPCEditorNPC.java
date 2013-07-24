package game.graphics.gui.editors;

import game.Game;
import game.data.Item;
import game.data.NPC;
import game.data.util.Buffer;
import game.network.packet.editors.EditorData;
import game.settings.Settings;
import graphics.gl00.Context;

public class NPCEditorNPC extends NPC {
  private Stats _stats;
  private Inv[] _inv;
  private Equip _equip;
  private long _curr;
  
  protected NPCEditorNPC(String file) { this(file, false); }
  protected NPCEditorNPC(String file, boolean newData) {
    super(file);
    
    _stats = new Stats();
    _inv = new Inv[Settings.Player.Inventory.Size];
    _equip = new Equip();
    
    for(int i = 0; i < _inv.length; i++) {
      _inv[i] = new Inv();
    }
    
    if(!newData) {
      request();
    } else {
      _loaded = true;
      _events.raiseLoad();
    }
  }
  
  protected long getCurr() { return _curr; }
  
  protected void setName  (String name)   { _name = name; }
  protected void setNote  (String note)   { _note = note; }
  protected void setSprite(String sprite) { _sprite = sprite; }
  protected void setCurr  (long curr)     { _curr = curr; }
  
  protected Stats stats()        { return _stats; }
  protected Equip equip()        { return _equip; }
  protected Inv   inv(int index) { return _inv[index]; }
  
  public void request() {
    EditorData.Request p = new EditorData.Request(this);
    Game.getInstance().send(p, EditorData.Response.class, new Game.PacketCallback<EditorData.Response>() {
      public boolean recieved(final EditorData.Response packet) {
        if(packet.getType() == EditorData.DATA_TYPE_NPC && packet.getFile().equals(getFile())) {
          remove();
          
          Context.getContext().addLoadCallback(new Context.Loader.Callback() {
            public void load() {
              deserialize(new Buffer(packet.getData()));
              
              System.out.println("Full NPC " + getFile() + " synced from server");
              _loaded = true;
              _events.raiseLoad();
            }
          }, false, "npceditornpc");
          
          return true;
        }
        
        return false;
      }
    });
  }
  
  protected void serializeInternal(Buffer b) {
    b.put(_sprite);
    
    b.put(_stats.STR);
    b.put(_stats.DEX);
    b.put(_stats.INT);
    
    for(Inv inv : _inv) {
      b.put(inv.file);
      b.put(inv.val);
    }
    
    b.put(_equip.hand1.file);
    b.put(_equip.hand2.file);
    
    for(Inv armour : _equip.armour) b.put(armour.file);
    for(Inv bling  : _equip.bling ) b.put(bling .file);
    
    b.put(_curr);
  }
  
  protected void deserializeInternal(Buffer b) {
    _sprite = b.getString();
    
    _stats.STR = b.getInt();
    _stats.DEX = b.getInt();
    _stats.INT = b.getInt();
    
    for(Inv inv : _inv) {
      inv.file = b.getString();
      inv.val = b.getInt();
    }
    
    _equip.hand1.file = b.getString();
    _equip.hand2.file = b.getString();
    
    for(int i = 0; i < _equip.armour.length; i++) _equip.armour[i].file = b.getString();
    for(int i = 0; i < _equip.bling .length; i++) _equip.bling [i].file = b.getString();
    
    _curr = b.getLong();
  }
  
  public class Stats {
    public int STR, INT, DEX;
  }
  
  public class Inv {
    public String file;
    public    int val;
  }
  
  public class Equip {
    public Inv   hand1;
    public Inv   hand2;
    public Inv[] armour = new Inv[Item.ITEM_TYPE_ARMOUR_COUNT];
    public Inv[] bling  = new Inv[Item.ITEM_TYPE_BLING_COUNT];
    
    public Equip() {
      hand1 = new Inv();
      hand2 = new Inv();
      
      for(int i = 0; i < armour.length; i++) armour[i] = new Inv();
      for(int i = 0; i < bling .length; i++) bling [i] = new Inv();
    }
  }
}