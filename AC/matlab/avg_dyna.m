clear all;
close all;

path = make_save_folder('dyna');

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
    t = strcat('dyna-', num2str(step), '-', num2str(trials), '-iterations-', num2str(episodes), '-episodes');
    h = errorbaralpha(mean(cr), 1.96.*std(cr)./sqrt(trials), 'title', t);
    saveas(h, strcat(path, t), 'png');
    
    figure;
    t = strcat('dyna-rmse-', num2str(step), '-', num2str(trials), '-iterations-', num2str(episodes), '-episodes');
    h = errorbaralpha(mean(rmse), 1.96.*std(rmse)./sqrt(trials), 'title', t);
    saveas(h, strcat(path, t), 'png');
    
    h = figure;
    t = strcat('dyna-', num2str(step), '-', num2str(trials), '-iterations-', num2str(episodes), '-episodes-curves');
    title(t);
    xlabel('Trials');
    ylabel('Average reward');
    hold on;
    for i=1:trials
        plot(cr(i,:));
    end
    hold off;
    saveas(h, strcat(path, t), 'png');
end
parfor_progress(0);