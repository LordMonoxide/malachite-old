package game.graphics.gui.editors;

import game.data.Map;
import game.data.util.Buffer;

public class MapEditorMap extends Map {
  private Map _map;
  private int _mapCRC;
  
  public MapEditorMap(Map map) {
    super(map.getWorld(), map.getX(), map.getY());
    
    _map = map;
    
    // Deep-copy source Map into
    // this MapEditorMap's structure
    Buffer b = _map.serialize();
    deserialize(b);
    _mapCRC = b.crc();
  }
  
  public boolean isChanged() {
    return _mapCRC != serialize().crc();
  }
  
  public void update() {
    _map.deserialize(serialize());
  }
  
  public Map getMap() {
    return _map;
  }
  
  public Layer getLayer(int z) {
    return _layer[z];
  }
}