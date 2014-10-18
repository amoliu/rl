function [critic, actor, cr] = s_ac_walker()
    % Initialize parameters
    actor.alpha   = 0.01;      % Learning rate for the actor
    actor.grids   = 16;        % Total of grid used for the actor
    actor.tiles   = 863840;     % Total of tiles per grid used for the actor
    actor.offset  = linspace(0, actor.grids-1, actor.grids)*actor.tiles;
    
    critic.alpha  = 0.2;      % Learning rate for the critic
    critic.grids  = 16;       % Total of grid used for the actor
    critic.tiles  = 863840;   % Total of tiles per grid used for the actor
    critic.offset = linspace(0, critic.grids-1, critic.grids)*critic.tiles;
    
    gamma         = 0.99;     % Discount rate
    lambda        = 0.92;     % Decay rate
    memory_size   = 100;      % Eligibility trace memory
    episodes      = 400;       % Total of episodes
    steps         = 500;      % Steps per episode
    sd            = 0.5;      % Standard-deviation for gaussian noise in action
        
    norm_factor   = [ 0.0838, 0.1047, 0.1111, 0.2222 ]; % Normalization factor used in observations
    
    % Initialize weights for FA
    critic.weights = (rand([critic.grids critic.tiles]))/critic.grids;
    actor.weights = zeros([actor.grids actor.tiles]);
    
    % Initialize learning curve
    cr = zeros(1, episodes);

    % Initialize simulation
    spec = env_walker('init');
    
    for ee=1:episodes
        % Show progress
        disp(ee)

        % Reset simulation to initial condition
        env_walker('start');
        
        % Initialize traces
        Z_values = zeros([memory_size 1]);
        Z_obs = zeros([memory_size critic.grids]);
        
        % Random action
        a = normrnd(0, sd);
        a = max(a, spec.action_min);
        a = min(a, spec.action_max);
        
        [obs, ~, terminal] = env_walker('step', a);
        norm_old_obs = obs ./ norm_factor;
        
        for tt=1:steps
            if terminal
                break;
            end
            
            random_u = normrnd(0, sd);
            % Calculate action
            a = fa_estimate(norm_old_obs, actor) + random_u;
            a = max(a, spec.action_min);
            a = min(a, spec.action_max);
            
            disp([obs a]);
            
            % Actuate
            [obs, reward, terminal] = env_walker('step', a);
            norm_obs = obs ./ norm_factor;
            
            % Decay Eligibility trace
            Z_values = Z_values*lambda*gamma;
            
            % Add obs to Eligibility trace
            [~, last_trace] = min(Z_values);
            Z_values(last_trace) = 1;
            Z_obs(last_trace,:) = GetTiles_Mex(critic.grids, norm_old_obs, critic.tiles, 1) + critic.offset;
            
            % Don't learn on the first step
            if (tt > 1)
                % TD-error
                delta = reward + gamma*fa_estimate(norm_obs, critic) ...
                        - fa_estimate(norm_old_obs, critic);
                
                % Update actor and critic
                % Critic update using Eligibility trace
                critic_update = critic.alpha*delta/critic.grids;
                critic.weights(Z_obs(1:last_trace,:)) = critic.weights(Z_obs(1:last_trace,:)) + repmat(Z_values(1:last_trace,:).*critic_update, 1, critic.grids);
                
                % Actor update
                actor_update = (actor.alpha*random_u*delta)/actor.grids;
                
                active_tiles = GetTiles_Mex(actor.grids, norm_old_obs, actor.tiles, 1);
                actor.weights(active_tiles + actor.offset) = actor.weights(active_tiles + actor.offset) + actor_update;
                
                %disp([reward delta actor_update max(max(actor.weights))]);
            end

            % Prepare for next timestep
            norm_old_obs = norm_obs;

            % Keep track of learning curve
            cr(ee) = cr(ee) + reward;
        end
    end

    % Destroy simulation
    env_walker('fini');   
end