clear all;
close all;

episodes = 400;
trials = 20;

cr = zeros(trials,episodes);
rmse = zeros(trials,episodes);

parfor i=1:trials
    [~, ~, cr(i,:), rmse(i,:)] = mlac_pendulum(episodes);
end

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