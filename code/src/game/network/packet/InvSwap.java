package game.network.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import network.packet.Packet;

public class InvSwap extends Packet {
  private int _inv1, _inv2;
  private int _val;
  
  public InvSwap(int inv1, int inv2, int val) {
    _inv1 = inv1;
    _inv2 = inv2;
    _val = val;
  }
  
  public int getIndex() {
    return 30;
  }
  
  public ByteBuf serialize() {
    ByteBuf b = Unpooled.buffer();
    b.writeInt(_inv1);
    b.writeInt(_inv2);
    b.writeInt(_val);
    return b;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    
  }
  
  public void process() {
    
  }
}