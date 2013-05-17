package game.network;

import java.util.ArrayList;

import game.data.account.Account;
import game.data.account.Player;
import network.packet.Packet;

public class Connection extends network.Connection {
  private Handler _handler;
  private Account _account;
  private ArrayList<Player> _player;
  
  private boolean _loggedIn;
  private boolean _inGame;
  
  public boolean isLoggedIn() {
    return _loggedIn;
  }
  
  public void setLoggedIn(boolean loggedIn) {
    _loggedIn = loggedIn;
  }
  
  public boolean isInGame() {
    return _inGame;
  }
  
  public void setInGame(boolean inGame) {
    _inGame = inGame;
  }
  
  public Account getAccount() {
    return _account;
  }
  
  public void setAccount(Account account) {
    _account = account;
  }
  
  public ArrayList<Player> getPlayer() {
    return _player;
  }
  
  public void setPlayer(ArrayList<Player> player) {
    _player = player;
  }
  
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