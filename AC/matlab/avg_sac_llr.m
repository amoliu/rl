clear all;
close all;

path = make_save_folder('sac');

episodes = 400;
trials = 25;

cr = zeros(trials,episodes);

parfor_progress(trials);
parfor i=1:trials
    [~, ~, cr(i,:)] = sac_pendulum(episodes);
    parfor_progress;
end
parfor_progress(0);

t = strcat('sac-', num2str(trials), '-iterations-', num2str(episodes), '-episodes');
h = errorbaralpha(mean(cr), 1.96.*std(cr)./sqrt(trials), 'title', t);
saveas(h, strcat(path, t), 'png');

h = figure;
t = strcat('sac-', num2str(trials), '-iterations-', num2str(episodes), '-episodes-curves');
title(t);
xlabel('Trials');
ylabel('Average reward');
hold on;
for i=1:trials
    plot(cr(i,:));
end
hold off;
saveas(h, strcat(path, t), 'png');