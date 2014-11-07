package br.ufrj.ppgi.rl.ac;

import java.io.Serializable;

import br.ufrj.ppgi.rl.ActorLLR;
import br.ufrj.ppgi.rl.CriticLLR;
import br.ufrj.ppgi.rl.Specification;

public interface Agent extends Serializable
{
  /**
   * Initialize the agent
   */
  public void init(Specification specification);

  /**
   * Start (or restart) an episode, given the first observation
   * 
   * @param observation
   * @return the action to take
   */
  public double[][] start(double[][] observation);

  /**
   * Following steps, given the current reward and the next observation
   * 
   * @param reward
   * @param observation
   * @return the next action to take
   */
  public double[][] step(double reward, double[][] observation);

  /**
   * Signal the end of an episode
   * 
   * @param reward
   * @return
   */
  public double[][] end(double reward);

  /**
   * To exit cleanly after all episodes are finished.
   */
  public void fini();
  
  /**
   * Get the critic learned
   */
  public CriticLLR getCritic();
  
  /**
   * Get the actor learned
   */
  public ActorLLR getActor();
}
