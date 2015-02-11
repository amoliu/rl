package br.ufrj.ppgi.rl.fa;

import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

public class LLRUniformUnitTest
{

  @Test
  public void testConstantFunction_MemorySize4()
  {
    LWR lwr = LWR.createLLR();
    lwr.setSize(5).setInputDimension(1).setOutputDimension(1).setK(5).setValuesToRebuildTheTree(1)
       .setMemoryManagement(LLRMemoryManagement.LLR_MEMORY_UNIFORM);

    Random rnd = new Random();
    for (int i = 0; i < 100; i++)
    {
      lwr.add(rnd.nextInt(10), 1);
    }

    System.out.println(lwr.getDataInput() + ". " + Arrays.toString(lwr.getRelevance()));
  }
}
