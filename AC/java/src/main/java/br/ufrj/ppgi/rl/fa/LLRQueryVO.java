package br.ufrj.ppgi.rl.fa;

import java.io.Serializable;
import java.util.List;

import org.ejml.simple.SimpleMatrix;

public class LLRQueryVO implements Serializable
{
  private static final long serialVersionUID = -4546987902103479820L;

  private SimpleMatrix      result;

  private List<Integer>     neighbors;

  public LLRQueryVO(SimpleMatrix result, List<Integer> neighbors)
  {
    this.result = result;
    this.neighbors = neighbors;
  }

  public SimpleMatrix getResult()
  {
    return result;
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
}
