package br.ufrj.ppgi.rl.fa;

public class LLR extends LWR
{
  private static final long serialVersionUID = 590628262223725666L;

  public LLR(int size, int input_dimensions, int output_dimensions, int k)
  {
    super(size, input_dimensions, output_dimensions, k);
    setWeightFunction(new ConstantFunction());
  }

  public LLR(int size, int input_dimensions, int output_dimensions, int k, double initial_value)
  {
    super(size, input_dimensions, output_dimensions, k, initial_value);
    setWeightFunction(new ConstantFunction());
  }

  public LLR(int size, int input_dimensions, int output_dimensions, int k, double initial_value, double tikhonov,
             double gamma)
  {
    super(size, input_dimensions, output_dimensions, k, initial_value, tikhonov, gamma);
    setWeightFunction(new ConstantFunction());
  }

}
