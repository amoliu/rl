function [critic, actor, cr] = llr_ac_pendulum(episodes)
    % Initialize environment
    spec = env_mops_sim('init');
    javaSpec = br.ufrj.ppgi.rl.Specification;

    javaSpec.setActorAlpha(0.01);
    javaSpec.setActorMemory(5000);
    javaSpec.setActorNeighbors(20)
    javaSpec.setActorMin(spec.action_min);
    javaSpec.setActorMax(spec.action_max);
    
    javaSpec.setCriticInitialValue(0);
    javaSpec.setCriticAlpha(0.3);
    javaSpec.setCriticMemory(4000);
    javaSpec.setCriticNeighbors(10);

    javaSpec.setObservationDimensions(spec.observation_dims);
    javaSpec.setActionDimensions(spec.action_dims);

    javaSpec.setLamda(0.65);
    javaSpec.setGamma(0.97);
    javaSpec.setSd(1.0);  
    
    agent = br.ufrj.ppgi.rl.ac.StandardActorCritic;
    agent.init(javaSpec);
    
    % Initialize learning curve
    cr = zeros(1, episodes);
    
    norm_factor = [ pi/10, pi ]; % Normalization factor used in observations
        
    for ee=1:episodes
        % Show progress
        disp(ee)

        % Reset simulation to initial condition
        first_obs = env_mops_sim('start');
        norm_first_obs = first_obs ./ norm_factor;
        
        action = agent.start(norm_first_obs);      
        
        for tt=1:100
             % Actuate
            [obs, reward, terminal] = env_mops_sim('step', action);
            norm_obs = obs ./ norm_factor;
            
            if terminal
                agent.end(reward);
                break;
            end
            
            % Learn and choose next action
            action = agent.step(reward, norm_obs);
                      
            % Keep track of learning curve
        end
        
        cr(ee) = test_ac_llr(agent);
    end  
    
    % Destroy simulation
    env_mops_sim('fini');
    agent.fini();
    
    critic = agent.getCritic();
    actor = agent.getActor();
end