clear all;
close all;

episodes = 100;
trials = 10;
steps_per_iteration = 0;

for step=0:steps_per_iteration
    cr = zeros(trials,episodes);
    rmse = zeros(trials,episodes);

    parfor i=1:trials
        [~, ~, cr(i,:), rmse(i,:)] = llr_lwr_ac_pendulum(episodes, step);
    end

    figure;
    errorbaralpha(mean(cr), 1.96.*std(cr)./sqrt(trials), 'title', strcat('dyna-', num2str(step), '-', num2str(trials), '-iterations'));
    
    figure;
    errorbaralpha(mean(rmse), 1.96.*std(rmse)./sqrt(trials), 'title', strcat('rmse-dyna-', num2str(step), '-', num2str(trials), '-iterations'));
    
    figure;
    title(strcat('All curves - step', num2str(step)));
    xlabel('Trials');
    ylabel('Average reward');
    hold on;
    for i=1:trials
        plot(cr(i,:));
    end
    hold off;
end