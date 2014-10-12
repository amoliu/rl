clear all;
close all;

episodes = 300;
trials = 10;

cr = zeros(trials,episodes);

parfor i=1:trials
    [~, ~, cr(i,:)] = s_ac_pendulum(episodes);
end

errorbaralpha(mean(cr), std(cr), 'title', strcat('sac-tc-', num2str(trials), '-iterations-', num2str(episodes), '-episodes'));