package br.ufrj.ppgi.matlab;

import java.io.Serializable;

import org.ejml.simple.SimpleMatrix;

public class EJMLMatlabUtils implements Serializable
{
  private static final long serialVersionUID = 4777124000071928621L;

  public static double[][] getMatlabMatrixFromSimpleMatrix(SimpleMatrix matrix)
  {
    double[][] matlabResult = new double[matrix.numRows()][matrix.numCols()];

    for (int i = 0; i < matrix.numRows(); i++)
    {
      System.arraycopy(matrix.getMatrix().data, (i * matrix.numCols()), matlabResult[i], 0, matrix.numCols());
    }

    return matlabResult;
  }

  public static SimpleMatrix wrap(SimpleMatrix value, SimpleMatrix maxValue, SimpleMatrix minValue)
  {
    for (int i = 0; i < value.numRows(); i++)
    {
      for (int j = 0; j < value.numCols(); j++)
      {
        if (value.get(i, j) > maxValue.get(i, j))
        {
          value.set(i, j, maxValue.get(i, j));
        }

        if (value.get(i, j) < minValue.get(i, j))
        {
          value.set(i, j, minValue.get(i, j));
        }
      }
    }
    return value;
  }
  
  public static SimpleMatrix denormalize(SimpleMatrix observation, SimpleMatrix normalizationFactor)
  {
    SimpleMatrix denormalized = new SimpleMatrix(observation);
    
    for (int i = 0; i < observation.numRows(); i++)
    {
      for (int j = 0; j < observation.numCols(); j++)
      {
        denormalized.set(i, j, observation.get(i, j) * normalizationFactor.get(i, j));
      }
    }
    
    return denormalized;
  }

}
