package br.ufrj.ppgi.rl.ac;

import java.io.Serializable;

public class StepVO implements Serializable
{
  private static final long serialVersionUID = -2290815722035382357L;

  private double            error;

  private double[][]        action;

  private int               modelSkiped;

  private double            meanDistance;

  private double            actorUpdate;

  private double            criticUpdate;

  public StepVO(double[][] action)
  {
    this.action = action;
    this.error = 0;
    this.modelSkiped = 0;
    this.meanDistance = 0;
  }

  public StepVO(double error, double[][] action)
  {
    this.error = error;
    this.action = action;
    this.modelSkiped = 0;
    this.meanDistance = 0;
  }

  public StepVO(double error, double[][] action, int modelSkiped, double meanDistance)
  {
    this.error = error;
    this.action = action;
    this.modelSkiped = modelSkiped;
    this.meanDistance = meanDistance;
  }

  public double getError()
  {
    return error;
  }

  public void setError(double error)
  {
    this.error = error;
  }

  public double[][] getAction()
  {
    return action;
  }

  public void setAction(double[][] action)
  {
    this.action = action;
  }

  public int getModelSkiped()
  {
    return modelSkiped;
  }

  public void setModelSkiped(int modelSkiped)
  {
    this.modelSkiped = modelSkiped;
  }

  public double getMeanDistance()
  {
    return meanDistance;
  }

  public void setMeanDistance(double meanDistance)
  {
    this.meanDistance = meanDistance;
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
