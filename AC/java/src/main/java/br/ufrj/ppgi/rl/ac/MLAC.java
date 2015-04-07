package br.ufrj.ppgi.rl.ac;

import static org.ejml.simple.SimpleMatrix.END;

import org.ejml.simple.SimpleMatrix;

import br.ufrj.ppgi.matlab.EJMLMatlabUtils;
import br.ufrj.ppgi.rl.ActionVO;
import br.ufrj.ppgi.rl.ActorLLR;
import br.ufrj.ppgi.rl.CriticLLR;
import br.ufrj.ppgi.rl.ProcessModelLWR;
import br.ufrj.ppgi.rl.Specification;
import br.ufrj.ppgi.rl.fa.LWRQueryVO;

public class MLAC implements Agent
{
  private static final long serialVersionUID = 4327216385743752778L;

  protected ActorLLR        actor;

  protected CriticLLR       critic;

  protected ProcessModelLWR processModel;

  protected Specification   specification;

  protected SimpleMatrix    lastObservation;

  protected SimpleMatrix    lastAction;

  protected ActionVO        lastActionVO;

  protected double          lastValueFunction;

  protected int             step;

  public MLAC()
  {
    actor = new ActorLLR();
    critic = new CriticLLR();
    processModel = new ProcessModelLWR();

    specification = null;

    lastObservation = null;
    lastActionVO = null;
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

    step = 0;
  }

  @Override
  public StepVO start(double[][] observation)
  {
    lastObservation = new SimpleMatrix(observation);
    critic.resetEligibilityTrace();

    return new StepVO(chooseAction(lastObservation));
  }

  @Override
  public StepVO step(double reward, double[][] observation)
  {
    double actorUpdate = update(reward, new SimpleMatrix(observation));
    lastObservation = new SimpleMatrix(observation);
    step++;

    return new StepVO(actorUpdate, chooseAction(lastObservation));
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

  protected double update(double reward, SimpleMatrix observation)
  {
    LWRQueryVO modelQuery = processModel.query(lastObservation, lastAction);
    processModel.add(lastObservation, lastAction, observation);

    LWRQueryVO criticResult = critic.query(modelQuery.getResult());

    SimpleMatrix criticXs = getXs(criticResult.getX());
    SimpleMatrix modelXa = getXa(modelQuery.getX());

    double actorUpdate = criticXs.mult(modelXa).get(0);
    actor.update(actorUpdate, lastObservation, lastAction, false);

    critic.update(lastObservation, lastAction, reward, observation);

    //return Math.pow(NormOps.normP2(observation.minus(modelQuery.getResult()).getMatrix()), 2);
    return actorUpdate;
  }

  protected double[][] chooseAction(SimpleMatrix observation)
  {
    lastActionVO = actor.action(observation);

    if (step % specification.getExplorationRate() == 0)
    {
      lastAction = lastActionVO.getAction();
    }
    else
    {
      lastAction = lastActionVO.getPolicyAction();
    }

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

  protected SimpleMatrix getXs(SimpleMatrix x)
  {
    return x.extractMatrix(0, END, 0, specification.getObservationDimensions());
  }

  protected SimpleMatrix getXa(SimpleMatrix x)
  {
    return x.extractMatrix(0, specification.getObservationDimensions(), specification.getObservationDimensions(),
                           specification.getObservationDimensions() + specification.getActionDimensions());
  }

  public ProcessModelLWR getProcessModel()
  {
    return processModel;
  }
}
