package br.ufrj.ppgi.rl.fa;

import java.io.Serializable;
import java.util.List;

import org.ejml.simple.SimpleMatrix;

import br.ufrj.ppgi.matlab.EJMLMatlabUtils;

public class LWRQueryVO implements Serializable
{
  private static final long serialVersionUID = 1314581578287330362L;

  private SimpleMatrix      result;

  private SimpleMatrix      x;

  private List<Integer>     neighbors;

  public LWRQueryVO(SimpleMatrix result, SimpleMatrix x, List<Integer> neighbors)
  {
    this.result = result;
    this.neighbors = neighbors;
    this.x = x;
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

  public List<Integer> getNeighbors()
  {
    return neighbors;
  }

  public void setNeighbors(List<Integer> neighbors)
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

  @Override
  public String toString()
  {
    return "LLRQueryVO [result=" + result + ", x=" + x + ", neighbors=" + neighbors + "]";
  }
}
