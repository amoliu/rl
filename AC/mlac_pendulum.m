function [critic, actor, cr] = mlac_pendulum()
    % Initialize simulation
    spec = env_mops_sim('init');
    
    critic.memory   = 2000;
    critic.initial_value = 0;
    critic.llr      = LLR(critic.memory, spec.observation_dims, 1, 20);
    critic.alpha    = 0.2;
    critic.Xs       = zeros(1, spec.observation_dims);
    critic.Xb       = 0;
    
    actor.llr      = LLR(2000, spec.observation_dims, spec.action_dims, 9);
    actor.alpha    = 0.05;
    actor.Xs       = zeros(1, spec.observation_dims);
    actor.Xb       = 0;

    model.llr      = LLR(100, spec.observation_dims + spec.action_dims, spec.observation_dims, 9);
    model.Xs       = zeros(spec.observation_dims, 1);
    model.Xa       = zeros(spec.action_dims, 1);
    model.Xb       = 0;
    
    gamma         = 0.97;     % Discount rate
    lambda        = 0.65;     % Decay rate
    steps         = 100;      % Steps per episode
    sd            = 0.2;      % Random noise
    
    episodes      = 30;      % Total of episodes
    
    norm_factor   = [ pi/10, pi ]; % Normalization factor used in observations
    
    % Initialize learning curve
    cr = zeros(1, episodes);

    for ee=1:episodes
        % Show progress
        disp(ee);
        
        % Reset simulation to initial condition
        first_obs = env_mops_sim('start');
        norm_obs = first_obs ./ norm_factor;
        
        % Initialize traces
        Z_values = zeros([critic.memory 1]);
        
        model.Xs           = zeros(spec.observation_dims, 1);
        model.Xa           = zeros(spec.action_dims, 1);
        model.Xb           = 0;
        critic.Xs          = zeros(1, spec.observation_dims);
        critic.Xb          = 0;    
        actor.Xs           = zeros(1, spec.observation_dims);
        actor.Xb           = 0;
        old_value_function = critic.initial_value;
        
        % Random action
        next_a = choose_action(norm_obs);
        
        norm_old_obs = norm_obs;
        old_action = next_a;
        terminal = 0;
                
        for tt=1:steps
            if terminal
                break;
            end
            
            % Actuate
            [obs, reward, terminal] = env_mops_sim('step', next_a);
            norm_obs = obs ./ norm_factor;
            
            % Calculate action
            [next_a, X] = choose_action(norm_obs);
            actor.Xs = X(:,1:spec.observation_dims);
            actor.Xb = X(:,spec.observation_dims+1);
            
            % Update process model
            [~, X] = model.llr.query([norm_old_obs old_action]);
            model.Xs = X(:,1:spec.observation_dims);
            model.Xa = X(:,spec.observation_dims+1:spec.observation_dims+spec.action_dims);
            model.Xb = X(:,spec.observation_dims+spec.action_dims+1);

            model.llr.add([norm_old_obs old_action], norm_obs);

            % Update actor
            actor_update = actor.alpha*critic.Xs*model.Xa;
            actor.llr.add(norm_old_obs, old_action + actor_update);

            % Update actor
            [~, ~, old_actor_neighbors] = actor.llr.query(norm_old_obs);
            actor.llr.update(actor_update, old_actor_neighbors, spec.action_min, spec.action_max);

            % Update Critic
            [value_function, X, ~] = critic.llr.query(norm_obs);
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
    
    function [a, X, actor_neighbors] = choose_action(norm_obs)
        random_u = normrnd(0, sd);
        [a, X, actor_neighbors] = actor.llr.query(norm_obs);
        a = min(max(a + random_u, spec.action_min), spec.action_max);
        disp([norm_obs .* norm_factor a]);
    end

    % Destroy simulation
    env_mops_sim('fini');   
end