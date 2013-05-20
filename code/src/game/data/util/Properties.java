package game.data.util;

public class Properties extends java.util.Properties {
  private static final long serialVersionUID = 1L;
  
  public String getString(String key) {
    return getProperty(key);
  }
  
  public byte getByte(String key) throws InvalidDataException {
    String value = getProperty(key);
    
    try {
      return Byte.parseByte(value);
    } catch(Exception e) {
      throw new InvalidDataException("byte", value);
    }
  }
  
  public int getInt(String key) throws InvalidDataException {
    String value = getProperty(key);
    
    try {
      return Integer.parseInt(value);
    } catch(Exception e) {
      throw new InvalidDataException("int", value);
    }
  }
  
  public double getDouble(String key) throws InvalidDataException {
    String value = getProperty(key);
    
    try {
      return Double.parseDouble(value);
    } catch(Exception e) {
      throw new InvalidDataException("double", value);
    }
  }
  
  public boolean getBool(String key) throws InvalidDataException {
    String value = getProperty(key);
    
    try {
      return Boolean.parseBoolean(value);
    } catch(Exception e) {
      throw new InvalidDataException("bool", value);
    }
  }
  
  public void set(String key, String  value) { setProperty(key, value); }
  public void set(String key, byte    value) { setProperty(key, String.valueOf(value)); }
  public void set(String key, int     value) { setProperty(key, String.valueOf(value)); }
  public void set(String key, double  value) { setProperty(key, String.valueOf(value)); }
  public void set(String key, boolean value) { setProperty(key, String.valueOf(value)); }
  
  public static class InvalidDataException extends Exception {
    private static final long serialVersionUID = 1L;
    public InvalidDataException(String expected, String got) {
      super("Invalid data: expected " + expected + ", got " + got);
    }
  }
}