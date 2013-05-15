package game.network;

import network.packet.Packet;

public class Connection extends network.Connection {
  private Handler _handler;
  
  public Handler getHandler() {
    return _handler;
  }
  
  public void setHandler(Handler handler) {
    _handler = handler;
  }
  
  public void handle(Packet p) {
    _handler.postPacket(p);
  }
}