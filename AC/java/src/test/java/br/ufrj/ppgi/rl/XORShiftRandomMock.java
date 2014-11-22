package br.ufrj.ppgi.rl;

import org.uncommons.maths.random.XORShiftRNG;

public class XORShiftRandomMock extends XORShiftRNG
{
  private static final long serialVersionUID = -7624248686008825101L;

  private double            rand;

  public XORShiftRandomMock(double rand)
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
