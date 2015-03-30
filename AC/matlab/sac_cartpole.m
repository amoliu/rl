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
    p.addOptional('figure', false, @islogical);
    p.parse(varargin{:});
    args = p.Results;

    % Initialize environment
    opts.swingup = 1;
    spec = env_cartpole('init', opts);
    
    % Normalization factor used in observations
    norm_factor = [1/12, 1, pi/10, pi];
    % norm_factor = [1, 1, 1, 1];
    
    javaSpec = br.ufrj.ppgi.rl.Specification;

    javaSpec.setActorAlpha(0.03);
    javaSpec.setActorMemory(8000);
    javaSpec.setActorNeighbors(15);
    javaSpec.setActorMin(spec.action_min);
    javaSpec.setActorMax(spec.action_max);
    javaSpec.setActorValuesToRebuildTree(1);
    
    javaSpec.setCriticAlpha(0.3);
    javaSpec.setCriticMemory(8500);
    javaSpec.setCriticNeighbors(20);
    javaSpec.setCriticMin(-12000);
    javaSpec.setCriticMax(0);
    javaSpec.setCriticValuesToRebuildTree(1);

    javaSpec.setObservationDimensions(spec.observation_dims);
    javaSpec.setActionDimensions(spec.action_dims);

    javaSpec.setExplorationRate(1);
    javaSpec.setLamda(0.65);
    javaSpec.setGamma(0.99);
    javaSpec.setSd(1.0);
    
    agent = br.ufrj.ppgi.rl.ac.StandardActorCritic;
    agent.init(javaSpec);
    
    [critic, actor, cr] = learn('cartpole', norm_factor, agent, args);
end