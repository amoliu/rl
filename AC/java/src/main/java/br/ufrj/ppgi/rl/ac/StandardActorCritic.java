package br.ufrj.ppgi.rl.ac;

import org.ejml.simple.SimpleMatrix;

import br.ufrj.ppgi.matlab.EJMLMatlabUtils;
import br.ufrj.ppgi.rl.ActorLLR;
import br.ufrj.ppgi.rl.CriticLLR;
import br.ufrj.ppgi.rl.Specification;

public class StandardActorCritic implements Agent
{
  private static final long serialVersionUID = -6663645034619646233L;

  protected ActorLLR        actor;

  protected CriticLLR       critic;

  private Specification     specification;

  protected SimpleMatrix    lastObservation;

  protected SimpleMatrix    lastAction;

  public StandardActorCritic()
  {
    actor = new ActorLLR();
    critic = new CriticLLR();

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
  }

  @Override
  public StepVO start(double[][] observation)
  {
    lastObservation = new SimpleMatrix(observation);
    critic.resetEligibilityTrace();

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

  private void update(double reward, SimpleMatrix observation)
  {
    double delta = critic.update(lastObservation, lastAction, reward, observation);
    actor.updateWithRandomness(delta, lastObservation, lastAction);
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
