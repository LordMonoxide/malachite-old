package network.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.CRC32;

public class Crypto {
  public static String sha256(String data) {
    try {
      MessageDigest sha2 = MessageDigest.getInstance("SHA-256");
      sha2.update(data.getBytes());
      return new BigInteger(1, sha2.digest()).toString(16);
    } catch(NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    
    return null;
  }
  
  public static int crc32(byte[] data) {
    CRC32 crc32 = new CRC32();
    crc32.update(data);
    return (int)crc32.getValue();
  }
  
  public static boolean validateText(String data) {
    if(data == null || data.length() == 0) return false;
    return data.matches("^[\\w ]{4,40}$");
  }
  
  public static boolean validateHash(String hash) {
    if(hash == null) return false;
    return hash.matches("^[\\da-f]{64}$");
  }
}