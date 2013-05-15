package network;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

import network.codec.Decoder;
import network.codec.DecoderLength;
import network.packet.Packet;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class Server {
  private ServerBootstrap _bootstrap;
  private Channel _channel;
  
  private Connections _connection;
  private Class<? extends Connection> _connectionClass;
  
  private Events _events;
  
  public Server(Class<? extends Connection> connectionClass) {
    final Server s = this;
    
    _bootstrap = new ServerBootstrap()
              .group(new NioEventLoopGroup(), new NioEventLoopGroup())
              .channel(NioServerSocketChannel.class)
              .childHandler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel ch) throws Exception {
                  ch.pipeline().addLast(new DecoderLength(), new Decoder(s));
                  ch.pipeline().addLast(new Handler());
                }
    });
    
    _connectionClass = connectionClass;
    _connection = new Connections();
    _events = new Events();
  }
  
  public Events events() {
    return _events;
  }
  
  public Connections connections() {
    return _connection;
  }
  
  public void setAddress(int port) {
    _bootstrap.localAddress(port);
  }
  
  public void setBacklog(int backlog) {
    _bootstrap.option(ChannelOption.SO_BACKLOG, backlog);
  }
  
  public void setNoDelay(boolean noDelay) {
    _bootstrap.childOption(ChannelOption.TCP_NODELAY, noDelay);
  }
  
  public void setKeepAlive(boolean keepAlive) {
    _bootstrap.childOption(ChannelOption.SO_KEEPALIVE, keepAlive);
  }
  
  public void bind() {
    try {
      _channel = _bootstrap.bind().sync().channel();
    } catch(InterruptedException e) {
      e.printStackTrace();
    }
  }
  
  public void bind(final Event callback) {
    _bootstrap.bind().addListener(new ChannelFutureListener() {
      public void operationComplete(ChannelFuture future) throws Exception {
        _channel = future.channel();
        
        if(callback != null) {
          callback.event(future.isSuccess());
        }
      }
    });
  }
  
  public void close() {
    try {
      _channel.close().sync();
    } catch(InterruptedException e) {
      e.printStackTrace();
    }
    
    _channel = null;
  }
  
  public void close(final Event callback) {
    _channel.close().addListener(new ChannelFutureListener() {
      public void operationComplete(ChannelFuture future) throws Exception {
        _channel = null;
        
        if(callback != null) {
          callback.event(future.isSuccess());
        }
      }
    });
  }
  
  public class Connections {
    private ConcurrentHashMap<Integer, Connection> _connection = new ConcurrentHashMap<Integer, Connection>();
    
    protected Connections() { }
    
    protected Connection add(Channel c) {
      if(!_connection.containsKey(c.id())) {
        try {
          Connection connection = _connectionClass.newInstance();
          connection.setChannel(c);
          _connection.put(c.id(), connection);
          return connection;
        } catch(InstantiationException | IllegalAccessException e) {
          e.printStackTrace();
        }
      }
      
      return null;
    }
    
    protected Connection remove(Channel c) {
      return _connection.remove(c.id());
    }
    
    public Connection get(Channel c) {
      return _connection.get(c.id());
    }
  }
  
  private class Handler extends ChannelInboundMessageHandlerAdapter<Packet> {
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
      throw new Exception(cause);
    }
    
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
      Connection c;
      
      if((c = _connection.add(ctx.channel())) != null) {
        _events.raiseConnect(c);
      }
    }
    
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
      _events.raiseDisconnect(_connection.remove(ctx.channel()));
    }
    
    public void messageReceived(ChannelHandlerContext ctx, Packet msg) throws Exception {
      _events.raisePacket(msg);
    }
  }
  
  public static interface Event {
    public void event(boolean success);
  }
  
  public static class Events {
    private LinkedList<Connect> _connect    = new LinkedList<Connect>();
    private LinkedList<Connect> _disconnect = new LinkedList<Connect>();
    private LinkedList<Packet>  _packet     = new LinkedList<Packet>();
    
    public void onConnect   (Connect e) { _connect.add(e); }
    public void onDisconnect(Connect e) { _disconnect.add(e); }
    public void onPacket    (Packet  e) { _packet.add(e); }
    
    protected Events() { }
    
    protected void raiseConnect(Connection c) {
      for(Connect e : _connect) {
        e.event(c);
      }
    }
    
    protected void raiseDisconnect(Connection c) {
      for(Connect e : _connect) {
        e.event(c);
      }
    }
    
    protected void raisePacket(network.packet.Packet p) {
      for(Packet e : _packet) {
        e.event(p);
      }
    }
    
    public static interface Connect { public void event(Connection c); }
    public static interface Packet  { public void event(network.packet.Packet p); }
  }
}