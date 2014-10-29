package br.ufrj.ppgi.rl;

import java.io.Serializable;

import org.ejml.simple.SimpleMatrix;

import br.ufrj.ppgi.rl.fa.LLR;

public class CriticLLR implements Serializable
{
  private static final long serialVersionUID = -4078177597508652947L;

  private LLR               llr;

  private Specification     specification;

  private double[]          eligibilityTrace;

  public void init(Specification specification)
  {
    this.specification = specification;

    llr = new LLR(specification.getCriticMemory(), specification.getInputDimensions(),
                  specification.getOutputDimensions(), specification.getCriticNeighbors(), 0);
    
    resetEligibilityTrace(specification.getCriticMemory());
  }

  private void resetEligibilityTrace(int size)
  {
    eligibilityTrace = new double[size];
    for(int i=0; i<specification.getCriticMemory(); i++)
    {
      eligibilityTrace[i] = 0;
    }
  }

  public double update(SimpleMatrix lastObservation, SimpleMatrix lastAction, double reward, SimpleMatrix observation)
  {
    return 0;
  }

}
