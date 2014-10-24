package br.ufrj.ppgi.rl.environment;

import org.ejml.data.FixedMatrix2_64F;

public class InvertedPendulum
{

  // Physical constants
  private static final float CONS_GRAVITY = 9.81f;

  // Inverted pendulum constants
  private static final float PEND_INERT   = 1.91e-4f; // Inertia
  private static final float PEND_MASS    = 5.5e-2f; // Mass
  private static final float PEND_LENGTH  = 4.2e-2f; // Pendulum length
  private static final float PEND_VDAMP   = 3e-6f;   // Viscous damping
  private static final float PEND_TORQUE  = 5.36e-2f; // Torque constant
  private static final float PEND_ROTOR   = 9.5f;    // Rotor resistance

  private FixedMatrix2_64F   state;

  private float              reward;

  public InvertedPendulum()
  {
    state = new FixedMatrix2_64F(0, 0);
    reward = 0f;
  }

  public void step(float action)
  {
    // gsl_vector_set(out,0,gsl_vector_get(in,1));
    // gsl_vector_set(out,1,(PEND_MASS*CONS_GRAVITY*PEND_LENGTH*sin(gsl_vector_get(in,0))
    // - (PEND_VDAMP + pow(PEND_TORQUE,2)/PEND_ROTOR)*gsl_vector_get(in,1) +
    // PEND_TORQUE/PEND_ROTOR*gsl_vector_get(in,2))/PEND_INERT);
    // gsl_vector_set(out,2,0);
  }

  public FixedMatrix2_64F getState()
  {
    return state;
  }

  public void setState(FixedMatrix2_64F state)
  {
    this.state = state;
  }

  public float getReward()
  {
    return reward;
  }

  public void setReward(float reward)
  {
    this.reward = reward;
  }
}
