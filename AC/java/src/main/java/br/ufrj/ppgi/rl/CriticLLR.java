package br.ufrj.ppgi.rl;

import java.io.Serializable;
import java.util.ArrayList;

import org.ejml.simple.SimpleMatrix;

import br.ufrj.ppgi.matlab.EJMLMatlabUtils;
import br.ufrj.ppgi.rl.fa.LWR;
import br.ufrj.ppgi.rl.fa.LWRQueryVO;

public class CriticLLR implements Serializable
{
  private static final long serialVersionUID = -8821872302477032620L;

  protected LWR             llr;

  private Specification     specification;

  protected SimpleMatrix    eligibilityTrace;

  public void init(Specification specification)
  {
    this.specification = specification;

    llr = LWR.createLLR().setSize(specification.getCriticMemory())
             .setInputDimension(specification.getObservationDimensions()).setOutputDimension(1)
             .setK(specification.getCriticNeighbors()).setInitialValue(this.specification.getCriticInitialValue())
             .setValuesToRebuildTheTree(specification.getCriticValuesToRebuildTree());

    if (specification.getCriticMemoryManagement() != null)
    {
      llr.setMemoryManagement(specification.getCriticMemoryManagement());
    }

    resetEligibilityTrace();
  }

  public void resetEligibilityTrace()
  {
    eligibilityTrace = new SimpleMatrix(specification.getCriticMemory(), 1);
    eligibilityTrace.zero();
  }

  public double update(SimpleMatrix lastObservation, SimpleMatrix lastAction, double reward, SimpleMatrix observation)
  {
    LWRQueryVO valueFunction = llr.query(observation);
    LWRQueryVO oldValueFunction = llr.query(lastObservation);

    double tdError = reward + specification.getGamma() * valueFunction.getResult().get(0)
                     - oldValueFunction.getResult().get(0);

    // Add to LLR
    int insertIndex = llr.add(lastObservation, oldValueFunction.getResult());

    SimpleMatrix update = new SimpleMatrix(specification.getCriticMemory(), 1);

    // Update ET
    for (int i = 0; i < eligibilityTrace.getNumElements(); i++)
    {
      eligibilityTrace.set(i, eligibilityTrace.get(i) * specification.getLamda() * specification.getGamma());
      update.set(i, eligibilityTrace.get(i) * specification.getCriticAlpha() * tdError);
    }

    for (Integer neighbor : oldValueFunction.getNeighbors())
    {
      eligibilityTrace.set(neighbor, 1);
      update.set(neighbor, specification.getCriticAlpha() * tdError);
    }

    // set ET of inserted sample to 1
    if (insertIndex != -1)
    {
      eligibilityTrace.set(insertIndex, 1);
      update.set(insertIndex, specification.getCriticAlpha() * tdError);
    }

    llr.update(update);

    return tdError;
  }

  public double update(SimpleMatrix lastObservation, SimpleMatrix lastAction, double reward, SimpleMatrix observation,
                       double alpha, double gamma)
  {
    LWRQueryVO valueFunction = llr.query(observation);
    LWRQueryVO oldValueFunction = llr.query(lastObservation);

    double tdError = reward + gamma * valueFunction.getResult().get(0) - oldValueFunction.getResult().get(0);

    // Add to LLR
    int insertIndex = llr.add(lastObservation, oldValueFunction.getResult().plus(tdError));

    ArrayList<Integer> updatedPoints = oldValueFunction.getNeighbors();
    // also update the insertedIndex
    if (insertIndex != -1 && !updatedPoints.contains(insertIndex))
    {
      updatedPoints.add(insertIndex);
    }

    SimpleMatrix upperLimit = new SimpleMatrix(1, 1);
    upperLimit.set(0);
    
    SimpleMatrix lowerLimit = new SimpleMatrix(1, 1);
    lowerLimit.set(Double.NEGATIVE_INFINITY);
    
    llr.update(updatedPoints, alpha * tdError, upperLimit, lowerLimit);

    return tdError;
  }

  public LWRQueryVO query(SimpleMatrix query)
  {
    return llr.query(query);
  }

  public LWR getLLR()
  {
    return llr;
  }

  public double[][] getEligibilityTrace()
  {
    return EJMLMatlabUtils.getMatlabMatrixFromSimpleMatrix(eligibilityTrace);
  }
}
