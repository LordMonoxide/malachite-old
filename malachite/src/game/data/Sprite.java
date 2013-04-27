package game.data;

import java.util.ArrayList;

import game.data.util.Buffer;
import game.data.util.Data;
import game.data.util.Serializable;
import graphics.gl00.Context;
import graphics.gl00.Drawable;
import graphics.shared.textures.Texture;

public class Sprite extends Serializable implements Data {
  private static final int VERSION = 1;
  
  protected String _name, _note;
  protected String _texture;
  protected int _w, _h;
  protected int _default;
  protected ArrayList<Frame> _frame = new ArrayList<Frame>();
  protected ArrayList<Anim> _anim = new ArrayList<Anim>();
  
  public Sprite() {
    super("sprites");
    
    _texture = "isaac.png";
    _w = 64;
    _h = 64;
    
    Frame f = new Frame();
    f._fx = 32;
    f._fy = 4;
    f._x = 0;
    f._y = 64;
    f._w = 64;
    f._h = 64;
    _frame.add(f);
    
    f = new Frame();
    f._fx = 32;
    f._fy = 4;
    f._x = 64;
    f._y = 64;
    f._w = 64;
    f._h = 64;
    _frame.add(f);
    
    f = new Frame();
    f._fx = 32;
    f._fy = 4;
    f._x = 128;
    f._y = 64;
    f._w = 64;
    f._h = 64;
    _frame.add(f);
    
    f = new Frame();
    f._fx = 32;
    f._fy = 4;
    f._x = 192;
    f._y = 64;
    f._w = 64;
    f._h = 64;
    _frame.add(f);
    
    f = new Frame();
    f._fx = 32;
    f._fy = 4;
    f._x = 256;
    f._y = 64;
    f._w = 64;
    f._h = 64;
    _frame.add(f);
    
    f = new Frame();
    f._fx = 32;
    f._fy = 4;
    f._x = 320;
    f._y = 64;
    f._w = 64;
    f._h = 64;
    _frame.add(f);
    
    Anim a = new Anim();
    a._name = "[walk1]";
    a._default = 0;
    
    List l = new List();
    l._frame = 0;
    l._time = 10000;
    a._list.add(l);
    
    l = new List();
    l._frame = 1;
    l._time = 500;
    a._list.add(l);
    
    l = new List();
    l._frame = 2;
    l._time = 3000;
    a._list.add(l);
    
    l = new List();
    l._frame = 3;
    l._time = 3000;
    a._list.add(l);
    
    l = new List();
    l._frame = 2;
    l._time = 500;
    a._list.add(l);
    
    l = new List();
    l._frame = 4;
    l._time = 3000;
    a._list.add(l);
    
    l = new List();
    l._frame = 2;
    l._time = 2000;
    a._list.add(l);
    
    l = new List();
    l._frame = 5;
    l._time = 1000;
    a._list.add(l);
    
    l = new List();
    l._frame = 2;
    l._time = 2000;
    a._list.add(l);
    
    l = new List();
    l._frame = 1;
    l._time = 500;
    a._list.add(l);
    
    _anim.add(a);
  }
  
  public String getName() { return _name; }
  public String getNote() { return _note; }
  public    int getW()    { return _w; }
  public    int getH()    { return _h; }
  
  public int getDefault() {
    return _default;
  }
  
  public Drawable[] createDrawables() {
    Drawable[] d = new Drawable[_frame.size()];
    
    Texture t = Context.getTextures().getTexture("sprites/" + _texture);
    
    int i = 0;
    for(Frame f : _frame) {
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
    Anim[] a = new Anim[_anim.size()];
    
    int i = 0;
    for(Anim anim : _anim) {
      a[i] = new Anim(anim);
      i++;
    }
    
    return a;
  }
  
  public boolean load() {
    return super.load(_name);
  }
  
  public void save() {
    super.save(_name);
  }
  
  public Buffer serialize() {
    Buffer b = new Buffer();
    b.put(VERSION);
    
    b.put(_name);
    b.put(_note);
    b.put(_texture);
    b.put(_w);
    b.put(_h);
    b.put(_default);
    
    b.put(_frame.size());
    b.put(_anim.size());
    
    for(Frame f : _frame) {
      b.put(f._fx);
      b.put(f._fy);
      b.put(f._x);
      b.put(f._y);
      b.put(f._w);
      b.put(f._h);
    }
    
    for(Anim a : _anim) {
      b.put(a._name);
      b.put(a._default);
      b.put(a._list.size());
      
      for(List l : a._list) {
        b.put(l._frame);
        b.put(l._time);
      }
    }
    
    return b;
  }
  
  public void deserialize(Buffer b) {
    switch(b.getInt()) {
      case 1: deserialize01(b);
    }
  }
  
  private void deserialize01(Buffer b) {
    _frame.clear();
    _anim.clear();
    
    _name = b.getString();
    _note = b.getString();
    _texture = b.getString();
    _w = b.getInt();
    _h = b.getInt();
    _default = b.getInt();
    
    int frames = b.getInt();
    int anims = b.getInt();
    
    _frame.ensureCapacity(frames);
    _anim.ensureCapacity(anims);
    
    for(int i = 0; i < frames; i++) {
      Frame f = new Frame();
      f._fx = b.getInt();
      f._fy = b.getInt();
      f._x = b.getInt();
      f._y = b.getInt();
      f._w = b.getInt();
      f._h = b.getInt();
      _frame.add(f);
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
      
      _anim.add(a);
    }
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