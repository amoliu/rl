episodes = 15;
trials = 25;

for s=10:11
    step = 2^s;
    
    cr_005 = zeros(trials,episodes);
    parfor_progress(trials);
    parfor i=1:trials
        [~, ~, cr_005(i,:)] = dyna_pendulum('episodes', episodes, 'steps', step, 'actorAlpha', 0.005);
        parfor_progress;
    end
    save(strcat('cr_005', num2str(step)), 'cr_005');

    cr_01 = zeros(trials,episodes);
    parfor_progress(trials);
    parfor i=1:trials
        [~, ~, cr_01(i,:)] = dyna_pendulum('episodes', episodes, 'steps', step, 'actorAlpha', 0.01);
        parfor_progress;
    end
    save(strcat('cr_01', num2str(step)), 'cr_01');

    cr_02 = zeros(trials,episodes);
    parfor_progress(trials);
    parfor i=1:trials
        [~, ~, cr_02(i,:)] = dyna_pendulum('episodes', episodes, 'steps', step, 'actorAlpha', 0.02);
        parfor_progress;
    end
    save(strcat('cr_02', num2str(step)), 'cr_02');

    crmlac_005 = zeros(trials,episodes);
    parfor_progress(trials);
    parfor i=1:trials
        [~, ~, crmlac_005(i,:)] = dyna_mlac_pendulum('episodes', episodes, 'steps', step, 'actorAlpha', 0.005);
        parfor_progress;
    end
    save(strcat('crmlac_005', num2str(step)), 'crmlac_005');

    crmlac_01 = zeros(trials,episodes);
    parfor_progress(trials);
    parfor i=1:trials
        [~, ~, crmlac_01(i,:)] = dyna_mlac_pendulum('episodes', episodes, 'steps', step, 'actorAlpha', 0.01);
        parfor_progress;
    end
    save(strcat('crmlac_01', num2str(step)), 'crmlac_01');

    crmlac_02 = zeros(trials,episodes);
    parfor_progress(trials);
    parfor i=1:trials
        [~, ~, crmlac_02(i,:)] = dyna_mlac_pendulum('episodes', episodes, 'steps', step, 'actorAlpha', 0.02);
        parfor_progress;
    end
    save(strcat('crmlac_02', num2str(step)), 'crmlac_02');
end