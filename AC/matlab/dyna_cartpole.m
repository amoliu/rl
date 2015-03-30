function [critic, actor, cr, rmse, model, skiped, distance, episodes] = dyna_cartpole(varargin)
%DYNA_PENDULUM Runs the dyna algorithim on the pendulum swing-up.
%   DYNA_PENDULUM(E, S) learns during E episodes,
%   doing S model steps per real step.
%
%   DYNA_PENDULUM(..., 'verbose', true) sets the output to verbose
%   DYNA_PENDULUM(..., 'steps', S) sets the total steps per iteration
%   See LEARN for more params.
%
%   C = DYNA_PENDULUM(...) return a handle to the Critic
%   [C, A] = DYNA_PENDULUM(...) also returns a handle to the Actor
%   [C, A, CR] = DYNA_PENDULUM(...) also returns the learning curve.
%   [C, A, CR, E] = DYNA_PENDULUM(...) also returns the error curve.
%
%   EXAMPLES:
%      [critic, actor, cr, rmse] = dyna_pendulum(100, 10);
%  
%   AUTHOR:
%       Bruno Costa <doravante2@gmail.com>

    % Argument parsing
    p = inputParser;
    expectedModes = {'episode','performance'};
    p.addParameter('mode','episode',...
                 @(x) any(validatestring(x,expectedModes)));
             
    p.addParameter('steps', 2, @isnumeric);
    p.addOptional('episodes', 100, @isnumeric);
        
    p.addOptional('alpha', 1, @isnumeric);
    p.addOptional('explorationRate', 1, @isnumeric);
        
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
    norm_factor = [1/10, 1, pi/5, pi];
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
    javaSpec.setProcessModelExplorationRate(args.explorationRate);
    javaSpec.setLamda(0.65);
    javaSpec.setGamma(0.99);
    javaSpec.setSd(1.0);
    javaSpec.setProcessModelSd(1.0);
    
    javaSpec.setProcessModelMemory(8000);
    javaSpec.setProcessModelNeighbors(20);
    javaSpec.setProcessModelValuesToRebuildTree(1);
    javaSpec.setObservationMinValue(spec.observation_min ./ norm_factor);
    javaSpec.setObservationMaxValue(spec.observation_max ./ norm_factor);
    
    javaSpec.setProcessModelCrossLimit(10);
    javaSpec.setProcessModelUpperBound([0 0 10 0]);
    javaSpec.setProcessModelThreshold(0.5);
    javaSpec.setProcessModelAnglePosition(2);
    
    javaSpec.setProcessModelStepsPerEpisode(args.steps);
    javaSpec.setProcessModelCriticAlpha(javaSpec.getCriticAlpha()/args.alpha);
    javaSpec.setProcessModelActorAplha(javaSpec.getActorAlpha()/args.alpha);
    javaSpec.setProcessModelGamma(0.97);
    javaSpec.setProcessModelIterationsWithoutLearning(0);
    javaSpec.setRewardCalculator(br.ufrj.ppgi.rl.reward.RewardCalculator.Cartpole);
    javaSpec.setNormalization(norm_factor);
    javaSpec.setProcessModelMeanDistanceLimit(0.65);
       
    agent = br.ufrj.ppgi.rl.ac.DynaActorCritic;
    agent.init(javaSpec);
    
    [critic, actor, cr, rmse, episodes, skiped, distance] = learn('cartpole', norm_factor, agent, args);
    model = agent.getProcessModel();
end