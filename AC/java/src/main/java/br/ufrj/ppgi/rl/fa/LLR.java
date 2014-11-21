package br.ufrj.ppgi.rl.fa;

public class LLR extends LWR
{
  private static final long serialVersionUID = 6458365029994146793L;

  public LLR(int size, int input_dimensions, int output_dimensions, int k, int valuesToRebuildTree)
  {
    super(size, input_dimensions, output_dimensions, k, valuesToRebuildTree);
    setWeightFunction(new ConstantFunction());
  }

  public LLR(int size, int input_dimensions, int output_dimensions, int k, double initial_value, int valuesToRebuildTree)
  {
    super(size, input_dimensions, output_dimensions, k, initial_value, valuesToRebuildTree);
    setWeightFunction(new ConstantFunction());
  }

  public LLR(int size, int input_dimensions, int output_dimensions, int k, double initial_value, double tikhonov,
             double gamma)
  {
    super(size, input_dimensions, output_dimensions, k, initial_value, tikhonov, gamma, k);
    setWeightFunction(new ConstantFunction());
  }

}
