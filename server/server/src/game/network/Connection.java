package game.network;

import java.util.ArrayList;

import game.data.account.Account;
import game.data.account.Character;
import network.packet.Packet;

public class Connection extends network.Connection {
  private Handler _handler;
  private Account _account;
  private ArrayList<Character> _player;
  
  private boolean _inGame;
  
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
  
  public ArrayList<Character> getPlayer() {
    return _player;
  }
  
  public void setPlayer(ArrayList<Character> player) {
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