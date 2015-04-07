package br.ufrj.ppgi.rl;

import java.io.Serializable;

import org.ejml.alg.dense.mult.MatrixDimensionException;
import org.ejml.simple.SimpleMatrix;
import org.uncommons.maths.random.XORShiftRNG;

import br.ufrj.ppgi.matlab.EJMLMatlabUtils;
import br.ufrj.ppgi.rl.fa.LWR;
import br.ufrj.ppgi.rl.fa.LWRQueryVO;

public class ActorLLR implements Serializable
{
  private static final long serialVersionUID = -9178817118835693301L;

  protected LWR             llr;

  private Specification     specification;

  protected XORShiftRNG     random;

  private double            lastRandom;

  public void init(Specification specification)
  {
    this.specification = specification;

    llr = LWR.createLLR()
             .setSize(specification.getActorMemory())
             .setInputDimension(specification.getObservationDimensions())
             .setOutputDimension(specification.getActionDimensions())
             .setK(specification.getActorNeighbors())
             .setValuesToRebuildTheTree(specification.getActorValuesToRebuildTree());
    
    if (specification.getActorMemoryManagement() != null)
    {
      llr.setMemoryManagement(specification.getActorMemoryManagement());
    }

    random = new XORShiftRNG();
  }

  public ActionVO action(SimpleMatrix observation, float sd)
  {
    SimpleMatrix action = actionWithoutRandomness(observation);
    nextRandom(sd);

    return new ActionVO(EJMLMatlabUtils.wrap(action.plus(lastRandom), specification.getActorMax(),
                                             specification.getActorMin()),
                        EJMLMatlabUtils.wrap(action, specification.getActorMax(), specification.getActorMin()));
  }

  public ActionVO action(SimpleMatrix observation)
  {
    SimpleMatrix action = actionWithoutRandomness(observation);
    nextRandom();

    return new ActionVO(EJMLMatlabUtils.wrap(action.plus(lastRandom), specification.getActorMax(),
                                             specification.getActorMin()),
                        EJMLMatlabUtils.wrap(action, specification.getActorMax(), specification.getActorMin()));
  }

  public double[][] actionWithoutRandomness(double[][] observation)
  {
    return EJMLMatlabUtils.getMatlabMatrixFromSimpleMatrix(actionWithoutRandomness(new SimpleMatrix(observation)));
  }

  public double[][] actionWithRandomness(SimpleMatrix observation)
  {
    return EJMLMatlabUtils.getMatlabMatrixFromSimpleMatrix(action(observation).getAction());
  }

  public double[][] actionWithRandomness(double[][] observation)
  {
    return EJMLMatlabUtils.getMatlabMatrixFromSimpleMatrix(action(new SimpleMatrix(observation)).getAction());
  }

  public SimpleMatrix actionWithoutRandomness(SimpleMatrix observation)
  {
    if (observation.numRows() != 1)
    {
      throw new MatrixDimensionException("Observation is not a row vector");
    }

    if (observation.numCols() != specification.getObservationDimensions())
    {
      throw new MatrixDimensionException("Observation is not the expected length");
    }

    LWRQueryVO queryResult = llr.query(observation);
    return EJMLMatlabUtils.wrap(queryResult.getResult(), specification.getActorMax(), specification.getActorMin());
  }

  public double update(double delta, SimpleMatrix observation, SimpleMatrix action, double alpha, boolean randomness)
  {
    delta = delta * alpha;
    
    if (randomness)
    {
      delta = delta * lastRandom;
      return update(delta, observation, action);
    }
    else
    {
      return update(delta, observation, action);
    }
  }

  public double update(double delta, SimpleMatrix observation, SimpleMatrix action, boolean randomness)
  {
	return update(delta, observation, action, specification.getActorAlpha(), randomness);
  }

  private double update(double delta, SimpleMatrix observation, SimpleMatrix action)
  {
    LWRQueryVO queryResult = llr.query(observation);
    int insertIndex = add(observation, action);

    if (insertIndex != -1)
      queryResult.getNeighbors().add(insertIndex);

    llr.update(queryResult.getNeighbors(), delta, specification.getActorMax(), specification.getActorMin());
    
    return delta;
  }

  private int add(SimpleMatrix observation, SimpleMatrix action)
  {
    return llr.add(observation, EJMLMatlabUtils.wrap(action, specification.getActorMax(), specification.getActorMin()));
  }

  private void nextRandom(float sd)
  {
    lastRandom = random.nextGaussian() * sd;
  }

  private void nextRandom()
  {
    nextRandom(specification.getSd());
  }

  public LWR getLLR()
  {
    return llr;
  }
}
