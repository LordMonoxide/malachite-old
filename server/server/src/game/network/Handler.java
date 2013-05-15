package game.network;

import java.util.concurrent.ConcurrentLinkedQueue;

import network.packet.Packet;

public class Handler implements Runnable {
  private ConcurrentLinkedQueue<Packet> _packet = new ConcurrentLinkedQueue<Packet>();
  
  private Thread _thread;
  private boolean _running;
  
  public void postPacket(Packet p) {
    if(_thread == null) start();
    _packet.add(p);
  }
  
  public void start() {
    System.out.println("Starting new packet handler.");
    _running = true;
    _thread = new Thread(this);
    _thread.start();
  }
  
  public void stop() {
    _running = false;
  }
  
  public void run() {
    Packet p;
    
    while(_running) {
      if((p = _packet.poll()) != null) {
        p.process();
      } else {
        try {
          Thread.sleep(1);
        } catch(InterruptedException e) { }
      }
    }
    
    System.out.println("Packet handler finished.");
  }
}