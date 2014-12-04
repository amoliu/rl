package br.ufrj.ppgi.rl;

import org.ejml.simple.SimpleMatrix;
import org.junit.Assert;
import org.junit.Test;

public class ProcessoModelLWRUnitTest
{

  private static final double DELTA = 0.1d;

  @Test
  public void testAdd_CrossBoundaries_Forward()
  {
    ProcessModelLWR processModelLLR = new ProcessModelLWR();

    Specification specification = getSpecification();

    processModelLLR.init(specification);

    SimpleMatrix observation = new SimpleMatrix(new double[][] { { 19.5, 1 } });
    SimpleMatrix action = new SimpleMatrix(new double[][] { { 2 } });

    SimpleMatrix new_observation = new SimpleMatrix(new double[][] { { 1, 2 } });

    processModelLLR.add(observation, action, new_observation, 0);

    SimpleMatrix inputLLR = processModelLLR.lwr.getDataInput().extractVector(true, 0);
    Assert.assertEquals(19.5, inputLLR.get(0), DELTA);
    Assert.assertEquals(1, inputLLR.get(1), DELTA);
    Assert.assertEquals(2, inputLLR.get(2), DELTA);

    SimpleMatrix outputLLR = processModelLLR.lwr.getDataOutput().extractVector(true, 0);
    Assert.assertEquals(21, outputLLR.get(0), DELTA);
    Assert.assertEquals(2, outputLLR.get(1), DELTA);

    inputLLR = processModelLLR.lwr.getDataInput().extractVector(true, 1);
    Assert.assertEquals(-0.5, inputLLR.get(0), DELTA);
    Assert.assertEquals(1, inputLLR.get(1), DELTA);
    Assert.assertEquals(2, inputLLR.get(2), DELTA);

    outputLLR = processModelLLR.lwr.getDataOutput().extractVector(true, 1);
    Assert.assertEquals(1, outputLLR.get(0), DELTA);
    Assert.assertEquals(2, outputLLR.get(1), DELTA);
  }

  @Test
  public void testAdd_CrossBoundaries_Backwards()
  {
    ProcessModelLWR processModelLLR = new ProcessModelLWR();

    Specification specification = getSpecification();

    processModelLLR.init(specification);

    SimpleMatrix observation = new SimpleMatrix(new double[][] { { 1, 1 } });
    SimpleMatrix action = new SimpleMatrix(new double[][] { { 2 } });

    SimpleMatrix new_observation = new SimpleMatrix(new double[][] { { 19.5, 2 } });

    processModelLLR.add(observation, action, new_observation, 0);

    SimpleMatrix inputLLR = processModelLLR.lwr.getDataInput().extractVector(true, 0);
    Assert.assertEquals(21, inputLLR.get(0), DELTA);
    Assert.assertEquals(1, inputLLR.get(1), DELTA);
    Assert.assertEquals(2, inputLLR.get(2), DELTA);

    SimpleMatrix outputLLR = processModelLLR.lwr.getDataOutput().extractVector(true, 0);
    Assert.assertEquals(19.5, outputLLR.get(0), DELTA);
    Assert.assertEquals(2, outputLLR.get(1), DELTA);

    inputLLR = processModelLLR.lwr.getDataInput().extractVector(true, 1);
    Assert.assertEquals(1, inputLLR.get(0), DELTA);
    Assert.assertEquals(1, inputLLR.get(1), DELTA);
    Assert.assertEquals(2, inputLLR.get(2), DELTA);

    outputLLR = processModelLLR.lwr.getDataOutput().extractVector(true, 1);
    Assert.assertEquals(-0.5, outputLLR.get(0), DELTA);
    Assert.assertEquals(2, outputLLR.get(1), DELTA);
  }

  @Test
  public void testAdd_NearThreshold_Positive()
  {
    ProcessModelLWR processModelLLR = new ProcessModelLWR();

    Specification specification = getSpecification();

    processModelLLR.init(specification);

    SimpleMatrix observation = new SimpleMatrix(new double[][] { { 0.3, 1 } });
    SimpleMatrix action = new SimpleMatrix(new double[][] { { 2 } });

    SimpleMatrix new_observation = new SimpleMatrix(new double[][] { { 0.2, 2 } });

    processModelLLR.add(observation, action, new_observation, 0);

    SimpleMatrix inputLLR = processModelLLR.lwr.getDataInput().extractVector(true, 0);
    Assert.assertEquals(0.3, inputLLR.get(0), DELTA);
    Assert.assertEquals(1, inputLLR.get(1), DELTA);
    Assert.assertEquals(2, inputLLR.get(2), DELTA);

    SimpleMatrix outputLLR = processModelLLR.lwr.getDataOutput().extractVector(true, 0);
    Assert.assertEquals(0.2, outputLLR.get(0), DELTA);
    Assert.assertEquals(2, outputLLR.get(1), DELTA);

    inputLLR = processModelLLR.lwr.getDataInput().extractVector(true, 1);
    Assert.assertEquals(20.3, inputLLR.get(0), DELTA);
    Assert.assertEquals(1, inputLLR.get(1), DELTA);
    Assert.assertEquals(2, inputLLR.get(2), DELTA);

    outputLLR = processModelLLR.lwr.getDataOutput().extractVector(true, 1);
    Assert.assertEquals(20.2, outputLLR.get(0), DELTA);
    Assert.assertEquals(2, outputLLR.get(1), DELTA);
  }

  @Test
  public void testAdd_NearThreshold_Negative()
  {
    ProcessModelLWR processModelLLR = new ProcessModelLWR();

    Specification specification = getSpecification();

    processModelLLR.init(specification);

    SimpleMatrix observation = new SimpleMatrix(new double[][] { { 19.7, 1 } });
    SimpleMatrix action = new SimpleMatrix(new double[][] { { 2 } });

    SimpleMatrix new_observation = new SimpleMatrix(new double[][] { { 19.9, 2 } });

    processModelLLR.add(observation, action, new_observation, 0);

    SimpleMatrix inputLLR = processModelLLR.lwr.getDataInput().extractVector(true, 0);
    Assert.assertEquals(19.7, inputLLR.get(0), DELTA);
    Assert.assertEquals(1, inputLLR.get(1), DELTA);
    Assert.assertEquals(2, inputLLR.get(2), DELTA);

    SimpleMatrix outputLLR = processModelLLR.lwr.getDataOutput().extractVector(true, 0);
    Assert.assertEquals(19.9, outputLLR.get(0), DELTA);
    Assert.assertEquals(2, outputLLR.get(1), DELTA);

    inputLLR = processModelLLR.lwr.getDataInput().extractVector(true, 1);
    Assert.assertEquals(-0.3, inputLLR.get(0), DELTA);
    Assert.assertEquals(1, inputLLR.get(1), DELTA);
    Assert.assertEquals(2, inputLLR.get(2), DELTA);

    outputLLR = processModelLLR.lwr.getDataOutput().extractVector(true, 1);
    Assert.assertEquals(-0.1, outputLLR.get(0), DELTA);
    Assert.assertEquals(2, outputLLR.get(1), DELTA);
  }

  @Test
  public void testQuery_ShouldWrapResult()
  {
    ProcessModelLWR processModelLLR = new ProcessModelLWR();

    Specification specification = getSpecification();

    processModelLLR.init(specification);

    processModelLLR.lwr.setRandom(new RandomMock(21));

    SimpleMatrix observation = new SimpleMatrix(new double[][] { { 1 } });
    SimpleMatrix action = new SimpleMatrix(new double[][] { { 2 } });

    SimpleMatrix query = processModelLLR.query(observation, action).getLWRQueryVO().getResult();
    Assert.assertEquals(1, query.get(0), DELTA);

    processModelLLR.lwr.setRandom(new RandomMock(-1));

    query = processModelLLR.query(observation, action).getLWRQueryVO().getResult();
    Assert.assertEquals(19, query.get(0), DELTA);
  }

  @Test
  public void testAdd_2Observation1Action()
  {
    ProcessModelLWR processModelLWR = new ProcessModelLWR();

    Specification specification = getSpecification();

    processModelLWR.init(specification);

    SimpleMatrix observation = new SimpleMatrix(new double[][] { { 1, 2 } });
    SimpleMatrix action = new SimpleMatrix(new double[][] { { -1 } });

    SimpleMatrix new_observation = new SimpleMatrix(new double[][] { { 3, 4 } });

    processModelLWR.add(observation, action, new_observation, -50);

    Assert.assertEquals(processModelLWR.lwr.getDataInput().numCols(), 3);
    Assert.assertEquals(processModelLWR.lwr.getDataOutput().numCols(), 3);

    SimpleMatrix inputLLR = processModelLWR.lwr.getDataInput().extractVector(true, 0);
    Assert.assertEquals(1, inputLLR.get(0), DELTA);
    Assert.assertEquals(2, inputLLR.get(1), DELTA);
    Assert.assertEquals(-1, inputLLR.get(2), DELTA);

    SimpleMatrix outputLLR = processModelLWR.lwr.getDataOutput().extractVector(true, 0);
    Assert.assertEquals(3, outputLLR.get(0), DELTA);
    Assert.assertEquals(4, outputLLR.get(1), DELTA);
    Assert.assertEquals(-50, outputLLR.get(2), DELTA);
  }

  private Specification getSpecification()
  {
    Specification specification = new Specification();
    specification.setObservationDimensions(2);
    specification.setActionDimensions(1);
    specification.setProcessModelMemory(10);
    specification.setProcessModelNeighbors(2);
    specification.setProcessModelValuesToRebuildTree(2);
    specification.setObservationMaxValue(new double[][] { { 20, 12 } });
    specification.setObservationMinValue(new double[][] { { 0, -12 } });
    specification.setProcessModelCrossLimit(10);
    specification.setProcessModelThreshold(0.5);
    specification.setProcessModelUpperBound(new double[][] { { 20, 0 } });

    return specification;
  }
}
