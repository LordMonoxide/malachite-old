package network;

import network.codec.EncoderLength;
import network.codec.Encoder;
import network.packet.Packet;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class Client {
  private NioEventLoopGroup _group;
  private Bootstrap _bootstrap;
  private Channel _channel;
  
  public Client() {
    _group = new NioEventLoopGroup();
    _bootstrap = new Bootstrap()
              .group(_group)
              .channel(NioSocketChannel.class)
              .handler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel ch) throws Exception {
                  ch.pipeline().addLast(new EncoderLength(), new Encoder());
                }
    });
  }
  
  public void setAddress(String host, int port) {
    _bootstrap.remoteAddress(host, port);
  }
  
  public void setTimeout(int timeout) {
    _bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeout);
  }
  
  public void setNoDelay(boolean noDelay) {
    _bootstrap.option(ChannelOption.TCP_NODELAY, noDelay);
  }
  
  public void setKeepAlive(boolean keepAlive) {
    _bootstrap.option(ChannelOption.SO_KEEPALIVE, keepAlive);
  }
  
  public void connect() {
    try {
      _channel = _bootstrap.connect().sync().channel();
    } catch(InterruptedException e) {
      e.printStackTrace();
    }
  }
  
  public void connect(final Event callback) {
    _bootstrap.connect().addListener(new ChannelFutureListener() {
      public void operationComplete(ChannelFuture future) throws Exception {
        _channel = future.channel();
        
        if(callback != null) {
          callback.event(future.isSuccess());
        }
      }
    });
  }
  
  public void close() {
    if(_channel != null) {
      try {
        _channel.close().sync();
      } catch(InterruptedException e) {
        e.printStackTrace();
      }
      
      _channel = null;
    }
  }
  
  public void close(final Event callback) {
    if(_channel != null) {
      _channel.close().addListener(new ChannelFutureListener() {
        public void operationComplete(ChannelFuture future) throws Exception {
          _channel = null;
          
          if(callback != null) {
            callback.event(future.isSuccess());
          }
        }
      });
    }
  }
  
  public void shutdown() {
    _group.shutdownGracefully();
  }
  
  public void send(Packet packet) {
    _channel.write(packet);
  }
  
  public static interface Event {
    public void event(boolean success);
  }
}