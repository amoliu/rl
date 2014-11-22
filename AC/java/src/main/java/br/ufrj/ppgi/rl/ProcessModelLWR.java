package br.ufrj.ppgi.rl;

import java.io.Serializable;

import org.ejml.simple.SimpleMatrix;

import br.ufrj.ppgi.rl.fa.LWR;
import br.ufrj.ppgi.rl.fa.LWRQueryVO;

public class ProcessModelLWR implements Serializable
{
  private static final long serialVersionUID = 2174345185650043152L;

  protected LWR             lwr;

  private Specification     specification;

  public void init(Specification specification)
  {
    this.specification = specification;

    lwr = new LWR(specification.getProcessModelMemory(), specification.getObservationDimensions()
                                                         + specification.getActionDimensions(),
                  specification.getObservationDimensions() + 2, specification.getProcessModelNeighbors());

  }

  public ProcessModelQueryVO query(SimpleMatrix observation, SimpleMatrix action)
  {
    LWRQueryVO query = lwr.query(createProcessoModelInput(observation, action));
    ProcessModelQueryVO result = decomposeProcessModelOutput(query.getResult());

    if (result.getObservation().get(0) < 0)
    {
      result.setObservation(result.getObservation().plus(specification.getProcessModelUpperBound()));
    }
    if (result.getObservation().get(0) > specification.getProcessModelUpperBound().get(0))
    {
      result.setObservation(result.getObservation().minus(specification.getProcessModelUpperBound()));
    }

    return result;
  }

  public void add(SimpleMatrix observation, SimpleMatrix action, SimpleMatrix nextObservation, double reward,
                  int terminal)
  {
    if (nextObservation.get(0) - observation.get(0) < -specification.getProcessModelCrossLimit())
    {
      addToLLR(observation, action, nextObservation.plus(specification.getProcessModelUpperBound()), reward, terminal);
      addToLLR(observation.minus(specification.getProcessModelUpperBound()), action, nextObservation, reward, terminal);
    }
    else
      if (nextObservation.get(0) - observation.get(0) > specification.getProcessModelCrossLimit())
      {
        addToLLR(observation.plus(specification.getProcessModelUpperBound()), action, nextObservation, reward, terminal);
        addToLLR(observation, action, nextObservation.minus(specification.getProcessModelUpperBound()), reward,
                 terminal);
      }
      else
      {
        addToLLR(observation, action, nextObservation, reward, terminal);
      }
  }

  private void addToLLR(SimpleMatrix observation, SimpleMatrix action, SimpleMatrix nextObservation, double reward,
                        int terminal)
  {
    lwr.add(createProcessoModelInput(observation, action), createProcessoModelOutput(nextObservation, reward, terminal));

    if (observation.get(0) - specification.getProcessModelThreshold() < 0)
    {
      SimpleMatrix newObservation = observation.plus(specification.getProcessModelUpperBound());
      SimpleMatrix newNextObservation = nextObservation.plus(specification.getProcessModelUpperBound());

      lwr.add(createProcessoModelInput(newObservation, action),
              createProcessoModelOutput(newNextObservation, reward, terminal));
    }

    if (observation.get(0) + specification.getProcessModelThreshold() > specification.getProcessModelUpperBound()
                                                                                     .get(0))
    {
      SimpleMatrix newObservation = observation.minus(specification.getProcessModelUpperBound());
      SimpleMatrix newNextObservation = nextObservation.minus(specification.getProcessModelUpperBound());

      lwr.add(createProcessoModelInput(newObservation, action),
              createProcessoModelOutput(newNextObservation, reward, terminal));
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

  private SimpleMatrix createProcessoModelOutput(SimpleMatrix observation, double reward, int terminal)
  {
    SimpleMatrix output = new SimpleMatrix(1, specification.getObservationDimensions() + 2);

    output.setRow(0, 0, observation.getMatrix().data);
    output.setRow(0, specification.getObservationDimensions(), reward);
    output.setRow(0, specification.getObservationDimensions() + 1, terminal);
    return output;
  }

  private ProcessModelQueryVO decomposeProcessModelOutput(SimpleMatrix output)
  {
    return new ProcessModelQueryVO(output.extractMatrix(0, 1, 0, specification.getObservationDimensions()),
                                   output.get(specification.getObservationDimensions()),
                                   (int) output.get(specification.getObservationDimensions() + 1));
  }

  public LWR getLWR()
  {
    return lwr;
  }
}
