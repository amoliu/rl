package br.ufrj.ppgi.rl;

import java.io.Serializable;

import org.ejml.simple.SimpleMatrix;

import br.ufrj.ppgi.matlab.EJMLMatlabUtils;
import br.ufrj.ppgi.rl.fa.LLR;
import br.ufrj.ppgi.rl.fa.LLRQueryVO;

public class CriticLLR implements Serializable
{
  private static final long serialVersionUID = -3959243196946425444L;

  protected LLR             llr;

  private Specification     specification;

  protected SimpleMatrix    eligibilityTrace;

  public void init(Specification specification)
  {
    this.specification = specification;

    llr = new LLR(specification.getCriticMemory(), specification.getObservationDimensions(), 1,
                  specification.getCriticNeighbors(), this.specification.getCriticInitialValue());

    resetEligibilityTrace();
  }

  public void resetEligibilityTrace()
  {
    eligibilityTrace = new SimpleMatrix(specification.getCriticMemory(), 1);
    eligibilityTrace.zero();
  }

  public double update(SimpleMatrix lastObservation, SimpleMatrix lastAction, double reward, SimpleMatrix observation)
  {
    LLRQueryVO valueFunction = llr.query(observation);
    LLRQueryVO oldValueFunction = llr.query(lastObservation);

    double tdError = reward + specification.getGamma() * valueFunction.getResult().get(0)
                     - oldValueFunction.getResult().get(0);

    // Add to LLR
    llr.add(lastObservation, oldValueFunction.getResult());

    // Update ET
    for (int i = 0; i < eligibilityTrace.getNumElements(); i++)
    {
      eligibilityTrace.set(i, eligibilityTrace.get(i) * specification.getLamda() * specification.getGamma());
    }

    for (Integer neighbor : oldValueFunction.getNeighbors())
    {
      eligibilityTrace.set(neighbor, 1);
    }

    SimpleMatrix update = new SimpleMatrix(specification.getCriticMemory(), 1);
    update.set(specification.getCriticAlpha() * tdError);

    llr.update(eligibilityTrace.elementMult(update));

    return tdError;
  }

  public double update(SimpleMatrix lastObservation, SimpleMatrix lastAction, double lastValueFunction, double reward,
                       SimpleMatrix observation)
  {
    double valueFunction = llr.query(observation).getResult().get(0);

    // Add to LLR
    int pos = llr.add(lastObservation, new SimpleMatrix(new double[][] { { lastValueFunction } }));
    LLRQueryVO oldResult = llr.query(lastObservation);

    double tdError = reward + specification.getGamma() * valueFunction - lastValueFunction;

    // Update ET
    for (int i = 0; i < eligibilityTrace.getNumElements(); i++)
    {
      eligibilityTrace.set(i, eligibilityTrace.get(i) * specification.getLamda() * specification.getGamma());
    }

    for (Integer neighbor : oldResult.getNeighbors())
    {
      eligibilityTrace.set(neighbor, 1);
    }
    eligibilityTrace.set(pos, 1);

    SimpleMatrix update = new SimpleMatrix(specification.getCriticMemory(), 1);
    update.set(specification.getCriticAlpha() * tdError);

    llr.update(eligibilityTrace.elementMult(update));

    return valueFunction + specification.getCriticAlpha() * tdError;
  }

  public LLRQueryVO query(SimpleMatrix query)
  {
    return llr.query(query);
  }

  public LLR getLLR()
  {
    return llr;
  }

  public double[][] getEligibilityTrace()
  {
    return EJMLMatlabUtils.getMatlabMatrixFromSimpleMatrix(eligibilityTrace);
  }
}
