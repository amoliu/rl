package br.ufrj.ppgi.rl.ac;

import org.ejml.ops.NormOps;
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

  private int               environmentStep;

  public DynaMLAC()
  {
    super();
  }

  @Override
  public void init(Specification specification)
  {
    super.init(specification);

    environmentStep = 0;
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
    SimpleMatrix matrixObservation = new SimpleMatrix(observation);
    super.update(reward, matrixObservation);

    LWRQueryVO modelVO = processModel.query(lastObservation, lastActionVO.getAction()).getLWRQueryVO();
    SimpleMatrix model = modelVO.getResult();
    double error = Math.pow(NormOps.normP2(matrixObservation.minus(model).getMatrix()), 2);

    processModel.add(lastObservation, lastActionVO.getAction(), matrixObservation, reward);
    int skiped = updateUsingModel();

    lastObservation = matrixObservation;
    environmentStep++;

    return new StepVO(error, chooseAction(matrixObservation), skiped);
  }

  private void restartModel()
  {
    modelStep = 0;
    lastModelObservation = firstObservation;
  }

  private int updateUsingModel()
  {
    if (environmentStep <= specification.getProcessModelIterationsWithoutLearning() * 100)
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
      if (i % specification.getExplorationRate() == 0)
      {
        actor.updateWithRandomness(actorUpdate, lastModelObservation, action, specification.getProcessModelActorAplha());
      }
      else
      {
        actor.updateWithoutRandomness(actorUpdate, lastModelObservation, action,
                                      specification.getProcessModelActorAplha());
      }

      critic.updateWithoutAddSample(lastModelObservation, action, modelQuery.getReward(), modelQuery.getLWRQueryVO()
                                                                                                    .getResult(),
                                    specification.getProcessModelCriticAlpha(), specification.getProcessModelGamma());

      lastModelObservation = modelQuery.getLWRQueryVO().getResult();
      modelStep++;

      // Restart model transition if hit a terminal state or
      // if we had model.steps iterations
      if (modelStep >= 100)
      {
        restartModel();
      }
    }

    return skiped;
  }

  private SimpleMatrix chooseAction(SimpleMatrix observation, int i)
  {
    if (i % specification.getExplorationRate() == 0)
    {
      return actor.action(observation).getAction();
    }
    else
    {
      return actor.action(observation).getPolicyAction();
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
