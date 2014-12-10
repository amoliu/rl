clear all;
close all;

path = make_save_folder('dyna');

episodes = 200;
trials = 25;
power_of_two = 12;

axis_limits = [0,episodes,-6000,0];

parfor_progress(trials*(power_of_two+1));
for power=0:power_of_two
    step = 2^power;
    
    cr = zeros(trials,episodes);
    rmse = zeros(trials,episodes);

    parfor i=1:trials
        [~, ~, temp_cr, temp_rmse] = dyna_pendulum('mode', 'episode', 'episodes', episodes, 'steps', step);
        
        filename = strcat('dyna-', num2str(step), '-', num2str(trials), '-iterations-', num2str(episodes), '-episodes-', num2str(i));
        parsave(strcat(path, filename), temp_cr);
    
        filename = strcat('dyna-rmse', num2str(step), '-', num2str(trials), '-iterations-', num2str(episodes), '-episodes-', num2str(i));
        parsave(strcat(path, filename), temp_rmse);
        
        cr(i,:) = temp_cr;
        rmse(i,:) = temp_rmse;
        
        parfor_progress;
    end

    figure;
    t = strcat('dyna-', num2str(step), '-', num2str(trials), '-iterations-', num2str(episodes), '-episodes');
    h = errorbaralpha(mean(cr), 1.96.*std(cr)./sqrt(trials), 'Title', t, 'Rendering', 'opaque', 'Axis', axis_limits);
    saveas(h, strcat(path, t), 'png');
    
    figure;
    t = strcat('dyna-rmse-', num2str(step), '-', num2str(trials), '-iterations-', num2str(episodes), '-episodes');
    h = errorbaralpha(mean(rmse), 1.96.*std(rmse)./sqrt(trials), 'Title', t, 'Rendering', 'opaque');
    saveas(h, strcat(path, t), 'png');
    
    h = figure;
    t = strcat('dyna-', num2str(step), '-', num2str(trials), '-iterations-', num2str(episodes), '-episodes-curves');
    title(t);
    axis(axis_limits);
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