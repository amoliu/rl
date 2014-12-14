package br.ufrj.ppgi.rl.ac;

import static org.ejml.simple.SimpleMatrix.END;

import org.ejml.ops.NormOps;
import org.ejml.simple.SimpleMatrix;

import br.ufrj.ppgi.matlab.EJMLMatlabUtils;
import br.ufrj.ppgi.rl.ActionVO;
import br.ufrj.ppgi.rl.ActorLLR;
import br.ufrj.ppgi.rl.CriticLLR;
import br.ufrj.ppgi.rl.ProcessModelLWR;
import br.ufrj.ppgi.rl.ProcessModelQueryVO;
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

  private boolean           randomness;

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
  }

  @Override
  public StepVO start(double[][] observation)
  {
    lastObservation = new SimpleMatrix(observation);
    critic.resetEligibilityTrace();

    step = 0;
    randomness = false;

    return new StepVO(chooseAction(new SimpleMatrix(observation)));
  }

  @Override
  public StepVO step(double reward, double[][] observation)
  {
    double error = update(reward, new SimpleMatrix(observation));
    lastObservation = new SimpleMatrix(observation);
    step++;

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

  protected double update(double reward, SimpleMatrix observation)
  {
    ProcessModelQueryVO modelQuery = processModel.query(lastObservation, lastAction);
    processModel.add(lastObservation, lastAction, observation, reward);

    LWRQueryVO criticResult = critic.query(modelQuery.getLWRQueryVO().getResult());

    SimpleMatrix criticXs = getXs(criticResult.getX());
    SimpleMatrix modelXa = getXa(modelQuery.getLWRQueryVO().getX());

    double actorUpdate = criticXs.mult(modelXa).get(0);
    actor.update(actorUpdate, lastObservation, lastAction, randomness);

    critic.update(lastObservation, lastAction, reward, observation);

    return Math.pow(NormOps.normP2(observation.minus(modelQuery.getLWRQueryVO().getResult()).getMatrix()), 2);
  }

  protected double[][] chooseAction(SimpleMatrix observation)
  {
    lastActionVO = actor.action(observation);

    if (step % specification.getExplorationRate() == 0)
    {
      lastAction = lastActionVO.getAction();
      randomness = true;
    }
    else
    {
      lastAction = lastActionVO.getPolicyAction();
      randomness = false;
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
