package br.ufrj.ppgi.rl.fa;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.ejml.simple.SimpleMatrix;
import org.junit.Test;

public class LLRUnitTest
{
  private static final double DELTA = 0.1d;
  private static final int SIZE = 2;
  
  @Test
  public void testInitParams_1Input1Output()
  {
    LLR llr = new LLR(SIZE, 1, 1, 2, 0);

    assertEquals(1, llr.dataInput.getMatrix().numCols);
    assertEquals(SIZE, llr.dataInput.getMatrix().numRows);
    
    assertEquals(1, llr.dataOutput.getMatrix().numCols);
    assertEquals(SIZE, llr.dataOutput.getMatrix().numRows);

    assertEquals(SIZE, llr.relevance.length);
  }

  @Test
  public void testInitParams_2Input1Output()
  {
    LLR llr = new LLR(SIZE, 2, 1, 2, 0);

    assertEquals(2, llr.dataInput.getMatrix().numCols);
    assertEquals(SIZE, llr.dataInput.getMatrix().numRows);
    
    assertEquals(1, llr.dataOutput.getMatrix().numCols);
    assertEquals(SIZE, llr.dataOutput.getMatrix().numRows);

    assertEquals(SIZE, llr.relevance.length);
  }

  @Test
  public void testInitParams_1Input2Output()
  {
    LLR llr = new LLR(SIZE, 1, 2, 2, 0);

    assertEquals(1, llr.dataInput.getMatrix().numCols);
    assertEquals(SIZE, llr.dataInput.getMatrix().numRows);
    
    assertEquals(2, llr.dataOutput.getMatrix().numCols);
    assertEquals(SIZE, llr.dataOutput.getMatrix().numRows);

    assertEquals(SIZE, llr.relevance.length);
  }

  @Test
  public void testAdd_MemoryEmpty_ShouldHaveOneEntry()
  {
    LLR llr = new LLR(SIZE, 1, 1, 2, 0);
    
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
  public void testAdd_MemoryFull_EntryNotRelevant_ShouldNotAdd()
  {
    LLR llr = new LLR(SIZE, 1, 1, 2, 0);
    
    SimpleMatrix input = new SimpleMatrix(1, 1);
    SimpleMatrix output = new SimpleMatrix(1, 1);
    
    input.set(0, 0);
    output.set(0, 0);
    llr.add(input, output);
    
    input.set(0, 1);
    output.set(0, 1);
    llr.add(input, output);
    
    input.set(0, 0.5);
    output.set(0, 0.5);
    llr.add(input, output);
    
    assertEquals(0, llr.relevance[0], DELTA);
    assertEquals(0, llr.dataInput.get(0, 0), DELTA);
    assertEquals(0, llr.dataOutput.get(0, 0), DELTA);
    
    assertEquals(0, llr.relevance[1], DELTA);
    assertEquals(1, llr.dataInput.get(1, 0),DELTA);
    assertEquals(1, llr.dataOutput.get(1, 0),DELTA);
  }

  @Test
  public void testAdd_MemoryFull_EntryRelevant_ShouldAdd()
  {
    LLR llr = new LLR(SIZE, 1, 1, 2, 0);
    
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
    
    assertEquals(4, llr.relevance[0], DELTA);
    assertEquals(2, llr.dataInput.get(0, 0), DELTA);
    assertEquals(4, llr.dataOutput.get(0, 0), DELTA);
    
    assertEquals(0, llr.relevance[1], DELTA);
    assertEquals(1, llr.dataOutput.get(1, 0), DELTA);
    assertEquals(1, llr.dataOutput.get(1, 0), DELTA);
  }
  
  @Test
  public void testQuery_MemoryEmpy_ShouldReturnRandom()
  {
    LLR llr = new LLR(SIZE, 1, 2, 2, 0);
    
    SimpleMatrix query = new SimpleMatrix(1, 1);
    query.set(0, 0);
    
    SimpleMatrix result = llr.query(query);
    
    assertEquals(0, result.get(0, 0), 2d);
    assertEquals(0, result.get(0, 1), 2d);
  }
  
  @Test
  public void testQuery_MemoryFull()
  {
    LLR llr = new LLR(SIZE, 1, 1, 2, 0);
    
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
    
    SimpleMatrix result = llr.query(query);
    
    assertEquals(1, result.get(0, 0), DELTA);
  }
  
  @Test
  public void testUpdate_SimpleMatrix()
  {
    LLR llr = new LLR(SIZE, 1, 1, 2, 0);
    
    SimpleMatrix input = new SimpleMatrix(1, 1);
    SimpleMatrix output = new SimpleMatrix(1, 1);
    
    input.set(0, 0);
    output.set(0, 0);
    llr.add(input, output);
    
    input.set(0, 2);
    output.set(0, 2);
    llr.add(input, output);
    
    SimpleMatrix delta = new SimpleMatrix(SIZE, 1);
    for (int i=0; i<SIZE; i++)
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
    LLR llr = new LLR(SIZE, 1, 1, 2, 0);
    
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
    LLR llr = new LLR(SIZE, 1, 1, 2, 0);
    
    SimpleMatrix input = new SimpleMatrix(1, 1);
    SimpleMatrix output = new SimpleMatrix(1, 1);
    
    input.set(0, 0);
    output.set(0, 0);
    llr.add(input, output);
    
    input.set(0, 2);
    output.set(0, 2);
    llr.add(input, output);
    
    llr.update(Arrays.asList(1), 1);
    
    assertEquals(0, llr.dataOutput.get(0, 0), DELTA);
    assertEquals(3, llr.dataOutput.get(1, 0), DELTA);
  }
}
