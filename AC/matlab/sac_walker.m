function [critic, actor, cr] = sac_walker(varargin)
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
    spec = env_walker('init');
    
    % Normalization factor used in observations
    norm_factor = [pi/10, pi/10, pi/10, pi/10];
    
    javaSpec = br.ufrj.ppgi.rl.Specification;

    javaSpec.setActorAlpha(0.05);
    javaSpec.setActorMemory(5000);
    javaSpec.setActorNeighbors(13);
    javaSpec.setActorMin(spec.action_min);
    javaSpec.setActorMax(spec.action_max);
    javaSpec.setActorValuesToRebuildTree(1);
    
    javaSpec.setCriticInitialValue(0);
    javaSpec.setCriticAlpha(0.25);
    javaSpec.setCriticMemory(6000);
    javaSpec.setCriticNeighbors(16);
    javaSpec.setCriticValuesToRebuildTree(1);

    javaSpec.setObservationDimensions(spec.observation_dims);
    javaSpec.setActionDimensions(spec.action_dims);

    javaSpec.setExplorationRate(3);
    javaSpec.setLamda(0.65);
    javaSpec.setGamma(0.92);
    javaSpec.setSd(0.6);
    
    agent = br.ufrj.ppgi.rl.ac.StandardActorCritic;
    agent.init(javaSpec);
    
    [critic, actor, cr] = learn('walker', norm_factor, agent, args);
end