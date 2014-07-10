function [critic, actor, cr, lwr] = lwr_ac_pendulum()
    % Initialize parameters
    actor.alpha   = 0.005;      % Learning rate for the actor
    actor.grids   = 16;         % Total of grid used for the actor
    actor.tiles   = 16384;     % Total of tiles per grid used for the actor
    
    critic.alpha  = 0.1;      % Learning rate for the critic
    critic.grids  = 16;       % Total of grid used for the actor
    critic.tiles  = 16384;   % Total of tiles per grid used for the actor
    
    gamma         = 0.97;     % Discount rate
    lambda        = 0.67;     % Decay rate
    memory_size   = 100;      % Eligibility trace memory
    episodes      = 150;      % Total of episodes
    steps         = 100;      % Steps per episode
    sd            = 1.0;      % Standard-deviation for gaussian noise in action
        
    % Initialize weights for FA
    critic.weights = (ones([critic.grids critic.tiles])*-1000)/critic.grids;
    actor.weights = zeros([actor.grids actor.tiles]);
    
    % Initialize learning curve
    cr = zeros(1, episodes);

    % Initialize simulation
    spec = env_mops_sim('init');
    
    % LWR parameters
    lwr_memory = steps*episodes;
    lwr_params = 2*spec.observation_dims + spec.action_dims + 2;
    k = 4 * (spec.observation_dims + spec.action_dims);
    lwr = zeros([lwr_memory lwr_params]);
    
    for ee=1:episodes
        % Show progress
        disp(ee)

        % Reset simulation to initial condition
        first_obs = env_mops_sim('start');
        norm_first_obs = first_obs ./ [ pi/10, pi];
        
        % Initialize traces
        Z_values_real = zeros([memory_size 1]);
        Z_obs_real = zeros([memory_size critic.grids]);
        Z_values_model = zeros([memory_size 1]);
        Z_obs_model = zeros([memory_size critic.grids]);
        
        % Random action
        a = normrnd(0, sd);
        [obs, ~, terminal] = env_mops_sim('step', a);
        norm_old_obs = obs ./ [ pi/10, pi];
        
        for tt=1:steps
            if terminal
                break;
            end
            
            random_u = normrnd(0, sd);
            % Calculate action
            a = choose_action(norm_old_obs);
            
            % Actuate
            [obs, reward, terminal] = env_mops_sim('step', a);
            norm_obs = obs ./ [ pi/10, pi];
            
            % Add to LWR
            pos = (ee-1)*steps + tt;
            lwr(pos,:) = [norm_old_obs a norm_obs-norm_old_obs reward terminal];
            
            % Update based on real observation
            if (tt > 1)
                [Z_values_real, Z_obs_real] = update(norm_old_obs, norm_obs, reward, Z_values_real, Z_obs_real);
            end

            % Try the model
            % Need enough observations
            if (pos > k)
                a = choose_action(norm_first_obs);
                
        
            end
            
            % Prepare for next timestep
            norm_old_obs = norm_obs;

            % Keep track of learning curve
            cr(ee) = cr(ee) + reward;
        end
    end

    function [Z_values, Z_obs] = update(norm_old_obs, norm_obs, reward, Z_values, Z_obs)
        % Decay Eligibility trace
        Z_values = Z_values*lambda*gamma;
    
        % Add obs to Eligibility trace
        [~, trace_pos] = min(Z_values);
        Z_values(trace_pos) = 1;
        Z_obs(trace_pos,:) = GetTiles_Mex(critic.grids, norm_old_obs, critic.tiles, 1);

        % TD-error
        delta = reward + gamma*fa_estimate(norm_obs, critic) ...
                - fa_estimate(norm_old_obs, critic);

        % Update actor and critic
        % Critic update using Eligibility trace
        update_matrix = zeros([critic.grids critic.tiles]);
        traces = find(Z_values);
        for tr=1:numel(traces)
            for ii=1:critic.grids
                update_matrix(ii, Z_obs(tr, ii)) = Z_values(traces(tr));
            end
        end
        critic.weights = critic.weights + (critic.alpha*delta*update_matrix)./critic.grids;

        % Actor update
        actor_update = (actor.alpha*random_u*delta)./actor.grids;
        actor.weights = actor.weights + fa_gradient(norm_old_obs, actor)*actor_update;

        %disp([reward delta actor_update max(max(actor.weights))]);
    end

    function a = choose_action(norm_old_obs)
        a = fa_estimate(norm_old_obs, actor) + random_u;
        a = max(a, spec.action_min);
        a = min(a, spec.action_max);
        disp([obs a]);
    end
    
    function model_transition(norm_old_obs, action)
        q = [norm_old_obs action];
        points = knnsearch(q, lwr(:,1:3), k);
        N = lwr(points, :);
        NI = N(:,1:3);
        NO = N(:,4:7);
        d = zeros(7,1);
        for dd=1:k
            d(dd,:) = norm(NI(dd,:) - q);
        end
        h = max(d);
        w = exp(-(d./h).^2);
        A = zeros(k, 4);
        for ii=1:k
            A(ii,:) = w(ii)*[NI(ii,:) 1];
        end
        B = zeros(k, 4);
        for ii=1:k
            B(ii,:) = w(ii)*NO(ii,:);
        end
        X = pinv(A'*A)*A'*B;
        
    end

    % Destroy simulation
    env_mops_sim('fini');   
end