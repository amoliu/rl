package br.ufrj.ppgi.rl.ac;

import java.io.Serializable;

import org.ejml.simple.SimpleMatrix;

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
  public SimpleMatrix start(SimpleMatrix observation);

  /**
   * Following steps, given the current reward and the next observation
   * 
   * @param reward
   * @param observation
   * @return the next action to take
   */
  public SimpleMatrix step(double reward, SimpleMatrix observation);

  /**
   * Signal the end of an episode
   * 
   * @param reward
   * @return
   */
  public SimpleMatrix end(double reward);

  /**
   * To exit cleanly after all episodes are finished.
   */
  public void fini();
}
