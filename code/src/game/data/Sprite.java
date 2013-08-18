package game.data;

import java.io.File;
import java.util.ArrayList;

import game.data.util.Buffer;
import game.data.util.GameData;
import graphics.gl00.Context;
import graphics.gl00.Drawable;
import graphics.shared.textures.Texture;

public class Sprite extends GameData {
  public String toString() {
    return "Sprite '" + getFile() + "' (" + super.toString() + ")";
  }
  
  protected String _texture;
  protected int _w, _h;
  protected int _default;
  public final ArrayList<Frame> frame = new ArrayList<Frame>();
  public final ArrayList<Anim> anim = new ArrayList<Anim>();
  protected String _script;
  
  public Sprite() { }
  public Sprite(String file) {
    init(file);
  }
  
  public void init(String file) {
    super.initInternal(1, new File("../data/sprites/" + file));
  }
  
  public    int getW()       { return _w; }
  public    int getH()       { return _h; }
  public    int getDefault() { return _default; }
  public String getScript()  { return _script; }
  
  public Drawable[] createDrawables() {
    Drawable[] d = new Drawable[frame.size()];
    
    Texture t = Context.getTextures().getTexture("sprites/" + _texture);
    
    int i = 0;
    for(Frame f : frame) {
      d[i] = Context.newDrawable();
      d[i].setTexture(t);
      d[i].setXYWH(-f._fx, f._fy - f._h, f._w, f._h);
      d[i].setTXYWH(f._x, f._y, f._w, f._h);
      d[i].createQuad();
      i++;
    }
    
    return d;
  }
  
  public Anim[] createAnimList() {
    Anim[] a = new Anim[anim.size()];
    
    int i = 0;
    for(Anim anim : this.anim) {
      a[i] = new Anim(anim);
      i++;
    }
    
    return a;
  }
  
  protected void serializeInternal(Buffer b) {
    b.put(_texture);
    b.put(_w);
    b.put(_h);
    b.put(_default);
    
    b.put(frame.size());
    b.put(anim.size());
    
    for(Frame f : frame) {
      b.put(f._fx);
      b.put(f._fy);
      b.put(f._x);
      b.put(f._y);
      b.put(f._w);
      b.put(f._h);
    }
    
    for(Anim a : anim) {
      b.put(a._name);
      b.put(a._default);
      b.put(a._list.size());
      
      for(List l : a._list) {
        b.put(l._frame);
        b.put(l._time);
      }
    }
    
    b.put(_script);
  }
  
  protected void deserializeInternal(Buffer b) {
    switch(getVersion()) {
      case 1: deserialize01(b); break;
    }
  }
  
  private void deserialize01(Buffer b) {
    frame.clear();
    anim.clear();
    
    _texture = b.getString();
    _w = b.getInt();
    _h = b.getInt();
    _default = b.getInt();
    
    int frames = b.getInt();
    int anims = b.getInt();
    
    frame.ensureCapacity(frames);
    anim.ensureCapacity(anims);
    
    for(int i = 0; i < frames; i++) {
      Frame f = new Frame();
      f._fx = b.getInt();
      f._fy = b.getInt();
      f._x = b.getInt();
      f._y = b.getInt();
      f._w = b.getInt();
      f._h = b.getInt();
      frame.add(f);
    }
    
    for(int i = 0; i < anims; i++) {
      Anim a = new Anim();
      a._name = b.getString();
      a._default = b.getInt();
      
      int lists = b.getInt();
      
      a._list.ensureCapacity(lists);
      
      for(int n = 0; n < lists; n++) {
        List l = new List();
        l._frame = b.getInt();
        l._time = b.getInt();
        a._list.add(l);
      }
      
      anim.add(a);
    }
    
    _script = b.getString();
  }
  
  public static class Frame {
    public int _fx, _fy;
    public int _x, _y;
    public int _w, _h;
    
    public Frame() { }
    public Frame(Frame f) {
      _x = f._x;
      _y = f._y;
      _w = f._w;
      _h = f._h;
      _fx = f._fx;
      _fy = f._fy;
    }
  }
  
  public static class Anim {
    public String _name;
    public int _default;
    public ArrayList<List> _list = new ArrayList<List>();
    
    public Anim() { }
    public Anim(Anim anim) {
      _name = new String(anim._name);
      _default = anim._default;
      
      for(List l : anim._list) {
        _list.add(new List(l));
      }
    }
    
    public int getDefault() {
      return _default;
    }
    
    public int getListSize() {
      return _list.size();
    }
    
    public List getList(int list) {
      return _list.get(list);
    }
  }
  
  public static class List {
    public int _frame;
    public int _time;
    
    public List() { }
    public List(List list) {
      _frame = list._frame;
      _time = list._time;
    }
    
    public int getFrame() {
      return _frame;
    }
    
    public int getTime() {
      return _time;
    }
  }
}