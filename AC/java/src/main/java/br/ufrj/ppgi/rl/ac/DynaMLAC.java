package br.ufrj.ppgi.rl.ac;

import org.ejml.simple.SimpleMatrix;

import br.ufrj.ppgi.rl.ProcessModelQueryVO;
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
      ProcessModelQueryVO modelQuery = processModel.query(lastModelObservation, action);

      // If the variance is greater exceeds the range of state params, restart
      // model
      if (!isModelGood(modelQuery))
      {
        skiped++;
        restartModel();
        continue;
      }

      LWRQueryVO criticResult = critic.query(modelQuery.getLWRQueryVO().getResult());

      SimpleMatrix criticXs = getXs(criticResult.getX());
      SimpleMatrix modelXa = getXa(modelQuery.getLWRQueryVO().getX());

      double actorUpdate = criticXs.mult(modelXa).get(0);
      actor.update(actorUpdate, lastModelObservation, action, specification.getProcessModelActorAplha(), false);

      critic.updateWithoutAddSample(lastModelObservation, action, modelQuery.getReward(), modelQuery.getLWRQueryVO()
                                                                                                    .getResult(),
                                    specification.getProcessModelCriticAlpha(), specification.getProcessModelGamma());

      lastModelObservation = modelQuery.getLWRQueryVO().getResult();
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

  private boolean isModelGood(ProcessModelQueryVO modelQuery)
  {
    for (int k = 0; k < specification.getObservationDimensions(); k++)
    {
      if (modelQuery.getLWRQueryVO().getVariance().get(k) > specification.getObservationRange().get(k))
      {
        return false;
      }
    }

    if (modelQuery.getLWRQueryVO().getVariance().get(specification.getObservationDimensions()) > specification.getRewardRange())
    {
      return false;
    }

    return true;
  }
}
