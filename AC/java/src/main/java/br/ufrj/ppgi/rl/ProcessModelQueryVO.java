package br.ufrj.ppgi.rl;

import java.io.Serializable;

import br.ufrj.ppgi.rl.fa.LWRQueryVO;

public class ProcessModelQueryVO implements Serializable
{
  private static final long serialVersionUID = 3209831204654697749L;

  private LWRQueryVO        lwrQueryVO;

  private double            reward;

  public ProcessModelQueryVO(LWRQueryVO lwrQueryVO, double reward)
  {
    this.lwrQueryVO = lwrQueryVO;
    this.reward = reward;
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
}
