package br.ufrj.ppgi.rl;

import java.io.Serializable;

import org.ejml.simple.SimpleMatrix;

public class ActionVO implements Serializable
{
  private static final long serialVersionUID = 8290227276378409525L;

  private SimpleMatrix      action;

  private SimpleMatrix      policyAction;

  public ActionVO(SimpleMatrix action)
  {
    this.action = action;
    this.policyAction = null;
  }

  public ActionVO(SimpleMatrix action, SimpleMatrix policyAction)
  {
    this.action = action;
    this.policyAction = policyAction;
  }

  public SimpleMatrix getAction()
  {
    return action;
  }

  public void setAction(SimpleMatrix action)
  {
    this.action = action;
  }

  public SimpleMatrix getPolicyAction()
  {
    return policyAction;
  }

  public void setPolicyAction(SimpleMatrix policyAction)
  {
    this.policyAction = policyAction;
  }
}
