function [critic, actor, cr, rmse, lwr] = lwr_ac_pendulum()
    % Initialize parameters
    actor.grids   = 16;         % Total of grid used for the actor
    actor.tiles   = 216384;     % Total of tiles per grid used for the actor
    actor.offset  = linspace(0, actor.grids-1, actor.grids)*actor.tiles;
    
    critic.grids  = 16;       % Total of grid used for the actor
    critic.tiles  = 216384;   % Total of tiles per grid used for the actor
    critic.offset = linspace(0, critic.grids-1, critic.grids)*critic.tiles;
    
    env.actor.alpha   = 0.005;
    env.critic.alpha  = 0.1;
    env.gamma         = 0.97;     % Discount rate
    env.lambda        = 0.67;     % Decay rate
    env.steps         = 100;      % Steps per episode
    env.sd            = 1.0;      % Standard-deviation for gaussian noise in action
    
    model.actor.alpha         = env.actor.alpha;
    model.critic.alpha        = env.critic.alpha;
    model.gamma               = 0.97;     % Discount rate
    model.lambda              = 0;        % Decay rate
    model.steps_per_episode   = 30;       % Max model steps per episode
    model.steps               = 100;      % Max model steps
    model.sd                  = 0.3;      % Standard-deviation for gaussian noise in action
    
    memory_size   = 100;      % Eligibility trace memory
    episodes      = 150;      % Total of episodes 
    
    random_u      = 0.0;      % Random noise for action - Global variable declaration
    threshold     = 0.5;      % Threshold for 0-2PI limits
        
    norm_factor   = [ pi/10, pi ]; % Normalization factor used in observations
    
    % Initialize weights for FA
    critic.weights = (ones([critic.grids critic.tiles])*-1000)/critic.grids;
    actor.weights = zeros([actor.grids actor.tiles]);
    
    % Initialize learning curve
    cr = zeros(1, episodes);
    
    % Initialize RMSE curve
    rmse = zeros(1, episodes);

    % Initialize simulation
    spec = env_mops_sim('init');
    
    % LWR parameters
    lwr_memory = env.steps*episodes*2;
    lwr_params = 2*spec.observation_dims + spec.action_dims + 2;
    k = 4 * (spec.observation_dims + spec.action_dims);
    lwr = zeros([lwr_memory lwr_params]);
    
    last_lwr_pos = 0;
    
    for ee=1:episodes
        % Show progress
        disp(ee)

        % Reset simulation to initial condition
        first_obs = env_mops_sim('start');
        norm_first_obs = first_obs ./ norm_factor;
        restart_model();
        
        % Initialize traces
        Z_values_real = zeros([memory_size 1]);
        Z_obs_real = zeros([memory_size critic.grids]);
        
        % Random action
        a = choose_action(norm_first_obs, env);
        [obs, ~, terminal] = env_mops_sim('step', a);
        norm_old_obs = obs ./ norm_factor;
        
        for tt=1:env.steps
            if terminal
                break;
            end
            
            % Calculate action
            a = choose_action(norm_old_obs, env);
            
            % Actuate
            [obs, reward, terminal] = env_mops_sim('step', a);
            norm_obs = obs ./ norm_factor;
            
            % Need enough observations
            if (last_lwr_pos > k)
                [model_norm_obs, ~, ~] = model_transition(norm_old_obs, a);                
                rmse(ee) = rmse(ee) + sum((model_norm_obs - norm_obs) .^ 2);
            end
            
            % Add to LWR
            add_lwr(norm_old_obs, a, norm_obs, reward, terminal);
            
            % Update based on real observation
            if (tt > 1)
                [Z_values_real, Z_obs_real] = update(norm_old_obs, norm_obs, reward, env, Z_values_real, Z_obs_real);
            end

            % Try the model
            % Need enough observations
            if (last_lwr_pos > k)                
                for mm=1:model.steps_per_episode
                    a = choose_action(model_norm_old_obs, model);
                    [model_norm_obs, model_reward, model_terminal] = model_transition(model_norm_old_obs, a);
                    %disp([model_norm_obs, model_reward, model_terminal]);
                                       
                    [Z_values_model, Z_obs_model] = update(model_norm_old_obs, model_norm_obs, model_reward, model, Z_values_model, Z_obs_model);
                    
                    model_norm_old_obs = model_norm_obs;
                    model_tt = model_tt + 1;
                    
                    % Restart model transition if hit a terminal state or
                    % if we had model.steps iterations
                    if  model_terminal || model_tt == model.steps
                        restart_model();
                    end
                end
                %disp(model_tt);
            end
            
            % Prepare for next timestep
            norm_old_obs = norm_obs;

            % Keep track of learning curve
            cr(ee) = cr(ee) + reward;
        end
    end

    function restart_model()
        % Set first state
        model_tt = 0;
        model_norm_old_obs = norm_first_obs;
        
        % Clear model eligibility traces
        Z_values_model = zeros([memory_size 1]);
        Z_obs_model = zeros([memory_size critic.grids]);
    end
    
    function add_lwr(norm_old_obs, a, norm_obs, reward, terminal)
        % Cross boundary?
        if norm_obs(1) - norm_old_obs(1) < -10
            add_lwr(norm_old_obs, a, norm_obs + [20 0], reward, terminal);
            add_lwr(norm_old_obs - [20 0], a, norm_obs, reward, terminal);
            return;
        end
        
        if norm_obs(1) - norm_old_obs(1) > 10
            add_lwr(norm_old_obs + [20 0], a, norm_obs, reward, terminal);
            add_lwr(norm_old_obs, a, norm_obs - [20 0], reward, terminal);
            return;
        end
        
        push_value_lwr(norm_old_obs, a, norm_obs, reward, terminal);
        
        if norm_obs(1) - threshold < 0
            new_norm_obs = norm_obs + [20 0];
            new_norm_old_obs = norm_old_obs + [20 0];
            
            push_value_lwr(new_norm_old_obs, a, new_norm_obs, reward, terminal);
        end
        
        if norm_obs(1) + threshold > 20
            new_norm_obs = norm_obs - [20 0];
            new_norm_old_obs = norm_old_obs - [20 0];
            
            push_value_lwr(new_norm_old_obs, a, new_norm_obs, reward, terminal);
        end
    end

    function push_value_lwr(norm_old_obs, a, norm_obs, reward, terminal)
        last_lwr_pos = last_lwr_pos + 1;
        lwr(last_lwr_pos,:) = [norm_old_obs a norm_obs-norm_old_obs reward terminal];
    end
    
    function [Z_values, Z_obs] = update(norm_old_obs, norm_obs, reward, params, Z_values, Z_obs)
        % Decay Eligibility trace
        Z_values = Z_values*params.lambda*params.gamma;

        % Add obs to Eligibility trace
        [~, last_trace] = min(Z_values);
        Z_values(last_trace) = 1;
        Z_obs(last_trace,:) = GetTiles_Mex(critic.grids, norm_old_obs, critic.tiles, 1) + critic.offset;

        % TD-error
        delta = reward + params.gamma*fa_estimate(norm_obs, critic) ...
                - fa_estimate(norm_old_obs, critic);

        % Update actor and critic
        % Critic update using Eligibility trace
        critic_update = params.critic.alpha*delta/critic.grids;
        critic.weights(Z_obs(1:last_trace,:)) = critic.weights(Z_obs(1:last_trace,:)) + repmat(Z_values(1:last_trace,:).*critic_update, 1, critic.grids);

        % Actor update
        actor_update = (params.actor.alpha*random_u*delta)/actor.grids;
        active_tiles = GetTiles_Mex(actor.grids, norm_old_obs, actor.tiles, 1);
        actor.weights(active_tiles + actor.offset) = actor.weights(active_tiles + actor.offset) + actor_update;

        %disp([reward delta actor_update max(max(actor.weights))]);
    end

    function a = choose_action(norm_old_obs, param)
        random_u = normrnd(0, param.sd);
        a = fa_estimate(norm_old_obs, actor) + random_u;
        a = max(a, spec.action_min);
        a = min(a, spec.action_max);
        disp([norm_old_obs .* norm_factor a]);
    end
    
    function [model_obs model_reward model_termination] = model_transition(norm_old_obs, action)
        q = [norm_old_obs action];
        points = knnsearch(q, lwr(1:last_lwr_pos,1:3), k);
        N = lwr(points, :);
        size_NI = spec.observation_dims+spec.action_dims;
        size_NO = spec.observation_dims+2;
        NI = N(:,1:size_NI);
        NO = N(:,size_NI+1:size_NI + size_NO);
        d = zeros(k,1);
        for dd=1:k
            d(dd,:) = norm(NI(dd,:) - q);
        end
        h = max(d) + 0.01;
        w = exp(-(d./h).^2);
        A = zeros(k, size_NO);
        for ii=1:k
            A(ii,:) = w(ii)*[NI(ii,:) 1];
        end
        B = zeros(k, size_NO);
        for ii=1:k
            B(ii,:) = w(ii)*NO(ii,:);
        end
        
        temp_inv = pinv(A'*A);
        X = temp_inv*A'*B;
        R = A*X - B;
        
        p = zeros(k, 1);
        for pp=1:k
            p(pp) = w(pp,:)*A(pp,:)*temp_inv*A(pp,:)';
        end
        
        n = sum(w.^2);
        variance = sum(sum(R.^2)) / (n - sum(p));
        
        mean = [q 1]*X;
        mean_transition = mean(1:2);
        mean_reward = mean(3);
        mean_termination = mean(4);
        
        model_obs = mean_transition + norm_old_obs;
        if model_obs(1) < 0
            model_obs = model_obs + [20 0];
        end
        
        if model_obs(1) > 20
            model_obs = model_obs - [20 0];
        end
            
        model_obs = max(model_obs, spec.observation_min ./ norm_factor);
        model_obs = min(model_obs, spec.observation_max ./ norm_factor);
        
        model_reward = mean_reward;
        model_termination = mean_termination;
        
        if any(variance > [spec.observation_max - spec.observation_min spec.action_max - spec.action_min])
            %disp([1 1 1]);
            %model_termination = 1;
        end
    end

    % Destroy simulation
    env_mops_sim('fini');   
end