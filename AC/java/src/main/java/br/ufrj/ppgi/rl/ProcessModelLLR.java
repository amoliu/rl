package br.ufrj.ppgi.rl;

import java.io.Serializable;

import org.ejml.simple.SimpleMatrix;

import br.ufrj.ppgi.rl.fa.LLR;
import br.ufrj.ppgi.rl.fa.LLRQueryVO;

public class ProcessModelLLR implements Serializable
{
  private static final long serialVersionUID = -4759399854724315005L;

  protected LLR             llr;

  private Specification     specification;

  public void init(Specification specification)
  {
    this.specification = specification;

    llr = new LLR(specification.getProcessModelMemory(), specification.getObservationDimensions()
                                                         + specification.getActionDimensions(),
                  specification.getObservationDimensions(), specification.getProcessModelNeighbors());

  }

  public LLRQueryVO query(SimpleMatrix observation)
  {
    return llr.query(observation);
  }

  public void add(SimpleMatrix observation, SimpleMatrix action, SimpleMatrix nextObservation)
  {
    SimpleMatrix input = new SimpleMatrix(1, specification.getObservationDimensions()
                                          + specification.getActionDimensions());
    
    input.setRow(0, 0, observation.getMatrix().data);
    input.setRow(0, specification.getObservationDimensions(), action.getMatrix().data);

    llr.add(input, nextObservation);
  }

  public LLR getLLR()
  {
    return llr;
  }
}
