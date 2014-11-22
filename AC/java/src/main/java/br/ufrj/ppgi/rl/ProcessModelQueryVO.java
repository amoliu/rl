package br.ufrj.ppgi.rl;

import java.io.Serializable;

import br.ufrj.ppgi.rl.fa.LWRQueryVO;

public class ProcessModelQueryVO implements Serializable
{
  private static final long serialVersionUID = -711054600461999634L;

  private LWRQueryVO        lwrQueryVO;

  private double            reward;

  private int               terminal;

  public ProcessModelQueryVO(LWRQueryVO lwrQueryVO, double reward, int terminal)
  {
    this.lwrQueryVO = lwrQueryVO;
    this.reward = reward;
    this.terminal = terminal;
  }

  public LWRQueryVO getLWRQueryVO()
  {
    return lwrQueryVO;
  }

  public void setLWRQueryVO(LWRQueryVO lwrQueryVO)
  {
    this.lwrQueryVO = lwrQueryVO;
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
