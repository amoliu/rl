package br.ufrj.ppgi.rl;

import java.io.Serializable;
import java.util.Random;

import org.ejml.alg.dense.mult.MatrixDimensionException;
import org.ejml.simple.SimpleMatrix;

import br.ufrj.ppgi.rl.fa.LLR;

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
    if (observation.numCols() != 1)
    {
      throw new MatrixDimensionException("Observation is not a column vector");
    }

    if (observation.numRows() != specification.getInputDimensions())
    {
      throw new MatrixDimensionException("Observation is not the expected length");
    }

    SimpleMatrix action = llr.query(observation);
    nextRandom();

    return wrap(action.plus(lastRandom));
  }

  public void update(double delta)
  {
    llr.update(specification.getActorAlpha() * delta);
  }

  private void nextRandom()
  {
    lastRandom = random.nextGaussian() * specification.getSd();
  }

  private SimpleMatrix wrap(SimpleMatrix action)
  {
    for(int i=0; i<action.numRows(); i++)
    {
      for(int j=0; j<action.numCols(); j++)
      {
        if (action.get(i, j) > specification.getActorMax().get(i, j))
        {
          action.set(i, j, specification.getActorMax().get(i, j));
        }
        
        if (action.get(i, j) < specification.getActorMin().get(i, j))
        {
          action.set(i, j, specification.getActorMin().get(i, j));
        }
      }
    }
    return action;
  }
}
