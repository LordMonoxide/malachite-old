package game.data.account;

public class Character {
  private int _id;
  private Account _account;
  private String _name;
  private String _world;
  private float _x, _y;
  private int _z;
  
  public Character(int id, Account account) {
    _id = id;
    _account = account;
  }
  
  public int     getID()      { return _id; }
  public Account getAccount() { return _account; }
  public String  getName()    { return _name; }
  public String  getWorld()   { return _world; }
  public float   getX()       { return _x; }
  public float   getY()       { return _y; }
  public int     getZ()       { return _z; }
  
  public void setID     (int id)          { _id = id; }
  public void setAccount(Account account) { _account = account; }
  public void setName   (String name)     { _name = name; }
  public void setWorld  (String world)    { _world = world; }
  public void setX      (float x)         { _x = x; }
  public void setY      (float y)         { _y = y; }
  public void setZ      (int z)           { _z = z; }
}