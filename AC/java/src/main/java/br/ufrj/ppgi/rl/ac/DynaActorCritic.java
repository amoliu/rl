package br.ufrj.ppgi.rl.ac;

import org.ejml.ops.NormOps;
import org.ejml.simple.SimpleMatrix;

import br.ufrj.ppgi.rl.ProcessModelLWR;
import br.ufrj.ppgi.rl.ProcessModelQueryVO;
import br.ufrj.ppgi.rl.Specification;
import br.ufrj.ppgi.rl.fa.LWRQueryVO;

public class DynaActorCritic extends StandardActorCritic
{
  private static final long serialVersionUID = -5704315970654225615L;

  protected ProcessModelLWR processModel;

  private SimpleMatrix      firstObservation;

  private int               modelStep;

  private SimpleMatrix      lastModelObservation;

  private int               environmentStep;

  public DynaActorCritic()
  {
    super();
    processModel = new ProcessModelLWR();
  }

  @Override
  public void init(Specification specification)
  {
    super.init(specification);
    processModel.init(specification);

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

    LWRQueryVO modelVO = processModel.query(lastObservation, lastAction).getLWRQueryVO();
    SimpleMatrix model = modelVO.getResult();
    double error = Math.pow(NormOps.normP2(matrixObservation.minus(model).getMatrix()), 2);

    processModel.add(lastObservation, lastAction, matrixObservation, reward);
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

      double delta = critic.updateWithoutAddSample(lastModelObservation, action, modelQuery.getReward(),
                                                   modelQuery.getLWRQueryVO().getResult(),
                                                   specification.getProcessModelCriticAlpha(),
                                                   specification.getProcessModelGamma());

      actor.updateWithoutRandomness(delta, lastObservation, lastAction, specification.getProcessModelActorAplha());

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
