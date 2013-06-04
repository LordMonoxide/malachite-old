package game.network.packet.menu;

import game.Game;
import graphics.gl00.Context;
import io.netty.buffer.ByteBuf;
import network.packet.Packet;

public class Permissions extends Packet {
  private game.data.account.Permissions _permissions = ((Game)Context.getGame()).getPermissions();
  
  public int getIndex() {
    return 3;
  }
  
  public ByteBuf serialize() {
    return null;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    _permissions.setAlterChars(data.readBoolean());
    _permissions.setSpeak(data.readBoolean());
    _permissions.setWhisper(data.readBoolean());
    _permissions.setShout(data.readBoolean());
    _permissions.setBroadcast(data.readBoolean());
    _permissions.setWarpSelf(data.readBoolean());
    _permissions.setWarpOthers(data.readBoolean());
    _permissions.setKick(data.readBoolean());
    _permissions.setBan(data.readBoolean());
    _permissions.setViewInfo(data.readBoolean());
    _permissions.setViewChatLogs(data.readBoolean());
    _permissions.setMute(data.readBoolean());
    _permissions.setSpawnSprites(data.readBoolean());
    _permissions.setSpawnItems(data.readBoolean());
    _permissions.setSpawnSpells(data.readBoolean());
    _permissions.setSpawnNPCs(data.readBoolean());
    _permissions.setSpawnEffects(data.readBoolean());
    _permissions.setRespawnMaps(data.readBoolean());
    _permissions.setAlterLocalWeather(data.readBoolean());
    _permissions.setAlterRegionalWeather(data.readBoolean());
    _permissions.setAlterGlobalWeather(data.readBoolean());
    _permissions.setAlterTime(data.readBoolean());
    _permissions.setEditMaps(data.readBoolean());
    _permissions.setEditSprites(data.readBoolean());
    _permissions.setEditItems(data.readBoolean());
    _permissions.setEditSpells(data.readBoolean());
    _permissions.setEditNPCs(data.readBoolean());
    _permissions.setEditEffects(data.readBoolean());
    _permissions.setGrantPermissions(data.readBoolean());
    _permissions.setViewDetailedInfo(data.readBoolean());
    _permissions.setBanPermanently(data.readBoolean());
  }
  
  public void process() {
    
  }
}