clear all;
close all;
clc;

path = make_save_folder('sac');

episodes = 1000;
trials = 5;

cr = zeros(trials,episodes);

parfor_progress(trials);
parfor i=1:trials
    [~, ~, cr(i,:)] = sac_walker('mode', 'episode', 'episodes', episodes);
    parfor_progress;
end
parfor_progress(0);

t = strcat('sac-', num2str(trials), '-iterations-', num2str(episodes), '-episodes');
h = errorbaralpha(mean(cr), 1.96.*std(cr)./sqrt(trials), 'Title', t, 'Rendering', 'opaque');
%saveas(h, strcat(path, t), 'png');