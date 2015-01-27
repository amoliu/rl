package br.ufrj.ppgi.rl;

import java.io.Serializable;

import org.ejml.simple.SimpleMatrix;

import br.ufrj.ppgi.rl.fa.LLRMemoryManagement;
import br.ufrj.ppgi.rl.reward.RewardCalculator;

public class Specification implements Serializable
{
  private static final long   serialVersionUID = -4101879734249891756L;

  private int                 actorMemory;

  private double              actorAlpha;

  private int                 actorNeighbors;

  private SimpleMatrix        actorMin;

  private SimpleMatrix        actorMax;

  private SimpleMatrix        actorRange;

  private int                 actorValuesToRebuildTree;

  private LLRMemoryManagement actorMemoryManagement;

  private double              criticInitialValue;

  private int                 criticMemory;

  private double              criticAlpha;

  private int                 criticNeighbors;

  private int                 criticValuesToRebuildTree;

  private LLRMemoryManagement criticMemoryManagement;

  private int                 observationDimensions;

  private int                 actionDimensions;

  private float               lamda;

  private float               gamma;

  private float               sd;

  private int                 processModelMemory;

  private int                 processModelNeighbors;

  private double              processModelCrossLimit;

  private SimpleMatrix        processModelUpperBound;

  private double              processModelThreshold;

  private int                 processModelStepsPerEpisode;

  private double              processModelCriticAlpha;

  private double              processModelActorAplha;

  private double              processModelGamma;

  private int                 processModelValuesToRebuildTree;

  private int                 processModelIterationsWithoutLearning;

  private int                 processModelExplorationRate;

  private float               processModelSd;

  private LLRMemoryManagement processModelMemoryManagement;

  private SimpleMatrix        observationMinValue;

  private SimpleMatrix        observationMaxValue;

  private SimpleMatrix        observationRange;

  private double              rewardRange;

  private int                 explorationRate;

  private RewardCalculator    rewardCalculator;

  private SimpleMatrix        normalization;

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

  public void setObservationMinValue(SimpleMatrix observationMinValue)
  {
    this.observationMinValue = observationMinValue;
  }

  public void setObservationMinValue(double[][] value)
  {
    this.observationMinValue = new SimpleMatrix(value);
  }

  public SimpleMatrix getObservationMaxValue()
  {
    return observationMaxValue;
  }

  public void setObservationMaxValue(SimpleMatrix observationMaxValue)
  {
    this.observationMaxValue = observationMaxValue;
  }

  public void setObservationMaxValue(double[][] value)
  {
    this.observationMaxValue = new SimpleMatrix(value);
  }

  public double getProcessModelCrossLimit()
  {
    return processModelCrossLimit;
  }

  public void setProcessModelCrossLimit(double processModelCrossLimit)
  {
    this.processModelCrossLimit = processModelCrossLimit;
  }

  public SimpleMatrix getProcessModelUpperBound()
  {
    return processModelUpperBound;
  }

  public void setProcessModelUpperBound(SimpleMatrix processModelUpperBound)
  {
    this.processModelUpperBound = processModelUpperBound;
  }

  public void setProcessModelUpperBound(double[][] value)
  {
    this.processModelUpperBound = new SimpleMatrix(value);
  }

  public double getProcessModelThreshold()
  {
    return processModelThreshold;
  }

  public void setProcessModelThreshold(double processModelThreshold)
  {
    this.processModelThreshold = processModelThreshold;
  }

  public int getProcessModelStepsPerEpisode()
  {
    return processModelStepsPerEpisode;
  }

  public void setProcessModelStepsPerEpisode(int processModelStepsPerEpisode)
  {
    this.processModelStepsPerEpisode = processModelStepsPerEpisode;
  }

  public double getProcessModelCriticAlpha()
  {
    return processModelCriticAlpha;
  }

  public void setProcessModelCriticAlpha(double processModelCriticAlpha)
  {
    this.processModelCriticAlpha = processModelCriticAlpha;
  }

  public double getProcessModelActorAplha()
  {
    return processModelActorAplha;
  }

  public void setProcessModelActorAplha(double processModelActorAplha)
  {
    this.processModelActorAplha = processModelActorAplha;
  }

  public double getProcessModelGamma()
  {
    return processModelGamma;
  }

  public void setProcessModelGamma(double processModelGamma)
  {
    this.processModelGamma = processModelGamma;
  }

  public int getActorValuesToRebuildTree()
  {
    return actorValuesToRebuildTree;
  }

  public void setActorValuesToRebuildTree(int actorValuesToRebuildTree)
  {
    this.actorValuesToRebuildTree = actorValuesToRebuildTree;
  }

  public int getCriticValuesToRebuildTree()
  {
    return criticValuesToRebuildTree;
  }

  public void setCriticValuesToRebuildTree(int criticValuesToRebuildTree)
  {
    this.criticValuesToRebuildTree = criticValuesToRebuildTree;
  }

  public int getProcessModelValuesToRebuildTree()
  {
    return processModelValuesToRebuildTree;
  }

  public void setProcessModelValuesToRebuildTree(int processModelValuesToRebuildTree)
  {
    this.processModelValuesToRebuildTree = processModelValuesToRebuildTree;
  }

  public int getExplorationRate()
  {
    return explorationRate;
  }

  public void setExplorationRate(int explorationRate)
  {
    this.explorationRate = explorationRate;
  }

  public int getProcessModelIterationsWithoutLearning()
  {
    return processModelIterationsWithoutLearning;
  }

  public void setProcessModelIterationsWithoutLearning(int processModelIterationsWithoutLearning)
  {
    this.processModelIterationsWithoutLearning = processModelIterationsWithoutLearning;
  }

  public SimpleMatrix getActorRange()
  {
    if (actorRange == null)
    {
      actorRange = actorMax.minus(actorMin);
    }

    return actorRange;
  }

  public SimpleMatrix getObservationRange()
  {
    if (observationRange == null)
    {
      observationRange = observationMaxValue.minus(observationMinValue).divide(100);
    }

    return observationRange;
  }

  public double getRewardRange()
  {
    return rewardRange;
  }

  public void setRewardRange(double rewardRange)
  {
    this.rewardRange = rewardRange;
  }

  public int getProcessModelExplorationRate()
  {
    return processModelExplorationRate;
  }

  public void setProcessModelExplorationRate(int processModelExplorationRate)
  {
    this.processModelExplorationRate = processModelExplorationRate;
  }

  public float getProcessModelSd()
  {
    return processModelSd;
  }

  public void setProcessModelSd(float processModelSd)
  {
    this.processModelSd = processModelSd;
  }

  public LLRMemoryManagement getActorMemoryManagement()
  {
    return actorMemoryManagement;
  }

  public void setActorMemoryManagement(LLRMemoryManagement actorMemoryManagement)
  {
    this.actorMemoryManagement = actorMemoryManagement;
  }

  public LLRMemoryManagement getCriticMemoryManagement()
  {
    return criticMemoryManagement;
  }

  public void setCriticMemoryManagement(LLRMemoryManagement criticMemoryManagement)
  {
    this.criticMemoryManagement = criticMemoryManagement;
  }

  public LLRMemoryManagement getProcessModelMemoryManagement()
  {
    return processModelMemoryManagement;
  }

  public void setProcessModelMemoryManagement(LLRMemoryManagement processModelMemoryManagement)
  {
    this.processModelMemoryManagement = processModelMemoryManagement;
  }

  public RewardCalculator getRewardCalculator()
  {
    return rewardCalculator;
  }

  public void setRewardCalculator(RewardCalculator rewardCalculator)
  {
    this.rewardCalculator = rewardCalculator;
  }

  public SimpleMatrix getNormalization()
  {
    return normalization;
  }

  public void setNormalization(double[][] normalization)
  {
    this.normalization = new SimpleMatrix(normalization);
  }
}
