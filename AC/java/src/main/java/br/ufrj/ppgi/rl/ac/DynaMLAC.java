package br.ufrj.ppgi.rl.ac;

import org.ejml.simple.SimpleMatrix;

import br.ufrj.ppgi.matlab.EJMLMatlabUtils;
import br.ufrj.ppgi.rl.Specification;
import br.ufrj.ppgi.rl.fa.LWRQueryVO;

public class DynaMLAC extends MLAC
{
  private static final long serialVersionUID = 6548814781969051192L;

  private SimpleMatrix      firstObservation;

  private int               modelStep;

  private SimpleMatrix      lastModelObservation;

  public DynaMLAC()
  {
    super();
  }

  @Override
  public void init(Specification specification)
  {
    super.init(specification);
  }

  @Override
  public StepVO start(double[][] observation)
  {
    StepVO step = super.start(observation);

    firstObservation = new SimpleMatrix(observation);
    restartModel();

    return step;
  }

  @Override
  public StepVO step(double reward, double[][] observation)
  {
    StepVO step = super.step(reward, observation);

    int skiped = updateUsingModel();
    step.setModelSkiped(skiped);

    return step;
  }

  private void restartModel()
  {
    modelStep = 0;
    lastModelObservation = firstObservation;
  }

  private int updateUsingModel()
  {
    if (super.step <= specification.getProcessModelIterationsWithoutLearning() * 100)
      return specification.getProcessModelStepsPerEpisode();

    int skiped = 0;
    for (int i = 0; i < specification.getProcessModelStepsPerEpisode(); i++)
    {
      SimpleMatrix action = chooseAction(lastModelObservation, i);
      LWRQueryVO modelQuery = processModel.query(lastModelObservation, action);

      // If the variance is greater exceeds the range of state params, restart
      // model
      if (!isModelGood(modelQuery))
      {
        skiped++;
        restartModel();
        continue;
      }

      LWRQueryVO criticResult = critic.query(modelQuery.getResult());

      SimpleMatrix criticXs = getXs(criticResult.getX());
      SimpleMatrix modelXa = getXa(modelQuery.getX());

      double actorUpdate = criticXs.mult(modelXa).get(0);
      actor.update(actorUpdate, lastModelObservation, action, specification.getProcessModelActorAplha(), false);

      SimpleMatrix denormalizedObservation = EJMLMatlabUtils.denormalize(lastModelObservation, specification.getNormalization());
      double reward = specification.getRewardCalculator().calculate(denormalizedObservation, action);
      
      critic.updateWithoutAddSample(lastModelObservation, action, reward, modelQuery.getResult(),
                                    specification.getProcessModelCriticAlpha(), specification.getProcessModelGamma());

      lastModelObservation = modelQuery.getResult();
      modelStep++;

      // Restart model transition if we had model.steps iterations
      if (modelStep >= 100)
      {
        restartModel();
      }
    }

    return skiped;
  }

  private SimpleMatrix chooseAction(SimpleMatrix observation, int i)
  {
    if (i % specification.getProcessModelExplorationRate() == 0)
    {
      return actor.action(observation, specification.getProcessModelSd()).getAction();
    }
    else
    {
      return actor.action(observation, specification.getProcessModelSd()).getPolicyAction();
    }
  }

  private boolean isModelGood(LWRQueryVO modelQuery)
  {
    for (int k = 0; k < specification.getObservationDimensions(); k++)
    {
      if (modelQuery.getVariance().get(k) > specification.getObservationRange().get(k))
      {
        return false;
      }
    }

    return true;
  }
}
