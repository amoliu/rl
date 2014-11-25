function ac_cartpole_viz(actor, cr, rmse)
    figure;
    plot(cr);

    % Simulation
    % Initialize simulation
    env_cartpole('init');
    
    % Reset simulation to initial condition
    obs = env_mops_sim('start');
    %norm_obs = obs ./ [ pi/10, pi];

    f = figure;
    h = viz_cartpole(obs);
    title('Cartpole');
    
    terminal = 0;
    while terminal == 0
        viz_cartpole(obs, h);
        
        a = actor.getLLR.query(obs).getMatlabResult;
        [obs, ~, terminal] = env_cartpole('step', a);
        
        pause(0.05);
    end

    % Destroy simulation
    env_cartpole('fini');
end