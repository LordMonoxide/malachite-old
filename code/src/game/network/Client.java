package game.network;

import network.packet.Packet;
import network.packet.Packets;
import game.network.packet.CharDel;
import game.network.packet.CharNew;
import game.network.packet.CharUse;
import game.network.packet.Connect;
import game.network.packet.Data;
import game.network.packet.EntityCreate;
import game.network.packet.EntityDestroy;
import game.network.packet.EntityMoveStart;
import game.network.packet.EntityMoveStop;
import game.network.packet.Login;
import game.network.packet.Permissions;
import game.settings.Settings;

public class Client {
  private network.Client _client;
  
  public Client() {
    _client = new network.Client();
    _client.setTimeout(3000);
    _client.setAddress(Settings.Net.IP, Settings.Net.Port);
    _client.events().onPacket(new network.Client.Events.Packet() {
      public boolean event(Packet p) {
        p.process();
        return false;
      }
    });
  }
  
  public network.Client.Events events() {
    return _client.events();
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
    Packets.add(CharUse.class);
    Packets.add(CharUse.Response.class);
    Packets.add(EntityCreate.class);
    Packets.add(EntityDestroy.class);
    Packets.add(EntityMoveStart.class);
    Packets.add(EntityMoveStop.class);
    Packets.add(Data.Request.class);
    Packets.add(Data.Response.class);
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