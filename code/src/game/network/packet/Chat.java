package game.network.packet;

import game.Game;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import network.packet.Packet;

public class Chat extends Packet {
  private String _text;
  
  public Chat() { }
  public Chat(String text) {
    _text = text;
  }
  
  public int getIndex() {
    return 16;
  }
  
  public ByteBuf serialize() {
    ByteBuf b = Unpooled.buffer();
    b.writeShort(_text.length());
    b.writeBytes(_text.getBytes());
    return b;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    byte[] arr = new byte[data.readShort()];
    data.readBytes(arr);
    _text = new String(arr);
  }
  
  public void process() {
    Game.getInstance().gotChat(_text);
  }
}