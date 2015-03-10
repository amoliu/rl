package br.ufrj.ppgi.rl.fa;

import java.io.Serializable;
import java.util.Set;

import org.ejml.simple.SimpleMatrix;

import br.ufrj.ppgi.matlab.EJMLMatlabUtils;

public class LWRQueryVO implements Serializable
{
  private static final long  serialVersionUID = 1537789904994978687L;

  private SimpleMatrix       result;

  private SimpleMatrix       x;

  private Set<Integer> 		 neighbors;

  private SimpleMatrix       variance;
  
  private double             meanDistance;
  
  public LWRQueryVO(SimpleMatrix result, SimpleMatrix x, Set<Integer> neighbors, SimpleMatrix variance, double meanDistance)
  {
    this.result = result;
    this.neighbors = neighbors;
    this.x = x;
    this.variance = variance;
    this.meanDistance = meanDistance;
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

  public Set<Integer> getNeighbors()
  {
    return neighbors;
  }

  public void setNeighbors(Set<Integer> neighbors)
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

  public double getMeanDistance()
  {
    return meanDistance;
  }

  public void setMeanDistance(double meanDistance)
  {
    this.meanDistance = meanDistance;
  }
}
