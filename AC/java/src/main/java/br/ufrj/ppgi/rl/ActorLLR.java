package br.ufrj.ppgi.rl;

import java.io.Serializable;
import java.util.Random;

import org.ejml.alg.dense.mult.MatrixDimensionException;
import org.ejml.simple.SimpleMatrix;

import br.ufrj.ppgi.matlab.EJMLMatlabUtils;
import br.ufrj.ppgi.rl.fa.LLR;
import br.ufrj.ppgi.rl.fa.LWRQueryVO;

public class ActorLLR implements Serializable
{
  private static final long serialVersionUID = -8562342529211372018L;

  protected LLR             llr;

  private Specification     specification;

  protected Random          random;

  private double            lastRandom;

  public void init(Specification specification)
  {
    this.specification = specification;

    llr = new LLR(specification.getActorMemory(), specification.getObservationDimensions(),
                  specification.getActionDimensions(), specification.getActorNeighbors());

    random = new Random();
  }

  public ActionVO action(SimpleMatrix observation)
  {
    SimpleMatrix action = actionWithoutRandomness(observation);
    nextRandom();

    return new ActionVO(EJMLMatlabUtils.wrap(action.plus(lastRandom), specification.getActorMax(),
                                             specification.getActorMin()),
                        EJMLMatlabUtils.wrap(action, specification.getActorMax(), specification.getActorMin()));
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
    return queryResult.getResult();
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
    add(observation, action.plus(delta));

    llr.update(queryResult.getNeighbors(), delta, specification.getActorMax(), specification.getActorMin());
  }

  private void add(SimpleMatrix observation, SimpleMatrix action)
  {
    llr.add(observation, EJMLMatlabUtils.wrap(action, specification.getActorMax(), specification.getActorMin()));
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
