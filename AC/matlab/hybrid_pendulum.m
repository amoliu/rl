function [critic, actor, cr, sac_updates, mlac_updates, agent] = hybrid_pendulum(varargin) 
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
    javaSpec.setActorMin(-1.5);
    javaSpec.setActorMax(1.5);
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
    
    javaSpec.setProcessModelMemory(100);
    javaSpec.setProcessModelNeighbors(9);
    javaSpec.setProcessModelValuesToRebuildTree(1);
    javaSpec.setObservationMinValue(spec.observation_min ./ norm_factor);
    javaSpec.setObservationMaxValue(spec.observation_max ./ norm_factor);
    
    javaSpec.setProcessModelCrossLimit(10);
    javaSpec.setProcessModelUpperBound([20 0]);
    javaSpec.setProcessModelThreshold(0.5);
    
    agent = br.ufrj.ppgi.rl.ac.HybridSACMLAC;
    agent.init(javaSpec);
    
    [critic, actor, cr, sac_updates, episodes, ~, mlac_updates] = learn('mops_sim', norm_factor, agent, args);
end