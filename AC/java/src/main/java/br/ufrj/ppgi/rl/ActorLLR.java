package br.ufrj.ppgi.rl;

import java.io.Serializable;
import java.util.Random;

import org.ejml.alg.dense.mult.MatrixDimensionException;
import org.ejml.simple.SimpleMatrix;

import br.ufrj.ppgi.matlab.EJMLMatlabUtils;
import br.ufrj.ppgi.rl.fa.LLR;
import br.ufrj.ppgi.rl.fa.LLRQueryVO;

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

    llr = new LLR(specification.getActorMemory(), specification.getInputDimensions(),
                  specification.getOutputDimensions(), specification.getActorNeighbors(), 0);

    random = new Random();
  }

  public SimpleMatrix action(SimpleMatrix observation)
  {
    if (observation.numRows() != 1)
    {
      throw new MatrixDimensionException("Observation is not a row vector");
    }

    if (observation.numCols() != specification.getInputDimensions())
    {
      throw new MatrixDimensionException("Observation is not the expected length");
    }

    LLRQueryVO queryResult = llr.query(observation);
    SimpleMatrix action = queryResult.getResult();
    nextRandom();

    return EJMLMatlabUtils.wrap(action.plus(lastRandom), specification.getActorMax(), specification.getActorMin());
  }

  public void update(double delta, SimpleMatrix observation, SimpleMatrix action)
  {
    delta = delta * lastRandom * specification.getActorAlpha();

    LLRQueryVO queryResult = llr.query(observation);
    llr.add(observation, EJMLMatlabUtils.wrap(action.plus(delta), specification.getActorMax(), specification.getActorMin()));

    llr.update(queryResult.getNeighbors(), delta, specification.getActorMax(), specification.getActorMin());
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
