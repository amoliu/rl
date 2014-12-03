package br.ufrj.ppgi.rl.fa;

import java.io.Serializable;

import org.ejml.data.DenseMatrix64F;
import org.ejml.simple.SimpleMatrix;

public interface LWRWeightFunction extends Serializable
{
  public double[] calculateWeight(DenseMatrix64F A, SimpleMatrix query);
}
