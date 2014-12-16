clear all;
close all;

path = make_save_folder('mlac');

episodes = 400;
trials = 25;

cr = zeros(trials,episodes);
rmse = zeros(trials,episodes);

parfor_progress(trials);
parfor i=1:trials
    [~, ~, cr(i,:), rmse(i,:)] = mlac_pendulum('mode', 'episode', 'episodes', episodes);
    parfor_progress;
end
parfor_progress(0);

axis_limits = [0,episodes,-6000,0];

t = strcat('mlac-', num2str(trials), '-iterations-', num2str(episodes), '-episodes');
h = errorbaralpha(mean(cr), 1.96.*std(cr)./sqrt(trials), 'Title', t, 'Rendering', 'opaque', 'Axis', axis_limits);
saveas(h, strcat(path, t), 'png');
save(strcat(path, t), 'cr');

figure;
t = strcat('mlac-rmse', num2str(trials), '-iterations-', num2str(episodes), '-episodes');
h = errorbaralpha(mean(rmse), 1.96.*std(rmse)./sqrt(trials), 'Title', t, 'Rendering', 'opaque');
saveas(h, strcat(path, t), 'png');
save(strcat(path, t), 'rmse');

h = figure;
t = strcat('mlac-', num2str(trials), '-iterations-', num2str(episodes), '-episodes-curves');
title(t);
axis(axis_limits);
xlabel('Trials');
ylabel('Average reward');
hold on;
for i=1:trials
    plot(cr(i,:));
end
hold off;
%saveas(h, strcat(path, t), 'png');