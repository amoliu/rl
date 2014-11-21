package br.ufrj.ppgi.rl;

import ec.util.MersenneTwisterFast;

public class MersenneRandomMock extends MersenneTwisterFast
{
  private static final long serialVersionUID = -7624248686008825101L;
  
  private double            rand;

  public MersenneRandomMock(double rand)
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
