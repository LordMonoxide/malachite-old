package graphics.gl00;

public abstract class Scalable extends Drawable {
  /*public   float   getX();
  public   float   getY();
  public   float   getW();
  public   float   getH();
  public   float   getTX();
  public   float   getTY();
  public   float   getTW();
  public   float   getTH();
  public   float[] getColour();
  public Texture   getTexture();
  public  Vertex[] getVertices();
  
  public void setX(float x);
  public void setY(float y);
  public void setXY(float x, float y);
  public void setW(float w);
  public void setH(float h);
  public void setWH(float w, float h);
  public void setXYWH(float x, float y, float w, float h);
  public void setTX(float x);
  public void setTY(float y);
  public void setTXY(float x, float y);
  public void setTW(float w);
  public void setTH(float h);
  public void setTWH(float w, float h);
  public void setTXYWH(float x, float y, float w, float h);
  public void setColour(float[] c);
  public void setTexture(Texture texture);
  public void setVertices(Vertex[] vertex);*/
  
  public abstract void setSize(float[] st, float[] sl, float tw, float th, float ts);
  //public void updateVertices();
  /*public void createQuad();
  public void draw();*/
}