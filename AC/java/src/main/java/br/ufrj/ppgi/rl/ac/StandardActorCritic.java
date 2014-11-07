package br.ufrj.ppgi.rl.ac;

import org.ejml.simple.SimpleMatrix;

import br.ufrj.ppgi.matlab.EJMLMatlabUtils;
import br.ufrj.ppgi.rl.ActorLLR;
import br.ufrj.ppgi.rl.CriticLLR;
import br.ufrj.ppgi.rl.Specification;

public class StandardActorCritic implements Agent
{
  private static final long serialVersionUID = -6663645034619646233L;

  private ActorLLR          actor;

  private CriticLLR         critic;

  private Specification     specification;

  private SimpleMatrix      lastObservation;

  private SimpleMatrix      lastAction;

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
  public double[][] start(double[][] observation)
  {
    lastObservation = new SimpleMatrix(observation);

    return chooseAction(new SimpleMatrix(observation));
  }

  @Override
  public double[][] step(double reward, double[][] observation)
  {
    update(reward, new SimpleMatrix(observation));

    return chooseAction(new SimpleMatrix(observation));
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
    actor.update(delta, lastObservation, lastAction);
  }

  private double[][] chooseAction(SimpleMatrix observation)
  {
    lastAction = actor.action(observation);
    return EJMLMatlabUtils.getMatlabMatrixFromSimpleMatrix(lastAction);
  }

  @Override
  public CriticLLR getCritic() {
	return critic;
  }

  @Override
  public ActorLLR getActor() {
	return actor;
  }
}
