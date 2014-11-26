package br.ufrj.ppgi.rl.ac;

import static org.ejml.simple.SimpleMatrix.END;

import org.ejml.ops.NormOps;
import org.ejml.simple.SimpleMatrix;

import br.ufrj.ppgi.matlab.EJMLMatlabUtils;
import br.ufrj.ppgi.rl.ActionVO;
import br.ufrj.ppgi.rl.ActorLLR;
import br.ufrj.ppgi.rl.CriticLLR;
import br.ufrj.ppgi.rl.ProcessModelLLR;
import br.ufrj.ppgi.rl.ProcessModelQueryVO;
import br.ufrj.ppgi.rl.Specification;
import br.ufrj.ppgi.rl.fa.LWRQueryVO;

public class MLAC implements Agent
{
  private static final long serialVersionUID = 4327216385743752778L;

  protected ActorLLR        actor;

  protected CriticLLR       critic;

  protected ProcessModelLLR processModel;

  private Specification     specification;

  protected SimpleMatrix    lastObservation;

  protected ActionVO        lastAction;

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
  public StepVO start(double[][] observation)
  {
    lastObservation = new SimpleMatrix(observation);
    critic.resetEligibilityTrace();

    return new StepVO(chooseAction(new SimpleMatrix(observation)));
  }

  @Override
  public StepVO step(double reward, double[][] observation)
  {
    double error = update(reward, new SimpleMatrix(observation));
    lastObservation = new SimpleMatrix(observation);

    return new StepVO(error, chooseAction(new SimpleMatrix(observation)));
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

  private double update(double reward, SimpleMatrix observation)
  {
    ProcessModelQueryVO modelQuery = processModel.query(lastObservation, lastAction.getPolicyAction());
    processModel.add(lastObservation, lastAction.getPolicyAction(), observation, reward, 0);

    LWRQueryVO criticResult = critic.query(modelQuery.getLWRQueryVO().getResult());

    SimpleMatrix criticXs = getXs(criticResult.getX());
    SimpleMatrix modelXa = getXa(modelQuery.getLWRQueryVO().getX());

    // check if withinBounds
    for (int i = 0; i < lastAction.getPolicyAction().getNumElements(); i++)
    {
      if (lastAction.getPolicyAction().get(i) < specification.getActorMin().get(i) * 0.92
          || lastAction.getPolicyAction().get(i) > specification.getActorMax().get(i) * 0.92)
      {
        modelXa.zero();
      }
    }

    double actorUpdate = criticXs.mult(modelXa).get(0);
    actor.updateWithoutRandomness(actorUpdate, lastObservation, lastAction.getAction());

    lastValueFunction = critic.update(lastObservation, lastAction.getAction(), lastValueFunction, reward, observation);

    return Math.pow(NormOps.normP2(observation.minus(modelQuery.getLWRQueryVO().getResult()).getMatrix()), 2);
  }

  private double[][] chooseAction(SimpleMatrix observation)
  {
    lastAction = actor.action(observation);
    return EJMLMatlabUtils.getMatlabMatrixFromSimpleMatrix(lastAction.getAction());
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
    return x.extractMatrix(0, END, specification.getObservationDimensions(), specification.getObservationDimensions()
                                                                             + specification.getActionDimensions());
  }
}
