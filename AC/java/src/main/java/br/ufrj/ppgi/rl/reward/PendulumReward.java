package br.ufrj.ppgi.rl.reward;

import static java.lang.Math.PI;
import static java.lang.Math.pow;

import org.ejml.simple.SimpleMatrix;

public class PendulumReward implements Reward
{
  @Override
  public double calculate(SimpleMatrix state, SimpleMatrix action)
  {
    return -5 * pow(state.get(0) - PI, 2) - 0.1 * pow(state.get(1), 2) - 1 * pow(action.get(0), 2);
  }
}
