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

    lwr = new LWR(specification.getProcessModelMemory(), getInputDimension(), getOutputDimension(),
                  specification.getProcessModelNeighbors(), specification.getProcessModelValuesToRebuildTree());

  }

  public ProcessModelQueryVO query(double[][] observation, double[][] action)
  {
    return query(new SimpleMatrix(observation), new SimpleMatrix(action));
  }
  
  public ProcessModelQueryVO query(SimpleMatrix observation, SimpleMatrix action)
  {
    LWRQueryVO query = lwr.query(createProcessoModelInput(observation, action));
    ProcessModelQueryVO result = decomposeProcessModelOutput(query);

    if (result.getLWRQueryVO().getResult().get(0) < 0)
    {
      result.getLWRQueryVO().setResult(result.getLWRQueryVO().getResult()
                                             .plus(specification.getProcessModelUpperBound()));
    }
    if (result.getLWRQueryVO().getResult().get(0) > specification.getProcessModelUpperBound().get(0))
    {
      result.getLWRQueryVO().setResult(result.getLWRQueryVO().getResult()
                                             .minus(specification.getProcessModelUpperBound()));
    }

    return result;
  }

  public void add(SimpleMatrix observation, SimpleMatrix action, SimpleMatrix nextObservation, double reward)
  {
    if (nextObservation.get(0) - observation.get(0) < -specification.getProcessModelCrossLimit())
    {
      addToLLR(observation, action, nextObservation.plus(specification.getProcessModelUpperBound()), reward);
      addToLLR(observation.minus(specification.getProcessModelUpperBound()), action, nextObservation, reward);
    }
    else
      if (nextObservation.get(0) - observation.get(0) > specification.getProcessModelCrossLimit())
      {
        addToLLR(observation.plus(specification.getProcessModelUpperBound()), action, nextObservation, reward);
        addToLLR(observation, action, nextObservation.minus(specification.getProcessModelUpperBound()), reward);
      }
      else
      {
        addToLLR(observation, action, nextObservation, reward);
      }
  }

  private void addToLLR(SimpleMatrix observation, SimpleMatrix action, SimpleMatrix nextObservation, double reward)
  {
    lwr.add(createProcessoModelInput(observation, action), createProcessoModelOutput(nextObservation, reward));

    if (observation.get(0) - specification.getProcessModelThreshold() < 0)
    {
      SimpleMatrix newObservation = observation.plus(specification.getProcessModelUpperBound());
      SimpleMatrix newNextObservation = nextObservation.plus(specification.getProcessModelUpperBound());

      lwr.add(createProcessoModelInput(newObservation, action), createProcessoModelOutput(newNextObservation, reward));
    }

    if (observation.get(0) + specification.getProcessModelThreshold() > specification.getProcessModelUpperBound()
                                                                                     .get(0))
    {
      SimpleMatrix newObservation = observation.minus(specification.getProcessModelUpperBound());
      SimpleMatrix newNextObservation = nextObservation.minus(specification.getProcessModelUpperBound());

      lwr.add(createProcessoModelInput(newObservation, action), createProcessoModelOutput(newNextObservation, reward));
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

  private SimpleMatrix createProcessoModelOutput(SimpleMatrix observation, double reward)
  {
    SimpleMatrix output = new SimpleMatrix(1, getOutputDimension());

    output.setRow(0, 0, observation.getMatrix().data);
    output.setRow(0, specification.getObservationDimensions(), reward);

    return output;
  }

  private ProcessModelQueryVO decomposeProcessModelOutput(LWRQueryVO lwrQueryVO)
  {
    SimpleMatrix result = lwrQueryVO.getResult();
    double reward = 0;

    reward = result.get(specification.getObservationDimensions());

    lwrQueryVO.setResult(lwrQueryVO.getResult().extractMatrix(0, 1, 0, specification.getObservationDimensions()));

    return new ProcessModelQueryVO(lwrQueryVO, reward);
  }

  public LWR getLWR()
  {
    return lwr;
  }

  protected int getOutputDimension()
  {
    return specification.getObservationDimensions() + 1;
  }

  protected int getInputDimension()
  {
    return specification.getObservationDimensions() + specification.getActionDimensions();
  }
}
