package graphics.gl14;

import graphics.gl00.Vertex;
import graphics.shared.textures.Texture;

import org.lwjgl.opengl.GL11;

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
  
  public Scalable() {
    _renderMode = GL11.GL_TRIANGLE_STRIP;
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
  }
  
  public void setXYWH(float[] loc) {
    _loc = loc;
    updateVertices();
  }
  
  public void setTexture(Texture texture) {
    _texture = texture;
    
    if(_texture != null) {
      setWH(texture.getW(), texture.getH());
    }
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
    if(_texture == null) return;
    
    _vertex = new Vertex[36];
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
    
    float[][] b = new float[_borderS.length][];
    
    for(int i = 0; i < _borderS.length; i++) {
      b[i] = new float[_borderS[i].length];
      b[i][0] = _borderS[i][0] / _texture.getW();
      b[i][2] = _borderS[i][2] / _texture.getW();
      b[i][1] = _borderS[i][1] / _texture.getH();
      b[i][3] = _borderS[i][3] / _texture.getH();
    }
    
    int i = 0;
    for(int n = 0; n < _border.length; n++) {
      Vertex[] v = Vertex.createQuad(_border[n], b[n], _col);
      _vertex[i++] = v[0];
      _vertex[i++] = v[1];
      _vertex[i++] = v[2];
      _vertex[i++] = v[3];
    }
  }
}