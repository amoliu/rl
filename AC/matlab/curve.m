close all;
clear all;
clc;

trials = 20;
performance = -1000;
times_in_row = 5;

power_of_two = 7;
x = linspace(0,2^power_of_two,2^power_of_two);

sac_episodes = zeros(1,trials);
mlac_episodes = zeros(1,trials);
dyna_episodes = zeros(1, power_of_two+1);

% sac dashed line
disp('SAC');
tic;
parfor_progress(trials);
parfor i=1:trials
    [~, ~, ~, sac_episodes(i)] = sac_pendulum('mode', 'performance', 'performance', performance, 'trialsInARow', times_in_row);
    parfor_progress;
end
parfor_progress(0);
toc;

% sac dashed line
disp('MLAC');
tic;
parfor_progress(trials);
parfor i=1:trials
    [~, ~, ~, ~, mlac_episodes(i)] = mlac_pendulum('mode', 'performance', 'performance', performance, 'trialsInARow', times_in_row);
    parfor_progress;
end
parfor_progress(0);
toc;

disp('DYNA');
tic;
parfor_progress(trials*(power_of_two+1));
for power=0:power_of_two
    step = 2^power;
    
    dyna_episodes_step = zeros(1,trials);
    
    parfor i=1:trials
        [~, ~, ~, ~, dyna_episodes_step(i)] = dyna_pendulum('mode', 'performance', 'steps', step, 'performance', performance, 'trialsInARow', times_in_row);
        parfor_progress;
    end
    
    dyna_episodes(power+1) = mean_without_outlier(dyna_episodes_step);
end
toc;

dyna_y = zeros(1, 2^power_of_two);
for i=power_of_two+1:-1:1
    dyna_y(:,1:2^(i-1)) = repmat(dyna_episodes(i), 1, 2^(i-1));
end

path = make_save_folder('curve');
h = figure;
hold on;
plot(x, repmat(mean_without_outlier(sac_episodes), 1, 2^power_of_two), '--r');
plot(x, repmat(mean_without_outlier(mlac_episodes), 1, 2^power_of_two), '--b');
plot(x, dyna_y, '--g');
legend('SAC','MLAC', 'DYNA');
xlabel('Computation time');
ylabel('Rise Time');
title('Performance of Dyna against SAC and MLAC');
filename = strcat('trials-', num2str(trials), '-performance-', num2str(performance), '-times_in_row-', num2str(times_in_row), '-power_of_two-', num2str(power_of_two));
saveas(h, strcat(path, filename), 'png');