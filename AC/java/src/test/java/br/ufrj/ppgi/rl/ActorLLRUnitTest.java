package br.ufrj.ppgi.rl;

import org.ejml.simple.SimpleMatrix;
import org.junit.Assert;
import org.junit.Test;

public class ActorLLRUnitTest
{
  private static final double DELTA = 0.1d;

  @Test
  public void testAction_1Input1Output()
  {
    Specification specification = getSpecification_1Input1Output();

    ActorLLR actor = new ActorLLR();
    actor.init(specification);
    actor.random = new RandomMock(0.5);
    actor.llr.setRandom(new RandomMock(1.5));
    
    SimpleMatrix observation = new SimpleMatrix(1, 1);
    observation.zero();

    SimpleMatrix action = actor.action(observation);
    Assert.assertEquals(1, action.numCols());
    Assert.assertEquals(1, action.numRows());
    Assert.assertEquals(2, Math.abs(action.get(0)), DELTA);
  }

  @Test
  public void testAction_1Input2Output()
  {
    Specification specification = getSpecification_1Input2Output();
    specification.setOutputDimensions(2);

    ActorLLR actor = new ActorLLR();
    actor.init(specification);
    actor.random = new RandomMock(0.5);
    actor.llr.setRandom(new RandomMock(1.5));

    SimpleMatrix observation = new SimpleMatrix(1, 1);
    observation.zero();

    SimpleMatrix action = actor.action(observation);
    Assert.assertEquals(2, action.numCols());
    Assert.assertEquals(1, action.numRows());
    Assert.assertEquals(2, Math.abs(action.get(0)), DELTA);
    Assert.assertEquals(2, Math.abs(action.get(1)), DELTA);
  }

  @Test
  public void testAction_1Input1Output_HighSD_ActionShouldBeWrapedAroundMinMaxValue()
  {
    Specification specification = getSpecification_1Input1Output();

    ActorLLR actor = new ActorLLR();
    actor.init(specification);

    actor.random = new RandomMock(30);

    SimpleMatrix observation = new SimpleMatrix(1, 1);
    observation.zero();

    SimpleMatrix action = actor.action(observation);
    Assert.assertEquals(3, action.get(0), DELTA);
    
    actor.random = new RandomMock(-30);
    
    action = actor.action(observation);
    Assert.assertEquals(-3, action.get(0), DELTA);
  }

  private Specification getSpecification_1Input2Output()
  {
    Specification specification = getSpecification();
    
    SimpleMatrix actorMin = new SimpleMatrix(1, 2);
    actorMin.set(0, -3);
    actorMin.set(1, -3);

    SimpleMatrix actorMax = new SimpleMatrix(1, 2);
    actorMax.set(0, 3);
    actorMax.set(1, 3);

    specification.setInputDimensions(1);
    specification.setOutputDimensions(2);
    specification.setActorMax(actorMax);
    specification.setActorMin(actorMin);
    return specification;
  }
  
  private Specification getSpecification_1Input1Output()
  {
    Specification specification = getSpecification();
    SimpleMatrix actorMin = new SimpleMatrix(1, 1);
    actorMin.set(0, -3);

    SimpleMatrix actorMax = new SimpleMatrix(1, 1);
    actorMax.set(0, 3);

    specification.setInputDimensions(1);
    specification.setOutputDimensions(1);
    specification.setActorMax(actorMax);
    specification.setActorMin(actorMin);
    return specification;
  }
  
  private Specification getSpecification()
  {
    Specification specification = new Specification();
    specification.setActorAlpha(0.1);
    specification.setActorMemory(10);
    specification.setActorNeighbors(2);
    specification.setSd(1f);
    return specification;
  }
}
