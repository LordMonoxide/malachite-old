package graphics.shared.gui.controls.compound;

import java.util.ArrayList;
import java.util.LinkedList;

import graphics.shared.gui.Control;
import graphics.shared.gui.ControlList;
import graphics.shared.gui.GUI;
import graphics.shared.gui.controls.Button;
import graphics.shared.gui.controls.Label;
import graphics.shared.gui.controls.Picture;
import graphics.shared.gui.controls.Scrollbar;
import graphics.shared.gui.controls.Scrollbar.ControlEventScroll;

public class ScrollPanel extends Control {
  private ArrayList<ScrollPanelItem> _item = new ArrayList<ScrollPanelItem>();
  
  private Picture   _tabs;
  private Picture   _panel;
  private Scrollbar _scroll;
  private Label     _num;
  
  private Button    _add;
  private Button    _del;
  
  private ScrollPanelItem _sel;

  private LinkedList<ControlEventButton> _eventButtonAdd = new LinkedList<ControlEventButton>();
  private LinkedList<ControlEventButton> _eventButtonDel = new LinkedList<ControlEventButton>();
  private LinkedList<ControlEventSelect> _eventSelect    = new LinkedList<ControlEventSelect>();

  public void addEventButtonAddHandler(ControlEventButton e) { _eventButtonAdd.add(e); }
  public void addEventButtonDelHandler(ControlEventButton e) { _eventButtonDel.add(e); }
  public void addEventSelect          (ControlEventSelect e) { _eventSelect   .add(e); }
  
  public ScrollPanel(GUI gui) {
    super(gui);
    
    ControlEventWheel wheel = new ControlEventWheel() {
      public void event(int delta) {
        _scroll.handleMouseWheel(delta);
      }
    };
    
    ControlEventClick addClick = new ControlEventClick() {
      public void event() {
        raiseButtonAdd();
      }
    };
    
    ControlEventClick delClick = new ControlEventClick() {
      public void event() {
        raiseButtonDel();
      }
    };
    
    _add = new Button(gui);
    _add.setText("Add");
    _add.addEventClickHandler(addClick);
    _add.addEventDoubleClickHandler(addClick);
    
    _del = new Button(gui);
    _del.setText("Delete");
    _del.setX(_add.getW());
    _del.addEventClickHandler(delClick);
    _del.addEventDoubleClickHandler(delClick);
    
    _tabs = new Picture(gui);
    _tabs.setH(_add.getH());
    
    _panel = new Picture(gui);
    _panel.setY(_tabs.getH());
    _panel.setBackColour(new float[] {0.33f, 0.33f, 0.33f, 0.66f});
    _panel.addEventMouseWheelHandler(wheel);
    
    _scroll = new Scrollbar(gui);
    _scroll.setY(_panel.getY());
    _scroll.setH(88);
    _scroll.addEventScrollHandler(new ControlEventScroll() {
      public void event(int delta) {
        setItem(_scroll.getVal());
      }
    });
    
    _num = new Label(gui);
    _num.setAutoSize(false);
    _num.setText(null);
    
    _add.setX(_scroll.getW());
    _del.setX(_add.getX() + _add.getW());
    _tabs.setX(_del.getX() + _del.getW() + 4);
    _panel.setX(_scroll.getW());
    
    super.Controls().add(_add);
    super.Controls().add(_del);
    super.Controls().add(_tabs);
    super.Controls().add(_panel);
    super.Controls().add(_scroll);
    super.Controls().add(_num);
    
    addEventMouseWheelHandler(wheel);
    
    setWH(400, 100);
    
    _panel.setEnabled(false);
    _scroll.setEnabled(false);
  }
  
  public ControlList Buttons() {
    return _tabs.Controls();
  }
  
  public ControlList Controls() {
    return _panel.Controls();
  }
  
  public void add(ScrollPanelItem item) {
    item._index = _item.size();
    _item.add(item);
    _scroll.setMax(_item.size() - 1);
    
    _panel.setEnabled(true);
    _scroll.setEnabled(true);
    
    setItem(item);
  }
  
  public void remove() {
    remove(_item.get(_scroll.getVal()));
  }
  
  public void remove(ScrollPanelItem item) {
    remove(item._index);
  }
  
  public void remove(int index) {
    _item.remove(index);
    
    if(_item.size() != 0) {
      if(_scroll.getVal() != _scroll.getMax()) {
        _scroll.setMax(_item.size() - 1);
        setItem(_scroll.getVal());
      } else {
        _scroll.setMax(_item.size() - 1);
      }
    } else {
      _panel.setEnabled(false);
      _scroll.setEnabled(false);
    }
  }
  
  public ScrollPanelItem getItem() {
    return _sel;
  }
  
  public void setItem(ScrollPanelItem item) {
    setItem(item._index);
  }
  
  public void setItem(int index) {
    _sel = _item.get(index);
    _scroll.setVal(index);
    _num.setText(String.valueOf(_sel._index));
    raiseSelect(_sel);
  }
  
  public int size() {
    return _item.size();
  }
  
  protected void resize() {
    _tabs.setW(getW() - _tabs.getX());
    _panel.setWH(getW() - _panel.getX(), getH() - _panel.getY());
    _num.setWH(_add.getX(), _scroll.getY());
  }
  
  protected void raiseButtonAdd() {
    for(ControlEventButton e : _eventButtonAdd) {
      e.setControl(this);
      e.event();
    }
  }
  
  protected void raiseButtonDel() {
    for(ControlEventButton e : _eventButtonDel) {
      e.setControl(this);
      e.event();
    }
  }
  
  protected void raiseSelect(ScrollPanelItem item) {
    for(ControlEventSelect e : _eventSelect) {
      e.setControl(this);
      e.event(item);
    }
  }
  
  public static class ScrollPanelItem {
    private int _index;
    
    public int getIndex() {
      return _index;
    }
    
    public void setIndex(int index) {
      _index = index;
    }
  }
  
  public static abstract class ControlEventButton extends ControlEvent {
    public abstract void event();
  }
  
  public static abstract class ControlEventSelect extends ControlEvent {
    public abstract void event(ScrollPanelItem item);
  }
}