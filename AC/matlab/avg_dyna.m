clear all;
close all;

episodes = 200;
trials = 25;
power_of_two = 5;

parfor_progress(trials*power_of_two);
for power=0:power_of_two
    step = 2^power;
    
    cr = zeros(trials,episodes);
    rmse = zeros(trials,episodes);

    parfor i=1:trials
        [~, ~, cr(i,:), rmse(i,:)] = dyna_pendulum(episodes, step);
        parfor_progress;
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
parfor_progress(0);