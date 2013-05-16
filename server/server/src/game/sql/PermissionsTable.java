package game.sql;

import game.data.account.Permissions;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import sql.Table;

public class PermissionsTable extends Table {
  private static PermissionsTable _instance = new PermissionsTable();
  
  public static PermissionsTable getInstance() {
    return _instance;
  }
  
  private Permissions _permissions;
  
  public PermissionsTable() {
    super("permissions", "p_id");
    _create = _sql.prepareStatement("CREATE TABLE permissions (" +
                                   "p_id INT NOT NULL AUTO_INCREMENT," +
                                   "p_p_login BOOL NOT NULL,p_p_alter_chars BOOL NOT NULL,p_p_speak BOOL NOT NULL,p_p_whisper BOOL NOT NULL,p_p_shout BOOL NOT NULL," +
                                   "p_m_broadcast BOOL NOT NULL,p_m_warp_self BOOL NOT NULL,p_m_warp_others BOOL NOT NULL,p_m_kick BOOL NOT NULL,p_m_ban BOOL NOT NULL,p_m_view_info BOOL NOT NULL,p_m_view_chat_logs BOOL NOT NULL,p_m_mute BOOL NOT NULL," +
                                   "p_g_spawn_sprites BOOL NOT NULL,p_g_spawn_items BOOL NOT NULL,p_g_spawn_spells BOOL NOT NULL,p_g_spawn_npcs BOOL NOT NULL,p_g_spawn_effects BOOL NOT NULL,p_g_respawn_maps BOOL NOT NULL,p_g_alter_local_weather BOOL NOT NULL,p_g_alter_regional_weather BOOL NOT NULL,p_g_alter_global_weather BOOL NOT NULL,p_g_alter_time BOOL NOT NULL," +
                                   "p_e_can_edit_maps BOOL NOT NULL,p_e_can_edit_sprites BOOL NOT NULL,p_e_can_edit_items BOOL NOT NULL,p_e_can_edit_spells BOOL NOT NULL,p_e_can_edit_npcs BOOL NOT NULL,p_e_can_edit_effects BOOL NOT NULL," +
                                   "p_a_can_grant_permissions BOOL NOT NULL,p_a_can_view_detailed_info BOOL NOT NULL,p_a_can_ban_permanently BOOL NOT NULL," +
                                   "CONSTRAINT pk_p_id UNIQUE (p_id)" +
                                   ")");
    _insert = _sql.prepareStatement("INSERT INTO permissions VALUES (null, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
    _update = _sql.prepareStatement("UPDATE characters SET " +
                                   "p_p_login=?,p_p_alter_chars=?,p_p_speak=?,p_p_whisper=?,p_p_shout=?," +
                                   "p_m_broadcast=?,p_m_warp_self=?,p_m_warp_others=?,p_m_kick=?,p_m_ban=?,p_m_view_info=?,p_m_view_chat_logs=?,p_m_mute=?" +
                                   "p_g_spawn_sprites=?,p_g_spawn_items=?,p_g_spawn_spells=?,p_g_spawn_npcs=?,p_g_spawn_effects=?,p_g_respawn_maps=?,p_g_alter_local_weather=?,p_g_alter_regional_weather=?,p_g_alter_global_weather=?,p_g_alter_time=?," +
                                   "p_e_can_edit_maps=?,p_e_can_edit_sprites=?,p_e_can_edit_items=?,p_e_can_edit_spells=?,p_e_can_edit_npcs=?,p_e_can_edit_effects=?,p_a_can_grant_permissions=?,p_a_can_view_detailed_info=?,p_a_can_ban_permanently=?," +
                                   "WHERE p_id=?");
  }
  
  public boolean exists() {
    return super.exists();
  }
  
  public void create() throws SQLException {
    super.create();
    
    _permissions = Permissions.getDefaultPermissions();
    insert();
  }
  
  public void drop() throws SQLException {
    super.drop();
  }
  
  public void insert() throws SQLException {
    updateValues(_insert);
    _insert.execute();
  }
  
  public void select() throws SQLException {
    super.select();
    
    if(_result.next()) {
      int i = 2;
      
      _permissions = new Permissions();
      _permissions.setLogin(_result.getBoolean(i++));
      _permissions.setAlterChars(_result.getBoolean(i++));
      _permissions.setSpeak(_result.getBoolean(i++));
      _permissions.setWhisper(_result.getBoolean(i++));
      _permissions.setShout(_result.getBoolean(i++));
      _permissions.setBroadcast(_result.getBoolean(i++));
      _permissions.setWarpSelf(_result.getBoolean(i++));
      _permissions.setWarpOthers(_result.getBoolean(i++));
      _permissions.setKick(_result.getBoolean(i++));
      _permissions.setBan(_result.getBoolean(i++));
      _permissions.setViewInfo(_result.getBoolean(i++));
      _permissions.setViewChatLogs(_result.getBoolean(i++));
      _permissions.setMute(_result.getBoolean(i++));
      _permissions.setSpawnSprites(_result.getBoolean(i++));
      _permissions.setSpawnItems(_result.getBoolean(i++));
      _permissions.setSpawnSpells(_result.getBoolean(i++));
      _permissions.setSpawnNPCs(_result.getBoolean(i++));
      _permissions.setSpawnEffects(_result.getBoolean(i++));
      _permissions.setRespawnMaps(_result.getBoolean(i++));
      _permissions.setAlterLocalWeather(_result.getBoolean(i++));
      _permissions.setAlterRegionalWeather(_result.getBoolean(i++));
      _permissions.setAlterGlobalWeather(_result.getBoolean(i++));
      _permissions.setAlterTime(_result.getBoolean(i++));
      _permissions.setEditMaps(_result.getBoolean(i++));
      _permissions.setEditSprites(_result.getBoolean(i++));
      _permissions.setEditItems(_result.getBoolean(i++));
      _permissions.setEditSpells(_result.getBoolean(i++));
      _permissions.setEditNPCs(_result.getBoolean(i++));
      _permissions.setEditEffects(_result.getBoolean(i++));
      _permissions.setGrantPermissions(_result.getBoolean(i++));
      _permissions.setViewDetailedInfo(_result.getBoolean(i++));
      _permissions.setBanPermanently(_result.getBoolean(i++));
    }
  }
  
  public void update() throws SQLException {
    updateValues(_update);
    _update.setInt(32, _id);
    _update.execute();
  }
  
  public void delete() throws SQLException {
    super.delete();
  }
  
  public Permissions getPermissions() {
    return _permissions;
  }
  
  private void updateValues(PreparedStatement p) throws SQLException {
    int i = 1;
    p.setBoolean(i++, _permissions.canLogin());
    p.setBoolean(i++, _permissions.canAlterChars());
    p.setBoolean(i++, _permissions.canSpeak());
    p.setBoolean(i++, _permissions.canWhisper());
    p.setBoolean(i++, _permissions.canShout());
    p.setBoolean(i++, _permissions.canBroadcast());
    p.setBoolean(i++, _permissions.canWarpSelf());
    p.setBoolean(i++, _permissions.canWarpOthers());
    p.setBoolean(i++, _permissions.canKick());
    p.setBoolean(i++, _permissions.canBan());
    p.setBoolean(i++, _permissions.canViewInfo());
    p.setBoolean(i++, _permissions.canViewChatLogs());
    p.setBoolean(i++, _permissions.canMute());
    p.setBoolean(i++, _permissions.canSpawnSprites());
    p.setBoolean(i++, _permissions.canSpawnItems());
    p.setBoolean(i++, _permissions.canSpawnSpells());
    p.setBoolean(i++, _permissions.canSpawnNPCs());
    p.setBoolean(i++, _permissions.canSpawnEffects());
    p.setBoolean(i++, _permissions.canRespawnMaps());
    p.setBoolean(i++, _permissions.canAlterLocalWeather());
    p.setBoolean(i++, _permissions.canAlterRegionalWeather());
    p.setBoolean(i++, _permissions.canAlterGlobalWeather());
    p.setBoolean(i++, _permissions.canAlterTime());
    p.setBoolean(i++, _permissions.canEditMaps());
    p.setBoolean(i++, _permissions.canEditSprites());
    p.setBoolean(i++, _permissions.canEditItems());
    p.setBoolean(i++, _permissions.canEditSpells());
    p.setBoolean(i++, _permissions.canEditNPCs());
    p.setBoolean(i++, _permissions.canEditEffects());
    p.setBoolean(i++, _permissions.canGrantPermissions());
    p.setBoolean(i++, _permissions.canViewDetailedInfo());
    p.setBoolean(i++, _permissions.canBanPermanently());
  }
}