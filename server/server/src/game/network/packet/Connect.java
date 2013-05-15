package game.network.packet;

import game.network.Server;
import game.settings.Settings;
import io.netty.buffer.ByteBuf;
import network.packet.Packet;

public class Connect extends Packet {
  private double _version;
  
  public int getIndex() {
    return 0;
  }
  
  public ByteBuf serialize() {
    return null;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    _version = data.readDouble();
  }
  
  public void process() {
    if(_version != Settings.Net.Version) {
      _connection.kick("Invalid version");
    } else {
      ((game.network.Connection)_connection).setHandler(Server.getLoginHandler());
    }
  }
}