package br.ufrj.ppgi.rl;

import org.ejml.simple.SimpleMatrix;
import org.junit.Assert;
import org.junit.Test;

public class ProcessoModelLWRUnitTest
{

  private static final double DELTA = 0.1d;

  @Test
  public void testAdd_2Observation1Action()
  {
    ProcessModelLWR processModelLWR = new ProcessModelLWR();

    Specification specification = getSpecification();

    processModelLWR.init(specification);

    SimpleMatrix observation = new SimpleMatrix(new double[][] { { 1, 2 } });
    SimpleMatrix action = new SimpleMatrix(new double[][] { { -1 } });

    SimpleMatrix new_observation = new SimpleMatrix(new double[][] { { 3, 4 } });

    processModelLWR.add(observation, action, new_observation, -50, 0);

    Assert.assertEquals(processModelLWR.lwr.getDataInput().numCols(), 3);
    Assert.assertEquals(processModelLWR.lwr.getDataOutput().numCols(), 4);

    SimpleMatrix inputLLR = processModelLWR.lwr.getDataInput().extractVector(true, 0);
    Assert.assertEquals(1, inputLLR.get(0), DELTA);
    Assert.assertEquals(2, inputLLR.get(1), DELTA);
    Assert.assertEquals(-1, inputLLR.get(2), DELTA);

    SimpleMatrix outputLLR = processModelLWR.lwr.getDataOutput().extractVector(true, 0);
    Assert.assertEquals(3, outputLLR.get(0), DELTA);
    Assert.assertEquals(4, outputLLR.get(1), DELTA);
    Assert.assertEquals(-50, outputLLR.get(2), DELTA);
    Assert.assertEquals(0, outputLLR.get(3), DELTA);
  }

  private Specification getSpecification()
  {
    Specification specification = new Specification();
    specification.setObservationDimensions(2);
    specification.setActionDimensions(1);
    specification.setProcessModelMemory(10);
    specification.setProcessModelNeighbors(2);
    specification.setObservationMaxValue(new double[][] { { 20, 12 } });
    specification.setObservationMinValue(new double[][] { { 0, -12 } });
    specification.setProcessModelCrossLimit(10);
    specification.setProcessModelThreshold(0.5);
    specification.setProcessModelUpperBound(new double[][] { { 20, 0 } });

    return specification;
  }
}
