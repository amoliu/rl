function [critic, actor, cr] = sac_cartpole(varargin)
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

    % Initialize environment
    opts.swingup = 1;
    spec = env_cartpole('init', opts);
    
    % Normalization factor used in observations
    norm_factor = [1/4, 1/2, 1/4, 1/7];
    % norm_factor = [1, 1, 1, 1];
    
    javaSpec = br.ufrj.ppgi.rl.Specification;

    javaSpec.setActorAlpha(0.05);
    javaSpec.setActorMemory(8000);
    javaSpec.setActorNeighbors(30)
    javaSpec.setActorMin(spec.action_min);
    javaSpec.setActorMax(spec.action_max);
    javaSpec.setActorValuesToRebuildTree(30);
    
    javaSpec.setCriticInitialValue(0);
    javaSpec.setCriticAlpha(0.15);
    javaSpec.setCriticMemory(8000);
    javaSpec.setCriticNeighbors(20);
    javaSpec.setCriticValuesToRebuildTree(20);

    javaSpec.setObservationDimensions(spec.observation_dims);
    javaSpec.setActionDimensions(spec.action_dims);

    javaSpec.setLamda(0.65);
    javaSpec.setGamma(0.97);
    javaSpec.setSd(1);  
    
    agent = br.ufrj.ppgi.rl.ac.StandardActorCritic;
    agent.init(javaSpec);
    
    [critic, actor, cr] = learn('cartpole', norm_factor, agent, args);
end