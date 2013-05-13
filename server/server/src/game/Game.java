package game;

import game.network.Server;

public class Game {
  private Server _server;
  
  public void start() {
    _server = new Server();
  }
}