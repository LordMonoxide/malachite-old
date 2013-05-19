package physics;

import graphics.util.Time;

import java.util.LinkedList;

public class Sandbox implements Runnable {
  private Thread _thread;
  private boolean _running;
  private double _timer;
  private double _timerInterval;
  
  private double _fpsTimer;
  private double _fpsTimerInterval;
  private int _fpsCount;
  private int _fps;
  
  private LinkedList<Movable> _obj = new LinkedList<Movable>();
  
  public int getFPS() { return _fps; }
  
  protected void addToSandbox(Movable m) {
    _obj.add(m);
  }
  
  protected void removeFromSandbox(Movable m) {
    _obj.remove(m);
  }
  
  public void run() {
    _fps = 120;
    _timerInterval = Time.HzToTicks(_fps);
    _timer = Time.getTime();
    
    _fpsTimerInterval = Time.HzToTicks(1);
    _fpsTimer = Time.getTime() + _fpsTimerInterval;
    
    while(_running) {
      if(_timer <= Time.getTime()) {
        for(Movable m : _obj) {
          if(m._velTarget != 0) {
            if(m._vel < m._velTerm) {
              m.setVel(m._vel + m._acc);
              if(m._vel > m._velTerm) m.setVel(m._velTerm);
            }
          } else {
            if(m._vel > 0) {
              m.setVel(m._vel - m._dec);
              if(m._vel < 0) m.setVel(0);
            }
          }
          
          if(m._vel != 0) {
            if(m._bear != -1) {
              double a = Math.toRadians(m._bear);
              m.setX(m._x + (float)Math.cos(a) * m._vel * m._velScaleX);
              m.setY(m._y + (float)Math.sin(a) * m._vel * m._velScaleY);
            } else {
              m.setX(m._x + m._vel * m._velScaleX);
              m.setY(m._y + m._vel * m._velScaleY);
            }
          }
        }
        
        _fpsCount++;
        
        _timer += _timerInterval;
      }
      
      if(_fpsTimer <= Time.getTime()) {
        _fpsTimer = Time.getTime() + _fpsTimerInterval;
        _fps = _fpsCount;
        _fpsCount = 0;
      }
      
      try {
        Thread.sleep(1);
      } catch(InterruptedException e) { }
    }
  }
  
  protected void startSandbox() {
    if(_thread != null) return;
    _running = true;
    _thread = new Thread(this);
    _thread.start();
  }
  
  protected void stopSandbox() {
    _running = false;
  }
}