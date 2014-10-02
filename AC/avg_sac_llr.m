clear all;
close all;

episodes = 200;
trials = 20;

cr = zeros(trials,episodes);

for i=1:trials
    [~, ~, cr(i,:)] = llr_ac_pendulum();
end

errorbaralpha(mean(cr));