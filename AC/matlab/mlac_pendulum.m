function [critic, actor, cr, rmse] = mlac_pendulum(episodes)
%MLAC_AC_PENDULUM Runs the MLAC algorithim on the pendulum swing-up.
%   MLAC_AC_PENDULUM(E) learns during E episodes
%
%   C = MLAC_AC_PENDULUM(...) return a handle to the Critic
%   [C, A] = MLAC_AC_PENDULUM(...) also returns a handle to the Actor
%   [C, A, CR] = MLAC_AC_PENDULUM(...) also returns the learning curve.
%   [C, A, CR, E] = MLAC_AC_PENDULUM(...) also returns the error curve.
%
%   EXAMPLES:
%      [critic, actor, cr, rmse] = mlac_pendulum(100);
%  
%   AUTHOR:
%       Bruno Costa <doravante2@gmail.com>

    % Initialize simulation
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
       
    agent = br.ufrj.ppgi.rl.ac.MLAC;
    agent.init(javaSpec);
    
    [critic, actor, cr, rmse] = learn(episodes, norm_factor, agent);
end