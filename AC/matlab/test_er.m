episodes = 100;
trials = 10;

step = 2^11;

er1 = zeros(trials,episodes);
er3 = zeros(trials,episodes);
er5 = zeros(trials,episodes);
er10 = zeros(trials,episodes);

parfor i=1:trials
[~, ~, er1(i,:)] = dyna_pendulum('mode', 'episode', 'episodes', episodes, 'steps', step, 'alpha', 500, 'explorationRate', 1, 'verbose', true);
end

parfor i=1:trials
[~, ~, er3(i,:)] = dyna_pendulum('mode', 'episode', 'episodes', episodes, 'steps', step, 'alpha', 500, 'explorationRate', 3, 'verbose', true);
end

parfor i=1:trials
[~, ~, er5(i,:)] = dyna_pendulum('mode', 'episode', 'episodes', episodes, 'steps', step, 'alpha', 500, 'explorationRate', 5, 'verbose', true);
end

parfor i=1:trials
[~, ~, er10(i,:)] = dyna_pendulum('mode', 'episode', 'episodes', episodes, 'steps', step, 'alpha', 500, 'explorationRate', 10, 'verbose', true);
end

plot_many('title', strcat('Dyna ', num2str(step), ' updates/control step using alpha = 500'), 'data', {er1, er3, er5, er10}, 'legend', {'Exploration rate 1', 'Exploration rate 3', 'Exploration rate 5', 'Exploration rate 10'});