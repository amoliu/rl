package br.ufrj.ppgi.rl.ac;

import org.ejml.ops.NormOps;
import org.ejml.simple.SimpleMatrix;

import br.ufrj.ppgi.rl.ProcessModelLWR;
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

    LWRQueryVO modelVO = processModel.query(lastObservation, lastAction);
    SimpleMatrix model = modelVO.getResult();
    double error = Math.pow(NormOps.normP2(matrixObservation.minus(model).getMatrix()), 2);

    processModel.add(lastObservation, lastAction, matrixObservation);
    int modelSkiped = updateUsingModel();

    lastObservation = matrixObservation;
    environmentStep++;

    return new StepVO(error, chooseAction(matrixObservation), modelSkiped);
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
      LWRQueryVO modelQuery = processModel.query(lastModelObservation, action);

      // If the variance is greater exceeds the range of state params, restart
      // model
      if (!isModelGood(modelQuery))
      {
        skiped++;
        restartModel();
        continue;
      }

      double reward = specification.getRewardCalculator().calculate(lastModelObservation, action);
      double delta = critic.updateWithoutAddSample(lastModelObservation, action, reward, modelQuery.getResult(),
                                                   specification.getProcessModelCriticAlpha(),
                                                   specification.getProcessModelGamma());

      if (i % specification.getExplorationRate() == 0)
      {
        actor.updateWithRandomness(delta, lastModelObservation, action, specification.getProcessModelActorAplha());
      }
      else
      {
        actor.updateWithoutRandomness(delta, lastModelObservation, action, specification.getProcessModelActorAplha());
      }

      lastModelObservation = modelQuery.getResult();
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

  public ProcessModelLWR getProcessModel()
  {
    return processModel;
  }
}
