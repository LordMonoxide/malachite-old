package game;

import game.network.Server;
import game.settings.Settings;

public class Game {
  private Server _net;
  
  public void start() {
    Settings.load();
    
    _net = new Server();
    _net.initPackets();
    _net.start();
    
    //TODO: Need to close SQL tables
  }
}