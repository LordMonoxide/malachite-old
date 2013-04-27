package physics;

public abstract class Movable {
  protected float _x, _y;
  protected float _acc, _dec;
  protected float _vel;
  protected float _velScaleX = 1;
  protected float _velScaleY = 1;
  protected float _velTerm;
  protected float _velTarget;
  protected float _bear;
  
  public final float getAcc() {
    return _acc;
  }
  
  public final void setAcc(float acc) {
    _acc = acc;
  }
  
  public final float getDec() {
    return _dec;
  }
  
  public final void setDec(float dec) {
    _dec = dec;
  }
  
  public final float getVel() {
    return _vel;
  }
  
  public void setVel(float vel) {
    _vel = vel;
  }
  
  public final float getVelScaleX() {
    return _velScaleX;
  }
  
  public final void setVelScaleX(float velScaleX) {
    _velScaleX = velScaleX;
  }
  
  public final float getVelScaleY() {
    return _velScaleY;
  }
  
  public final void setVelScaleY(float velScaleY) {
    _velScaleY = velScaleY;
  }
  
  public final float getVelTerm() {
    return _velTerm;
  }
  
  public final void setVelTerm(float velTerm) {
    _velTerm = velTerm;
  }
  
  public final float getVelTarget() {
    return _velTarget;
  }
  
  public final void setVelTarget(float velTarget) {
    _velTarget = velTarget;
  }
  
  public float getBear() {
    return _bear;
  }
  
  public void setBear(float bear) {
    _bear = bear;
  }
  
  public float getX() {
    return _x;
  }
  
  public void setX(float x) {
    _x = x;
  }
  
  public float getY() {
    return _y;
  }
  
  public void setY(float y) {
    _y = y;
  }
  
  public void startMoving() {
    _velTarget = _velTerm;
  }
  
  public void stopMoving() {
    _velTarget = 0;
  }
}