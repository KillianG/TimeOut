package fr.aryboo2.timeOut;

public class Schematic
{
  private short[] blocks;
  private byte[] data;
  private short width;
  private short lenght;
  private short height;
  
  public Schematic(short[] blocks2, byte[] data, short width, short lenght, short height)
  {
    this.blocks = blocks2;
    this.data = data;
    this.width = width;
    this.lenght = lenght;
    this.height = height;
  }
  
  public short[] getBlocks()
  {
    return this.blocks;
  }
  
  public byte[] getData()
  {
    return this.data;
  }
  
  public short getWidth()
  {
    return this.width;
  }
  
  public short getLenght()
  {
    return this.lenght;
  }
  
  public short getHeight()
  {
    return this.height;
  }
}
