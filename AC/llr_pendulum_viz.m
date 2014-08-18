function [V, policy] = llr_pendulum_viz(actor, critic, cr, rmse)
   
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
    title('Performance');
    xlabel('Trials');
    ylabel('Average reward');
    
    if nargin == 4
        figure;
        plot(rmse);
        title('RMSE');
        xlabel('Trials');
        ylabel('RMSE');
    end
    
    %{
    x = linspace(0,6.2832,100);
    y = linspace(-37.6991,37.6991,100);
    %[X,Y] = meshgrid(x,y);
    % Critic
    figure;
    V = zeros(100,100);
    for i=1:100
        for j=1:100
            V(j,i) = critic.llr.query([x(i) ./ (pi/10) y(j) ./ pi]);
        end
    end
    contourf(x, y, V);
    title('Critic');
    xlabel('angle[rad]');
    ylabel('angular velocity[rad/s]');
    colorbar;
    
    % Actor
    figure;
    policy = zeros(100,100);
    for i=1:100
        for j=1:100
            policy(j,i) = actor.llr.query([x(i) ./ (pi/10) y(j) ./ pi]);
        end
    end
    contourf(x, y, policy);
    title('Actor');
    xlabel('angle[rad]');
    ylabel('angular velocity[rad/s]');
    colorbar;
    %}
    
    % Movie
    figure;
    for tt=1:steps       
        if terminal
            break;
        end
            
        % Calculate action
        a = actor.llr.query(norm_obs);
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