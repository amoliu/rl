package br.ufrj.ppgi.rl.ac;

import org.ejml.simple.SimpleMatrix;

import br.ufrj.ppgi.matlab.EJMLMatlabUtils;
import br.ufrj.ppgi.rl.ActorLLR;
import br.ufrj.ppgi.rl.CriticLLR;
import br.ufrj.ppgi.rl.ProcessModelLWR;
import br.ufrj.ppgi.rl.ProcessModelQueryVO;
import br.ufrj.ppgi.rl.Specification;

public class DynaActorCritic implements Agent
{
  private static final long serialVersionUID = 5199782775154605824L;

  protected ActorLLR        actor;

  protected CriticLLR       critic;

  protected ProcessModelLWR processModel;

  private Specification     specification;

  protected SimpleMatrix    lastObservation;

  protected SimpleMatrix    lastAction;

  private SimpleMatrix      firstObservation;

  private int               modelStep;

  private SimpleMatrix      lastModelObservation;

  public DynaActorCritic()
  {
    actor = new ActorLLR();
    critic = new CriticLLR();
    processModel = new ProcessModelLWR();

    specification = null;

    lastObservation = null;
    lastAction = null;
  }

  @Override
  public void init(Specification specification)
  {
    if (this.specification != null)
    {
      throw new IllegalStateException("Agent already started");
    }

    this.specification = specification;

    actor.init(specification);
    critic.init(specification);
    processModel.init(specification);
  }

  @Override
  public StepVO start(double[][] observation)
  {
    firstObservation = lastObservation = new SimpleMatrix(observation);
    restartModel();

    return new StepVO(chooseAction(new SimpleMatrix(observation)));
  }

  @Override
  public StepVO step(double reward, double[][] observation)
  {
    update(reward, new SimpleMatrix(observation));
    lastObservation = new SimpleMatrix(observation);

    return new StepVO(chooseAction(new SimpleMatrix(observation)));
  }

  @Override
  public double[][] end(double reward)
  {
    update(reward, lastObservation);

    return chooseAction(lastObservation);
  }

  @Override
  public void fini()
  {
    this.specification = null;
  }

  private void restartModel()
  {
    modelStep = 0;
    lastModelObservation = firstObservation;
  }

  private void update(double reward, SimpleMatrix observation)
  {
    double delta = critic.update(lastObservation, lastAction, reward, observation);
    actor.updateWithRandomness(delta, lastObservation, lastAction);

    processModel.add(lastObservation, lastAction, observation, reward, 0);

    updateUsingModel();
  }

  private void updateUsingModel()
  {
    for (int i = 0; i < specification.getProcessModelStepsPerEpisode(); i++)
    {
      SimpleMatrix action = actor.action(lastModelObservation).getAction();
      ProcessModelQueryVO modelQuery = processModel.query(lastModelObservation, action);

      double delta = critic.update(lastModelObservation, action, modelQuery.getReward(), modelQuery.getObservation(),
                                   specification.getProcessModelCriticAlpha(), specification.getProcessModelGamma());
      actor.updateWithRandomness(delta, lastObservation, lastAction, specification.getProcessModelActorAplha());

      lastModelObservation = modelQuery.getObservation();
      modelStep++;

      // Restart model transition if hit a terminal state or
      // if we had model.steps iterations
      if (modelStep >= 100)
      {
        restartModel();
      }
    }
  }

  private double[][] chooseAction(SimpleMatrix observation)
  {
    lastAction = actor.action(observation).getAction();
    return EJMLMatlabUtils.getMatlabMatrixFromSimpleMatrix(lastAction);
  }

  @Override
  public CriticLLR getCritic()
  {
    return critic;
  }

  @Override
  public ActorLLR getActor()
  {
    return actor;
  }

  @Override
  public double[][] stepWithoutLearn(double[][] observation)
  {
    return EJMLMatlabUtils.getMatlabMatrixFromSimpleMatrix(actor.actionWithoutRandomness(new SimpleMatrix(observation)));
  }
}
