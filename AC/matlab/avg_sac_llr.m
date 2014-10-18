clear all;
close all;

episodes = 200;
trials = 20;

cr = zeros(trials,episodes);

parfor i=1:trials
    [~, ~, cr(i,:)] = llr_ac_pendulum(episodes);
end

errorbaralpha(mean(cr), 1.96.*std(cr)./sqrt(trials), 'title', strcat('sac-', num2str(trials), '-iterations-', num2str(episodes), '-episodes'));

figure;
title('All curves');
xlabel('Trials');
ylabel('Average reward');
hold on;
for i=1:trials
    plot(cr(i,:));
end
hold off;