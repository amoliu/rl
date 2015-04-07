package br.ufrj.ppgi.rl.ac;

import br.ufrj.ppgi.rl.ActorLLR;
import br.ufrj.ppgi.rl.CriticLLR;
import br.ufrj.ppgi.rl.Specification;

public class HybridSACMLAC implements Agent
{
  private static final long   serialVersionUID = -537321737372785170L;

  private StandardActorCritic sac = new StandardActorCritic();

  private MLAC                mlac = new MLAC();

  @Override
  public void init(Specification specification)
  {
    sac.init(specification);
    mlac.init(specification);
  }

  @Override
  public StepVO start(double[][] observation)
  {
    mlac.start(observation);
    return sac.start(observation);
  }

  @Override
  public StepVO step(double reward, double[][] observation)
  {
    double mlacActorUpdate = mlac.step(reward, observation).getError();
    StepVO sacStep = sac.step(reward, observation);
    
    sacStep.setMeanDistance(mlacActorUpdate);
    
    return sacStep;
  }

  @Override
  public double[][] stepWithoutLearn(double[][] observation)
  {
    mlac.stepWithoutLearn(observation);
    return sac.stepWithoutLearn(observation);
  }

  @Override
  public double[][] end(double reward)
  {
    mlac.end(reward);
    return sac.end(reward);
  }

  @Override
  public void fini()
  {
    mlac.fini();
    sac.fini();
  }

  @Override
  public CriticLLR getCritic()
  {
    return null;
  }

  @Override
  public ActorLLR getActor()
  {
    return null;
  }
  
  public CriticLLR getSACCritic()
  {
    return sac.getCritic();
  }

  public ActorLLR getSACActor()
  {
    return sac.getActor();
  }

  public CriticLLR getMLACCritic()
  {
    return mlac.getCritic();
  }

  public ActorLLR getMLACActor()
  {
    return mlac.getActor();
  }
}
