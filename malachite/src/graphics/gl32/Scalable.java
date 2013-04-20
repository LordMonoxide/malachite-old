package graphics.gl32;

import graphics.shared.textures.Texture;

public class Scalable extends Drawable implements graphics.gl00.Scalable {
  private float _borderL;
  private float _borderT;
  private float _borderR;
  private float _borderB;
  private float _borderL2;
  private float _borderT2;
  private float _borderR2;
  private float _borderB2;
  private float _borderH2;
  private float _borderV2;
  
  private float[][] _border;
  private float[][] _borderS;
  
  private Drawable[] _d = new Drawable[9];
  
  public Scalable() {
    for(int i = 0; i < _d.length; i++) {
      _d[i] = new Drawable();
    }
    
    setSize1(new float[] {32, 32, 32, 32});
    setSize2(new float[] {21, 21, 21, 21});
    setBorderS(new float[][] {
        { 0,  0, 32, 32}, {32,  0,  1, 32}, {64,  0, 32, 32},
        { 0, 32, 32,  1}, {32, 32,  1,  1}, {64, 32, 32,  1},
        { 0, 64, 32, 32}, {32, 64,  1, 32}, {64, 64, 32, 32}
    });
  }
  
  public void setW(float w) {
    _loc[2] = w;
    updateVertices();
  }
  
  public void setH(float h) {
    _loc[3] = h;
    updateVertices();
  }
  
  public void setWH(float w, float h) {
    _loc[2] = w;
    _loc[3] = h;
    updateVertices();
  }
  
  public void setXYWH(float x, float y, float w, float h) {
    _loc[0] = x;
    _loc[1] = y;
    _loc[2] = w;
    _loc[3] = h;
    updateVertices();
    updateLoc();
  }
  
  public void setXYWH(float[] loc) {
    _loc = loc;
    updateVertices();
    updateLoc();
  }
  
  public void setColour(float[] c) {
    for(int i = 0; i < _d.length; i++) {
      _d[i].setColour(c);
    }
  }
  
  public void setTexture(Texture texture) {
    _texture = texture;
    
    for(int i = 0; i < _d.length; i++) {
      _d[i].setTexture(_texture);
    }
    
    if(_texture != null) {
      setWH(texture.getW(), texture.getH());
    }
    
    updateVertices();
  }
  
  public void setSize1(float[] s) {
    _borderL = s[0];
    _borderT = s[1];
    _borderR = s[2];
    _borderB = s[3];
  }
  
  public void setSize2(float[] s) {
    _borderL2 = s[0];
    _borderT2 = s[1];
    _borderR2 = s[2];
    _borderB2 = s[3];
    _borderH2 = _borderL2 + _borderR2;
    _borderV2 = _borderT2 + _borderB2;
  }
  
  public void setBorderS(float[][] b) {
    _borderS = b;
  }
  
  public void updateVertices() {
    _border = new float[][] {
        {0, 0, _borderL, _borderT},
         {_borderL2, 0, _loc[2] - _borderH2, _borderT},
         {_loc[2] - _borderR2, 0, _borderR, _borderT},
        {0, _borderT2, _borderL, _loc[3] - _borderV2},
         {_borderL2, _borderT2, _loc[2] - _borderH2, _loc[3] - _borderV2},
         {_loc[2] - _borderR2, _borderT2, _borderR, _loc[3] - _borderV2},
        {0, _loc[3] - _borderB2, _borderR, _borderB},
         {_borderL2, _loc[3] - _borderB2, _loc[2] - _borderH2, _borderB},
         {_loc[2] - _borderR2, _loc[3] - _borderB2, _borderR, _borderB}
    };
    
    for(int i = 0; i < _d.length; i++) {
      _d[i].setXYWH(_border[i][0] + _loc[0], _border[i][1] + _loc[1], _border[i][2], _border[i][3]);
      _d[i].setTXYWH(_borderS[i][0], _borderS[i][1], _borderS[i][2], _borderS[i][3]);
      _d[i].createQuad();
    }
  }
  
  public void draw() {
    if(!_visible) return;
    
    for(int i = 0; i < _d.length; i++) {
      _d[i].draw();
    }
  }
}