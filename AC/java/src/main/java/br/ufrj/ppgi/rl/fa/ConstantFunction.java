package br.ufrj.ppgi.rl.fa;

import org.ejml.data.DenseMatrix64F;
import org.ejml.simple.SimpleMatrix;

public class ConstantFunction implements LWRWeightFunction
{
  private static final long serialVersionUID = -4780299092399136320L;

  @Override
  public double[] calculateWeight(DenseMatrix64F A, SimpleMatrix query)
  {
    double[] distance = new double[A.numRows];

    for (int i = 0; i < distance.length; i++)
    {
      distance[i] = 1.0;
    }

    return distance;
  }
}
