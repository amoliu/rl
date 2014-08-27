function [critic, cr] = discrete_ac_pendulum()
% Discrete Actor-free reinforcement learning algorithm
% Solving the pendulum problem
% This algorithm discretize the number of possible actions
% Then chooses the best next state we can by looking in a process model and the critic.
    % Initialize simulation
    spec = env_mops_sim('init');
    
    discrete_actions = 6;
    actions = linspace(spec.action_min, spec.action_max, discrete_actions);
    
    critic.memory     = 2000;
    critic.llr        = LLR(critic.memory, spec.observation_dims, 1, 20);
    critic.alpha      = 0.2;
    
    model.llr         = LLR(1000, spec.observation_dims + spec.action_dims, spec.observation_dims, 9);
    
    env.gamma         = 0.97;     % Discount rate
    env.lambda        = 0.67;     % Decay rate
    env.steps         = 100;      % Steps per episode
    env.sd            = 1.0;      % Standard-deviation for gaussian noise in action
    
    episodes          = 30;            % Total of episodes
    norm_factor       = [ pi/10, pi ]; % Normalization factor used in observations
    
    threshold         = 0.5;           % Threshold for 0-2PI limits
    
    % Initialize learning curve
    cr = zeros(1, episodes);
    
    for ee=1:episodes
        % Show progress
        disp(ee)

        % Reset simulation to initial condition
        first_obs = env_mops_sim('start');
        norm_first_obs = first_obs ./ norm_factor;
        
        % Initialize traces
        Z_values = zeros([critic.memory 1]);
        
        % Random action
        a = choose_action(norm_first_obs);
        [obs, ~, terminal] = env_mops_sim('step', a);
        norm_old_obs = obs ./ norm_factor;
        
        for tt=1:env.steps
            if terminal
                break;
            end
            
            % Calculate action
            a = choose_action(norm_old_obs);
            
            % Actuate
            [obs, reward, terminal] = env_mops_sim('step', a);
            norm_obs = obs ./ norm_factor;
            
            model.llr.add([norm_old_obs a], norm_obs);
            
            % Update based on real observation
            update(norm_old_obs, norm_obs, reward);
            
            % Prepare for next timestep
            norm_old_obs = norm_obs;

            % Keep track of learning curve
            cr(ee) = cr(ee) + reward;
        end
    end
    
    function update(norm_old_obs, norm_obs, reward)
        % Critic
        value_function = critic.llr.query(norm_obs);
        [old_value_function, ~, old_critic_neighbors] = critic.llr.query(norm_old_obs);
        
        % TD-error
        delta = reward + env.gamma*value_function - old_value_function;
        
        % Add to Critic LLR
        critic.llr.add(norm_old_obs, old_value_function);
        if norm_old_obs(1) - threshold < 0
            new_norm_obs = norm_old_obs + [20 0];
            critic.llr.add(new_norm_obs, old_value_function);
        end
        
        if norm_old_obs(1) + threshold > 20
            new_norm_obs = norm_old_obs - [20 0];
            critic.llr.add(new_norm_obs, old_value_function);
        end
        
        % Update ET
        Z_values = Z_values*env.lambda*env.gamma;
        Z_values(old_critic_neighbors) = 1;
        
        critic.llr.update(Z_values.*critic.alpha*delta);
    end

    function a = choose_action(norm_obs)
        values = zeros(1, discrete_actions);
        
        for aa=1:discrete_actions
            next_state = model.llr.query([norm_obs actions(aa)]);
            values(aa) = critic.llr.query(next_state);
        end
       
        [~, pos_a] = max(values);
        a = actions(pos_a);
        disp([norm_obs .* norm_factor a]);
    end

    % Destroy simulation
    env_mops_sim('fini');   
end