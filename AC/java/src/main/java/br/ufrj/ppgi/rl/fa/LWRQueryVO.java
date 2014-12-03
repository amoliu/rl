package br.ufrj.ppgi.rl.fa;

import java.io.Serializable;
import java.util.ArrayList;

import org.ejml.simple.SimpleMatrix;

import br.ufrj.ppgi.matlab.EJMLMatlabUtils;

public class LWRQueryVO implements Serializable
{
  private static final long  serialVersionUID = 1537789904994978687L;

  private SimpleMatrix       result;

  private SimpleMatrix       x;

  private ArrayList<Integer> neighbors;

  private SimpleMatrix       variance;

  public LWRQueryVO(SimpleMatrix result, SimpleMatrix x, ArrayList<Integer> neighbors, SimpleMatrix variance)
  {
    this.result = result;
    this.neighbors = neighbors;
    this.x = x;
    this.variance = variance;
  }

  public SimpleMatrix getResult()
  {
    return result;
  }

  public double[][] getMatlabResult()
  {
    return EJMLMatlabUtils.getMatlabMatrixFromSimpleMatrix(result);
  }

  public void setResult(SimpleMatrix result)
  {
    this.result = result;
  }

  public ArrayList<Integer> getNeighbors()
  {
    return neighbors;
  }

  public void setNeighbors(ArrayList<Integer> neighbors)
  {
    this.neighbors = neighbors;
  }

  public SimpleMatrix getX()
  {
    return x;
  }

  public void setX(SimpleMatrix x)
  {
    this.x = x;
  }

  public SimpleMatrix getVariance()
  {
    return variance;
  }

  public void setVariance(SimpleMatrix variance)
  {
    this.variance = variance;
  }

  @Override
  public String toString()
  {
    return "LWRQueryVO [result=" + result + ", x=" + x + ", neighbors=" + neighbors + ", variance=" + variance + "]";
  }
}
