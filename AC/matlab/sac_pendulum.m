function [critic, actor, cr] = sac_pendulum(episodes, varargin)
%SAC_PENDULUM Runs the standard ac algorithim on the pendulum swing-up.
%   SAC_PENDULUM(E) learns during E episodes
%
%   SAC_PENDULUM(..., 'verbose', 1) sets the output to verbose
%
%   C = SAC_PENDULUM(...) return a handle to the Critic
%   [C, A] = SAC_PENDULUM(...) also returns a handle to the Actor
%   [C, A, CR] = SAC_PENDULUM(...) also returns the learning curve.
%
%   EXAMPLES:
%      [critic, actor, cr] = sac_pendulum(100);
%  
%   AUTHOR:
%       Bruno Costa <doravante2@gmail.com>

    % Argument parsing
    p = inputParser;
    p.addOptional('verbose', 0);
    p.parse(varargin{:});
    args = p.Results;

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
    javaSpec.setActorValuesToRebuildTree(20);
    
    javaSpec.setCriticInitialValue(0);
    javaSpec.setCriticAlpha(0.3);
    javaSpec.setCriticMemory(4000);
    javaSpec.setCriticNeighbors(10);
    javaSpec.setCriticValuesToRebuildTree(10)

    javaSpec.setObservationDimensions(spec.observation_dims);
    javaSpec.setActionDimensions(spec.action_dims);

    javaSpec.setLamda(0.65);
    javaSpec.setGamma(0.97);
    javaSpec.setSd(1.0);  
    
    agent = br.ufrj.ppgi.rl.ac.StandardActorCritic;
    agent.init(javaSpec);
    
    [critic, actor, cr] = learn(episodes, norm_factor, agent, args);
end