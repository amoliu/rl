package br.ufrj.ppgi.rl.ac;

import org.ejml.ops.NormOps;
import org.ejml.simple.SimpleMatrix;

import br.ufrj.ppgi.rl.ProcessModelLWR;
import br.ufrj.ppgi.rl.ProcessModelQueryVO;
import br.ufrj.ppgi.rl.Specification;

public class DynaActorCritic extends StandardActorCritic
{
  private static final long serialVersionUID = -8686323211453331278L;

  protected ProcessModelLWR processModel;

  private SimpleMatrix      firstObservation;

  private int               modelStep;

  private SimpleMatrix      lastModelObservation;

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

    SimpleMatrix model = processModel.query(lastObservation, lastAction).getLWRQueryVO().getResult();
    double error = Math.pow(NormOps.normP2(matrixObservation.minus(model).getMatrix()), 2);

    processModel.add(lastObservation, lastAction, matrixObservation, reward, 0);
    updateUsingModel();

    lastObservation = matrixObservation;

    return new StepVO(error, chooseAction(matrixObservation));
  }

  private void restartModel()
  {
    modelStep = 0;
    lastModelObservation = firstObservation;
  }

  private void updateUsingModel()
  {
    for (int i = 0; i < specification.getProcessModelStepsPerEpisode(); i++)
    {
      SimpleMatrix action = actor.action(lastModelObservation).getAction();
      ProcessModelQueryVO modelQuery = processModel.query(lastModelObservation, action);

      double delta = critic.updateWithoutAddSample(lastModelObservation, action, modelQuery.getReward(),
                                                   modelQuery.getLWRQueryVO().getResult(),
                                                   specification.getProcessModelCriticAlpha(),
                                                   specification.getProcessModelGamma());

      if (i % specification.getExplorationRate() == 0)
      {
        actor.updateWithRandomness(delta, lastObservation, lastAction, specification.getProcessModelActorAplha());
      }
      else
      {
        actor.updateWithoutRandomness(delta, lastObservation, lastAction, specification.getProcessModelActorAplha());
      }

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
}
