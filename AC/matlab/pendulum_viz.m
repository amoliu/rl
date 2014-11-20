function pendulum_viz(actor, critic, cr, rmse)
%PENDULUM_VIZ Vizualizer for the pendulum swing-up problem.
%   PENDULUM_VIZ(A, C, CR) show the Actor and Critic memory, plot the
%   learning curve CR and shows a video for the Actor iteration with the
%   environment.
%
%   AUTHOR:
%       Bruno Costa <doravante2@gmail.com>
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
    
    figure;
    criticInput = critic.getLLR.getMatlabDataInput;
    criticOutput = critic.getLLR.getMatlabDataOutput;
    scatter(criticInput(:,1), criticInput(:,2), [], criticOutput, 'x')
    title('Critic');
    xlabel('angle[rad]');
    ylabel('angular velocity[rad/s]');
    colorbar;
    
    figure;
    actorInput = actor.getLLR.getMatlabDataInput;
    actorOutput = actor.getLLR.getMatlabDataOutput;
    scatter(actorInput(:,1), actorInput(:,2), [], actorOutput, 'x')
    title('Actor');
    xlabel('angle[rad]');
    ylabel('angular velocity[rad/s]');
    colorbar;
        
    % Movie
    figure;
    for tt=1:steps       
        if terminal
            break;
        end
            
        % Calculate action
        a = actor.getLLR.query(norm_obs).getMatlabResult;
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