package br.ufrj.ppgi.rl;

import java.util.Random;

public class RandomMock extends Random
{
  private static final long serialVersionUID = -1713516863211749550L;

  private double            rand;

  public RandomMock(double rand)
  {
    this.rand = rand;
  }

  @Override
  public synchronized double nextGaussian()
  {
    return rand;
  }
  
  @Override
  public double nextDouble()
  {
    return rand;
  }

}
