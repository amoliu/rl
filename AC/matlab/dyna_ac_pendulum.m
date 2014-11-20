function [critic, actor, cr, rmse] = dyna_ac_pendulum(episodes, steps_per_episode)
%DYNA_AC_PENDULUM Runs the dyna algorithim on the pendulum swing-up.
%   DYNA_AC_PENDULUM(E, S) learns during E episodes,
%   doing S model steps per real step.
%
%   C = DYNA_AC_PENDULUM(...) return a handle to the Critic
%   [C, A] = DYNA_AC_PENDULUM(...) also returns a handle to the Actor
%   [C, A, CR] = DYNA_AC_PENDULUM(...) also returns the learning curve.
%   [C, A, CR, E] = DYNA_AC_PENDULUM(...) also returns the error curve.
%
%   EXAMPLES:
%      [critic, actor, cr, rmse] = dyna_ac_pendulum(100, 10);
%  
%   AUTHOR:
%       Bruno Costa <doravante2@gmail.com>

    % Initialize environment
    spec = env_mops_sim('init');
    
    % Normalization factor used in observations
    norm_factor   = [ pi/10, pi ];
    
    javaSpec = br.ufrj.ppgi.rl.Specification;

    javaSpec.setActorAlpha(0.01);
    javaSpec.setActorMemory(5000);
    javaSpec.setActorNeighbors(20)
    javaSpec.setActorMin(spec.action_min);
    javaSpec.setActorMax(spec.action_max);
    
    javaSpec.setCriticInitialValue(0);
    javaSpec.setCriticAlpha(0.3);
    javaSpec.setCriticMemory(4000);
    javaSpec.setCriticNeighbors(10);

    javaSpec.setObservationDimensions(spec.observation_dims);
    javaSpec.setActionDimensions(spec.action_dims);

    javaSpec.setLamda(0.65);
    javaSpec.setGamma(0.97);
    javaSpec.setSd(1.0);  
    
    javaSpec.setProcessModelMemory(300);
    javaSpec.setProcessModelNeighbors(10);
    javaSpec.setObservationMinValue(spec.observation_min ./ norm_factor);
    javaSpec.setObservationMaxValue(spec.observation_max ./ norm_factor);
    
    javaSpec.setProcessModelCrossLimit(10);
    javaSpec.setProcessModelUpperBound([20 0]);
    javaSpec.setProcessModelThreshold(0.5);
    
    javaSpec.setProcessModelStepsPerEpisode(steps_per_episode);
    javaSpec.setProcessModelCriticAlpha(javaSpec.getCriticAlpha()/1500);
    javaSpec.setProcessModelActorAplha(javaSpec.getActorAlpha()/1500);
    javaSpec.setProcessModelGamma(0.67);
       
    agent = br.ufrj.ppgi.rl.ac.DynaActorCritic;
    agent.init(javaSpec);
    
    [critic, actor, cr, rmse] = learn(episodes, norm_factor, agent); 
end