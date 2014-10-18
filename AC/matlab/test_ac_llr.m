function [cr] = test_ac_llr(actor, spec)    
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
        action = actor.llr.query(norm_old_obs);
        action = max(action, spec.action_min);
        action = min(action, spec.action_max);

        % Actuate
        [obs, reward, terminal] = env_mops_sim('step', action);
        norm_obs = obs ./ norm_factor;

        % Prepare for next timestep
        norm_old_obs = norm_obs;

        % Keep track of learning curve
        cr = cr + reward;
    end
end

