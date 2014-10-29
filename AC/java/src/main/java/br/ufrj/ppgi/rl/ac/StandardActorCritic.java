package br.ufrj.ppgi.rl.ac;

import org.ejml.simple.SimpleMatrix;

import br.ufrj.ppgi.rl.ActorLLR;
import br.ufrj.ppgi.rl.CriticLLR;
import br.ufrj.ppgi.rl.Specification;

public class StandardActorCritic implements Agent
{
  private static final long serialVersionUID = 6398617648631155363L;

  private ActorLLR      actor;

  private CriticLLR     critic;

  private Specification specification;

  private SimpleMatrix  lastObservation;

  private SimpleMatrix  lastAction;

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
  public SimpleMatrix start(SimpleMatrix observation)
  {
    lastObservation = observation;

    return chooseAction(observation);
  }

  @Override
  public SimpleMatrix step(double reward, SimpleMatrix observation)
  {
    update(reward, observation);

    return chooseAction(observation);
  }

  @Override
  public SimpleMatrix end(double reward)
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
    actor.update(delta);
  }

  private SimpleMatrix chooseAction(SimpleMatrix observation)
  {
    lastAction = actor.action(observation);
    return lastAction;
  }
}
