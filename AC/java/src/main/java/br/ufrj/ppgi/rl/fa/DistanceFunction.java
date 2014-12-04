package br.ufrj.ppgi.rl.fa;

import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.NormOps;
import org.ejml.simple.SimpleMatrix;

public class DistanceFunction implements LWRWeightFunction
{
  private static final long serialVersionUID = 2233637347671355523L;

  @Override
  public double[] calculateWeight(DenseMatrix64F A, SimpleMatrix query)
  {
    double[] distance = new double[A.numRows];
    double h;

    SimpleMatrix AA = SimpleMatrix.wrap(A);

    distance[0] = NormOps.normP2(AA.extractVector(true, 0).minus(query).getMatrix());
    h = distance[0];
    for (int i = 1; i < distance.length; i++)
    {
      distance[i] = NormOps.normP2(AA.extractVector(true, i).minus(query).getMatrix());
      if (distance[i] > h)
      {
        h = distance[i];
      }
    }
    h += 0.01;

    for (int i = 0; i < distance.length; i++)
    {
      distance[i] = Math.sqrt(Math.exp(-Math.pow(distance[i] / h, 2)));
    }

    return distance;
  }
}
