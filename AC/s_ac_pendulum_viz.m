function s_ac_pendulum_viz(actor, cr)
   
    steps   = 100;   % Steps per episode
    radius  = 2;     % Radius for plot
    
    % Initialize simulation
    spec = env_mops_sim('init');
    
    % Reset simulation to initial condition
    obs = env_mops_sim('start');
    norm_obs = normalize(obs, spec.observation_dims, spec.observation_min, spec.observation_max);

    terminal = 0;
    
    trials = size(cr, 2);
    x = linspace(1, trials, trials);
    plot(x, cr);
    
    figure;  
    for tt=1:steps       
        if terminal
            break;
        end
            
        % Calculate action
        a = fa_estimate(norm_obs, actor);
        a = max(a, spec.action_min);
        a = min(a, spec.action_max);
        % disp(a);

        % Actuate
        [obs, ~, terminal] = env_mops_sim('step', a);
        norm_obs = normalize(obs, spec.observation_dims, spec.observation_min, spec.observation_max);  
        
        x = radius * cos(norm_obs(2)-pi/2) + 2;
        y = radius * sin(norm_obs(2)-pi/2) + 4;
        
        plot(x,y,'r*');
        xlabel('x');
        ylabel('y');
        axis([0,2*pi,0,2*pi])
        title('Policy');
        M(tt)=getframe;
    end

    % Destroy simulation
    env_mops_sim('fini');
    
    % Plays animation
    movie(M,2,10);
end