package br.ufrj.ppgi.rl;

import java.io.Serializable;

import org.ejml.simple.SimpleMatrix;

import br.ufrj.ppgi.rl.fa.LWR;
import br.ufrj.ppgi.rl.fa.LWRQueryVO;

public class ProcessModelLWR implements Serializable
{
  private static final long serialVersionUID = -7082951471695313867L;

  protected LWR             lwr;

  protected Specification   specification;

  public void init(Specification specification)
  {
    this.specification = specification;

    lwr = LWR.createLWR().setSize(specification.getProcessModelMemory()).setInputDimension(getInputDimension())
             .setOutputDimension(getOutputDimension()).setK(specification.getProcessModelNeighbors())
             .setValuesToRebuildTheTree(specification.getProcessModelValuesToRebuildTree());

    if (specification.getProcessModelMemoryManagement() != null)
    {
      lwr.setMemoryManagement(specification.getProcessModelMemoryManagement());
    }
  }

  public LWRQueryVO query(double[][] observation, double[][] action)
  {
    return query(new SimpleMatrix(observation), new SimpleMatrix(action));
  }

  public LWRQueryVO query(SimpleMatrix observation, SimpleMatrix action)
  {
    LWRQueryVO query = lwr.query(createProcessoModelInput(observation, action));

    if (query.getResult().get(specification.getProcessModelAnglePosition()) < 0)
    {
      query.setResult(query.getResult().plus(specification.getProcessModelUpperBound()));
    }
    if (query.getResult().get(specification.getProcessModelAnglePosition()) > specification.getProcessModelUpperBound()
                                                                                           .get(specification.getProcessModelAnglePosition()))
    {
      query.setResult(query.getResult().minus(specification.getProcessModelUpperBound()));
    }

    return query;
  }

  public void add(SimpleMatrix observation, SimpleMatrix action, SimpleMatrix nextObservation)
  {
    if (nextObservation.get(specification.getProcessModelAnglePosition())
        - observation.get(specification.getProcessModelAnglePosition()) < -specification.getProcessModelCrossLimit())
    {
      addToLLR(observation, action, nextObservation.plus(specification.getProcessModelUpperBound()));
      addToLLR(observation.minus(specification.getProcessModelUpperBound()), action, nextObservation);
    }
    else
      if (nextObservation.get(specification.getProcessModelAnglePosition())
          - observation.get(specification.getProcessModelAnglePosition()) > specification.getProcessModelCrossLimit())
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
    lwr.add(createProcessoModelInput(observation, action), nextObservation);

    if (observation.get(specification.getProcessModelAnglePosition()) - specification.getProcessModelThreshold() < 0)
    {
      SimpleMatrix newObservation = observation.plus(specification.getProcessModelUpperBound());
      SimpleMatrix newNextObservation = nextObservation.plus(specification.getProcessModelUpperBound());

      lwr.add(createProcessoModelInput(newObservation, action), newNextObservation);
    }

    if (observation.get(specification.getProcessModelAnglePosition()) + specification.getProcessModelThreshold() > specification.getProcessModelUpperBound()
                                                                                                                                .get(0))
    {
      SimpleMatrix newObservation = observation.minus(specification.getProcessModelUpperBound());
      SimpleMatrix newNextObservation = nextObservation.minus(specification.getProcessModelUpperBound());

      lwr.add(createProcessoModelInput(newObservation, action), newNextObservation);
    }
  }

  private SimpleMatrix createProcessoModelInput(SimpleMatrix observation, SimpleMatrix action)
  {
    SimpleMatrix input = new SimpleMatrix(1, specification.getObservationDimensions()
                                             + specification.getActionDimensions());

    input.setRow(0, 0, observation.getMatrix().data);
    input.setRow(0, specification.getObservationDimensions(), action.getMatrix().data);
    return input;
  }

  public LWR getLWR()
  {
    return lwr;
  }

  protected int getOutputDimension()
  {
    return specification.getObservationDimensions();
  }

  protected int getInputDimension()
  {
    return specification.getObservationDimensions() + specification.getActionDimensions();
  }
}
