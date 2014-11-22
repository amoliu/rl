package br.ufrj.ppgi.rl.performance;

import java.util.Random;

import org.apache.commons.lang3.time.StopWatch;
import org.uncommons.maths.random.CellularAutomatonRNG;
import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.maths.random.XORShiftRNG;

public class RandomGenerationTest
{
  private static final int TOTAL_RUN   = 10;
  private static final int TOTAL_QUERY = 5000000;

  public static void main(String[] args)
  {
    Random rand = new Random();
    MersenneTwisterRNG mersenneTwisterRNG = new MersenneTwisterRNG();
    XORShiftRNG xorrand = new XORShiftRNG();
    CellularAutomatonRNG cellularAutomatonRNG = new CellularAutomatonRNG();

    run(rand, "java.util.Rand");
    run(mersenneTwisterRNG, "MersenneTwisterRNG");
    run(xorrand, "XORShiftRNG");
    run(cellularAutomatonRNG, "CellularAutomatonRNG");
  }

  private static void run(Random rand, String name)
  {
    StopWatch watch = new StopWatch();
    long time = 0;

    for (int r = 0; r < TOTAL_RUN; r++)
    {
      watch.reset();
      watch.start();
      for (int i = 0; i < TOTAL_QUERY; i++)
      {
        rand.nextGaussian();
      }
      watch.stop();
      time += watch.getTime();
    }
    System.out.println(name + ": " + time / (double) TOTAL_RUN + " milliseconds.");
  }

}
