package br.ufrj.ppgi.rl.fa;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.ejml.data.DenseMatrix64F;
import org.ejml.factory.DecompositionFactory;
import org.ejml.interfaces.decomposition.CholeskyDecomposition;
import org.ejml.ops.NormOps;
import org.ejml.simple.SimpleMatrix;

import ags.utils.dataStructures.MaxHeap;
import ags.utils.dataStructures.trees.thirdGenKD.DistanceFunction;
import ags.utils.dataStructures.trees.thirdGenKD.KdTree;
import ags.utils.dataStructures.trees.thirdGenKD.SquareEuclideanDistanceFunction;

public class LLR
{
  private static final double                   DEFAUL_TIKHONOV = 0.000001d;

  private static final double                   DEFAUL_GAMMA    = 0.9d;

  private static final double                   BIAS            = 1d;

  protected SimpleMatrix                        data;

  protected double[]                            relevance;

  protected int                                 size;

  private int                                   input_dimension;

  private int                                   output_dimension;

  private int                                   k;

  private SimpleMatrix                          tikhonov;

  private double                                gamma;

  protected int                                 last_llr;

  private double                                initial_value;

  private KdTree<Integer>                       tree;

  private DistanceFunction                      distanceFunction;

  private CholeskyDecomposition<DenseMatrix64F> choleskyDecomposition;

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

    this.data = new SimpleMatrix(size, input_dimensions + output_dimensions);
    this.data.zero();

    this.relevance = new double[size];

    this.last_llr = 0;

    buildKDTree();
    distanceFunction = new SquareEuclideanDistanceFunction();
    choleskyDecomposition = DecompositionFactory.chol(input_dimensions + 1, false);
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
      int posMinRelevance = positionLessRelevant();
      if (rel <= relevance[posMinRelevance])
        return;
    }

    relevance[pos] = rel;
    int col = 0;

    for (int i = 0; i < input.getNumElements(); i++)
    {
      data.set(pos, col, input.get(i));
      col++;
    }

    for (int i = 0; i < output.getNumElements(); i++)
    {
      data.set(pos, col, output.get(i));
      col++;
    }

    tree.addPoint(input.getMatrix().getData(), pos);
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

  public void update()
  {

  }

  public SimpleMatrix query(SimpleMatrix query)
  {
    if (!hasEnoughNeighbors())
    {
      SimpleMatrix result = SimpleMatrix.random(1, output_dimension, 0, 1, new Random());
      result.plus(initial_value);

      return result;
    }

    List<Integer> neighbors = getNeighbors(query);
    return queryForNeighbors(query, neighbors);
  }

  private SimpleMatrix queryForNeighbors(SimpleMatrix query, List<Integer> neighbors)
  {
    SimpleMatrix A = new SimpleMatrix(input_dimension + 1, neighbors.size());
    SimpleMatrix B = new SimpleMatrix(output_dimension, neighbors.size());

    for (int n = 0; n < k; n++)
    {
      Integer pos = neighbors.get(n);

      for (int i = 0; i < input_dimension; i++)
      {
        A.set(i, n, data.get(pos, i));
      }
      A.set(input_dimension, n, BIAS);

      for (int i = 0; i < output_dimension; i++)
      {
        B.set(i, n, data.get(pos, input_dimension + i));
      }
    }

    // Using Cholesky
    // A = U'U
    // inv(A) = inv(U)*inv(U)'

    choleskyDecomposition.decompose(A.mult(A.transpose()).plus(tikhonov).getMatrix());
    DenseMatrix64F U = choleskyDecomposition.getT(null);

    SimpleMatrix iU = new SimpleMatrix(U).invert();
    SimpleMatrix temp_inv = iU.mult(iU.transpose());

    SimpleMatrix X = B.mult(A.transpose()).mult(temp_inv);

    SimpleMatrix queryBias = new SimpleMatrix(1, query.numCols() + 1);
    for (int i = 0; i < query.numCols(); i++)
    {
      queryBias.set(0, i, query.get(0, i));
    }
    queryBias.set(0, query.numCols(), 1);

    return queryBias.mult(X.transpose());
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

      SimpleMatrix query = data.extractMatrix(pos, pos + 1, 0, input_dimension);
      SimpleMatrix predict_value = queryForNeighbors(query, neighbors);

      SimpleMatrix real_value = data.extractMatrix(pos, pos + 1, input_dimension, input_dimension + output_dimension);

      double rel = Math.pow(NormOps.normP2(real_value.minus(predict_value).getMatrix()), 2);

      relevance[pos] = gamma * relevance[pos] + (1 - gamma) * rel;
    }

    SimpleMatrix predict_value = queryForNeighbors(input, neighbors);
    return Math.pow(NormOps.normP2(output.minus(predict_value).getMatrix()), 2);
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
      double[] location = new double[2];
      for (int j = 0; j < input_dimension; j++)
      {
        location[j] = data.get(i, j);
      }
      tree.addPoint(location, i);
    }
  }
}
