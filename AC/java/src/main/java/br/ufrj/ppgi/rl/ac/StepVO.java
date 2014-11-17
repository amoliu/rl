package br.ufrj.ppgi.rl.ac;

import java.io.Serializable;

public class StepVO implements Serializable
{
  private static final long serialVersionUID = -7960585062630414374L;

  private double            error;

  private double[][]        action;

  public StepVO(double[][] action)
  {
    this.error = 0;
    this.action = action;
  }

  public StepVO(double error, double[][] action)
  {
    this.error = error;
    this.action = action;
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
}
