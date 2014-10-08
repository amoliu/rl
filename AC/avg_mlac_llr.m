clear all;
close all;

episodes = 100;
trials = 10;

cr = zeros(trials,episodes);
rmse = zeros(trials,episodes);

parfor i=1:trials
    [~, ~, cr(i,:), rmse(i,:)] = mlac_pendulum();
end

errorbaralpha(mean(cr), std(cr));