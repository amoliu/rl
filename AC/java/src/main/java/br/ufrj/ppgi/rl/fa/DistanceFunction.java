package br.ufrj.ppgi.rl.fa;

import org.ejml.ops.NormOps;
import org.ejml.simple.SimpleMatrix;

public class DistanceFunction implements LWRWeightFunction
{
  private static final long serialVersionUID = -6491107115982244606L;

  @Override
  public double[] calculateWeight(SimpleMatrix A, SimpleMatrix query)
  {
    double[] distance = new double[A.numCols()];
    double h;

    SimpleMatrix queryTranspose = query.transpose();

    distance[0] = NormOps.normP2(A.extractVector(false, 0).minus(queryTranspose).getMatrix());
    h = distance[0];
    for (int i = 1; i < distance.length; i++)
    {
      distance[i] = NormOps.normP2(A.extractVector(false, i).minus(queryTranspose).getMatrix());
      if (distance[i] > h)
      {
        h = distance[i];
      }
    }
    h += 0.01;

    for (int i = 0; i < distance.length; i++)
    {
      distance[i] = Math.exp(-Math.pow(distance[i] / h, 2));
    }

    return distance;
  }
}
