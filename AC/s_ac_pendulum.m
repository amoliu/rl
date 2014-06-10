function [critic, actor, cr] = s_ac_pendulum()
    % Initialize parameters
    actor.alpha   = 0.25;  % Learning rate for the actor
    actor.grids   = 16;     % Total of grid used for the actor
    actor.tiles   = 4096;  % Total of tiles per grid used for the actor
    
    critic.alpha  = 0.25;  % Learning rate for the critic
    critic.grids   = 16;    % Total of grid used for the actor
    critic.tiles   = 2048; % Total of tiles per grid used for the actor
    
    gamma         = 0.98;  % Discount rate
    lambda        = 0.92;  % Decay rate
    memory_size   = 100;   % Eligibility trace memory
    episodes      = 150;  % Total of episodes
    steps         = 100;   % Steps per episode
    sd            = 0.1;   % Standard-deviation for gaussian noise in action
        
    % Initialize weights for FA
    critic.weights = zeros([critic.grids critic.tiles]);
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
        Z_obs = zeros([memory_size 2]);
        
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
            a = fa_estimate(norm_obs, actor) + random_u;
            a = max(a, spec.action_min);
            a = min(a, spec.action_max);
            % disp(a);
            
            % Actuate
            [obs, reward, terminal] = env_mops_sim('step', a);
%            norm_obs = normalize(obs, spec.observation_dims, spec.observation_min, spec.observation_max);
            norm_obs = obs ./ [ pi/10, pi];
%            norm_reward = normalize(reward, 1, spec.reward_min, spec.reward_max);
            norm_reward = reward;
            
            % Decay Eligibility trace
            Z_values = Z_values*lambda*gamma;
            
            % Add obs to Eligibility trace
            [~, trace_pos] = min(Z_values);
            Z_values(trace_pos) = 1;
            Z_obs(trace_pos,:) = norm_obs;
            
            % Don't learn on the first step
            if (tt > 0)
                % TD-error
                delta = norm_reward + gamma*fa_estimate(norm_obs, critic) ...
                        - fa_estimate(old_norm_obs, critic);
                
                % Update actor and critic
                % Critic update using Eligibility trace
                update_matrix = zeros([critic.grids critic.tiles]);
                traces = find(Z_values);
                for ii=1:numel(traces)
                    update_matrix = update_matrix + fa_gradient(Z_obs(traces(ii),:), critic)*Z_values(traces(ii));
                end
                critic.weights = critic.weights + (critic.alpha)*delta*update_matrix;
                
                % Actor update
                actor.weights = actor.weights + (actor.alpha)*delta*random_u*fa_gradient(norm_obs, actor);
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