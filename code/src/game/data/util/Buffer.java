package game.data.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import network.util.Crypto;

public class Buffer {
  private int _allocSize = 64;
  private byte[] _buffer;
  private int _available;
  private int _read;
  private int _write;
  
  public Buffer() {
    _buffer = new byte[_allocSize];
  }
  
  public Buffer(int allocSize) {
    _allocSize = allocSize;
    _buffer = new byte[_allocSize];
  }
  
  public Buffer(File f) throws IOException {
    load(f);
  }
  
  public Buffer(byte[] data) {
    _buffer = data;
    _write = data.length;
  }
  
  public int crc() {
    return Crypto.crc32(_buffer);
  }
  
  public int size() {
    return _write;
  }
  
  public void clear() {
    _available = _buffer.length;
    _read = 0;
    _write = 0;
  }
  
  public void allocate(int size) {
    if(size > _available) {
      int newSize = _buffer.length;
      
      if(size <= _allocSize) {
        newSize += _allocSize;
        _available += _allocSize;
      } else {
        newSize += size;
        _available += size;
      }
      
      byte[] newBuffer = new byte[newSize];
      System.arraycopy(_buffer, 0, newBuffer, 0, _buffer.length);
      _buffer = newBuffer;
    }
  }
  
  public void trim() {
    int size = remaining();
    byte[] newBuffer = new byte[size];
    System.arraycopy(_buffer, _read, newBuffer, 0, size);
    _buffer = newBuffer;
  }
  
  public int remaining() {
    return _write - _read;
  }
  
  public void save(File f) throws IOException {
    f.getParentFile().mkdirs();
    f.createNewFile();
    
    FileOutputStream fs = new FileOutputStream(f);
    
    fs.write(serialize());
    fs.close();
  }
  
  public void load(File f) throws IOException {
    FileInputStream fs = new FileInputStream(f);
    
    _buffer = new byte[(int)f.length()];
    _write = _buffer.length;
    
    fs.read(_buffer);
    fs.close();
  }
  
  public byte[] serialize() {
    return serialize(_write);
  }
  
  public byte[] serialize(int size) {
    if(size > _write) {
      System.err.println("serialize size out of range");
      return null;
    }
    
    byte[] b = new byte[size];
    System.arraycopy(_buffer, 0, b, 0, size);
    return b;
  }
  
  public void deserialize(byte[] b) {
    clear();
    put(b, b.length);
  }
  
  public void put(byte[] b) {
    put(b.length);
    put(b, b.length);
  }
  
  public void put(byte[] b, int size) {
    if(size == -1) size = b.length;
    if(size == 0) return;
    if(_available < size) allocate(size);
    
    System.arraycopy(b, 0, _buffer, _write, size);
    _write += size;
    _available -= size;
  }
  
  public void put(byte b) {
    if(_available < 1) allocate(1);
    _buffer[_write++] = b;
    _available--;
  }
  
  public void put(boolean b) {
    put(b ? (byte)1 : (byte)0);
  }
  
  public void put(short s) {
    if(_available < 2) allocate(4);
    byte[] b = toByteArray(s);
    System.arraycopy(b, 0, _buffer, _write, 2);
    _write += 2;
    _available -= 2;
  }
  
  public void put(int i) {
    if(_available < 4) allocate(4);
    byte[] b = toByteArray(i);
    System.arraycopy(b, 0, _buffer, _write, 4);
    _write += 4;
    _available -= 4;
  }
  
  public void put(long l) {
    if(_available < 8) allocate(8);
    byte[] b = toByteArray(l);
    System.arraycopy(b, 0, _buffer, _write, 8);
    _write += 8;
    _available -= 8;
  }
  
  public void put(float f) {
    put(Float.floatToRawIntBits(f));
  }
  
  public void put(double d) {
    put(Double.doubleToRawLongBits(d));
  }
  
  public void put(String s) {
    if(s == null || s.length() == 0) {
      put(0);
      return;
    }
    
    put(s.getBytes());
  }
  
  public byte[] getBytes() {
    int size = getInt();
    if(size == 0) return null;
    if(size < 0 || size > remaining()) {
      System.err.println("getBytes size out of range");
      return null;
    }
    
    return getBytes(size);
  }
  
  public byte[] getBytes(int size) {
    if(size == 0) return null;
    if(size == -1 || size > remaining()) size = remaining();
    
    byte[] data = new byte[size];
    System.arraycopy(_buffer, _read, data, 0, size);
    _read += size;
    return data;
  }
  
  public byte getByte() {
    if(remaining() < 1) {
      System.err.println("No buffer space left");
      return 0;
    }
    
    return _buffer[_read++];
  }
  
  public boolean getBool() {
    return (getByte() == 1) ? true : false;
  }
  
  public short getShort() {
    if(remaining() < 2) {
      System.err.println("No buffer space left");
      return 0;
    }
    
    byte[] b = new byte[2];
    System.arraycopy(_buffer, _read, b, 0, 2);
    _read += 2;
    return shortFromByteArray(b);
  }
  
  public int getInt() {
    if(remaining() < 4) {
      System.err.println("No buffer space left");
      return 0;
    }
    
    byte[] b = new byte[4];
    System.arraycopy(_buffer, _read, b, 0, 4);
    _read += 4;
    return intFromByteArray(b);
  }
  
  public long getLong() {
    if(remaining() < 8) {
      System.err.println("No buffer space left");
      return 0;
    }
    
    byte[] b = new byte[8];
    System.arraycopy(_buffer, _read, b, 0, 8);
    _read += 8;
    return longFromByteArray(b);
  }
  
  public float getFloat() {
    return Float.intBitsToFloat(getInt());
  }
  
  public double getDouble() {
    return Double.longBitsToDouble(getLong());
  }
  
  public String getString() {
    byte[] b = getBytes();
    if(b != null) return new String(b);
    return null;
  }
  
  public static byte[] toByteArray(short i) {
    return new byte[] {
        (byte)((i >> 8) & 0xFF),
        (byte)( i       & 0xFF)
    };
  }
  
  public static byte[] toByteArray(int i) {
    return new byte[] {
        (byte)((i >> 24) & 0xFF),
        (byte)((i >> 16) & 0xFF),
        (byte)((i >>  8) & 0xFF),
        (byte)( i        & 0xFF)
    };
  }
  
  public static byte[] toByteArray(long l) {
    return new byte[] {
        (byte)((l >> 56) & 0xFF),
        (byte)((l >> 48) & 0xFF),
        (byte)((l >> 40) & 0xFF),
        (byte)((l >> 32) & 0xFF),
        (byte)((l >> 24) & 0xFF),
        (byte)((l >> 16) & 0xFF),
        (byte)((l >>  8) & 0xFF),
        (byte)( l        & 0xFF)
    };
  }
  
  public static short shortFromByteArray(byte[] b) {
    return (short)((b[0] & 0xFF) << 8 |
                    b[1] & 0xFF);
  }
  
  public static int intFromByteArray(byte[] b) {
    return (b[0] & 0xFF) << 24 |
           (b[1] & 0xFF) << 16 |
           (b[2] & 0xFF) <<  8 |
            b[3] & 0xFF;
  }
  
  public static long longFromByteArray(byte[] b) {
    return (b[0] & 0xFFl) << 56 |
           (b[1] & 0xFFl) << 48 |
           (b[2] & 0xFFl) << 40 |
           (b[3] & 0xFFl) << 32 |
           (b[4] & 0xFFl) << 24 |
           (b[5] & 0xFFl) << 16 |
           (b[6] & 0xFFl) <<  8 |
            b[7] & 0xFFl;
  }
}