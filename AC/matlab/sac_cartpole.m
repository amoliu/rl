function [critic, actor, cr] = sac_cartpole(episodes, varargin)
    % Argument parsing
    p = inputParser;
    p.addOptional('verbose', 0);
    p.parse(varargin{:});
    args = p.Results;

    % Initialize environment
    spec = env_cartpole('init');
    
    % Normalization factor used in observations
    norm_factor   = [ 1, 1, 1, 1 ];
    
    javaSpec = br.ufrj.ppgi.rl.Specification;

    javaSpec.setActorAlpha(0.01);
    javaSpec.setActorMemory(10000);
    javaSpec.setActorNeighbors(20)
    javaSpec.setActorMin(spec.action_min);
    javaSpec.setActorMax(spec.action_max);
    javaSpec.setActorValuesToRebuildTree(20);
    
    javaSpec.setCriticInitialValue(0);
    javaSpec.setCriticAlpha(0.3);
    javaSpec.setCriticMemory(10000);
    javaSpec.setCriticNeighbors(10);
    javaSpec.setCriticValuesToRebuildTree(10)

    javaSpec.setObservationDimensions(spec.observation_dims);
    javaSpec.setActionDimensions(spec.action_dims);

    javaSpec.setLamda(0.65);
    javaSpec.setGamma(0.97);
    javaSpec.setSd(1.0);  
    
    agent = br.ufrj.ppgi.rl.ac.StandardActorCritic;
    agent.init(javaSpec);
    
    [critic, actor, cr] = learn('cartpole', episodes, norm_factor, agent, args);
end