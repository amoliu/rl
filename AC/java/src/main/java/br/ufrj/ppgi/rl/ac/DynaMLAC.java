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

    LWRQueryVO modelVO = processModel.query(lastObservation, lastAction.getAction()).getLWRQueryVO();
    SimpleMatrix model = modelVO.getResult();
    double error = Math.pow(NormOps.normP2(matrixObservation.minus(model).getMatrix()), 2);

    processModel.add(lastObservation, lastAction.getAction(), matrixObservation, reward);
    updateUsingModel();

    lastObservation = matrixObservation;
    environmentStep++;

    return new StepVO(error, chooseAction(matrixObservation));
  }

  private void restartModel()
  {
    modelStep = 0;
    lastModelObservation = firstObservation;
  }

  private void updateUsingModel()
  {
    if (environmentStep <= specification.getProcessModelIterationsWithoutLearning() * 100)
      return;

    for (int i = 0; i < specification.getProcessModelStepsPerEpisode(); i++)
    {
      SimpleMatrix action = actor.actionWithoutRandomness(lastModelObservation);
      ProcessModelQueryVO modelQuery = processModel.query(lastModelObservation, action);

      // If the variance is greater exceeds the range of state params, restart
      // model
      if (!isModelGood(modelQuery))
      {
        restartModel();
        continue;
      }

      LWRQueryVO criticResult = critic.query(modelQuery.getLWRQueryVO().getResult());

      SimpleMatrix criticXs = getXs(criticResult.getX());
      SimpleMatrix modelXa = getXa(modelQuery.getLWRQueryVO().getX());

      double actorUpdate = criticXs.mult(modelXa).get(0);
      actor.updateWithoutRandomness(actorUpdate, lastObservation, lastAction.getAction(),
                                    specification.getProcessModelActorAplha());

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
  }

  private boolean isModelGood(ProcessModelQueryVO modelQuery)
  {
    for (int k = 0; k < specification.getObservationDimensions(); k++)
    {
      if (modelQuery.getLWRQueryVO().getVariance().get(k) > Math.abs(specification.getObservationRange().get(k)))
      {
        return false;
      }
    }

    return true;
  }
}
