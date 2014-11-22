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

    lwr = new LWR(specification.getProcessModelMemory(), getInputDimension(), getOutputDimension(),
                  specification.getProcessModelNeighbors(), specification.getProcessModelValuesToRebuildTree());

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

  private ProcessModelQueryVO decomposeProcessModelOutput(LWRQueryVO lwrQueryVO)
  {
    SimpleMatrix result = lwrQueryVO.getResult();
    double reward = result.get(specification.getObservationDimensions());
    int terminal = (int) result.get(specification.getObservationDimensions() + 1);

    lwrQueryVO.setResult(lwrQueryVO.getResult().extractMatrix(0, 1, 0, specification.getObservationDimensions()));

    return new ProcessModelQueryVO(lwrQueryVO, reward, terminal);
  }

  public LWR getLWR()
  {
    return lwr;
  }

  public int getOutputDimension()
  {
    return specification.getObservationDimensions() + 2;
  }

  public int getInputDimension()
  {
    return specification.getObservationDimensions() + specification.getActionDimensions();
  }
}
