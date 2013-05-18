package game.network;

import game.network.packet.CharDel;
import game.network.packet.CharNew;
import game.network.packet.Connect;
import game.network.packet.Login;
import game.network.packet.Permissions;
import game.settings.Settings;
import network.packet.Packet;
import network.packet.Packets;

public class Server {
  private static Handler _connect = new Handler();
  private static Handler _login = new Handler();
  
  public static Handler getLoginHandler() { return _login; }
  
  private network.Server _server;
  
  public Server() {
    System.out.println("Initialising server...");
    
    _server = new network.Server(Connection.class);
    _server.setAddress(Settings.Net.Port());
    _server.setBacklog(100);
    _server.setKeepAlive(true);
    _server.setNoDelay(true);
    
    _server.events().onConnect(new network.Server.Events.Connect() {
      public void event(network.Connection c) {
        ((Connection)c).setHandler(_connect);
      }
    });
    
    _server.events().onPacket(new network.Server.Events.Packet() {
      public void event(Packet p) {
        Connection c = (Connection)p.getConnection();
        c.getHandler().postPacket(p);
      }
    });
  }
  
  public void initPackets() {
    Packets.add(Connect.class);
    Packets.add(Login.class);
    Packets.add(Login.Response.class);
    Packets.add(Permissions.class);
    Packets.add(CharDel.class);
    Packets.add(CharDel.Response.class);
    Packets.add(CharNew.class);
    Packets.add(CharNew.Response.class);
  }
  
  public void start() {
    System.out.println("Binding server...");
    _server.bind(new network.Server.Event() {
      public void event(boolean success) {
        if(success) {
          System.out.println("Server bound to port " + Settings.Net.Port());
        } else {
          System.out.println("Bind failed");
        }
      }
    });
  }
}