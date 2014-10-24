package br.ufrj.ppgi.rl.ac;

import br.ufrj.ppgi.rl.Actor;
import br.ufrj.ppgi.rl.Critic;
import br.ufrj.ppgi.rl.Specification;
import br.ufrj.ppgi.rl.environment.InvertedPendulum;

public class StandardActorCritic {

	private Actor actor;
	
	private Critic critic;
	
	private InvertedPendulum invertedPendulum;
	
	private Specification specification;

	public StandardActorCritic() {
		specification = new Specification();
		specification.setGamma(0.97f);
		specification.setLamda(0.67f);
		specification.setSteps(100);
		specification.setSd(1.0f);
		
		invertedPendulum = new InvertedPendulum();
		
		actor = new Actor();
		critic = new Critic();
	}
}
