clear all;
close all;

episodes = 200;
trials = 25;

cr = zeros(trials,episodes);
rmse = zeros(trials,episodes);

parfor_progress(trials);
parfor i=1:trials
    [~, ~, cr(i,:), rmse(i,:)] = mlac_pendulum(episodes);
    parfor_progress;
end
parfor_progress(0);

errorbaralpha(mean(cr), 1.96.*std(cr)./sqrt(trials), 'title', strcat('mlac-', num2str(trials), '-iterations'));

figure;
errorbaralpha(mean(rmse), 1.96.*std(rmse)./sqrt(trials), 'title', strcat('mlac-rmse-', num2str(trials), '-iterations'));

figure;
title('All curves');
xlabel('Trials');
ylabel('Average reward');
hold on;
for i=1:trials
    plot(cr(i,:));
end
hold off;