package br.ufrj.ppgi.rl.reward;

import org.ejml.simple.SimpleMatrix;

public class CartpoleReward implements Reward
{
  @Override
  public double calculate(SimpleMatrix state, SimpleMatrix action)
  {
    if (failed(state))
      return -10000;

    return -1 * Math.pow(state.get(2), 2) - 0.1 * Math.pow(state.get(3), 2) - 2 * Math.pow(state.get(0), 2) - 0.1
           * Math.pow(state.get(1), 2);
  }

  private boolean failed(SimpleMatrix state)
  {
    if (state.get(0) < -2.4 || state.get(0) > 2.4)
      return true;

    return false;
  }

}
