package network;

import network.packet.Packet;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

public abstract class Connection {
  private Channel _channel;
  
  public Channel getChannel() {
    return _channel;
  }
  
  protected void setChannel(Channel channel) {
    _channel = channel;
  }
  
  public void close() {
    try {
      _channel.close().sync();
    } catch(InterruptedException e) {
      e.printStackTrace();
    }
  }
  
  public void close(final Event callback) {
    _channel.close().addListener(new ChannelFutureListener() {
      public void operationComplete(ChannelFuture future) throws Exception {
        _channel = null;
        
        if(callback != null) {
          callback.event();
        }
      }
    });
  }
  
  public void kick(String reason) {
    System.err.println(_channel.remoteAddress().toString() + " is being kicked: " + reason);
    close(null);
  }
  
  public void send(Packet packet) {
    _channel.write(packet);
  }
  
  public static interface Event {
    public void event();
  }
}