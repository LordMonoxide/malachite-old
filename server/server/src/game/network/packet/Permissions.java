package game.network.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import network.packet.Packet;

public class Permissions extends Packet {
  private game.data.account.Permissions _permissions;
  
  public int getIndex() {
    return 3;
  }
  
  public game.data.account.Permissions getPermissions() {
    return _permissions;
  }
  
  public void setPermissions(game.data.account.Permissions permissions) {
    _permissions = permissions;
  }
  
  public ByteBuf serialize() {
    ByteBuf b = Unpooled.buffer(31);
    b.writeBoolean(_permissions.canAlterChars());
    b.writeBoolean(_permissions.canSpeak());
    b.writeBoolean(_permissions.canWhisper());
    b.writeBoolean(_permissions.canShout());
    b.writeBoolean(_permissions.canBroadcast());
    b.writeBoolean(_permissions.canWarpSelf());
    b.writeBoolean(_permissions.canWarpOthers());
    b.writeBoolean(_permissions.canKick());
    b.writeBoolean(_permissions.canBan());
    b.writeBoolean(_permissions.canViewInfo());
    b.writeBoolean(_permissions.canViewChatLogs());
    b.writeBoolean(_permissions.canMute());
    b.writeBoolean(_permissions.canSpawnSprites());
    b.writeBoolean(_permissions.canSpawnItems());
    b.writeBoolean(_permissions.canSpawnSpells());
    b.writeBoolean(_permissions.canSpawnNPCs());
    b.writeBoolean(_permissions.canSpawnEffects());
    b.writeBoolean(_permissions.canRespawnMaps());
    b.writeBoolean(_permissions.canAlterLocalWeather());
    b.writeBoolean(_permissions.canAlterRegionalWeather());
    b.writeBoolean(_permissions.canAlterGlobalWeather());
    b.writeBoolean(_permissions.canAlterTime());
    b.writeBoolean(_permissions.canEditMaps());
    b.writeBoolean(_permissions.canEditSprites());
    b.writeBoolean(_permissions.canEditItems());
    b.writeBoolean(_permissions.canEditSpells());
    b.writeBoolean(_permissions.canEditNPCs());
    b.writeBoolean(_permissions.canEditEffects());
    b.writeBoolean(_permissions.canGrantPermissions());
    b.writeBoolean(_permissions.canViewDetailedInfo());
    b.writeBoolean(_permissions.canBanPermanently());
    return b;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    
  }
  
  public void process() {
    
  }
}