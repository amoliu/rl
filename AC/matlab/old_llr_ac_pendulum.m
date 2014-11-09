function [critic, actor, cr] = old_llr_ac_pendulum(episodes)
    % Initialize simulation
    spec = env_mops_sim('init');
    
    actor.memory      = 4000;
    actor.llr         = LLR(actor.memory, spec.observation_dims, spec.action_dims, 25);
    actor.alpha       = 0.05;
    
    critic.memory     = 4000;
    critic.llr        = LLR(critic.memory, spec.observation_dims, 1, 15);
    critic.alpha      = 0.3;
    
    env.gamma         = 0.97;     % Discount rate
    env.lambda        = 0.67;     % Decay rate
    env.steps         = 100;      % Steps per episode
    env.sd            = 0.0;      % Standard-deviation for gaussian noise in action
    
    random_u          = 1.0;           % Random noise for action - Global variable declaration
    norm_factor       = [ pi/10, pi ]; % Normalization factor used in observations
    
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
            action = choose_action(norm_old_obs);
            
            % Actuate
            [obs, reward, terminal] = env_mops_sim('step', action);
            norm_obs = obs ./ norm_factor;
            
            % Update based on real observation
            update(norm_old_obs, action, norm_obs, reward);
            
            % Prepare for next timestep
            norm_old_obs = norm_obs;

            % Keep track of learning curve
        end
        
        cr(ee) = test_ac_llr(actor, spec);
    end
    
    function update(norm_old_obs, action, norm_obs, reward)
        % Critic
        value_function = critic.llr.query(norm_obs);
        [old_value_function, ~, old_critic_neighbors] = critic.llr.query(norm_old_obs);
        
        % TD-error
        delta = reward + env.gamma*value_function - old_value_function;
        
        disp([reward delta norm_obs value_function norm_old_obs old_value_function]);
        
        % Add to Critic LLR
        critic.llr.add(norm_old_obs, old_value_function);
        
        % Update ET
        Z_values = Z_values*env.lambda*env.gamma;
        Z_values(old_critic_neighbors) = 1;
        
        critic.llr.update(Z_values.*critic.alpha*delta);
        
        % Update actor
        actor_update = actor.alpha*random_u*delta;
        actor.llr.add(norm_old_obs, min(max(action + actor_update, spec.action_min), spec.action_max));
        
        [~, ~, actor_neighbors] = actor.llr.query(norm_old_obs);
        actor.llr.update(actor_update, actor_neighbors, spec.action_min, spec.action_max);
    end

    function a = choose_action(norm_old_obs)
        random_u = normrnd(0, env.sd);
        a = actor.llr.query(norm_old_obs) + random_u;
        a = max(a, spec.action_min);
        a = min(a, spec.action_max);
        
        %disp([norm_old_obs .* norm_factor a]);
    end

    % Destroy simulation
    env_mops_sim('fini');   
end