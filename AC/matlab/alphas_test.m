episodes = 150;
trials = 10;

step = 2^4;

cr100 = zeros(trials,episodes);
cr500 = zeros(trials,episodes);
cr1000 = zeros(trials,episodes);
cr2000 = zeros(trials,episodes);

parfor i=1:trials
[~, ~, cr100(i,:)] = dyna_pendulum('mode', 'episode', 'episodes', episodes, 'steps', step, 'alpha', 100, 'verbose', true);
end

parfor i=1:trials
[~, ~, cr500(i,:)] = dyna_pendulum('mode', 'episode', 'episodes', episodes, 'steps', step, 'alpha', 500, 'verbose', true);
end

parfor i=1:trials
[~, ~, cr1000(i,:)] = dyna_pendulum('mode', 'episode', 'episodes', episodes, 'steps', step, 'alpha', 1000, 'verbose', true);
end

parfor i=1:trials
[~, ~, cr2000(i,:)] = dyna_pendulum('mode', 'episode', 'episodes', episodes, 'steps', step, 'alpha', 2000,  'verbose', true);
end

plot_many('title', strcat('Dyna ', num2str(step), ' updates/control step'), 'data', {cr100, cr500, cr1000, cr2000}, 'legend', {'Alpha 100', 'Alpha 500', 'Alpha 1000', 'Alpha 2000'})