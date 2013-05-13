package game.network;

public class LoginManager implements Runnable {
  private Thread _thread;
  private boolean _running;
  
  public void start() {
    _running = true;
    _thread = new Thread(this);
    _thread.start();
  }
  
  public void stop() {
    _running = false;
  }
  
  public void run() {
    while(_running) {
      
      
      try {
        Thread.sleep(1);
      } catch(InterruptedException e) { }
    }
  }
}