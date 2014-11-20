package br.ufrj.ppgi.rl.fa;

import org.ejml.simple.SimpleMatrix;

public class ConstantFunction implements LWRWeightFunction
{
  private static final long serialVersionUID = -5984526906379860436L;

  @Override
  public double[] calculateWeight(SimpleMatrix A, SimpleMatrix query)
  {
    double[] distance = new double[A.numCols()];

    for (int i = 0; i < distance.length; i++)
    {
      distance[i] = 1.0;
    }

    return distance;
  }
}
