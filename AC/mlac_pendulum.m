function [critic, actor, cr] = mlac_pendulum()
    % Initialize simulation
    spec = env_mops_sim('init');

    % Initialize parameters
    llr.tikhonov = 0.01;
    llr.gamma = 0.9;
    
    critic.memory   = 2000;
    critic.llr      = LLR(critic.memory, spec.observation_dims, 1, 20, llr.tikhonov, llr.gamma);
    critic.weight   = [1 0.1];    % Wc used in LLR
    critic.alpha    = 0.1;
    critic.Xs       = zeros(1, spec.observation_dims);
    critic.Xb       = 0;
    
    actor.llr      = LLR(2000, spec.observation_dims, spec.action_dims, 9, llr.tikhonov, llr.gamma);
    actor.weight   = [1 0.1];    % Wc used in LLR
    actor.alpha    = 0.005;
    actor.Xs       = zeros(1, spec.observation_dims);
    actor.Xb       = 0;

    model.llr      = LLR(100, spec.observation_dims + spec.action_dims, spec.observation_dims, 9, llr.tikhonov, llr.gamma);
    model.weight   = [1 0.1 1];
    model.Xs       = zeros(spec.observation_dims, 1);
    model.Xa       = zeros(spec.action_dims, 1);
    model.Xb       = 0;
        
    gamma         = 0.97;     % Discount rate
    lambda        = 0.65;     % Decay rate
    steps         = 100;      % Steps per episode
    sd            = 1.0;      % Random noise
    
    episodes      = 100;      % Total of episodes
    
    norm_factor   = [ pi/10, pi ]; % Normalization factor used in observations
    
    % Initialize learning curve
    cr = zeros(1, episodes);

    for ee=1:episodes
        % Show progress
        disp(ee)

        if ee == 21
            disp(ee);
        end
        
        % Reset simulation to initial condition
        first_obs = env_mops_sim('start');
        norm_first_obs = first_obs ./ norm_factor;
        
        % Initialize traces
        Z_values = zeros([critic.memory 1]);
        
        old_value_function = 0;
        
        % Random action
        a = choose_action(norm_first_obs);
        [obs, ~, terminal] = env_mops_sim('step', a);
        norm_old_obs = obs ./ norm_factor;
        old_action = a;
                
        for tt=1:steps
            if terminal
                break;
            end
            
            % Calculate action
            [a, X, actor_neighbors] = choose_action(norm_old_obs);
            actor.Xs = X(:,1:spec.observation_dims);
            actor.Xb = X(:,spec.observation_dims+1);
            
            % Actuate
            [obs, reward, terminal] = env_mops_sim('step', a);
            norm_obs = obs ./ norm_factor;
            
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
            actor.llr.update(actor_update, actor_neighbors, spec.action_min, spec.action_max);
            
            % Update Critic
            [value_function, X, critic_neighbors] = critic.llr.query(norm_obs);
            critic.Xs = X(:,1:spec.observation_dims);
            critic.Xb = X(:,spec.observation_dims+1);
            
            % Add to Critic LLR
            critic.llr.add(norm_old_obs, old_value_function);
            
            % Update ET
            Z_values = Z_values*gamma*lambda;
            Z_values(critic_neighbors) = 1;
            
            % Critic update using Eligibility trace
            critic_update = reward + gamma*value_function - old_value_function;
            critic.llr.update(Z_values.*critic.alpha*critic_update);
            
            % Prepare for next timestep
            norm_old_obs = norm_obs;
            old_action = a;
            old_value_function = value_function;

            % Keep track of learning curve
            cr(ee) = cr(ee) + reward;
        end
    end
    
    function [a, X, actor_neighbors] = choose_action(norm_old_obs)
        random_u = normrnd(0, sd);
        [a, X, actor_neighbors] = actor.llr.query(norm_old_obs);
        a = min(max(a + random_u, spec.action_min), spec.action_max);
        disp([norm_old_obs .* norm_factor a]);
    end

    % Destroy simulation
    env_mops_sim('fini');   
end