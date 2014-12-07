package br.ufrj.ppgi.rl.ac;

import java.io.Serializable;

public class StepVO implements Serializable
{
  private static final long serialVersionUID = -2290815722035382357L;

  private double            error;

  private double[][]        action;

  private int               modelSkiped;

  public StepVO(double[][] action)
  {
    this.action = action;
    this.error = 0;
    this.modelSkiped = 0;
  }

  public StepVO(double error, double[][] action)
  {
    this.error = error;
    this.action = action;
    this.modelSkiped = 0;
  }

  public StepVO(double error, double[][] action, int modelSkiped)
  {
    this.error = error;
    this.action = action;
    this.modelSkiped = modelSkiped;
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
}
