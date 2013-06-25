package game.graphics.gui.editors;

import game.data.util.GameData;

public interface Editor {
  public void newData(String file);
  public void editData(GameData data);
}