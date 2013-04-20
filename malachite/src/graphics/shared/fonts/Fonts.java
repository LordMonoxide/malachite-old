package graphics.shared.fonts;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import graphics.gl00.Context;
import graphics.shared.textures.Texture;
import graphics.shared.textures.Textures;

public class Fonts {
  private static Fonts _instance = new Fonts();
  
  public static Fonts getInstance() {
    return _instance;
  }
  
  private Textures _textures = Context.getTextures();
  private HashMap<String, Font> _fonts = new HashMap<String, Font>();
  
  private Font _default = getFont("Tahoma", 11);
  
  private Fonts() { }
  
  public Font getDefault() {
    return _default;
  }
  
  private FontRenderContext _rendCont;
  private java.awt.Font _font;
  
  private ArrayList<GlyphMetrics> _metrics;
  private ArrayList<Glyph> _glyph;
  
  private int _w,  _h;
  private int _w2, _h2;
  
  public Font getFont(String name, int size) {
    String fullName = name + "." + size;
    if(_fonts.containsKey(fullName)) {
      System.out.println("Font \"" + fullName + "\" already loaded.");
      return _fonts.get(fullName);
    }
    
    _font = new java.awt.Font(name, 0, size);
    _rendCont = new FontRenderContext(null, true, true);
    
    FontMetrics fm = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB_PRE).getGraphics().getFontMetrics(_font);
    
    int start = 0x20;
    int end = 0x3FF;
    _w = 0; _h = 0;
    _w2 = 0; _h2 = 0;
    
    _metrics = new ArrayList<GlyphMetrics>();
    _glyph = new ArrayList<Glyph>();
    _metrics.ensureCapacity(end - start);
    
    // Add enough null elements so that the code
    // points will sync up with the array location
    for(int i = 0; i < start; i++) {
      _glyph.add(null);
    }
    
    for(int i = start; i <= end; i++) {
      addGlyph(i);
    }
    
    _w2 = graphics.util.Math.nextPowerOfTwo(_w);
    byte[] b = new byte[(_w2 - _w) * 4];
    
    ByteBuffer buffer = ByteBuffer.allocateDirect(_w2 * _h * 4);
    
    for(int y = 0; y < _h2; y++) {
      for(GlyphMetrics glyph : _metrics) {
        buffer.put(glyph.getRow());
      }
      
      buffer.put(b);
    }
    
    buffer.position(0);
    
    Texture texture = _textures.getTexture("Font." + _font.getFontName() + "." + _font.getSize(), _w2, _h, buffer);
    _glyph.ensureCapacity(end);
    
    int x = 0;
    int y = 0;
    
    for(GlyphMetrics glyph : _metrics) {
      // Specify array location to ensure code points sync up
      _glyph.add(glyph.getCode(), new Glyph(x, y, fm.charWidth(glyph.getCode()), glyph.getH(), glyph.getW2(), glyph.getH2(), texture));
      x += glyph.getW2();
    }
    
    Font f = new Font(_h2, _glyph);
    f.setTexture(texture);
    _fonts.put(fullName, f);
    
    System.out.println("Font \"" + fullName + "\" created (" + _w2 + "x" + _h + ").");
    
    return f;
  }
  
  private void addGlyph(int i) {
    if(!Character.isValidCodePoint(i)) return;
    
    char[] character = Character.toChars(i);
    
    Rectangle2D bounds = _font.getStringBounds(character, 0, character.length, _rendCont);
    
    if(bounds.getWidth() == 0) {
      //System.out.println("Zero width character ignored: " + Integer.toHexString(i));
      _glyph.add(null);
      return;
    }
    
    BufferedImage bi = new BufferedImage((int)bounds.getWidth(), (int)bounds.getHeight(), BufferedImage.TYPE_INT_ARGB);
    
    Graphics2D g = (Graphics2D)bi.getGraphics();
    //g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setFont(_font);
    g.drawString(new String(character), 0, (int)(bounds.getHeight() - bounds.getMaxY()));
    
    int[] argb = null;
    argb = bi.getData().getPixels(0, 0, (int)bounds.getWidth(), (int)bounds.getHeight(), argb);
    
    byte[] argbByte = new byte[argb.length];
    for(int n = 0; n < argb.length; n++) {
      argbByte[n] = (byte)argb[n];
    }
    
    GlyphMetrics glyphMetric = new GlyphMetrics(i, (int)bounds.getWidth(), (int)bounds.getHeight(), argbByte);
    _metrics.add(glyphMetric);
    
    _w += glyphMetric.getW2();
    if(glyphMetric.getH() > _h2) {
      _h = glyphMetric.getH2();
      _h2 = glyphMetric.getH();
    }
  }
}