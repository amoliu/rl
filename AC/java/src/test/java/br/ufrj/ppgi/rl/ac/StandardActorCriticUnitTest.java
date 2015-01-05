package br.ufrj.ppgi.rl.ac;

import org.ejml.simple.SimpleMatrix;
import org.junit.Assert;
import org.junit.Test;

import br.ufrj.ppgi.rl.Specification;
import br.ufrj.ppgi.rl.fa.LLRMemoryManagement;

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

  @Test
  public void testStart_ShouldResetCriticET()
  {
    StandardActorCritic actorCritic = new StandardActorCritic();
    Specification spec = getSpecification();

    actorCritic.init(spec);

    actorCritic.start(new double[][] { { 3 } });
    actorCritic.step(5, new double[][] { { 2 } });
    actorCritic.step(4, new double[][] { { 3 } });
    actorCritic.step(5.5, new double[][] { { 4 } });
    actorCritic.step(6, new double[][] { { 5 } });
    actorCritic.step(6.5, new double[][] { { 5.5 } });
    actorCritic.step(7, new double[][] { { 6 } });
    Assert.assertEquals(actorCritic.getCritic().getEligibilityTrace()[3][0], 1, DELTA);
    Assert.assertEquals(actorCritic.getCritic().getEligibilityTrace()[4][0], 1, DELTA);
    Assert.assertEquals(actorCritic.getCritic().getEligibilityTrace()[5][0], 1, DELTA);

    actorCritic.start(new double[][] { { 5 } });
    Assert.assertEquals(actorCritic.getCritic().getEligibilityTrace()[3][0], 0, DELTA);
    Assert.assertEquals(actorCritic.getCritic().getEligibilityTrace()[4][0], 0, DELTA);
    Assert.assertEquals(actorCritic.getCritic().getEligibilityTrace()[5][0], 0, DELTA);
  }

  private Specification getSpecification()
  {
    Specification specification = new Specification();
    specification.setCriticAlpha(0.1);
    specification.setCriticMemory(10);
    specification.setCriticNeighbors(2);
    specification.setCriticInitialValue(0);
    specification.setCriticValuesToRebuildTree(2);
    specification.setCriticMemoryManagement(LLRMemoryManagement.LLR_MEMORY_PREDICTION);

    specification.setActorAlpha(0.1);
    specification.setActorMemory(10);
    specification.setActorNeighbors(2);
    specification.setActorValuesToRebuildTree(2);
    specification.setActorMemoryManagement(LLRMemoryManagement.LLR_MEMORY_PREDICTION);
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

    specification.setExplorationRate(1);
    return specification;
  }
}
