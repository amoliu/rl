clear all;
close all;

episodes = 100;
trials = 10;

cr = zeros(trials,episodes);

parfor i=1:trials
    [~, ~, cr(i,:)] = llr_ac_pendulum();
end

errorbaralpha(mean(cr), std(cr));