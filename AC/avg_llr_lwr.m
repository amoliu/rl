clear all;
close all;

episodes = 50;
trials = 10;
steps_per_iteration = 20;

for step=0:steps_per_iteration
    cr = zeros(trials,episodes);
    rmse = zeros(trials,episodes);

    parfor i=1:trials
        [~, ~, cr(i,:), rmse(i,:)] = llr_lwr_ac_pendulum(episodes, step);
    end

    figure;
    errorbaralpha(mean(cr), std(cr), 'title', strcat('dyna-', num2str(step), '-', num2str(trials), '-iterations'));
    
    %errorbaralpha(mean(rmse), std(rmse));
end