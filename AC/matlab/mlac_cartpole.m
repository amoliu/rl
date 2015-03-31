function [critic, actor, cr, rmse, model, episodes] = mlac_cartpole(varargin)
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
    norm_factor = [1/8, 1/4, pi/10, 1];
    % norm_factor = [1, 1, 1, 1];
    
    javaSpec = br.ufrj.ppgi.rl.Specification;

    javaSpec.setActorAlpha(0.006);
    javaSpec.setActorMemory(9000);
    javaSpec.setActorNeighbors(20);
    javaSpec.setActorMin(spec.action_min);
    javaSpec.setActorMax(spec.action_max);
    javaSpec.setActorValuesToRebuildTree(1);
    
    javaSpec.setCriticAlpha(0.03);
    javaSpec.setCriticMemory(9000);
    javaSpec.setCriticNeighbors(20);
    javaSpec.setCriticMin(-12000);
    javaSpec.setCriticMax(0);
    javaSpec.setCriticValuesToRebuildTree(1);

    javaSpec.setObservationDimensions(spec.observation_dims);
    javaSpec.setActionDimensions(spec.action_dims);

    javaSpec.setExplorationRate(1);
    javaSpec.setLamda(0.67);
    javaSpec.setGamma(0.96);
    javaSpec.setSd(1.0);
    
    javaSpec.setProcessModelMemory(4000);
    javaSpec.setProcessModelNeighbors(20);
    javaSpec.setProcessModelValuesToRebuildTree(1);
    javaSpec.setObservationMinValue(spec.observation_min ./ norm_factor);
    javaSpec.setObservationMaxValue(spec.observation_max ./ norm_factor);
    javaSpec.setProcessModelAnglePosition(2);
    
    javaSpec.setProcessModelCrossLimit(10);
    javaSpec.setProcessModelUpperBound([0 0 20 0]);
    javaSpec.setProcessModelThreshold(0.5);
    
    agent = br.ufrj.ppgi.rl.ac.MLAC;
    agent.init(javaSpec);
    
    [critic, actor, cr, rmse, episodes] = learn('cartpole', norm_factor, agent, args);
    model = agent.getProcessModel();
end