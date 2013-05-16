package game.data.account;

public class Permissions {
  public static Permissions getDefaultPermissions() {
    Permissions p = new Permissions();
    p._login = true;
    p._alterChars = true;
    p._speak = true;
    p._whisper = true;
    p._shout = true;
    return p;
  }
  
  private boolean _login;
  private boolean _alterChars;
  private boolean _speak;
  private boolean _whisper;
  private boolean _shout;
  
  private boolean _broadcast;
  private boolean _warpSelf;
  private boolean _warpOthers;
  private boolean _kick;
  private boolean _ban;
  private boolean _viewInfo;
  private boolean _viewChatLogs;
  private boolean _mute;
  
  private boolean _spawnSprites;
  private boolean _spawnItems;
  private boolean _spawnSpells;
  private boolean _spawnNPCs;
  private boolean _spawnEffects;
  private boolean _respawnMaps;
  private boolean _alterLocalWeather;
  private boolean _alterRegionalWeather;
  private boolean _alterGlobalWeather;
  private boolean _alterTime;
  
  private boolean _editMaps;
  private boolean _editSprites;
  private boolean _editItems;
  private boolean _editSpells;
  private boolean _editNPCs;
  private boolean _editEffects;
  private boolean _grantPermissions;
  private boolean _viewDetailedInfo;
  private boolean _banPermanently;
  
  public boolean canLogin() {
    return _login;
  }
  
  public void setLogin(boolean login) {
    _login = login;
  }
  
  public boolean canAlterChars() {
    return _alterChars;
  }
  
  public void setAlterChars(boolean alterChars) {
    _alterChars = alterChars;
  }
  
  public boolean canSpeak() {
    return _speak;
  }
  
  public void setSpeak(boolean speak) {
    _speak = speak;
  }
  
  public boolean canWhisper() {
    return _whisper;
  }
  
  public void setWhisper(boolean whisper) {
    _whisper = whisper;
  }
  
  public boolean canShout() {
    return _shout;
  }
  
  public void setShout(boolean shout) {
    _shout = shout;
  }
  
  public boolean canBroadcast() {
    return _broadcast;
  }
  
  public void setBroadcast(boolean broadcast) {
    _broadcast = broadcast;
  }
  
  public boolean canWarpSelf() {
    return _warpSelf;
  }
  
  public void setWarpSelf(boolean warpSelf) {
    _warpSelf = warpSelf;
  }
  
  public boolean canWarpOthers() {
    return _warpOthers;
  }
  
  public void setWarpOthers(boolean warpOthers) {
    _warpOthers = warpOthers;
  }
  
  public boolean canKick() {
    return _kick;
  }
  
  public void setKick(boolean kick) {
    _kick = kick;
  }
  
  public boolean canBan() {
    return _ban;
  }
  
  public void setBan(boolean ban) {
    _ban = ban;
  }
  
  public boolean canViewInfo() {
    return _viewInfo;
  }
  
  public void setViewInfo(boolean viewInfo) {
    _viewInfo = viewInfo;
  }
  
  public boolean canViewChatLogs() {
    return _viewChatLogs;
  }
  
  public void setViewChatLogs(boolean viewChatLogs) {
    _viewChatLogs = viewChatLogs;
  }
  
  public boolean canMute() {
    return _mute;
  }
  
  public void setMute(boolean mute) {
    _mute = mute;
  }
  
  public boolean canSpawnSprites() {
    return _spawnSprites;
  }
  
  public void setSpawnSprites(boolean spawnSprites) {
    _spawnSprites = spawnSprites;
  }
  
  public boolean canSpawnItems() {
    return _spawnItems;
  }
  
  public void setSpawnItems(boolean spawnItems) {
    _spawnItems = spawnItems;
  }
  
  public boolean canSpawnSpells() {
    return _spawnSpells;
  }
  
  public void setSpawnSpells(boolean spawnSpells) {
    _spawnSpells = spawnSpells;
  }
  
  public boolean canSpawnNPCs() {
    return _spawnNPCs;
  }
  
  public void setSpawnNPCs(boolean spawnNPCs) {
    _spawnNPCs = spawnNPCs;
  }
  
  public boolean canSpawnEffects() {
    return _spawnEffects;
  }
  
  public void setSpawnEffects(boolean spawnEffects) {
    _spawnEffects = spawnEffects;
  }
  
  public boolean canRespawnMaps() {
    return _respawnMaps;
  }
  
  public void setRespawnMaps(boolean respawnMaps) {
    _respawnMaps = respawnMaps;
  }
  
  public boolean canAlterLocalWeather() {
    return _alterLocalWeather;
  }
  
  public void setAlterLocalWeather(boolean alterLocalWeather) {
    _alterLocalWeather = alterLocalWeather;
  }
  
  public boolean canAlterRegionalWeather() {
    return _alterRegionalWeather;
  }
  
  public void setAlterRegionalWeather(boolean alterRegionalWeather) {
    _alterRegionalWeather = alterRegionalWeather;
  }
  
  public boolean canAlterGlobalWeather() {
    return _alterGlobalWeather;
  }
  
  public void setAlterGlobalWeather(boolean alterGlobalWeather) {
    _alterGlobalWeather = alterGlobalWeather;
  }
  
  public boolean canAlterTime() {
    return _alterTime;
  }
  
  public void setAlterTime(boolean alterTime) {
    _alterTime = alterTime;
  }
  
  public boolean canEditMaps() {
    return _editMaps;
  }
  
  public void setEditMaps(boolean editMaps) {
    _editMaps = editMaps;
  }
  
  public boolean canEditSprites() {
    return _editSprites;
  }
  
  public void setEditSprites(boolean editSprites) {
    _editSprites = editSprites;
  }
  
  public boolean canEditItems() {
    return _editItems;
  }
  
  public void setEditItems(boolean editItems) {
    _editItems = editItems;
  }
  
  public boolean canEditSpells() {
    return _editSpells;
  }
  
  public void setEditSpells(boolean editSpells) {
    _editSpells = editSpells;
  }
  
  public boolean canEditNPCs() {
    return _editNPCs;
  }
  
  public void setEditNPCs(boolean editNPCs) {
    _editNPCs = editNPCs;
  }
  
  public boolean canEditEffects() {
    return _editEffects;
  }
  
  public void setEditEffects(boolean editEffects) {
    _editEffects = editEffects;
  }
  
  public boolean canGrantPermissions() {
    return _grantPermissions;
  }
  
  public void setGrantPermissions(boolean grantPermissions) {
    _grantPermissions = grantPermissions;
  }
  
  public boolean canViewDetailedInfo() {
    return _viewDetailedInfo;
  }
  
  public void setViewDetailedInfo(boolean viewDetailedInfo) {
    _viewDetailedInfo = viewDetailedInfo;
  }
  
  public boolean canBanPermanently() {
    return _banPermanently;
  }
  
  public void setBanPermanently(boolean banPermanently) {
    _banPermanently = banPermanently;
  }
}