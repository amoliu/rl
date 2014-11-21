package br.ufrj.ppgi.rl;

import java.io.Serializable;

import org.ejml.simple.SimpleMatrix;

import br.ufrj.ppgi.matlab.EJMLMatlabUtils;
import br.ufrj.ppgi.rl.fa.LLR;
import br.ufrj.ppgi.rl.fa.LWRQueryVO;

public class ProcessModelLLR implements Serializable
{
  private static final long serialVersionUID = 6886121151297463522L;

  protected LLR             llr;

  private Specification     specification;

  public void init(Specification specification)
  {
    this.specification = specification;

    llr = new LLR(specification.getProcessModelMemory(), specification.getObservationDimensions()
                                                         + specification.getActionDimensions(),
                  specification.getObservationDimensions(), specification.getProcessModelNeighbors(),
                  specification.getProcessModelValuesToRebuildTree());

  }

  public LWRQueryVO query(SimpleMatrix observation, SimpleMatrix action)
  {
    LWRQueryVO query = llr.query(createProcessoModelQuery(observation, action));
    query.setResult(EJMLMatlabUtils.wrap(query.getResult(), specification.getObservationMaxValue(),
                                         specification.getObservationMinValue()));

    return query;
  }

  public void add(SimpleMatrix observation, SimpleMatrix action, SimpleMatrix nextObservation)
  {
    if (nextObservation.get(0) - observation.get(0) < -specification.getProcessModelCrossLimit())
    {
      addToLLR(observation, action, nextObservation.plus(specification.getProcessModelUpperBound()));
      addToLLR(observation.minus(specification.getProcessModelUpperBound()), action, nextObservation);
    }
    else
      if (nextObservation.get(0) - observation.get(0) > specification.getProcessModelCrossLimit())
      {
        addToLLR(observation.plus(specification.getProcessModelUpperBound()), action, nextObservation);
        addToLLR(observation, action, nextObservation.minus(specification.getProcessModelUpperBound()));
      }
      else
      {
        addToLLR(observation, action, nextObservation);
      }
  }

  private void addToLLR(SimpleMatrix observation, SimpleMatrix action, SimpleMatrix nextObservation)
  {
    SimpleMatrix input = createProcessoModelQuery(observation, action);

    llr.add(input, nextObservation);

    if (observation.get(0) - specification.getProcessModelThreshold() < 0)
    {
      SimpleMatrix newObservation = observation.plus(specification.getProcessModelUpperBound());
      SimpleMatrix newNextObservation = nextObservation.plus(specification.getProcessModelUpperBound());

      llr.add(createProcessoModelQuery(newObservation, action), newNextObservation);
    }

    if (observation.get(0) + specification.getProcessModelThreshold() > specification.getProcessModelUpperBound()
                                                                                     .get(0))
    {
      SimpleMatrix newObservation = observation.minus(specification.getProcessModelUpperBound());
      SimpleMatrix newNextObservation = nextObservation.minus(specification.getProcessModelUpperBound());

      llr.add(createProcessoModelQuery(newObservation, action), newNextObservation);
    }
  }

  public SimpleMatrix createProcessoModelQuery(SimpleMatrix observation, SimpleMatrix action)
  {
    SimpleMatrix input = new SimpleMatrix(1, specification.getObservationDimensions()
                                             + specification.getActionDimensions());

    input.setRow(0, 0, observation.getMatrix().data);
    input.setRow(0, specification.getObservationDimensions(), action.getMatrix().data);
    return input;
  }

  public LLR getLLR()
  {
    return llr;
  }
}
