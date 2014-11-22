package br.ufrj.ppgi.rl;

import br.ufrj.ppgi.rl.fa.LLR;

public class ProcessModelLLR extends ProcessModelLWR
{
  private static final long serialVersionUID = -2725915438974691683L;

  public void init(Specification specification)
  {
    super.init(specification);

    lwr = new LLR(specification.getProcessModelMemory(), getInputDimension(), getOutputDimension(),
                  specification.getProcessModelNeighbors(), specification.getProcessModelValuesToRebuildTree());

  }
}
