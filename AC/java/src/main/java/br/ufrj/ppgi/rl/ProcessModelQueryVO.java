package br.ufrj.ppgi.rl;

import java.io.Serializable;

import org.ejml.simple.SimpleMatrix;

public class ProcessModelQueryVO implements Serializable
{
  private static final long serialVersionUID = 1233875606427979503L;

  private SimpleMatrix      observation;

  private double            reward;

  private int               terminal;

  public ProcessModelQueryVO(SimpleMatrix observation, double reward, int terminal)
  {
    this.observation = observation;
    this.reward = reward;
    this.terminal = terminal;
  }

  public SimpleMatrix getObservation()
  {
    return observation;
  }

  public void setObservation(SimpleMatrix observation)
  {
    this.observation = observation;
  }

  public double getReward()
  {
    return reward;
  }

  public void setReward(double reward)
  {
    this.reward = reward;
  }

  public int getTerminal()
  {
    return terminal;
  }

  public void setTerminal(int terminal)
  {
    this.terminal = terminal;
  }
}
