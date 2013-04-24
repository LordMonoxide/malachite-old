package graphics.themes;

public class Theme {
  private static Theme _instance = new Theme();
  public static Theme getInstance() { return _instance; }
  
  protected String _fontName = "Tahoma";
  protected    int _fontSize = 11;
  
  protected String   _buttonBackgroundTexture = "gui/button.png";
  protected  float[] _buttonBackgroundSize1  = {2, 2, 2, 2};
  protected  float[] _buttonBackgroundSize2  = {2, 2, 2, 2};
  protected  float[] _buttonBackgroundSize3  = {5, 5, 1};
  protected  float[] _buttonBackColour       = {0.2f, 0.2f, 0.2f, 1};
  protected  float[] _buttonGlowColour       = {0.3f, 0.3f, 0.3f, 1};
  protected  float[] _buttonForeColour       = {1, 1, 1, 1};
  protected  float[] _buttonSize             = {90, 20};
  protected String   _buttonText             = "Button";
  
  protected  float[] _labelForeColour        = {1, 1, 1, 1};
  protected boolean  _labelAutoSize          = true;
  protected String   _labelText              = "Label";
  
  protected Theme() { };
  
  public String   getFontName() { return _fontName; }
  public    int   getFontSize() { return _fontSize; }
  
  public String   getButtonBackgroundTexture() { return _buttonBackgroundTexture; }
  public  float[] getButtonBackgroundSize1()   { return _buttonBackgroundSize1; }
  public  float[] getButtonBackgroundSize2()   { return _buttonBackgroundSize2; }
  public  float   getButtonBackgroundTW()      { return _buttonBackgroundSize3[0]; }
  public  float   getButtonBackgroundTH()      { return _buttonBackgroundSize3[1]; }
  public  float   getButtonBackgroundTS()      { return _buttonBackgroundSize3[2]; }
  public  float[] getButtonBackColour()        { return _buttonBackColour; }
  public  float[] getButtonGlowColour()        { return _buttonGlowColour; }
  public  float[] getButtonForeColour()        { return _buttonForeColour; }
  public  float   getButtonWidth()             { return _buttonSize[0]; }
  public  float   getButtonHeight()            { return _buttonSize[1]; }
  public String   getButtonText()              { return _buttonText; }
  
  public  float[] getLabelForeColour()         { return _labelForeColour; }
  public boolean  getLabelAutoSize()           { return _labelAutoSize; }
  public String   getLabelText()               { return _labelText; }
}