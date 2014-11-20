package br.ufrj.ppgi.rl.fa;

import java.io.Serializable;

import org.ejml.simple.SimpleMatrix;

public interface LWRWeightFunction extends Serializable
{
  public double[] calculateWeight(SimpleMatrix A, SimpleMatrix query);
}
