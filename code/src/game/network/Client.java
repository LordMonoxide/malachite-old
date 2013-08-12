package game.network;

import network.packet.Packet;
import network.packet.Packets;
import game.network.packet.Chat;
import game.network.packet.DataMap;
import game.network.packet.Data;
import game.network.packet.EntityAttack;
import game.network.packet.EntityCreate;
import game.network.packet.EntityCurrency;
import game.network.packet.EntityDespawn;
import game.network.packet.EntityDestroy;
import game.network.packet.EntityEquip;
import game.network.packet.EntityInteract;
import game.network.packet.EntityInv;
import game.network.packet.EntityInvUpdate;
import game.network.packet.EntityMoveStart;
import game.network.packet.EntityMoveStop;
import game.network.packet.EntityPhysics;
import game.network.packet.EntitySpawn;
import game.network.packet.EntityStats;
import game.network.packet.EntityVitals;
import game.network.packet.InvDrop;
import game.network.packet.InvSwap;
import game.network.packet.InvUnequip;
import game.network.packet.InvUse;
import game.network.packet.editors.EditorData;
import game.network.packet.editors.EditorDataMap;
import game.network.packet.editors.EditorSave;
import game.network.packet.menu.CharDel;
import game.network.packet.menu.CharNew;
import game.network.packet.menu.CharUse;
import game.network.packet.menu.Connect;
import game.network.packet.menu.Login;
import game.network.packet.menu.Permissions;
import game.settings.Settings;

public class Client {
  private network.Client _client;
  
  public Client() {
    _client = new network.Client();
    _client.setTimeout(3000);
    _client.setAddress(Settings.Net.IP, Settings.Net.Port);
    _client.events().onPacket(new network.Client.Events.Packet() {
      public boolean event(Packet p) {
        System.out.println(p);
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
    Packets.add(Chat.class);
    Packets.add(EditorSave.Map.class);
    Packets.add(DataMap.Request.class);
    Packets.add(DataMap.Response.class);
    Packets.add(EditorSave.Sprite.class);
    Packets.add(EntityVitals.class);
    Packets.add(EntityStats.class);
    Packets.add(EditorSave.Item.class);
    Packets.add(EntityInv.class);
    Packets.add(EntityInvUpdate.class);
    Packets.add(EntityInteract.class);
    Packets.add(InvUse.class);
    Packets.add(EntityEquip.class);
    Packets.add(InvSwap.class);
    Packets.add(InvDrop.class);
    Packets.add(InvUnequip.class);
    Packets.add(EntityCurrency.class);
    Packets.add(EditorDataMap.Request.class);
    Packets.add(EditorDataMap.Response.class);
    Packets.add(EditorSave.NPC.class);
    Packets.add(EditorData.Request.class);
    Packets.add(EditorData.Response.class);
    Packets.add(EditorData.List.class);
    Packets.add(EntityAttack.class);
    Packets.add(EntityPhysics.class);
    Packets.add(EntitySpawn.class);
    Packets.add(EntityDespawn.class);
  }
  
  public void connect(network.Client.Event event) {
    System.out.println("Connecting to " + Settings.Net.IP + ":" + Settings.Net.Port + "...");
    _client.connect(event);
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