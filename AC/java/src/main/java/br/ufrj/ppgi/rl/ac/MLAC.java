package br.ufrj.ppgi.rl.ac;

import static org.ejml.simple.SimpleMatrix.END;

import org.ejml.simple.SimpleMatrix;

import br.ufrj.ppgi.matlab.EJMLMatlabUtils;
import br.ufrj.ppgi.rl.ActorLLR;
import br.ufrj.ppgi.rl.CriticLLR;
import br.ufrj.ppgi.rl.ProcessModelLLR;
import br.ufrj.ppgi.rl.Specification;
import br.ufrj.ppgi.rl.fa.LLRQueryVO;

public class MLAC implements Agent
{
  private static final long serialVersionUID = -1297896020589293781L;

  protected ActorLLR        actor;

  protected CriticLLR       critic;

  protected ProcessModelLLR processModel;

  private Specification     specification;

  protected SimpleMatrix    lastObservation;

  protected SimpleMatrix    lastAction;

  protected double          lastValueFunction;

  public MLAC()
  {
    actor = new ActorLLR();
    critic = new CriticLLR();
    processModel = new ProcessModelLLR();

    specification = null;

    lastObservation = null;
    lastAction = null;
    lastValueFunction = 0;
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
  public double[][] start(double[][] observation)
  {
    lastObservation = new SimpleMatrix(observation);

    return chooseAction(new SimpleMatrix(observation));
  }

  @Override
  public double[][] step(double reward, double[][] observation)
  {
    update(reward, new SimpleMatrix(observation));
    lastObservation = new SimpleMatrix(observation);

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
    LLRQueryVO model = processModel.query(lastObservation, lastAction);
    processModel.add(lastObservation, lastAction, observation);

    LLRQueryVO criticResult = critic.query(model.getResult());

    // check if withinBounds

    double actorUpdate = getXs(criticResult.getX()).mult(getXa(model.getX())).get(0);
    actor.updateWithoutRandomness(actorUpdate, lastObservation, lastAction);

    lastValueFunction = critic.update(lastObservation, lastAction, lastValueFunction, reward, observation);
  }

  private double[][] chooseAction(SimpleMatrix observation)
  {
    lastAction = actor.action(observation);
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

  private SimpleMatrix getXs(SimpleMatrix x)
  {
    return x.extractMatrix(0, END, 0, specification.getObservationDimensions());
  }

  private SimpleMatrix getXa(SimpleMatrix x)
  {
    return x.extractMatrix(0, END, specification.getObservationDimensions() + 1,
                           specification.getObservationDimensions() + specification.getActionDimensions());
  }
}
