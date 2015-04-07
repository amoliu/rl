package br.ufrj.ppgi.rl.ac;

import java.io.Serializable;

public class SACStepVO implements Serializable
{
  private static final long serialVersionUID = 7854829380297588918L;

  private double            actorUpdate;

  private double            criticUpdate;

  public SACStepVO(double actorUpdate, double criticUpdate)
  {
    this.actorUpdate = actorUpdate;
    this.criticUpdate = criticUpdate;
  }

  public double getActorUpdate()
  {
    return actorUpdate;
  }

  public void setActorUpdate(double actorUpdate)
  {
    this.actorUpdate = actorUpdate;
  }

  public double getCriticUpdate()
  {
    return criticUpdate;
  }

  public void setCriticUpdate(double criticUpdate)
  {
    this.criticUpdate = criticUpdate;
  }
}
