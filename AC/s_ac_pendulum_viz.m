function s_ac_pendulum_viz(actor, cr)
   
    steps   = 100;   % Steps per episode
    radius  = 2;     % Radius for plot
    
    % Initialize simulation
    spec = env_mops_sim('init');
    
    % Reset simulation to initial condition
    obs = env_mops_sim('start');
    norm_obs = obs ./ [ pi/10, pi];

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
        disp(a);

        % Actuate
        [obs, ~, terminal] = env_mops_sim('step', a);
        norm_obs = obs ./ [ pi/10, pi];
        
        x = radius * cos(obs(1)-pi/2) + radius;
        y = radius * sin(obs(1)-pi/2) + radius;
        
        plot(x,y,'r*', [radius x], [radius y], '-');
        xlabel('x');
        ylabel('y');
        axis([0,2*radius,0,2*radius]);
        axis square
        title('Policy');
        M(tt)=getframe;
    end

    % Destroy simulation
    env_mops_sim('fini');
    
    % Plays animation
    movie(M,2,10);
end