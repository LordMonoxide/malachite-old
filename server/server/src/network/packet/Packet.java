package network.packet;

import network.Connection;
import io.netty.buffer.ByteBuf;

public abstract class Packet {
  protected Connection _connection;
  
  public Connection getConnection() {
    return _connection;
  }
  
  public abstract int getIndex();
  public abstract ByteBuf serialize();
  public abstract void deserialize(ByteBuf data) throws NotEnoughDataException;
  public abstract void process();
  
  public static class NotEnoughDataException extends Exception {
    private static final long serialVersionUID = 1L;
    public NotEnoughDataException() {
      super("Not enough data");
    }
  }
}