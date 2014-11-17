package br.ufrj.ppgi.rl.ac;

import org.ejml.simple.SimpleMatrix;
import org.junit.Assert;
import org.junit.Test;

import br.ufrj.ppgi.rl.Specification;

public class StandardActorCriticUnitTest
{
  private static final double DELTA = 0.1d;

  @Test
  public void testUpdate_ShouldUpdateLastObservation()
  {
    StandardActorCritic actorCritic = new StandardActorCritic();
    Specification spec = getSpecification();

    actorCritic.init(spec);
    
    double[][] action = actorCritic.start(new double[][] { { 3 } }).getAction();
    Assert.assertEquals(actorCritic.lastObservation.get(0), 3, DELTA);
    Assert.assertEquals(actorCritic.lastAction.get(0), action[0][0], DELTA);

    action = actorCritic.step(5, new double[][] { { 2 } }).getAction();
    Assert.assertEquals(actorCritic.lastObservation.get(0), 2, DELTA);
    Assert.assertEquals(actorCritic.lastAction.get(0), action[0][0], DELTA);
  }
  
  private Specification getSpecification()
  {
    Specification specification = new Specification();
    specification.setCriticAlpha(0.1);
    specification.setCriticMemory(10);
    specification.setCriticNeighbors(2);
    specification.setCriticInitialValue(0);

    specification.setActorAlpha(0.1);
    specification.setActorMemory(10);
    specification.setActorNeighbors(2);
    SimpleMatrix actorMin = new SimpleMatrix(1, 1);
    actorMin.set(0, -3);

    SimpleMatrix actorMax = new SimpleMatrix(1, 1);
    actorMax.set(0, 3);

    specification.setObservationDimensions(1);
    specification.setActionDimensions(1);
    specification.setActorMax(actorMax);
    specification.setActorMin(actorMin);
    
    specification.setSd(1f);
    specification.setObservationDimensions(1);
    specification.setActionDimensions(1);
    return specification;
  }
}
