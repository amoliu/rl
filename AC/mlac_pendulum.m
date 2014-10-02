function [critic, actor, cr, rmse] = mlac_pendulum()
    % Initialize simulation
    spec = env_mops_sim('init');
    
    critic.memory   = 2000;
    critic.initial_value = 0;
    critic.llr      = LLR(critic.memory, spec.observation_dims, 1, 15);
    critic.alpha    = 0.3;
    critic.Xs       = zeros(1, spec.observation_dims);
    critic.Xb       = zeros(1, 1);
    
    actor.llr      = LLR(2000, spec.observation_dims, spec.action_dims, 25);
    actor.alpha    = 0.05;

    model.llr      = LLR(100, spec.observation_dims + spec.action_dims, spec.observation_dims, 10);
    model.Xs       = zeros(spec.observation_dims, spec.observation_dims);
    model.Xa       = zeros(spec.observation_dims, spec.action_dims);
    model.Xb       = zeros(spec.observation_dims, 1);
    
    gamma         = 0.97;     % Discount rate
    lambda        = 0.65;     % Decay rate
    steps         = 100;      % Steps per episode
    sd            = 1.0;      % Random noise
    
    threshold     = 0.5;      % Threshold for 0-2PI limits
    
    episodes      = 50;       % Total of episodes
    
    norm_factor   = [ pi/10, pi ]; % Normalization factor used in observations
    upper_bound   = 20;            % Upper bound to add or subtract to observations
    cross_limit   = 10;            % Difference in previous and current observation to decide if change sides
    
    % Initialize learning curve
    cr = zeros(1, episodes);
    
    % Initialize RMSE curve
    rmse = zeros(1, episodes);

    for ee=1:episodes
        % Show progress
        disp(ee);
        
        % Reset simulation to initial condition
        first_obs = env_mops_sim('start');
        norm_old_obs = first_obs ./ norm_factor;
        
        % Initialize traces
        Z_values = zeros([critic.memory 1]);
        
        model.Xs           = zeros(spec.observation_dims, spec.observation_dims);
        model.Xa           = zeros(spec.observation_dims, spec.action_dims);
        model.Xb           = zeros(spec.observation_dims, 1);
        critic.Xs          = zeros(1, spec.observation_dims);
        critic.Xb          = zeros(1, 1);

        old_value_function = critic.llr.query(norm_old_obs);
        terminal = 0;
        
        % Calculate action
        [action, policy_action] = choose_action(norm_old_obs);
        
        for tt=1:steps
            if terminal
                break;
            end
            
            % Actuate
            [obs, reward, terminal] = env_mops_sim('step', action);
            norm_obs = obs ./ norm_factor;
            
            % Need enough observations
            if (ee > 2)
                model_norm_obs = model_transition(norm_old_obs, action);
                rmse(ee) = rmse(ee) + sum((model_norm_obs - norm_obs) .^ 2);
            end
            
            % Update process model
            [model_obs, X] = model_transition(norm_old_obs, policy_action);
            model.Xs = X(:,1:spec.observation_dims);
            model.Xa = X(:,spec.observation_dims+1:spec.observation_dims+spec.action_dims);
            model.Xb = X(:,spec.observation_dims+spec.action_dims+1);
                        
            add_model(norm_old_obs, policy_action, norm_obs);
            
            [~, X] = critic.llr.query(model_obs);
            critic.Xs = X(:,1:spec.observation_dims);
            critic.Xb = X(:,spec.observation_dims+1);

            % check if withinBounds
            if policy_action < spec.action_min*0.92 || policy_action > spec.action_max*0.92
                model.Xa = zeros(spec.observation_dims, spec.action_dims);
            end
            
            % Update actor
            actor_update = actor.alpha*critic.Xs*model.Xa;
            actor.llr.add(norm_old_obs, action + actor_update);

            % Update actor
            [~, ~, old_actor_neighbors] = actor.llr.query(norm_old_obs);
            actor.llr.update(actor_update, old_actor_neighbors, spec.action_min, spec.action_max);
            
            % Get next action
            [action, policy_action] = choose_action(norm_obs);
            
            % Update Critic
            [value_function, X] = critic.llr.query(norm_obs);
            critic.Xs = X(:,1:spec.observation_dims);
            critic.Xb = X(:,spec.observation_dims+1);

            % Add to Critic LLR
            critic_pos = critic.llr.add(norm_old_obs, old_value_function);
            [~, ~, old_critic_neighbors] = critic.llr.query(norm_old_obs);

            % Update ET
            Z_values = Z_values*gamma*lambda;
            Z_values([old_critic_neighbors; critic_pos]) = 1;

            % Critic update using Eligibility trace
            critic_update = reward + gamma*value_function - old_value_function;
            critic.llr.update(Z_values.*critic.alpha*critic_update);
            value_function = value_function + critic.alpha*critic_update;
                
            % Prepare for next timestep
            norm_old_obs = norm_obs;
            old_value_function = value_function;

            % Keep track of learning curve
            cr(ee) = cr(ee) + reward;
        end
    end
    
    function [u, u_policy, X, actor_neighbors] = choose_action(norm_obs)
        random_u = normrnd(0, sd);
        [u, X, actor_neighbors] = actor.llr.query(norm_obs);
        u_policy = min(max(u, spec.action_min), spec.action_max);
        u = min(max(u + random_u, spec.action_min), spec.action_max);
        %disp([norm_obs .* norm_factor u]);
    end

    function add_model(norm_old_obs, a, norm_obs)        
        % Cross boundary?       
        if norm_obs(1) - norm_old_obs(1) < -cross_limit
            add_model(norm_old_obs, a, norm_obs + [upper_bound 0]);
            add_model(norm_old_obs - [upper_bound 0], a, norm_obs);
            return;
        end
        
        if norm_obs(1) - norm_old_obs(1) > cross_limit
            add_model(norm_old_obs + [upper_bound 0], a, norm_obs);
            add_model(norm_old_obs, a, norm_obs - [upper_bound 0]);
            return;
        end
        
        model.llr.add([norm_old_obs a], norm_obs);
        
        if norm_obs(1) - threshold < 0
            new_norm_obs = norm_obs + [upper_bound 0];
            new_norm_old_obs = norm_old_obs + [upper_bound 0];
            
            model.llr.add([new_norm_old_obs a], new_norm_obs);
        end
        
        if norm_obs(1) + threshold > upper_bound
            new_norm_obs = norm_obs - [upper_bound 0];
            new_norm_old_obs = norm_old_obs - [upper_bound 0];
            
            model.llr.add([new_norm_old_obs a], new_norm_obs);
        end
    end

    function [model_obs, X] = model_transition(norm_old_obs, action)
        [model_obs, X] = model.llr.query([norm_old_obs action]);
        if model_obs(1) < 0
            model_obs = model_obs + [upper_bound 0];
        end
        
        if model_obs(1) > upper_bound
            model_obs = model_obs - [upper_bound 0];
        end
            
        model_obs = max(model_obs, spec.observation_min ./ norm_factor);
        model_obs = min(model_obs, spec.observation_max ./ norm_factor);
    end

    % Destroy simulation
    env_mops_sim('fini');   
end