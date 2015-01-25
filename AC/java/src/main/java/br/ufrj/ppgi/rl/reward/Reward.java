package br.ufrj.ppgi.rl.reward;

import org.ejml.simple.SimpleMatrix;

public interface Reward
{
  public double calculate(SimpleMatrix state, SimpleMatrix action);
}
