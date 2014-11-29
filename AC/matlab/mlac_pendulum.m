function [critic, actor, cr, rmse, episodes] = mlac_pendulum(varargin)
%MLAC_PENDULUM Runs the MLAC algorithim on the pendulum swing-up.
%   MLAC_PENDULUM(E) learns during E episodes
%
%   MLAC_PENDULUM(..., 'verbose', true) sets the output to verbose
%   See LEARN for more params.
%
%   C = MLAC_PENDULUM(...) return a handle to the Critic
%   [C, A] = MLAC_PENDULUM(...) also returns a handle to the Actor
%   [C, A, CR] = MLAC_PENDULUM(...) also returns the learning curve.
%   [C, A, CR, E] = MLAC_PENDULUM(...) also returns the error curve.
%
%   EXAMPLES:
%      [critic, actor, cr, rmse] = mlac_pendulum(100);
%  
%   AUTHOR:
%       Bruno Costa <doravante2@gmail.com>

    % Argument parsing
    p = inputParser;
    expectedModes = {'episode','performance'};
    p.addParameter('mode','episode',...
                 @(x) any(validatestring(x,expectedModes)));
    
    p.addOptional('episodes', 100, @isnumeric);
    
    p.addOptional('performance', -900, @isnumeric);
    p.addOptional('trialsInARow', 3, @isnumeric);
    
    p.addOptional('verbose', false, @islogical);
    p.parse(varargin{:});
    args = p.Results;
    
    % Initialize simulation
    spec = env_mops_sim('init');
    
    % Normalization factor used in observations
    norm_factor   = [ pi/10, pi ];
    
    javaSpec = br.ufrj.ppgi.rl.Specification;

    javaSpec.setActorAlpha(0.01);
    javaSpec.setActorMemory(5000);
    javaSpec.setActorNeighbors(20);
    javaSpec.setActorMin(spec.action_min);
    javaSpec.setActorMax(spec.action_max);
    javaSpec.setActorValuesToRebuildTree(20);
    
    javaSpec.setCriticInitialValue(0);
    javaSpec.setCriticAlpha(0.3);
    javaSpec.setCriticMemory(4000);
    javaSpec.setCriticNeighbors(10);
    javaSpec.setCriticValuesToRebuildTree(10);

    javaSpec.setObservationDimensions(spec.observation_dims);
    javaSpec.setActionDimensions(spec.action_dims);

    javaSpec.setExplorationRate(2);
    javaSpec.setLamda(0.65);
    javaSpec.setGamma(0.97);
    javaSpec.setSd(2.0);
    
    javaSpec.setProcessModelMemory(600);
    javaSpec.setProcessModelNeighbors(10);
    javaSpec.setProcessModelValuesToRebuildTree(1);
    javaSpec.setObservationMinValue(spec.observation_min ./ norm_factor);
    javaSpec.setObservationMaxValue(spec.observation_max ./ norm_factor);
    
    javaSpec.setProcessModelCrossLimit(10);
    javaSpec.setProcessModelUpperBound([20 0]);
    javaSpec.setProcessModelThreshold(0.5);
       
    agent = br.ufrj.ppgi.rl.ac.MLAC;
    agent.init(javaSpec);
    
    [critic, actor, cr, rmse, episodes] = learn('mops_sim', norm_factor, agent, args);
end