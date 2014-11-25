function [critic, actor, cr, rmse, episodes] = learn(env_name, episodes, norm_factor, agent, args)
%LEARN Make the agent learn about the environment.
%   LEARN(ENV, N, A) learns using N as norm factor to the observations,
%   given the agent A in the ENV environment.
%
%   LEARN(..., 'Mode', MODE) sets how it should learn.
%      MODE is one of 'episodes' : Will run for a fixed number of episodes.
%                     'performace': Will run until this performance is
%                     achieved.
%   LEARN(..., 'Episodes', E) sets the fixed number of episodes. Only on
%   mode 'episodes'.
%   LEARN(..., 'Performance', P) sets the required perforamnce. Only on
%   mode 'performance'.
%   LEARN(..., 'TrialsInARow', R) sets the required perforamnce to be the 
%   achieved for R trials in a row. Only on mode 'performance'.
%
%   C = LEARN(...) return a handle to the Critic
%   [C, A] = LEARN(...) also returns a handle to the Actor
%   [C, A, CR] = LEARN(...) also returns the learning curve.
%   [C, A, CR, E] = LEARN(...) also returns the error curve.
%   [C, A, CR, E, EE] = LEARN(...) also returns the total episodes it took.
%
%   AUTHOR:
%       Bruno Costa <doravante2@gmail.com>

    env = str2func(['env_' env_name]);

    % Initialize learning curve
    cr = [];

    % Initialize RMSE curve
    rmse = [];

    episodes = 1;
    
    if strcmp(args.mode, 'episodes')
        while_cond = @condition_mode_episodes;
    else
        while_cond = @condition_mode_performance;
    end
    
    while while_cond()
        % Show progress
        if args.verbose
            disp(episodes);
        end

        % Reset simulation to initial condition
        first_obs = env('start');
        norm_first_obs = first_obs ./ norm_factor;

        stepVO = agent.start(norm_first_obs);
        rmse(episodes) = 0;
        
        while 1
             % Actuate
            [obs, reward, terminal] = env('step', stepVO.getAction);
            norm_obs = obs ./ norm_factor;

            if terminal
                agent.end(reward);
                break;
            end

            rmse(episodes) = rmse(episodes) + stepVO.getError;
            
            % Learn and choose next action
            stepVO = agent.step(reward, norm_obs);
        end
        
        cr(episodes) = agent_performance(agent, norm_factor);
        episodes = episodes + 1;
    end

    rmse = sqrt(rmse ./ 100);
    
    % Destroy simulation
    env('fini');
    agent.fini();

    critic = agent.getCritic();
    actor = agent.getActor();
    
    function r = condition_mode_episodes()
        r = episodes <= args.episodes;
    end

    function r = condition_mode_performance()
        if episodes <= args.TrialsInARow
            r = 1;
        else
            r = 1;
            for i=1:args.TrialsInARow
                r = r && cr(episodes-i) >= args.Performance;
            end
        end
    end
end