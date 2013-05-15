package network.packet;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;

import network.Connection;

public class Packets {
private static ArrayList<Class<?>> _packet = new ArrayList<Class<?>>();
  
  public static void add(Class<?> packet) {
    _packet.add(packet);
  }
  
  public static Packet create(int index, ByteBuf data, Connection c) throws IndexOutOfBoundsException, Packet.NotEnoughDataException {
    Class<?> packet;
    
    packet = _packet.get(index);
    
    try {
      Packet p = (Packet)packet.newInstance();
      p._connection = c;
      p.deserialize(data);
      return p;
    } catch(InstantiationException | IllegalAccessException e) {
      e.printStackTrace();
    }
    
    return null;
  }
}