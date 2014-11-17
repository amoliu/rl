package br.ufrj.ppgi.rl;

import java.io.Serializable;

import org.ejml.simple.SimpleMatrix;

public class Specification implements Serializable
{
  private static final long serialVersionUID = 9069308321270284946L;

  private int               actorMemory;

  private double            actorAlpha;

  private int               actorNeighbors;

  private SimpleMatrix      actorMin;

  private SimpleMatrix      actorMax;

  private double            criticInitialValue;

  private int               criticMemory;

  private double            criticAlpha;

  private int               criticNeighbors;

  private int               observationDimensions;

  private int               actionDimensions;

  private float             lamda;

  private float             gamma;

  private float             sd;

  private int               processModelMemory;

  private int               processModelNeighbors;

  private SimpleMatrix      observationMinValue;

  private SimpleMatrix      observationMaxValue;

  public int getActorMemory()
  {
    return actorMemory;
  }

  public void setActorMemory(int actorMemory)
  {
    this.actorMemory = actorMemory;
  }

  public double getActorAlpha()
  {
    return actorAlpha;
  }

  public void setActorAlpha(double actorAlpha)
  {
    this.actorAlpha = actorAlpha;
  }

  public int getActorNeighbors()
  {
    return actorNeighbors;
  }

  public void setActorNeighbors(int actorNeighbors)
  {
    this.actorNeighbors = actorNeighbors;
  }

  public SimpleMatrix getActorMin()
  {
    return actorMin;
  }

  public void setActorMin(double[][] value)
  {
    setActorMin(new SimpleMatrix(value));
  }

  public void setActorMin(SimpleMatrix actorMin)
  {
    this.actorMin = actorMin;
  }

  public SimpleMatrix getActorMax()
  {
    return actorMax;
  }

  public void setActorMax(double[][] value)
  {
    setActorMax(new SimpleMatrix(value));
  }

  public void setActorMax(SimpleMatrix actorMax)
  {
    this.actorMax = actorMax;
  }

  public double getCriticInitialValue()
  {
    return criticInitialValue;
  }

  public void setCriticInitialValue(double criticInitialValue)
  {
    this.criticInitialValue = criticInitialValue;
  }

  public int getCriticMemory()
  {
    return criticMemory;
  }

  public void setCriticMemory(int criticMemory)
  {
    this.criticMemory = criticMemory;
  }

  public double getCriticAlpha()
  {
    return criticAlpha;
  }

  public void setCriticAlpha(double criticAlpha)
  {
    this.criticAlpha = criticAlpha;
  }

  public int getCriticNeighbors()
  {
    return criticNeighbors;
  }

  public void setCriticNeighbors(int criticNeighbors)
  {
    this.criticNeighbors = criticNeighbors;
  }

  public float getLamda()
  {
    return lamda;
  }

  public void setLamda(float lamda)
  {
    this.lamda = lamda;
  }

  public float getGamma()
  {
    return gamma;
  }

  public void setGamma(float gamma)
  {
    this.gamma = gamma;
  }

  public float getSd()
  {
    return sd;
  }

  public void setSd(float sd)
  {
    this.sd = sd;
  }

  public int getProcessModelMemory()
  {
    return processModelMemory;
  }

  public int getProcessModelNeighbors()
  {
    return processModelNeighbors;
  }

  public void setProcessModelMemory(int processModelMemory)
  {
    this.processModelMemory = processModelMemory;
  }

  public void setProcessModelNeighbors(int processModelNeighbors)
  {
    this.processModelNeighbors = processModelNeighbors;
  }

  public int getObservationDimensions()
  {
    return observationDimensions;
  }

  public int getActionDimensions()
  {
    return actionDimensions;
  }

  public void setObservationDimensions(int observationDimensions)
  {
    this.observationDimensions = observationDimensions;
  }

  public void setActionDimensions(int actionDimensions)
  {
    this.actionDimensions = actionDimensions;
  }

  public SimpleMatrix getObservationMinValue()
  {
    return observationMinValue;
  }

  public void setObservationMinValue(double[][] value)
  {
    this.observationMinValue = new SimpleMatrix(value);
  }

  public SimpleMatrix getObservationMaxValue()
  {
    return observationMaxValue;
  }

  public void setObservationMaxValue(double[][] value)
  {
    this.observationMaxValue = new SimpleMatrix(value);
  }
}
