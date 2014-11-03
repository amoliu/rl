package br.ufrj.ppgi.rl;

import java.util.Arrays;

import org.ejml.simple.SimpleMatrix;
import org.junit.Assert;
import org.junit.Test;

public class CriticLLRUnitTest
{
  private static final int CRITIC_INITIAL_VALUE = -1000;
  private static final double DELTA = 0.1d;

  @Test
  public void testResetEligibilityTrace()
  {
    Specification specification = getSpecification();

    CriticLLR critic = new CriticLLR();
    critic.init(specification);
    
    double[] zeroET = new double[specification.getCriticMemory()];
    Arrays.fill(zeroET, 0);
    
    Assert.assertArrayEquals(zeroET, critic.eligibilityTrace.getMatrix().data, DELTA);

    SimpleMatrix observation = new SimpleMatrix(1, 1);
    observation.set(1);

    SimpleMatrix action = new SimpleMatrix(1, 1);
    action.set(2);
    
    critic.update(observation, action, 0, observation);
    Assert.assertArrayEquals(zeroET, critic.eligibilityTrace.getMatrix().data, DELTA);
    
    observation.set(2);
    action.set(2);
    critic.update(observation, action, 0, observation);
    Assert.assertArrayEquals(zeroET, critic.eligibilityTrace.getMatrix().data, DELTA);
    
    observation.set(3);
    action.set(2);
    critic.update(observation, action, 0, observation);
    
    Assert.assertEquals(1, critic.eligibilityTrace.get(0), DELTA);
    Assert.assertEquals(1, critic.eligibilityTrace.get(1), DELTA);
    
    critic.resetEligibilityTrace();
    Assert.assertArrayEquals(zeroET, critic.eligibilityTrace.getMatrix().data, DELTA);
  }

  @Test
  public void testUpdate_ShouldAddEntryToLLR_ShouldUpdateNeighbors_ShouldUpdateET()
  {
    Specification specification = getSpecification();

    CriticLLR critic = new CriticLLR();
    critic.init(specification);
    critic.llr.setRandom(new RandomMock(0));
    
    SimpleMatrix observation = new SimpleMatrix(1, 1);
    observation.set(1);

    SimpleMatrix action = new SimpleMatrix(1, 1);
    action.set(2);
    
    critic.update(observation, action, 0, observation);
    Assert.assertEquals(1, critic.llr.getDataInput().get(0), DELTA);
    Assert.assertEquals(CRITIC_INITIAL_VALUE, critic.llr.getDataOutput().get(0), DELTA);
    
    observation.set(2);
    action.set(2);
    critic.update(observation, action, 0, observation);
    Assert.assertEquals(2, critic.llr.getDataInput().get(1), DELTA);
    Assert.assertEquals(CRITIC_INITIAL_VALUE, critic.llr.getDataOutput().get(1), DELTA);
    
    observation.set(3);
    action.set(2);
    critic.update(observation, action, 0, observation);
    Assert.assertEquals(1, critic.llr.getDataInput().get(0), DELTA);
    Assert.assertEquals(2, critic.llr.getDataOutput().get(0), DELTA);
    
    Assert.assertEquals(2, critic.llr.getDataInput().get(1), DELTA);
    Assert.assertEquals(2, critic.llr.getDataOutput().get(1), DELTA);
    
    Assert.assertEquals(3, critic.llr.getDataInput().get(2), DELTA);
    Assert.assertEquals(2, critic.llr.getDataOutput().get(2), DELTA);
  }
  
  private Specification getSpecification()
  {
    Specification specification = new Specification();
    specification.setCriticAlpha(0.1);
    specification.setCriticMemory(10);
    specification.setCriticNeighbors(2);
    specification.setCriticInitialValue(CRITIC_INITIAL_VALUE);
    specification.setSd(1f);
    
    specification.setInputDimensions(1);
    specification.setOutputDimensions(1);
    return specification;
  }
}
