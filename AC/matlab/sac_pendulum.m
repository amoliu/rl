function [critic, actor, cr, episodes] = sac_pendulum(varargin)
%SAC_PENDULUM Runs the standard ac algorithim on the pendulum swing-up.
%   SAC_PENDULUM(E) learns during E episodes
%
%   SAC_PENDULUM(..., 'verbose', true) sets the output to verbose
%   See LEARN for more params.
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
    expectedModes = {'episode','performance'};
    p.addParameter('mode','episode',...
                 @(x) any(validatestring(x,expectedModes)));
    
    p.addOptional('episodes', 100, @isnumeric);
       
    p.addOptional('performance', -900, @isnumeric);
    p.addOptional('trialsInARow', 3, @isnumeric);
    
    p.addOptional('verbose', false, @islogical);
    p.addOptional('figure', false, @islogical);
    p.parse(varargin{:});
    args = p.Results;
    
    % Initialize environment
    spec = env_mops_sim('init');
    
    % Normalization factor used in observations
    norm_factor   = [ pi/10, pi ];
    
    javaSpec = br.ufrj.ppgi.rl.Specification;

    javaSpec.setActorAlpha(0.03);
    javaSpec.setActorMemory(2000);
    javaSpec.setActorNeighbors(10);
    javaSpec.setActorMin(spec.action_min);
    javaSpec.setActorMax(spec.action_max);
    javaSpec.setActorValuesToRebuildTree(5);
    
    javaSpec.setCriticAlpha(0.2);
    javaSpec.setCriticMemory(2000);
    javaSpec.setCriticNeighbors(20);
    javaSpec.setCriticMin(-3000);
    javaSpec.setCriticMax(0);
    javaSpec.setCriticValuesToRebuildTree(5);

    javaSpec.setObservationDimensions(spec.observation_dims);
    javaSpec.setActionDimensions(spec.action_dims);

    javaSpec.setExplorationRate(1);
    javaSpec.setLamda(0.65);
    javaSpec.setGamma(0.97);
    javaSpec.setSd(1.0);
    
    agent = br.ufrj.ppgi.rl.ac.StandardActorCritic;
    agent.init(javaSpec);
    
    [critic, actor, cr, ~, episodes] = learn('mops_sim', norm_factor, agent, args);
end