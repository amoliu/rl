function [critic, actor, cr] = s_ac_pendulum()
    % Initialize parameters
    actor.alpha   = 0.1;      % Learning rate for the actor
    actor.grids   = 16;       % Total of grid used for the actor
    actor.tiles   = 1048576;  % Total of tiles per grid used for the actor
    
    critic.alpha  = 0.005;    % Learning rate for the critic
    critic.grids  = 16;       % Total of grid used for the actor
    critic.tiles  = 1048576;  % Total of tiles per grid used for the actor
    
    gamma         = 0.97;     % Discount rate
    lambda        = 0.65;     % Decay rate
    memory_size   = 100;      % Eligibility trace memory
    episodes      = 50;       % Total of episodes
    steps         = 100;      % Steps per episode
    sd            = 1.0;      % Standard-deviation for gaussian noise in action
        
    % Initialize weights for FA
    critic.weights = ones([critic.grids critic.tiles])*-10;
    actor.weights = zeros([actor.grids actor.tiles]);

    % Initialize learning curve
    cr = zeros(1, episodes);

    % Initialize simulation
    spec = env_mops_sim('init');
    
    for ee=1:episodes
        % Show progress
        disp(ee)

        % Reset simulation to initial condition
        env_mops_sim('start');
        
        % Initialize traces
        Z_values = zeros([memory_size 1]);
        Z_obs = zeros([memory_size critic.grids]);
        
        % Random action
        a = normrnd(0, sd);
        [obs, ~, terminal] = env_mops_sim('step', a);
        %norm_obs = normalize(obs, spec.observation_dims, spec.observation_min, spec.observation_max);
        
        norm_obs = obs ./ [ pi/10, pi];

        for tt=0:steps
            if terminal
                break;
            end
            
            random_u = normrnd(0, sd);
            % Calculate action
            a_fa = fa_estimate(norm_obs, actor);
            a = a_fa + random_u;
            a = max(a, spec.action_min);
            a = min(a, spec.action_max);
            disp([obs a]);
            
            % Actuate
            [obs, reward, terminal] = env_mops_sim('step', a);
%            norm_obs = normalize(obs,   spec.observation_dims, spec.observation_min, spec.observation_max);
            norm_obs = obs ./ [ pi/10, pi];
%            norm_reward = normalize(reward, 1, spec.reward_min, spec.reward_max, spec.action_min, spec.action_max);
            norm_reward = reward;
            
            % Decay Eligibility trace
            Z_values = Z_values*lambda*gamma;
            
            % Add obs to Eligibility trace
            [~, trace_pos] = min(Z_values);
            Z_values(trace_pos) = 1;
            Z_obs(trace_pos,:) = GetTiles_Mex(critic.grids, norm_obs, critic.tiles, 1);
            
            % Don't learn on the first step
            if (tt > 0)
                % TD-error
                delta = norm_reward + gamma*fa_estimate(norm_obs, critic) ...
                        - fa_estimate(old_norm_obs, critic);
                
                % Update actor and critic
                % Critic update using Eligibility trace
                update_matrix = zeros([critic.grids critic.tiles]);
                traces = find(Z_values);
                for tr=1:numel(traces)
                    for ii=1:critic.grids
                        update_matrix(ii, Z_obs(tr, ii)) = Z_values(traces(tr));
                    end
                end
                critic.weights = critic.weights + (critic.alpha)*delta*update_matrix;
                
                % Actor update
                actor.weights = actor.weights + (actor.alpha)*fa_gradient(norm_obs, actor)*(random_u - a_fa)*delta;
            end

            % Prepare for next timestep
            old_norm_obs = norm_obs;

            % Keep track of learning curve
            cr(ee) = cr(ee) + reward;
        end
    end

    % Destroy simulation
    env_mops_sim('fini');   
end