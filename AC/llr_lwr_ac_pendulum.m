function [critic, actor, cr, rmse, lwr] = llr_lwr_ac_pendulum()
    % Initialize simulation
    spec = env_mops_sim('init');

    % Initialize parameters
    llr.tikhonov      = 0.000001;
    
    actor.memory      = 2000;
    actor.llr         = LLR(actor.memory, spec.observation_dims, 1, 20);
    
    critic.memory     = 2000;
    critic.llr        = LLR(critic.memory, spec.observation_dims, 1, 20, -1000);
    
    env.actor.alpha   = 0.05;
    env.critic.alpha  = 0.25;
    env.gamma         = 0.97;     % Discount rate
    env.lambda        = 0.67;     % Decay rate
    env.steps         = 100;      % Steps per episode
    env.sd            = 1.0;      % Standard-deviation for gaussian noise in action
    env.add_llr       = 1;        % This env add exp to LLR?
    
    model.actor.alpha         = env.actor.alpha/1500;
    model.critic.alpha        = env.critic.alpha/1500;
    model.gamma               = 0.67;     % Discount rate
    model.lambda              = 0;        % Decay rate
    model.steps_per_episode   = 30;      % Max model steps per episode
    model.steps               = 100;      % Max model steps
    model.sd                  = 0.2;      % Standard-deviation for gaussian noise in action
    model.add_llr             = 0;        % This env add exp to LLR?
    
    episodes      = 30;      % Total of episodes
    
    random_u      = 0.0;      % Random noise for action - Global variable declaration
    threshold     = 0.5;      % Threshold for 0-2PI limits
        
    norm_factor   = [ pi/10, pi ]; % Normalization factor used in observations
    
    % Initialize learning curve
    cr = zeros(1, episodes);
    
    % Initialize RMSE curve
    rmse = zeros(1, episodes);
    
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
        Z_values_real = zeros([critic.memory 1]);
        
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
                model_norm_obs = model_transition(norm_old_obs, a);
                curr_rmse = sum((model_norm_obs - norm_obs) .^ 2);
                rmse(ee) = rmse(ee) + curr_rmse;
            end
            
            % Add to LWR
            add_lwr(norm_old_obs, a, norm_obs, reward, terminal);
            
            % Update based on real observation
            if (tt > 1)
                Z_values_real = update(norm_old_obs, norm_obs, reward, env, Z_values_real);
            end

            % Try the model
            % Need enough observations
            if (last_lwr_pos > k)                
                for mm=1:model.steps_per_episode
                    a = choose_action(model_norm_old_obs, model);
                    [model_norm_obs, model_reward, model_terminal] = model_transition(model_norm_old_obs, a);
                    %disp([model_norm_obs, model_reward, model_terminal]);
                                       
                    Z_values_model = update(model_norm_old_obs, model_norm_obs, model_reward, model, Z_values_model);
                    
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
        Z_values_model = zeros([critic.memory 1]);
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
    
    function [Z_values] = update(norm_old_obs, norm_obs, reward, params, Z_values)
        % Critic
        value_function = critic.llr.query(norm_obs);
        [old_value_function, ~, old_critic_neighbors] = critic.llr.query(norm_old_obs);
        
        % TD-error
        delta = reward + params.gamma*value_function - old_value_function;
        
        % Add to Critic LLR
        if params.add_llr
            if norm_old_obs(1) - threshold < 0
                new_norm_obs = norm_old_obs + [20 0];
                critic.llr.add(new_norm_obs, old_value_function);
            end

            if norm_old_obs(1) + threshold > 20
                new_norm_obs = norm_old_obs - [20 0];
                critic.llr.add(new_norm_obs, old_value_function);
            end
            
            critic.llr.add(norm_old_obs, old_value_function);
        end
        
        % Update ET
        Z_values = Z_values*params.lambda*params.gamma;
        Z_values(old_critic_neighbors) = 1;   
        
        critic.llr.update(Z_values.*params.critic.alpha*delta);
        
        % Update actor
        [~, ~, actor_neighbors] = actor.llr.query(norm_old_obs);
        actor_update = params.actor.alpha*random_u*delta;
        actor.llr.update(actor_update, actor_neighbors, spec.action_min, spec.action_max);
    end

    function a = choose_action(norm_old_obs, params)
        random_u = normrnd(0, params.sd);
        a = actor.llr.query(norm_old_obs) + random_u;
        a = max(a, spec.action_min);
        a = min(a, spec.action_max);
        if params.add_llr
            if norm_old_obs(1) - threshold < 0
                new_norm_obs = norm_old_obs + [20 0];
                actor.llr.add(new_norm_obs, a);
            end

            if norm_old_obs(1) + threshold > 20
                new_norm_obs = norm_old_obs - [20 0];
                actor.llr.add(new_norm_obs, a);
            end
            
            actor.llr.add(norm_old_obs, a);
        end
        
        %disp([norm_old_obs .* norm_factor a]);
    end

    function [model_obs, model_reward, model_termination] = model_transition(norm_old_obs, action)
        q = [norm_old_obs action];
        %points = kdtree.knnsearch(q, 'K', k);
        points = knnsearch(q, lwr(1:last_lwr_pos,1:3), k);
        N = lwr(points, :);
        size_NI = spec.observation_dims+spec.action_dims;
        size_NO = spec.observation_dims+2;
        knn = numel(points);
        NI = N(:,1:size_NI);
        NO = N(:,size_NI+1:size_NI + size_NO);
        d = zeros(knn,1);
        for dd=1:knn
            d(dd,:) = norm(NI(dd,:) - q);
        end
        h = max(d) + 0.01;
        w = exp(-(d./h).^2);
        A = zeros(knn, size_NO);
        for ii=1:knn
            A(ii,:) = w(ii)*[NI(ii,:) 1];
        end
        B = zeros(knn, size_NO);
        for ii=1:knn
            B(ii,:) = w(ii)*NO(ii,:);
        end
        
        % Using Cholesky
        % A = U'U
        % inv(A) = inv(U)*inv(U)'
        
        U = chol(A'*A + eye(size_NO)*llr.tikhonov);
        iU = inv(U);
        temp_inv = iU*iU';
        
        X = temp_inv*A'*B;
        %R = A*X - B;
        
        %p = zeros(knn, 1);
        %for pp=1:knn
        %    p(pp) = w(pp,:)*A(pp,:)*temp_inv*A(pp,:)';
        %end
        
        %n = sum(w.^2);
        %variance = sum(sum(R.^2)) / (n - sum(p));
        
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
        
        %if any(variance > [spec.observation_max - spec.observation_min spec.action_max - spec.action_min])
        %    disp([1 1 1]);
        %    model_termination = 1;
        %end
    end

    % Destroy simulation
    env_mops_sim('fini');   
end