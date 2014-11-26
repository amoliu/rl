function cr = agent_performance(env, agent, norm_factor)    
%AGENT_PERFORMANCE Calculate the reward for this agent on the pendulum swing-up environment
%   
%   CR = AGENT_PERFORMANCE(...) returns the reward for a full trial on the
%   environment.
%
%   AUTHOR:
%       Bruno Costa <doravante2@gmail.com>  
    % Initialize learning curve
    cr = 0;
    
    % Reset simulation to initial condition
    first_obs = env('start');
    norm_old_obs = first_obs ./ norm_factor;
        
    terminal = 0;
    
    while 1
        if terminal
            break;
        end

        % Calculate action
        action = agent.stepWithoutLearn(norm_old_obs);

        % Actuate
        [obs, reward, terminal] = env('step', action);
        norm_obs = obs ./ norm_factor;

        % Prepare for next timestep
        norm_old_obs = norm_obs;

        % Keep track of learning curve
        cr = cr + reward;
    end
end

