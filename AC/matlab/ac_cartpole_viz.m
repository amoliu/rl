function ac_cartpole_viz(actor, critic, cr)
    figure;
    plot(cr);
   
    figure;
    criticInput = critic.getLLR.getMatlabDataInput;
    criticOutput = critic.getLLR.getMatlabDataOutput;
    scatter(criticInput(:,1), criticInput(:,2), 25, criticOutput, 'filled')
    title('Critic');
    xlabel('Velocity');
    ylabel('Position');
    colorbar;
    
    figure;
    actorInput = actor.getLLR.getMatlabDataInput;
    actorOutput = actor.getLLR.getMatlabDataOutput;
    scatter(actorInput(:,1), actorInput(:,2), 25, actorOutput, 'filled')
    title('Actor');
    xlabel('Velocity');
    ylabel('Position');
    colorbar;
    
    figure;
    criticInput = critic.getLLR.getMatlabDataInput;
    criticOutput = critic.getLLR.getMatlabDataOutput;
    scatter(criticInput(:,3), criticInput(:,4), 25, criticOutput, 'filled')
    title('Critic');
    xlabel('angle[rad]');
    ylabel('angular velocity[rad/s]');
    colorbar;
    
    figure;
    actorInput = actor.getLLR.getMatlabDataInput;
    actorOutput = actor.getLLR.getMatlabDataOutput;
    scatter(actorInput(:,3), actorInput(:,4), 25, actorOutput, 'filled')
    title('Actor');
    xlabel('angle[rad]');
    ylabel('angular velocity[rad/s]');
    colorbar;
    
    % Simulation
    % Initialize simulation
    opts.swingup = 1;
    env_cartpole('init', opts);
    
    norm_factor = [1/10, 1/4, pi/10, pi/2];
    
    % Reset simulation to initial condition
    obs = env_mops_sim('start');
    norm_obs = obs ./ norm_factor;

    f = figure;
    h = viz_cartpole(obs);
    title('Cartpole');
    
    terminal = 0;
    while terminal == 0
        viz_cartpole(obs, h);
        
        a = actor.actionWithoutRandomness(norm_obs);
        disp(a);
        [obs, ~, terminal] = env_cartpole('step', a);
        norm_obs = obs ./ norm_factor;
        
        pause(0.05);
    end

    % Destroy simulation
    env_cartpole('fini');
end