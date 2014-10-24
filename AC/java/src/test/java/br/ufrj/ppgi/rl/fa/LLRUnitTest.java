package br.ufrj.ppgi.rl.fa;

import static org.junit.Assert.assertEquals;

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

    assertEquals(2, llr.data.getMatrix().numCols);
    assertEquals(SIZE, llr.data.getMatrix().numRows);

    assertEquals(SIZE, llr.relevance.length);
  }

  @Test
  public void testInitParams_2Input1Output()
  {
    LLR llr = new LLR(SIZE, 2, 1, 2, 0);

    assertEquals(3, llr.data.getMatrix().numCols);
    assertEquals(SIZE, llr.data.getMatrix().numRows);

    assertEquals(SIZE, llr.relevance.length);
  }

  @Test
  public void testInitParams_1Input2Output()
  {
    LLR llr = new LLR(SIZE, 1, 2, 2, 0);

    assertEquals(3, llr.data.getMatrix().numCols);
    assertEquals(SIZE, llr.data.getMatrix().numRows);

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
    assertEquals(1, llr.data.get(0, 0), DELTA);
    assertEquals(1, llr.data.get(0, 1), DELTA);
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
    assertEquals(0, llr.data.get(0, 0), DELTA);
    assertEquals(0, llr.data.get(0, 1), DELTA);
    
    assertEquals(0, llr.relevance[1], DELTA);
    assertEquals(1, llr.data.get(1, 0),DELTA);
    assertEquals(1, llr.data.get(1, 1),DELTA);
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
    assertEquals(2, llr.data.get(0, 0), DELTA);
    assertEquals(4, llr.data.get(0, 1), DELTA);
    
    assertEquals(0, llr.relevance[1], DELTA);
    assertEquals(1, llr.data.get(1, 0), DELTA);
    assertEquals(1, llr.data.get(1, 1), DELTA);
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
}
