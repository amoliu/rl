package br.ufrj.ppgi.rl;

import java.io.Serializable;

import org.ejml.alg.dense.mult.MatrixDimensionException;
import org.ejml.simple.SimpleMatrix;
import org.uncommons.maths.random.XORShiftRNG;

import br.ufrj.ppgi.matlab.EJMLMatlabUtils;
import br.ufrj.ppgi.rl.fa.LLR;
import br.ufrj.ppgi.rl.fa.LWRQueryVO;

public class ActorLLR implements Serializable
{
  private static final long serialVersionUID = -9178817118835693301L;

  protected LLR             llr;

  private Specification     specification;

  protected XORShiftRNG     random;

  private double            lastRandom;

  public void init(Specification specification)
  {
    this.specification = specification;

    llr = new LLR(specification.getActorMemory(), specification.getObservationDimensions(),
                  specification.getActionDimensions(), specification.getActorNeighbors(),
                  specification.getActorValuesToRebuildTree());

    random = new XORShiftRNG();
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

  public void update(double delta, SimpleMatrix observation, SimpleMatrix action, double alpha, boolean randomness)
  {
    if (randomness)
    {
      updateWithRandomness(delta, observation, action, alpha);
    }
    else
    {
      updateWithoutRandomness(delta, observation, action, alpha);
    }
  }

  public void updateWithRandomness(double delta, SimpleMatrix observation, SimpleMatrix action, double alpha)
  {
    delta = delta * lastRandom * alpha;

    LWRQueryVO queryResult = llr.query(observation);
    llr.update(queryResult.getNeighbors(), delta, specification.getActorMax(), specification.getActorMin());
  }

  public void updateWithoutRandomness(double delta, SimpleMatrix observation, SimpleMatrix action, double alpha)
  {
    delta = delta * alpha;

    LWRQueryVO queryResult = llr.query(observation);
    llr.update(queryResult.getNeighbors(), delta, specification.getActorMax(), specification.getActorMin());
  }

  public void update(double delta, SimpleMatrix observation, SimpleMatrix action, boolean randomness)
  {
    if (randomness)
    {
      updateWithRandomness(delta, observation, action);
    }
    else
    {
      updateWithoutRandomness(delta, observation, action);
    }
  }

  public void updateWithRandomness(double delta, SimpleMatrix observation, SimpleMatrix action)
  {
    delta = delta * lastRandom * specification.getActorAlpha();
    update(delta, observation, action);
  }

  public void updateWithoutRandomness(double delta, SimpleMatrix observation, SimpleMatrix action)
  {
    delta = delta * specification.getActorAlpha();

    update(delta, observation, action);
  }

  private void update(double delta, SimpleMatrix observation, SimpleMatrix action)
  {
    LWRQueryVO queryResult = llr.query(observation);
    int insertIndex = add(observation, action);

    if (insertIndex != -1)
      queryResult.getNeighbors().add(insertIndex);

    llr.update(queryResult.getNeighbors(), delta, specification.getActorMax(), specification.getActorMin());
  }

  private int add(SimpleMatrix observation, SimpleMatrix action)
  {
    return llr.add(observation, EJMLMatlabUtils.wrap(action, specification.getActorMax(), specification.getActorMin()));
  }

  private void nextRandom()
  {
    lastRandom = random.nextGaussian() * specification.getSd();
  }

  public LLR getLLR()
  {
    return llr;
  }
}
