clear all;
close all;

episodes = 20;
trials = 10;

cr = zeros(trials,episodes);

for i=1:trials
    [~, ~, cr(i,:)] = llr_lwr_ac_pendulum();
end

plot(mean(cr));