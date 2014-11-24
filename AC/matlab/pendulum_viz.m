function pendulum_viz(actor, critic, cr, rmse)
%PENDULUM_VIZ Vizualizer for the pendulum swing-up problem.
%   PENDULUM_VIZ(A, C, CR) show the Actor and Critic memory, plot the
%   learning curve CR and shows a video for the Actor iteration with the
%   environment.
%
%   AUTHOR:
%       Bruno Costa <doravante2@gmail.com>
    steps   = 100;   % Steps per episode
        
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
        
    % Simulation
    % Initialize simulation
    env_mops_sim('init');
    
    % Reset simulation to initial condition
    obs = env_mops_sim('start');
    norm_obs = obs ./ [ pi/10, pi];

    terminal = 0;
    
    f = figure;
    h = viz_mops_sim(obs);
    title('Pendulum Swing-up');
    for tt=1:steps       
        if terminal
            break;
        end
            
        % Calculate action
        a = actor.getLLR.query(norm_obs).getMatlabResult;

        % Actuate
        [obs, ~, terminal] = env_mops_sim('step', a);
        norm_obs = obs ./ [ pi/10, pi];
        
        viz_mops_sim(obs, h);
        pause(0.05);
    end

    % Destroy simulation
    env_mops_sim('fini');
end