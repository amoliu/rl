function [critic, actor, cr, rmse] = learn(episodes, norm_factor, agent)
%LEARN Make the agent learn about the environment.
%   LEARN(E, N, A) learns during E episodes, using N as norm factor to the
%   observations, given the agent A.
%
%   C = LEARN(...) return a handle to the Critic
%   [C, A] = LEARN(...) also returns a handle to the Actor
%   [C, A, CR] = LEARN(...) also returns the learning curve.
%   [C, A, CR, E] = LEARN(...) also returns the error curve.
%
%   AUTHOR:
%       Bruno Costa <doravante2@gmail.com>

    % Initialize learning curve
    cr = zeros(1, episodes);

    % Initialize RMSE curve
    rmse = zeros(1, episodes);

    for ee=1:episodes
        % Show progress
        disp(ee);

        % Reset simulation to initial condition
        first_obs = env_mops_sim('start');
        norm_first_obs = first_obs ./ norm_factor;

        stepVO = agent.start(norm_first_obs);

        for tt=1:100
             % Actuate
            [obs, reward, terminal] = env_mops_sim('step', stepVO.getAction);
            norm_obs = obs ./ norm_factor;

            if terminal
                agent.end(reward);
                break;
            end

            rmse(ee) = rmse(ee) + stepVO.getError;
            
            % Learn and choose next action
            stepVO = agent.step(reward, norm_obs);
        end

       cr(ee) = agent_performance(agent);
    end

    % Destroy simulation
    env_mops_sim('fini');
    agent.fini();

    critic = agent.getCritic();
    actor = agent.getActor();
end