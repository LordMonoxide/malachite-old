package game.graphics.gui.editors;

import game.Game;
import game.data.Projectile;
import game.data.util.Buffer;
import game.network.packet.editors.EditorData;
import graphics.gl00.Context;

public class ProjectileEditorProjectile extends Projectile {
  private String _sprite;
  private int    _damage;
  private int    _life;
  private float  _vel;
  private float  _dec;
  
  protected ProjectileEditorProjectile(String file) { this(file, false); }
  protected ProjectileEditorProjectile(String file, boolean newData) {
    super(file);
    
    if(!newData) {
      request();
    } else {
      _loaded = true;
      _events.raiseLoad();
    }
  }
  
  protected String getSprite() { return _sprite; }
  protected int    getDamage() { return _damage; }
  protected int    getLife()   { return _life; }
  protected float  getVel()    { return _vel; }
  protected float  getDec()    { return _dec; }
  
  protected void setName  (String name)   { _name = name; }
  protected void setNote  (String note)   { _note = note; }
  protected void setSprite(String sprite) { _sprite = sprite; }
  protected void setDamage(int damage)    { _damage = damage; }
  protected void setLife  (int life)      { _life = life; }
  protected void setVel   (float vel)     { _vel = vel; }
  protected void setDec   (float dec)     { _dec = dec; }
  
  public void request() {
    EditorData.Request p = new EditorData.Request(this);
    Game.getInstance().send(p, EditorData.Response.class, new Game.PacketCallback<EditorData.Response>() {
      public boolean recieved(final EditorData.Response packet) {
        if(packet.getType() == EditorData.DATA_TYPE_PROJECTILE && packet.getFile().equals(getFile())) {
          remove();
          
          Context.getContext().addLoadCallback(new Context.Loader.Callback() {
            public void load() {
              deserialize(new Buffer(packet.getData()));
              
              System.out.println("Full projectile " + getFile() + " synced from server");
              _loaded = true;
              _events.raiseLoad();
            }
          }, false, "projectileeditorprojectile");
          
          return true;
        }
        
        return false;
      }
    });
  }
  
  protected void serializeInternal(Buffer b) {
    b.put(_sprite);
    b.put(_damage);
    b.put(_life);
    b.put(_vel);
    b.put(_dec);
  }
  
  protected void deserializeInternal(Buffer b) {
    _sprite = b.getString();
    _damage = b.getInt();
    _life   = b.getInt();
    _vel    = b.getFloat();
    _dec    = b.getFloat();
  }
}