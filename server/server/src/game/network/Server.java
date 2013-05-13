package game.network;

public class Server {
  private LoginManager _login;
  
  private network.Server _server;
  
  public Server() {
    _login = new LoginManager();
    
    _server = new network.Server();
    _server.setAddress(4000);
    _server.setBacklog(100);
    _server.setKeepAlive(true);
    _server.setNoDelay(true);
    _server.bind();
  }
}