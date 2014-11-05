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

}
