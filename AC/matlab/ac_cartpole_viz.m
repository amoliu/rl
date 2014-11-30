function ac_cartpole_viz(actor, cr, rmse)
    figure;
    plot(cr);

    % Simulation
    % Initialize simulation
    opts.swingup = 1;
    env_cartpole('init', opts);
    
    norm_factor = [1/4, 1/2, 1/4, 1/7];
    
    % Reset simulation to initial condition
    obs = env_mops_sim('start');
    norm_obs = obs ./ norm_factor;

    f = figure;
    h = viz_cartpole(obs);
    title('Cartpole');
    
    terminal = 0;
    while terminal == 0
        viz_cartpole(obs, h);
        
        a = actor.getLLR.query(norm_obs).getMatlabResult;
        [obs, ~, terminal] = env_cartpole('step', a);
        norm_obs = obs ./ norm_factor;
        
        pause(0.05);
    end

    % Destroy simulation
    env_cartpole('fini');
end