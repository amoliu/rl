package br.ufrj.ppgi.rl;

public class Specification {

	private float lamda;

	private float gamma;

	private int steps;

	private float sd;

	public float getLamda() {
		return lamda;
	}

	public void setLamda(float lamda) {
		this.lamda = lamda;
	}

	public float getGamma() {
		return gamma;
	}

	public void setGamma(float gamma) {
		this.gamma = gamma;
	}

	public int getSteps() {
		return steps;
	}

	public void setSteps(int steps) {
		this.steps = steps;
	}

	public float getSd() {
		return sd;
	}

	public void setSd(float sd) {
		this.sd = sd;
	}
}
