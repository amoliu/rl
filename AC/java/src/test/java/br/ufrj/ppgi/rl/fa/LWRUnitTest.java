package br.ufrj.ppgi.rl.fa;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.ejml.simple.SimpleMatrix;
import org.junit.Test;

import br.ufrj.ppgi.rl.RandomMock;

public class LWRUnitTest
{
  private static final double     RANDOM_VALUE = 1.3;
  private static final RandomMock RANDOM_MOCK  = new RandomMock(RANDOM_VALUE);
  private static final double     DELTA        = 0.1d;
  private static final int        SIZE         = 2;

  @Test
  public void testInitParams_1Input1Output()
  {
    LWR llr = LWRMock.createLLR().setSize(SIZE).setInputDimension(1).setOutputDimension(1).setK(2)
                     .setValuesToRebuildTheTree(2);

    assertEquals(1, llr.dataInput.getMatrix().numCols);
    assertEquals(SIZE, llr.dataInput.getMatrix().numRows);

    assertEquals(1, llr.dataOutput.getMatrix().numCols);
    assertEquals(SIZE, llr.dataOutput.getMatrix().numRows);

    assertEquals(SIZE, llr.relevance.length);
  }

  @Test
  public void testInitParams_2Input1Output()
  {
    LWR llr = LWRMock.createLLR().setSize(SIZE).setInputDimension(2).setOutputDimension(1).setK(2)
                     .setValuesToRebuildTheTree(2);

    assertEquals(2, llr.dataInput.getMatrix().numCols);
    assertEquals(SIZE, llr.dataInput.getMatrix().numRows);

    assertEquals(1, llr.dataOutput.getMatrix().numCols);
    assertEquals(SIZE, llr.dataOutput.getMatrix().numRows);

    assertEquals(SIZE, llr.relevance.length);
  }

  @Test
  public void testInitParams_1Input2Output()
  {
    LWR llr = LWRMock.createLLR().setSize(SIZE).setInputDimension(1).setOutputDimension(2).setK(2)
                     .setValuesToRebuildTheTree(2);

    assertEquals(1, llr.dataInput.getMatrix().numCols);
    assertEquals(SIZE, llr.dataInput.getMatrix().numRows);

    assertEquals(2, llr.dataOutput.getMatrix().numCols);
    assertEquals(SIZE, llr.dataOutput.getMatrix().numRows);

    assertEquals(SIZE, llr.relevance.length);
  }

  @Test
  public void testAdd_MemoryEmpty_ShouldHaveOneEntry_1Input1Output()
  {
    LWR llr = LWRMock.createLLR().setSize(SIZE).setInputDimension(1).setOutputDimension(1).setK(2)
                     .setValuesToRebuildTheTree(2);

    SimpleMatrix input = new SimpleMatrix(1, 1);
    SimpleMatrix output = new SimpleMatrix(1, 1);

    input.set(0, 1);
    output.set(0, 1);

    llr.add(input, output);

    assertEquals(0, llr.relevance[0], DELTA);
    assertEquals(1, llr.dataInput.get(0, 0), DELTA);
    assertEquals(1, llr.dataOutput.get(0, 0), DELTA);
  }

  @Test
  public void testAdd_MemoryEmpty_ShouldHaveOneEntry_1Input2Output()
  {
    LWR llr = LWRMock.createLLR().setSize(SIZE).setInputDimension(1).setOutputDimension(2).setK(2)
                     .setValuesToRebuildTheTree(2);

    SimpleMatrix input = new SimpleMatrix(1, 1);
    SimpleMatrix output = new SimpleMatrix(2, 1);

    input.set(0, 1);

    output.set(0, 1);
    output.set(1, 1);

    llr.add(input, output);

    assertEquals(0, llr.relevance[0], DELTA);

    assertEquals(1, llr.dataInput.get(0, 0), DELTA);

    assertEquals(1, llr.dataOutput.get(0, 0), DELTA);
    assertEquals(1, llr.dataOutput.get(0, 1), DELTA);
  }

  @Test
  public void testAdd_MemoryEmpty_ShouldHaveOneEntry_2Input1Output()
  {
    LWR llr = LWRMock.createLLR().setSize(SIZE).setInputDimension(2).setOutputDimension(1).setK(2)
                     .setValuesToRebuildTheTree(2);

    SimpleMatrix input = new SimpleMatrix(2, 1);
    SimpleMatrix output = new SimpleMatrix(1, 1);

    input.set(0, 1);
    input.set(1, 1);

    output.set(0, 1);

    llr.add(input, output);

    assertEquals(0, llr.relevance[0], DELTA);

    assertEquals(1, llr.dataInput.get(0, 0), DELTA);
    assertEquals(1, llr.dataInput.get(0, 1), DELTA);

    assertEquals(1, llr.dataOutput.get(0, 0), DELTA);
  }

  @Test
  public void testAdd_MemoryEmpty_ShouldHaveOneEntry_2Input2Output()
  {
    LWR llr = LWRMock.createLLR().setSize(SIZE).setInputDimension(2).setOutputDimension(2).setK(2)
                     .setValuesToRebuildTheTree(2);

    SimpleMatrix input = new SimpleMatrix(2, 1);
    SimpleMatrix output = new SimpleMatrix(2, 1);

    input.set(0, 1);
    input.set(1, 1);

    output.set(0, 1);
    output.set(1, 1);

    llr.add(input, output);

    assertEquals(0, llr.relevance[0], DELTA);

    assertEquals(1, llr.dataInput.get(0, 0), DELTA);
    assertEquals(1, llr.dataInput.get(0, 1), DELTA);

    assertEquals(1, llr.dataOutput.get(0, 0), DELTA);
    assertEquals(1, llr.dataOutput.get(0, 1), DELTA);
  }

  @Test
  public void testAdd_MemoryFull_1Input1Output()
  {
    LWR llr = LWRMock.createLLR().setSize(SIZE).setInputDimension(1).setOutputDimension(1).setK(2)
                     .setValuesToRebuildTheTree(2);

    SimpleMatrix input = new SimpleMatrix(1, 1);
    SimpleMatrix output = new SimpleMatrix(1, 1);

    input.set(0, 0);
    output.set(0, 0);
    llr.add(input, output);

    input.set(0, 1);
    output.set(0, 1);
    llr.add(input, output);

    input.set(0, 2);
    output.set(0, 4);
    llr.add(input, output);

    assertEquals(Math.pow(2, 2), llr.relevance[0], DELTA);
    assertEquals(2, llr.dataInput.get(0, 0), DELTA);
    assertEquals(4, llr.dataOutput.get(0, 0), DELTA);

    assertEquals(Math.pow(0, 2), llr.relevance[1], DELTA);
    assertEquals(1, llr.dataInput.get(1, 0), DELTA);
    assertEquals(1, llr.dataOutput.get(1, 0), DELTA);

    input.set(0, 0);
    output.set(0, 0);
    llr.add(input, output);

    assertEquals(3.6, llr.relevance[0], DELTA);
    assertEquals(2, llr.dataInput.get(0, 0), DELTA);
    assertEquals(4, llr.dataOutput.get(0, 0), DELTA);

    assertEquals(4, llr.relevance[1], DELTA);
    assertEquals(0, llr.dataInput.get(1, 0), DELTA);
    assertEquals(0, llr.dataOutput.get(1, 0), DELTA);
  }

  @Test
  public void testAdd_MemoryFull_1Input2Output()
  {
    LWR llr = LWRMock.createLLR().setSize(SIZE).setInputDimension(1).setOutputDimension(2).setK(2)
                     .setValuesToRebuildTheTree(2);

    SimpleMatrix input = new SimpleMatrix(1, 1);
    SimpleMatrix output = new SimpleMatrix(1, 2);

    input.set(0, 0);
    output.set(0, 0);
    output.set(1, 0);
    llr.add(input, output);

    input.set(0, 1);
    output.set(0, 1);
    output.set(1, 1);
    llr.add(input, output);

    input.set(0, 2);
    output.set(0, 4);
    output.set(1, 4);
    llr.add(input, output);

    assertEquals(8, llr.relevance[0], DELTA);
    assertEquals(2, llr.dataInput.get(0, 0), DELTA);
    assertEquals(4, llr.dataOutput.get(0, 0), DELTA);
    assertEquals(4, llr.dataOutput.get(0, 1), DELTA);

    assertEquals(0, llr.relevance[1], DELTA);
    assertEquals(1, llr.dataInput.get(1, 0), DELTA);
    assertEquals(1, llr.dataOutput.get(1, 0), DELTA);
    assertEquals(1, llr.dataOutput.get(1, 1), DELTA);
  }

  @Test
  public void testAdd_MemoryFull_2Input1Output()
  {
    LWR llr = LWRMock.createLLR().setSize(SIZE).setInputDimension(2).setOutputDimension(1).setK(2)
                     .setValuesToRebuildTheTree(2);

    SimpleMatrix input = new SimpleMatrix(2, 1);
    SimpleMatrix output = new SimpleMatrix(1, 1);

    input.set(0, 0);
    input.set(1, 0);
    output.set(0, 0);
    llr.add(input, output);

    input.set(0, 1);
    input.set(1, 1);
    output.set(0, 1);
    llr.add(input, output);

    input.set(0, 2);
    input.set(1, 2);
    output.set(0, 4);
    llr.add(input, output);

    assertEquals(Math.pow(2.5, 2), llr.relevance[0], DELTA);
    assertEquals(2, llr.dataInput.get(0, 0), DELTA);
    assertEquals(2, llr.dataInput.get(0, 1), DELTA);
    assertEquals(4, llr.dataOutput.get(0, 0), DELTA);

    assertEquals(Math.pow(0.05, 2), llr.relevance[1], DELTA);
    assertEquals(1, llr.dataInput.get(1, 0), DELTA);
    assertEquals(1, llr.dataInput.get(1, 1), DELTA);
    assertEquals(1, llr.dataOutput.get(1, 0), DELTA);
  }

  @Test
  public void ttestAdd_MemoryFull_2Input2Output()
  {
    LWR llr = LWRMock.createLLR().setSize(SIZE).setInputDimension(2).setOutputDimension(2).setK(2)
                     .setValuesToRebuildTheTree(2);

    SimpleMatrix input = new SimpleMatrix(1, 2);
    SimpleMatrix output = new SimpleMatrix(1, 2);

    input.set(0, 0);
    input.set(1, 0);
    output.set(0, 0);
    output.set(1, 0);
    llr.add(input, output);

    input.set(0, 1);
    input.set(1, 1);
    output.set(0, 1);
    output.set(1, 1);
    llr.add(input, output);

    input.set(0, 2);
    input.set(1, 2);
    output.set(0, 4);
    output.set(1, 4);
    llr.add(input, output);

    assertEquals(8, llr.relevance[0], DELTA);
    assertEquals(2, llr.dataInput.get(0, 0), DELTA);
    assertEquals(2, llr.dataInput.get(0, 1), DELTA);
    assertEquals(4, llr.dataOutput.get(0, 0), DELTA);
    assertEquals(4, llr.dataOutput.get(0, 1), DELTA);

    assertEquals(0.07, llr.relevance[1], DELTA);
    assertEquals(1, llr.dataInput.get(1, 0), DELTA);
    assertEquals(1, llr.dataInput.get(1, 1), DELTA);
    assertEquals(1, llr.dataOutput.get(1, 0), DELTA);
    assertEquals(1, llr.dataOutput.get(1, 1), DELTA);
  }

  @Test
  public void testQuery_MemoryEmpy_ShouldReturnRandom_1Input1Output()
  {
    LWR llr = LWRMock.createLLR().setSize(SIZE).setInputDimension(1).setOutputDimension(1).setK(2)
                     .setValuesToRebuildTheTree(2);
    llr.setRandom(RANDOM_MOCK);

    SimpleMatrix query = new SimpleMatrix(1, 1);
    query.set(0, 0);

    LWRQueryVO queryResult = llr.query(query);

    assertEquals(RANDOM_VALUE, queryResult.getResult().get(0, 0), DELTA);
  }

  @Test
  public void testQuery_MemoryEmpy_ShouldReturnRandom_1Input2Output()
  {
    LWR llr = LWRMock.createLLR().setSize(SIZE).setInputDimension(1).setOutputDimension(2).setK(2)
                     .setValuesToRebuildTheTree(2);
    llr.setRandom(RANDOM_MOCK);

    SimpleMatrix query = new SimpleMatrix(1, 1);
    query.set(0, 0);

    LWRQueryVO queryResult = llr.query(query);

    assertEquals(RANDOM_VALUE, queryResult.getResult().get(0, 0), DELTA);
    assertEquals(RANDOM_VALUE, queryResult.getResult().get(0, 1), DELTA);
  }

  @Test
  public void testQuery_MemoryEmpy_ShouldReturnRandom_2Input1Output()
  {
    LWR llr = LWRMock.createLLR().setSize(SIZE).setInputDimension(2).setOutputDimension(1).setK(2)
                     .setValuesToRebuildTheTree(2);
    llr.setRandom(RANDOM_MOCK);

    SimpleMatrix query = new SimpleMatrix(2, 1);
    query.set(0, 0);
    query.set(1, 0);

    LWRQueryVO queryResult = llr.query(query);

    assertEquals(RANDOM_VALUE, queryResult.getResult().get(0, 0), DELTA);
  }

  @Test
  public void testQuery_MemoryEmpy_ShouldReturnRandom_2Input2Output()
  {
    LWR llr = LWRMock.createLLR().setSize(SIZE).setInputDimension(2).setOutputDimension(2).setK(2)
                     .setValuesToRebuildTheTree(2);
    llr.setRandom(RANDOM_MOCK);

    SimpleMatrix query = new SimpleMatrix(2, 1);
    query.set(0, 0);
    query.set(1, 0);

    LWRQueryVO queryResult = llr.query(query);

    assertEquals(RANDOM_VALUE, queryResult.getResult().get(0, 0), DELTA);
    assertEquals(RANDOM_VALUE, queryResult.getResult().get(0, 1), DELTA);
  }

  @Test
  public void testQuery_MemoryFull_1Input1Output()
  {
    LWR llr = LWRMock.createLLR().setSize(SIZE).setInputDimension(1).setOutputDimension(1).setK(2)
                     .setValuesToRebuildTheTree(2);

    SimpleMatrix input = new SimpleMatrix(1, 1);
    SimpleMatrix output = new SimpleMatrix(1, 1);

    input.set(0, 0);
    output.set(0, 0);
    llr.add(input, output);

    input.set(0, 2);
    output.set(0, 2);
    llr.add(input, output);

    SimpleMatrix query = new SimpleMatrix(1, 1);
    query.set(0, 1);

    LWRQueryVO queryResult = llr.query(query);

    assertEquals(1, queryResult.getResult().get(0, 0), DELTA);
  }

  @Test
  public void testQuery_MemoryFull_2Input1Output()
  {
    LWR llr = LWRMock.createLLR().setSize(SIZE).setInputDimension(2).setOutputDimension(1).setK(2)
                     .setValuesToRebuildTheTree(2);

    SimpleMatrix input = new SimpleMatrix(2, 1);
    SimpleMatrix output = new SimpleMatrix(1, 1);

    input.set(0, 0);
    input.set(1, 0);
    output.set(0, 0);
    llr.add(input, output);

    input.set(0, 2);
    input.set(1, 2);
    output.set(0, 2);
    llr.add(input, output);

    SimpleMatrix query = new SimpleMatrix(2, 1);
    query.set(0, 1);
    query.set(1, 1);

    LWRQueryVO queryResult = llr.query(query);

    assertEquals(1, queryResult.getResult().get(0, 0), DELTA);
  }

  @Test
  public void testQuery_MemoryFull_1Input2Output()
  {
    LWR llr = LWRMock.createLLR().setSize(SIZE).setInputDimension(1).setOutputDimension(2).setK(2)
                     .setValuesToRebuildTheTree(2);

    SimpleMatrix input = new SimpleMatrix(1, 1);
    SimpleMatrix output = new SimpleMatrix(2, 1);

    input.set(0, 0);
    output.set(0, 0);
    output.set(1, 0);
    llr.add(input, output);

    input.set(0, 2);
    output.set(0, 2);
    output.set(1, 2);
    llr.add(input, output);

    SimpleMatrix query = new SimpleMatrix(1, 1);
    query.set(0, 1);

    LWRQueryVO queryResult = llr.query(query);

    assertEquals(1, queryResult.getResult().get(0, 0), DELTA);
    assertEquals(1, queryResult.getResult().get(0, 1), DELTA);
  }

  @Test
  public void testQuery_MemoryFull_2Input2Output()
  {
    LWR llr = LWRMock.createLLR().setSize(SIZE).setInputDimension(2).setOutputDimension(2).setK(2)
                     .setValuesToRebuildTheTree(2);

    SimpleMatrix input = new SimpleMatrix(2, 1);
    SimpleMatrix output = new SimpleMatrix(2, 1);

    input.set(0, 0);
    input.set(1, 0);
    output.set(0, 0);
    output.set(1, 0);
    llr.add(input, output);

    input.set(0, 2);
    input.set(1, 2);
    output.set(0, 2);
    output.set(1, 2);
    llr.add(input, output);

    SimpleMatrix query = new SimpleMatrix(2, 1);
    query.set(0, 1);
    query.set(1, 1);

    LWRQueryVO queryResult = llr.query(query);

    assertEquals(1, queryResult.getResult().get(0, 0), DELTA);
    assertEquals(1, queryResult.getResult().get(0, 1), DELTA);
  }

  @Test
  public void testUpdate_SimpleMatrix()
  {
    LWR llr = LWRMock.createLLR().setSize(SIZE).setInputDimension(1).setOutputDimension(1).setK(2)
                     .setValuesToRebuildTheTree(2);

    SimpleMatrix input = new SimpleMatrix(1, 1);
    SimpleMatrix output = new SimpleMatrix(1, 1);

    input.set(0, 0);
    output.set(0, 0);
    llr.add(input, output);

    input.set(0, 2);
    output.set(0, 2);
    llr.add(input, output);

    SimpleMatrix delta = new SimpleMatrix(SIZE, 1);
    for (int i = 0; i < SIZE; i++)
    {
      delta.set(i, 0, 1);
    }
    delta.set(0, 0, 0);

    llr.update(delta);

    assertEquals(0, llr.dataOutput.get(0, 0), DELTA);
    assertEquals(3, llr.dataOutput.get(1, 0), DELTA);
  }

  @Test
  public void testUpdate_doubleValue()
  {
    LWR llr = LWRMock.createLLR().setSize(SIZE).setInputDimension(1).setOutputDimension(1).setK(2)
                     .setValuesToRebuildTheTree(2);

    SimpleMatrix input = new SimpleMatrix(1, 1);
    SimpleMatrix output = new SimpleMatrix(1, 1);

    input.set(0, 0);
    output.set(0, 0);
    llr.add(input, output);

    input.set(0, 2);
    output.set(0, 2);
    llr.add(input, output);

    llr.update(1);

    assertEquals(1, llr.dataOutput.get(0, 0), DELTA);
    assertEquals(3, llr.dataOutput.get(1, 0), DELTA);
  }

  @Test
  public void testUpdate_NeighborsAndDelta()
  {
    LWR llr = LWRMock.createLLR().setSize(SIZE).setInputDimension(1).setOutputDimension(1).setK(2)
                     .setValuesToRebuildTheTree(2);

    SimpleMatrix input = new SimpleMatrix(1, 1);
    SimpleMatrix output = new SimpleMatrix(1, 1);

    input.set(0, 0);
    output.set(0, 0);
    llr.add(input, output);

    input.set(0, 2);
    output.set(0, 2);
    llr.add(input, output);

    Set<Integer> points = new HashSet<Integer>();
    points.add(1);
    
    llr.update(points, 1);

    assertEquals(0, llr.dataOutput.get(0, 0), DELTA);
    assertEquals(3, llr.dataOutput.get(1, 0), DELTA);
  }

  class LWRMock extends LWR
  {
    private static final long serialVersionUID = 5157239159495382652L;

    @Override
    protected boolean hasEnoughNeighbors()
    {
      return last_llr > 1;
    }
  }
}
