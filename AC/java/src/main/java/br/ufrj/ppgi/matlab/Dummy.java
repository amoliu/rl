package br.ufrj.ppgi.matlab;

import java.io.Serializable;

/**
 * Exemplify how to interact matlab with java.
 * <p>
 * First, build this into a jar and load it in matlab using
 * javaaddpath('/home/bruno/Documentos/rl/AC/java/build/matlab/projeto.jar');
 * <p>
 * Then, build a Dummy object dummy = br.ufrj.ppgi.matlab.Dummy([1 2; 3 4]) and
 * call any method (i.e. dummy.getNumRows). The matrix to double[][] conversion
 * is automatic.
 * 
 */
public class Dummy implements Serializable
{
  private static final long serialVersionUID = 7022568782725853420L;

  private double[][]        data;

  public Dummy(double[][] data)
  {
    this.data = data;
  }

  public int getNumRows()
  {
    return data.length;
  }

  public int getNumCols()
  {
    return data[0].length;
  }

  public double[][] getData()
  {
    return data;
  }

  public void setData(double[][] data)
  {
    this.data = data;
  }
}
