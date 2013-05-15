package game.network;

import network.packet.Packet;
import network.packet.Packets;
import game.network.packet.Connect;
import game.network.packet.Login;
import game.settings.Settings;

public class Client {
  private network.Client _client;
  
  public Client() {
    _client = new network.Client();
    _client.setTimeout(3000);
    _client.setAddress(Settings.Net.IP, Settings.Net.Port);
  }
  
  public void initPackets() {
    Packets.add(Connect.class);
    Packets.add(Login.class);
  }
  
  public void connect() {
    System.out.println("Connecting to " + Settings.Net.IP + ":" + Settings.Net.Port + "...");
    _client.connect(new network.Client.Event() {
      public void event(boolean success) {
        if(success) {
          System.out.println("Connected.");
          _client.send(new Connect());
        } else {
          System.out.println("Connection failed.");
        }
      }
    });
  }
  
  public void close() {
    _client.close();
  }
  
  public void shutdown() {
    _client.shutdown();
  }
  
  public void send(Packet packet) {
    _client.send(packet);
  }
}