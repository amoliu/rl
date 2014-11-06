package br.ufrj.ppgi.rl.fa;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.ejml.alg.dense.decomposition.chol.CholeskyDecompositionInner_D64;
import org.ejml.alg.dense.linsol.chol.LinearSolverChol;
import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.NormOps;
import org.ejml.simple.SimpleMatrix;

import br.ufrj.ppgi.matlab.EJMLMatlabUtils;
import ags.utils.dataStructures.MaxHeap;
import ags.utils.dataStructures.trees.thirdGenKD.DistanceFunction;
import ags.utils.dataStructures.trees.thirdGenKD.KdTree;
import ags.utils.dataStructures.trees.thirdGenKD.SquareEuclideanDistanceFunction;

public class LLR implements Serializable
{
  private static final long serialVersionUID = 4191791627521406565L;

  private static final double DEFAUL_TIKHONOV       = 0.000001d;

  private static final double DEFAUL_GAMMA          = 0.9d;

  private static final double DEFAULT_INITIAL_VALUE = 0;

  private static final double BIAS                  = 1d;

  protected SimpleMatrix      dataInput;

  protected SimpleMatrix      dataOutput;

  protected double[]          relevance;

  protected int               size;

  private int                 input_dimension;

  private int                 output_dimension;

  private int                 k;

  private SimpleMatrix        tikhonov;

  private double              gamma;

  protected int               last_llr;

  private double              initial_value;

  private KdTree<Integer>     tree;

  private DistanceFunction    distanceFunction;

  private LinearSolverChol    solver;

  private Random              random;

  public LLR(int size, int input_dimensions, int output_dimensions, int k)
  {
    this(size, input_dimensions, output_dimensions, k, DEFAULT_INITIAL_VALUE, DEFAUL_TIKHONOV, DEFAUL_GAMMA);
  }

  public LLR(int size, int input_dimensions, int output_dimensions, int k, double initial_value)
  {
    this(size, input_dimensions, output_dimensions, k, initial_value, DEFAUL_TIKHONOV, DEFAUL_GAMMA);
  }

  public LLR(int size, int input_dimensions, int output_dimensions, int k, double initial_value, double tikhonov,
             double gamma)
  {
    if (k <= 1)
      throw new IllegalArgumentException("K must be greater than one");

    if (input_dimensions <= 0)
      throw new IllegalArgumentException("Input must be greater than zero");

    if (output_dimensions <= 0)
      throw new IllegalArgumentException("Output must be greater than zero");

    this.size = size;
    this.input_dimension = input_dimensions;
    this.output_dimension = output_dimensions;
    this.initial_value = initial_value;
    this.k = k;
    this.tikhonov = SimpleMatrix.identity(input_dimensions + 1).scale(tikhonov);
    this.gamma = gamma;

    this.dataInput = new SimpleMatrix(size, input_dimensions);
    this.dataInput.zero();

    this.dataOutput = new SimpleMatrix(size, output_dimensions);
    this.dataOutput.zero();

    this.relevance = new double[size];

    this.last_llr = 0;
    this.random = new Random();

    buildKDTree();
    distanceFunction = new SquareEuclideanDistanceFunction();
    solver = new LinearSolverChol(new CholeskyDecompositionInner_D64());
  }

  /**
   * Matlab proxy to real method call
   */
  public void add(double[][] input, double[][] output)
  {
    add(new SimpleMatrix(input), new SimpleMatrix(output));
  }
  
  public void add(SimpleMatrix input, SimpleMatrix output)
  {
    int pos = 0;
    double rel = updateRelevanceForPoint(input, output);

    if (last_llr < size)
    {
      pos = last_llr;
      last_llr += 1;
    }
    else
    {
      pos = positionLessRelevant();
      if (rel <= relevance[pos])
        return;
    }

    relevance[pos] = rel;

    dataInput.setRow(pos, 0, input.getMatrix().getData());
    dataOutput.setRow(pos, 0, output.getMatrix().getData());

    buildKDTree();
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

  public void update(SimpleMatrix delta)
  {
    dataOutput = dataOutput.plus(delta);
  }

  public void update(double delta)
  {
    dataOutput = dataOutput.plus(delta);
  }

  public LLRQueryVO query(double[][] query)
  {
    return query(new SimpleMatrix(query));
  }
  
  public LLRQueryVO query(SimpleMatrix query)
  {
    if (!hasEnoughNeighbors())
    {
      SimpleMatrix result = SimpleMatrix.random(1, output_dimension, 0, 1, random);
      result = result.plus(initial_value);

      List<Integer> neighbors = Collections.emptyList();

      return new LLRQueryVO(result, neighbors);
    }

    List<Integer> neighbors = getNeighbors(query);
    return new LLRQueryVO(queryForNeighbors(query, neighbors), neighbors);
  }

  private SimpleMatrix queryForNeighbors(SimpleMatrix query, List<Integer> neighbors)
  {
    SimpleMatrix A = new SimpleMatrix(input_dimension + 1, neighbors.size());
    SimpleMatrix B = new SimpleMatrix(neighbors.size(), output_dimension);
    DenseMatrix64F X = new DenseMatrix64F(input_dimension + 1, output_dimension);

    for (int n = 0; n < k; n++)
    {
      Integer pos = neighbors.get(n);

      for (int i = 0; i < input_dimension; i++)
      {
        A.set(i, n, dataInput.get(pos, i));
      }
      A.set(input_dimension, n, BIAS);

      for (int i = 0; i < output_dimension; i++)
      {
        B.set(n, i, dataOutput.get(pos, i));
      }
    }

    solver.setA(A.mult(A.transpose()).plus(tikhonov).getMatrix());
    solver.solve(A.mult(B).getMatrix(), X);

    SimpleMatrix queryBias = new SimpleMatrix(1, input_dimension + 1);
    for (int i = 0; i < query.numRows(); i++)
    {
      queryBias.set(0, i, query.get(i, 0));
    }
    queryBias.set(0, query.numRows(), 1);

    return queryBias.mult(SimpleMatrix.wrap(X));
  }

  private double updateRelevanceForPoint(SimpleMatrix input, SimpleMatrix output)
  {
    if (!hasEnoughNeighbors())
    {
      return 0;
    }

    List<Integer> neighbors = getNeighbors(input);

    for (int n = 0; n < k; n++)
    {
      Integer pos = neighbors.get(n);

      SimpleMatrix query = dataInput.extractVector(true, pos);
      SimpleMatrix predict_value = queryForNeighbors(query, neighbors);

      SimpleMatrix real_value = dataOutput.extractVector(true, pos);

      double rel = NormOps.normP2(real_value.minus(predict_value).getMatrix());

      relevance[pos] = gamma * relevance[pos] + (1 - gamma) * rel;
    }

    SimpleMatrix predict_value = queryForNeighbors(input, neighbors).transpose();
    return NormOps.normP2(output.minus(predict_value).getMatrix());
  }

  private boolean hasEnoughNeighbors()
  {
    return last_llr >= k;
  }

  private List<Integer> getNeighbors(SimpleMatrix query)
  {
    MaxHeap<Integer> heap = tree.findNearestNeighbors(query.getMatrix().getData(), k, distanceFunction);
    List<Integer> neighbors = new ArrayList<Integer>();

    for (int i = 0; i < k; i++)
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
      double[] location = new double[input_dimension];
      for (int j = 0; j < input_dimension; j++)
      {
        location[j] = dataInput.get(i, j);
      }
      tree.addPoint(location, i);
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
}
