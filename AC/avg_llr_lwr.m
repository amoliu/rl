clear all;
close all;

episodes = 30;
trials = 10;

cr = zeros(trials,episodes);
rmse = zeros(trials,episodes);

for i=1:trials
    [~, ~, cr(i,:), rmse(i,:)] = llr_lwr_ac_pendulum();
end

errorbaralpha(mean(cr), mean(rmse));