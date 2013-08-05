package game.network.packet.editors;

import java.util.ArrayList;

import game.data.util.GameData;
import game.graphics.gui.editors.MapEditorMap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import network.packet.Packet;

public abstract class EditorSave extends Packet {
  protected ArrayList<GameData> _data = new ArrayList<GameData>();
  
  private EditorSave() { }
  
  public abstract int getIndex();
  
  public void addData(GameData data) {
    _data.add(data);
  }
  
  public int size() {
    return _data.size();
  }
  
  public ByteBuf serialize() {
    ByteBuf b = Unpooled.buffer();
    
    byte[] arr;
    for(GameData data : _data) {
      arr = data.serialize().serialize();
      b.writeShort(data.getFile().length());
      b.writeBytes(data.getFile().getBytes());
      b.writeInt(arr.length);
      b.writeBytes(arr);
    }
    
    return b;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    
  }
  
  public void process() {
    
  }
  
  public static class Map extends EditorSave {
    public int getIndex() {
      return 17;
    }
    
    public ByteBuf serialize() {
      ByteBuf b = Unpooled.buffer();
      
      byte[] arr;
      for(GameData data : _data) {
        MapEditorMap m = (MapEditorMap)data;
        arr = m.serialize().serialize();
        b.writeInt(m.getX());
        b.writeInt(m.getY());
        b.writeInt(arr.length);
        b.writeBytes(arr);
      }
      
      return b;
    }
  }
  
  public static class Sprite extends EditorSave {
    public int getIndex() {
      return 20;
    }
  }
  
  public static class Item extends EditorSave {
    public int getIndex() {
      return 23;
    }
  }
  
  public static class NPC extends EditorSave {
    public int getIndex() {
      return 35;
    }
  }
}