package game.graphics.gui.editors;

import game.data.util.Data;

public interface Editor {
  public void newData(String file);
  public void editData(Data data);
}