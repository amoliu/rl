function [critic, actor, cr, rmse, model, episodes] = dyna_mlac_cartpole(varargin)
    % Argument parsing
    p = inputParser;
    expectedModes = {'episode','performance'};
    p.addParameter('mode','episode',...
                 @(x) any(validatestring(x,expectedModes)));
    
    p.addParameter('steps', 2, @isnumeric);
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
    norm_factor = [1/8, 1/2, pi/10, pi];
    % norm_factor = [1, 1, 1, 1];
    
    javaSpec = br.ufrj.ppgi.rl.Specification;

    javaSpec.setActorAlpha(0.10);
    javaSpec.setActorMemory(4000);
    javaSpec.setActorNeighbors(15);
    javaSpec.setActorMin(spec.action_min);
    javaSpec.setActorMax(spec.action_max);
    javaSpec.setActorValuesToRebuildTree(1);
    
    javaSpec.setCriticInitialValue(0);
    javaSpec.setCriticAlpha(0.25);
    javaSpec.setCriticMemory(5000);
    javaSpec.setCriticNeighbors(20);
    javaSpec.setCriticValuesToRebuildTree(1);

    javaSpec.setObservationDimensions(spec.observation_dims);
    javaSpec.setActionDimensions(spec.action_dims);

    javaSpec.setProcessModelExplorationRate(3);
    javaSpec.setExplorationRate(1);
    javaSpec.setLamda(0.67);
    javaSpec.setGamma(0.97);
    javaSpec.setSd(10.0);
    javaSpec.setProcessModelSd(3.0);   
    
    javaSpec.setProcessModelMemory(100);
    javaSpec.setProcessModelNeighbors(10);
    javaSpec.setProcessModelValuesToRebuildTree(1);
    javaSpec.setObservationMinValue(spec.observation_min ./ norm_factor);
    javaSpec.setObservationMaxValue(spec.observation_max ./ norm_factor);
    
    javaSpec.setProcessModelCrossLimit(1000);
    javaSpec.setProcessModelUpperBound([0 0 0 0]);
    javaSpec.setProcessModelThreshold(-2.0);
    
    javaSpec.setProcessModelStepsPerEpisode(args.steps);
    javaSpec.setProcessModelCriticAlpha(javaSpec.getCriticAlpha()/1000);
    javaSpec.setProcessModelActorAplha(javaSpec.getActorAlpha()/1000);
    javaSpec.setProcessModelGamma(0.97);
    javaSpec.setProcessModelIterationsWithoutLearning(0);
    javaSpec.setRewardRange((spec.reward_max - spec.reward_min)/100);
    
    agent = br.ufrj.ppgi.rl.ac.DynaMLAC;
    agent.init(javaSpec);
    
    [critic, actor, cr, rmse, episodes] = learn('cartpole', norm_factor, agent, args);
    model = agent.getProcessModel();
end