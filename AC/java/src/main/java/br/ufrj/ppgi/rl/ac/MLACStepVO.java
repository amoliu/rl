package br.ufrj.ppgi.rl.ac;

import java.io.Serializable;

public class MLACStepVO implements Serializable
{
  private static final long serialVersionUID = 4346503592225388853L;

  private double            error;

  private double            actorUpdate;

  private double            criticUpdate;

  public MLACStepVO(double error, double actorUpdate, double criticUpdate)
  {
    this.error = error;
    this.actorUpdate = actorUpdate;
    this.criticUpdate = criticUpdate;
  }

  public double getError()
  {
    return error;
  }

  public void setError(double error)
  {
    this.error = error;
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
