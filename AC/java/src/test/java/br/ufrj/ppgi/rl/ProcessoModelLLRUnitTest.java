package br.ufrj.ppgi.rl;

import org.ejml.simple.SimpleMatrix;
import org.junit.Assert;
import org.junit.Test;

public class ProcessoModelLLRUnitTest
{

  private static final double DELTA = 0.1d;

  @Test
  public void testAdd_1Observation1Action()
  {
    ProcessModelLLR processModelLLR = new ProcessModelLLR();

    Specification specification = getSpecification();

    processModelLLR.init(specification);

    SimpleMatrix observation = new SimpleMatrix(new double[][] { { 1 } });
    SimpleMatrix action = new SimpleMatrix(new double[][] { { 2 } });

    SimpleMatrix new_observation = new SimpleMatrix(new double[][] { { 3 } });

    processModelLLR.add(observation, action, new_observation);

    Assert.assertEquals(processModelLLR.llr.getDataInput().numCols(), 2);
    Assert.assertEquals(processModelLLR.llr.getDataOutput().numCols(), 1);

    SimpleMatrix inputLLR = processModelLLR.llr.getDataInput().extractVector(true, 0);
    Assert.assertEquals(1, inputLLR.get(0), DELTA);
    Assert.assertEquals(2, inputLLR.get(1), DELTA);
    
    SimpleMatrix outputLLR = processModelLLR.llr.getDataOutput().extractVector(true, 0);
    Assert.assertEquals(3, outputLLR.get(0), DELTA);
  }

  private Specification getSpecification()
  {
    Specification specification = new Specification();
    specification.setObservationDimensions(1);
    specification.setActionDimensions(1);
    specification.setProcessModelMemory(10);
    specification.setProcessModelNeighbors(2);
    return specification;
  }

}
