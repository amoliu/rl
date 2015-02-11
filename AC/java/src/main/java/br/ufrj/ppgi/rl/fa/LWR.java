package br.ufrj.ppgi.rl.fa;

import static br.ufrj.ppgi.rl.fa.LLRMemoryManagement.LLR_MEMORY_UNIFORM;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.ejml.alg.dense.mult.VectorVectorMult;
import org.ejml.data.DenseMatrix64F;
import org.ejml.factory.LinearSolverFactory;
import org.ejml.interfaces.linsol.LinearSolver;
import org.ejml.ops.CommonOps;
import org.ejml.ops.NormOps;
import org.ejml.simple.SimpleMatrix;

import ags.utils.dataStructures.MaxHeap;
import ags.utils.dataStructures.trees.thirdGenKD.DistanceFunction;
import ags.utils.dataStructures.trees.thirdGenKD.KdTree;
import ags.utils.dataStructures.trees.thirdGenKD.SquareEuclideanDistanceFunction;
import br.ufrj.ppgi.matlab.EJMLMatlabUtils;

public class LWR implements Serializable
{
  private static final long                serialVersionUID          = 1243362004074465105L;

  private static final double              DEFAUL_RIDGE              = 0.000001d;

  private static final double              DEFAUL_GAMMA              = 0.9d;

  private static final LLRMemoryManagement DEFAULT_MEMORY_MANAGEMENT = LLR_MEMORY_UNIFORM;

  private static final double              BIAS                      = 1d;

  protected SimpleMatrix                   dataInput;

  protected SimpleMatrix                   dataOutput;

  protected double[]                       relevance;

  protected int                            size;

  private int                              input_dimension;

  private int                              output_dimension;

  private int                              k;

  private double                           ridge;

  private double                           gamma;

  protected int                            last_llr;

  private KdTree<Integer>                  tree;

  private DistanceFunction                 distanceFunction;

  private int                              tree_size;

  private LinearSolver<DenseMatrix64F>     solver;

  private Random                           random;

  private LWRWeightFunction                weightFunction;

  private int                              valuesToRebuildTree;

  private LLRMemoryManagement              memoryManagement;

  protected LWR()
  {
  };

  private static LWR create()
  {
    LWR lwr = new LWR();

    lwr.ridge = DEFAUL_RIDGE;
    lwr.gamma = DEFAUL_GAMMA;

    lwr.last_llr = 0;
    lwr.random = new Random();

    lwr.distanceFunction = new SquareEuclideanDistanceFunction();
    lwr.tree_size = 0;
    lwr.memoryManagement = DEFAULT_MEMORY_MANAGEMENT;

    return lwr;
  }

  public static LWR createLWR()
  {
    return LWR.create().setWeightFunction(new br.ufrj.ppgi.rl.fa.DistanceFunction());
  }

  public static LWR createLLR()
  {
    return LWR.create().setWeightFunction(new br.ufrj.ppgi.rl.fa.ConstantFunction());
  }

  public LWR setK(int k)
  {
    if (k <= 1)
      throw new IllegalArgumentException("K must be greater than one");

    this.k = k;
    return this;
  }

  public LWR setInputDimension(int input_dimension)
  {
    if (input_dimension <= 0)
      throw new IllegalArgumentException("Input must be greater than zero");

    this.input_dimension = input_dimension;
    this.dataInput = new SimpleMatrix(size, input_dimension);
    this.dataInput.zero();

    buildKDTree();
    solver = LinearSolverFactory.symmPosDef(input_dimension + 1);

    return this;
  }

  public LWR setOutputDimension(int output_dimension)
  {
    if (output_dimension <= 0)
      throw new IllegalArgumentException("Output must be greater than zero");

    this.output_dimension = output_dimension;
    this.dataOutput = new SimpleMatrix(size, output_dimension);
    this.dataOutput.zero();

    return this;
  }

  public LWR setSize(int size)
  {
    this.size = size;
    this.relevance = new double[size];

    return this;
  }

  public LWR setRidge(double ridge)
  {
    this.ridge = ridge;

    return this;
  }

  public LWR setGamma(double gamma)
  {
    this.gamma = gamma;

    return this;
  }

  public LWR setMemoryManagement(LLRMemoryManagement memoryManagement)
  {
    this.memoryManagement = memoryManagement;

    return this;
  }

  public LWR setWeightFunction(LWRWeightFunction weightFunction)
  {
    this.weightFunction = weightFunction;

    return this;
  }

  public LWR setValuesToRebuildTheTree(int valuesToRebuildTree)
  {
    this.valuesToRebuildTree = valuesToRebuildTree;

    return this;
  }

  // Test helper function
  protected void add(double input, double output)
  {
    SimpleMatrix smInput = new SimpleMatrix(1, 1);
    smInput.set(input);

    SimpleMatrix smOutput = new SimpleMatrix(1, 1);
    smOutput.set(output);

    add(smInput, smOutput);
  }

  /**
   * Matlab proxy to real method call
   */
  public void add(double[][] input, double[][] output)
  {
    add(new SimpleMatrix(input), new SimpleMatrix(output));
  }

  public int add(SimpleMatrix input, SimpleMatrix output)
  {
    int pos = 0;
    ArrayList<Integer> neighborsFromRemovedPoint = new ArrayList<Integer>();

    double rel = calculateRelevance(input, output);

    if (last_llr < size)
    {
      pos = last_llr;
      last_llr += 1;
    }
    else
    {
      if (memoryManagement.equals(LLRMemoryManagement.LLR_MEMORY_RANDOM))
      {
        pos = random.nextInt(size);
      }
      else
      {
        pos = positionLessRelevant();
      }

      if (rel < relevance[pos])
        return -1;

      neighborsFromRemovedPoint = getNeighbors(pos);
    }

    relevance[pos] = rel;
    dataInput.setRow(pos, 0, input.getMatrix().getData());
    dataOutput.setRow(pos, 0, output.getMatrix().getData());

    tree_size++;
    if (hasToRebuildKDTree())
    {
      buildKDTree();
    }

    updateRelevanceGivenRemovedPoint(neighborsFromRemovedPoint);
    updateRelevanceGivenAddedPoint(input);

    return pos;
  }

  private boolean hasToRebuildKDTree()
  {
    if (last_llr < size)
    {
      return true;

    }
    return tree_size % valuesToRebuildTree == 0;
  }

  private int positionLessRelevant()
  {
    double minRelevance;
    int posMinRelevance = 0;

    minRelevance = relevance[posMinRelevance];

    for (int i = 1; i < last_llr; i++)
    {
      if (relevance[i] < minRelevance)
      {
        posMinRelevance = i;
        minRelevance = relevance[posMinRelevance];
      }
    }

    return posMinRelevance;
  }

  /**
   * Matlab proxy to real method call
   */
  public void update(double[][] delta)
  {
    if (delta.length == 1 && delta[0].length == 1)
    {
      update(delta[0][0]);
      return;
    }

    update(new SimpleMatrix(delta));
  }

  public void update(List<Integer> points, double delta)
  {
    for (Integer pos : points)
    {
      for (int i = 0; i < output_dimension; i++)
      {
        dataOutput.set(pos, i, dataOutput.get(pos, i) + delta);
      }
    }
  }

  public void update(List<Integer> points, double delta, SimpleMatrix maxValue, SimpleMatrix minValue)
  {
    for (Integer pos : points)
    {
      SimpleMatrix update = EJMLMatlabUtils.wrap(dataOutput.extractVector(true, pos).plus(delta), maxValue, minValue);
      for (int i = 0; i < output_dimension; i++)
      {
        dataOutput.set(pos, i, update.get(i));
      }
    }
  }

  public void update(SimpleMatrix delta)
  {
    dataOutput = dataOutput.plus(delta);
  }

  public void update(double delta)
  {
    dataOutput = dataOutput.plus(delta);
  }

  public LWRQueryVO query(double[][] query)
  {
    return query(new SimpleMatrix(query));
  }

  public LWRQueryVO query(SimpleMatrix query)
  {
    if (!hasEnoughNeighbors())
    {
      SimpleMatrix result = new SimpleMatrix(1, output_dimension);
      result.zero();

      SimpleMatrix x = new SimpleMatrix(output_dimension, input_dimension + 1);
      x.zero();

      SimpleMatrix variance = new SimpleMatrix(1, output_dimension);
      variance.set(Double.MAX_VALUE);

      ArrayList<Integer> neighbors = new ArrayList<Integer>();

      return new LWRQueryVO(result, x, neighbors, variance);
    }

    ArrayList<Integer> neighbors = getNeighbors(query);
    return queryForNeighbors(query, neighbors);
  }

  private LWRQueryVO queryForNeighbors(SimpleMatrix query, ArrayList<Integer> neighbors)
  {
    DenseMatrix64F A = new DenseMatrix64F(neighbors.size(), input_dimension + 1);
    DenseMatrix64F B = new DenseMatrix64F(neighbors.size(), output_dimension);
    DenseMatrix64F X = new DenseMatrix64F(input_dimension + 1, output_dimension);

    for (int n = 0; n < neighbors.size(); n++)
    {
      Integer pos = neighbors.get(n);

      for (int i = 0; i < input_dimension; i++)
      {
        A.set(n, i, dataInput.get(pos, i));
      }
      A.set(n, input_dimension, BIAS);

      for (int i = 0; i < output_dimension; i++)
      {
        B.set(n, i, dataOutput.get(pos, i));
      }
    }

    SimpleMatrix queryBias = getQueryBias(query);

    double[] weights = weightFunction.calculateWeight(A, queryBias);
    for (int i = 0; i < A.numRows; i++)
    {
      for (int j = 0; j < A.numCols; j++)
      {
        A.set(i, j, A.get(i, j) * weights[i]);
      }
    }

    for (int i = 0; i < B.numRows; i++)
    {
      for (int j = 0; j < B.numCols; j++)
      {
        B.set(i, j, B.get(i, j) * weights[i]);
      }
    }

    DenseMatrix64F ATA = new DenseMatrix64F(input_dimension + 1, input_dimension + 1);
    CommonOps.multTransA(A, A, ATA);
    for (int i = 0; i < ATA.numRows; i++)
    {
      ATA.set(i, i, ATA.get(i, i) + ridge);
    }

    DenseMatrix64F ATAinv = new DenseMatrix64F(input_dimension + 1, input_dimension + 1);
    solver.setA(ATA);
    solver.invert(ATAinv);

    DenseMatrix64F ATB = new DenseMatrix64F(input_dimension + 1, output_dimension);
    CommonOps.multTransA(A, B, ATB);

    // Solve for X
    CommonOps.mult(ATAinv, ATB, X);

    SimpleMatrix variance = calculateVariance(A, B, X, weights, ATAinv);

    SimpleMatrix Xsm = SimpleMatrix.wrap(X);
    return new LWRQueryVO(queryBias.mult(Xsm), Xsm.transpose(), neighbors, variance);
  }

  private SimpleMatrix calculateVariance(DenseMatrix64F A, DenseMatrix64F B, DenseMatrix64F X, double[] weights,
                                         DenseMatrix64F ATAinv)
  {
    DenseMatrix64F residual = new DenseMatrix64F(B.numRows, B.numCols);
    CommonOps.mult(A, X, residual);
    CommonOps.subtractEquals(residual, B);

    double nLWR = 0;
    for (int i = 0; i < weights.length; i++)
    {
      nLWR += Math.pow(weights[i], 2);
    }

    double pLWR = 0;
    SimpleMatrix Asm = SimpleMatrix.wrap(A);
    for (int i = 0; i < weights.length; i++)
    {
      pLWR += Math.pow(weights[i], 2)
              * VectorVectorMult.innerProdA(Asm.extractVector(true, i).getMatrix(), ATAinv, Asm.extractVector(true, i)
                                                                                               .getMatrix());
    }

    double denominator = nLWR - pLWR;

    SimpleMatrix variance = new SimpleMatrix(1, output_dimension);
    for (int j = 0; j < output_dimension; j++)
    {
      double v = 0;
      for (int i = 0; i < residual.numRows; i++)
      {
        v += Math.pow(residual.get(i, j), 2);
      }
      v = v / denominator;
      variance.set(j, v);
    }

    return variance;
  }

  private SimpleMatrix getQueryBias(SimpleMatrix query)
  {
    SimpleMatrix queryBias = new SimpleMatrix(1, input_dimension + 1);
    for (int i = 0; i < query.numCols(); i++)
    {
      queryBias.set(0, i, query.get(i));
    }
    queryBias.set(0, query.numCols(), 1);
    return queryBias;
  }

  private void updateRelevanceGivenRemovedPoint(ArrayList<Integer> neighbors)
  {
    updateNeighborhood(neighbors);
  }

  private void updateRelevanceGivenAddedPoint(SimpleMatrix addedPoint)
  {
    ArrayList<Integer> neighbors = getNeighbors(addedPoint);

    updateNeighborhood(neighbors);
  }

  private void updateNeighborhood(ArrayList<Integer> neighbors)
  {
    switch (memoryManagement)
    {
      case LLR_MEMORY_UNIFORM:
        updateRelevanceEvenly(neighbors);
        break;

      case LLR_MEMORY_PREDICTION:
        updateRelevancePrediction(neighbors);
        break;

      case LLR_MEMORY_PREDICTION_NOISE:
        updateOutputToModelOutput(neighbors);
        ArrayList<Integer> neighborhood = getAllNeighborhood(neighbors);
        updateRelevancePrediction(neighborhood);
        break;

      default:
        break;
    }
  }

  private ArrayList<Integer> getAllNeighborhood(ArrayList<Integer> neighbors)
  {
    Set<Integer> neighborhood = new HashSet<Integer>(neighbors);

    for (Integer pos : neighbors)
    {
      neighborhood.addAll(getNeighbors(pos));
    }

    return new ArrayList<Integer>(neighborhood);
  }

  private double calculateRelevance(SimpleMatrix input, SimpleMatrix output)
  {
    if (!hasEnoughNeighbors())
    {
      return 0;
    }

    ArrayList<Integer> neighbors = getNeighbors(input);

    switch (memoryManagement)
    {
      case LLR_MEMORY_UNIFORM:
        return calculateRelevanceEvenly(neighbors, input);

      case LLR_MEMORY_PREDICTION:
      case LLR_MEMORY_PREDICTION_NOISE:
        SimpleMatrix predict_value = queryForNeighbors(input, neighbors).getResult();
        return calculateRelevancePrediction(output, predict_value);

      default:
        return 0;
    }
  }

  private void updateRelevancePrediction(ArrayList<Integer> neighbors)
  {
    for (Integer pos : neighbors)
    {
      SimpleMatrix query = dataInput.extractVector(true, pos);

      SimpleMatrix predict_value = query(query).getResult();
      SimpleMatrix real_value = dataOutput.extractVector(true, pos);

      double rel = calculateRelevancePrediction(real_value, predict_value);
      relevance[pos] = gamma * relevance[pos] + (1 - gamma) * rel;
    }
  }

  private void updateOutputToModelOutput(ArrayList<Integer> neighbors)
  {
    for (Integer pos : neighbors)
    {
      SimpleMatrix query = dataInput.extractVector(true, pos);

      SimpleMatrix predict_value = queryForNeighbors(query, neighbors).getResult();
      dataOutput.setRow(pos, 0, predict_value.getMatrix().data);
    }
  }

  private double calculateRelevancePrediction(SimpleMatrix input, SimpleMatrix output)
  {
    return Math.pow(NormOps.normP2(input.minus(output).getMatrix()), 2);
  }

  private void updateRelevanceEvenly(ArrayList<Integer> neighbors)
  {
    for (Integer pos : neighbors)
    {
      SimpleMatrix query = dataInput.extractVector(true, pos);

      ArrayList<Integer> queryNeighbors = getNeighbors(query);
      double averageDistance = calculateRelevanceEvenly(queryNeighbors, query);

      relevance[pos] = averageDistance;
    }
  }

  private double calculateRelevanceEvenly(ArrayList<Integer> neighbors, SimpleMatrix query)
  {
    double averageDistance = 0;
    for (Integer n : neighbors)
    {
      SimpleMatrix neighbor = dataInput.extractVector(true, n);
      averageDistance += NormOps.normP2(query.minus(neighbor).getMatrix());
    }
    averageDistance /= neighbors.size();
    return averageDistance;
  }

  protected boolean hasEnoughNeighbors()
  {
    return last_llr > 4 * input_dimension;
  }

  private ArrayList<Integer> getNeighbors(int position)
  {
    return getNeighbors(dataInput.extractVector(true, position));
  }

  private ArrayList<Integer> getNeighbors(SimpleMatrix query)
  {
    if (!hasEnoughNeighbors())
    {
      return new ArrayList<Integer>();
    }

    int totalNeighbors = 0;
    if (last_llr <= k)
    {
      totalNeighbors = last_llr;
    }
    else
    {
      totalNeighbors = k;
    }

    MaxHeap<Integer> heap = tree.findNearestNeighbors(query.getMatrix().getData(), totalNeighbors, distanceFunction);
    ArrayList<Integer> neighbors = new ArrayList<Integer>();

    for (int i = 0; i < totalNeighbors; i++)
    {
      Integer n = heap.getMax();
      heap.removeMax();

      neighbors.add(n);
    }

    return neighbors;
  }

  private void buildKDTree()
  {
    tree = new KdTree<Integer>(input_dimension);
    for (int i = 0; i < last_llr; i++)
    {
      tree.addPoint(dataInput.extractVector(true, i).getMatrix().data, i);
    }
  }

  public void setRandom(Random random)
  {
    this.random = random;
  }

  public SimpleMatrix getDataInput()
  {
    return dataInput;
  }

  public double[][] getMatlabDataInput()
  {
    return EJMLMatlabUtils.getMatlabMatrixFromSimpleMatrix(dataInput);
  }

  public SimpleMatrix getDataOutput()
  {
    return dataOutput;
  }

  public double[][] getMatlabDataOutput()
  {
    return EJMLMatlabUtils.getMatlabMatrixFromSimpleMatrix(dataOutput);
  }

  public double[] getRelevance()
  {
    return relevance;
  }

  public LWRWeightFunction getWeightFunction()
  {
    return weightFunction;
  }
}
