function [cr] = agent_performance(agent)    
%AGENT_PERFORMANCE Calculate the reward for this agent on the pendulum swing-up environment
%   
%   CR = AGENT_PERFORMANCE(...) returns the reward a full trial on the environment, i.e., 100 steps.
%
%   AUTHOR:
%       Bruno Costa <doravante2@gmail.com>

    steps         = 100;      % Steps per episode
    norm_factor   = [ pi/10, pi ]; % Normalization factor used in observations
    
    % Initialize learning curve
    cr = 0;
    
    % Reset simulation to initial condition
    first_obs = env_mops_sim('start');
    norm_old_obs = first_obs ./ norm_factor;
        
    terminal = 0;
    
    for tt=1:steps
        if terminal
            break;
        end

        % Calculate action
        action = agent.stepWithoutLearn(norm_old_obs);

        % Actuate
        [obs, reward, terminal] = env_mops_sim('step', action);
        norm_obs = obs ./ norm_factor;

        % Prepare for next timestep
        norm_old_obs = norm_obs;

        % Keep track of learning curve
        cr = cr + reward;
    end
end

