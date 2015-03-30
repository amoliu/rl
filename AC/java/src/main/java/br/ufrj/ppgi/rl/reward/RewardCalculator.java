package br.ufrj.ppgi.rl.reward;

import org.ejml.simple.SimpleMatrix;

public enum RewardCalculator
{
  Pendulum(new PendulumReward()), Cartpole(new CartpoleReward());

  private final Reward reward;

  private RewardCalculator(final Reward reward)
  {
    this.reward = reward;
  }

  public double calculate(double[][] state, double[][] action)
  {
    return reward.calculate(new SimpleMatrix(state), new SimpleMatrix(action));
  }

  public double calculate(SimpleMatrix state, SimpleMatrix action)
  {
    return reward.calculate(state, action);
  }
}
