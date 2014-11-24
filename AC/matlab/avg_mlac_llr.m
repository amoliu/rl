clear all;
close all;

path = make_save_folder('mlac');

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

t = strcat('mlac-', num2str(trials), '-iterations-', num2str(episodes), '-episodes');
h = errorbaralpha(mean(cr), 1.96.*std(cr)./sqrt(trials), 'title', t);
saveas(h, strcat(path, t), 'png');

figure;
t = strcat('mlac-rmse', num2str(trials), '-iterations-', num2str(episodes), '-episodes');
h = errorbaralpha(mean(rmse), 1.96.*std(rmse)./sqrt(trials), 'title', t);
saveas(h, strcat(path, t), 'png');

h = figure;
t = strcat('mlac-', num2str(trials), '-iterations-', num2str(episodes), '-episodes-curves');
title(t);
xlabel('Trials');
ylabel('Average reward');
hold on;
for i=1:trials
    plot(cr(i,:));
end
hold off;
saveas(h, strcat(path, t), 'png');