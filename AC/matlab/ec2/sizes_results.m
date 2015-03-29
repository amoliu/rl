folder = '../../results/mat/logplot/';

episodes = 20;
step = 2^6;
trials = 15;

actor = zeros(1, 5);
actorError = zeros(1, 5);

critic = zeros(1, 5);
criticError = zeros(1, 5);

processModel = zeros(1, 5);
processModelError = zeros(1, 5);

actorScale = 0.25;
criticScale = 1;
processScale = 1;
for a=1:5
    filename = strcat(folder, 'dyna-mlac-', num2str(actorScale), '-', num2str(criticScale), '-', num2str(processScale), '.mat');
    load(filename);
    
    m = mean(cr);
    er = 1.96.*std(cr)./sqrt(trials);
    
    actor(a) = m(1,end);
    actorError(a) = er(1, end);
    
    actorScale = actorScale * 2;
end

actorScale = 1;
criticScale = 0.25;
processScale = 1;
for c=1:5
    filename = strcat(folder, 'dyna-mlac-', num2str(actorScale), '-', num2str(criticScale), '-', num2str(processScale), '.mat');
    load(filename);
    
    m = mean(cr);
    er = 1.96.*std(cr)./sqrt(trials);
    
    critic(c) = m(1,end);
    criticError(c) = er(1,end);
    
    criticScale = criticScale * 2;
end

actorScale = 1;
criticScale = 1;
processScale = 0.25;
for p=1:5
    filename = strcat(folder, 'dyna-mlac-', num2str(actorScale), '-', num2str(criticScale), '-', num2str(processScale), '.mat');
    load(filename);
    
    m = mean(cr);
    er = 1.96.*std(cr)./sqrt(trials);
    
    processModel(p) = m(1,end);
    processModelError(p) = er(1,end);
    
    processScale = processScale * 2;
end

hold on;
ha = errorbaralpha(actor, actorError, 'Color', [1 0 0]);
hc = errorbaralpha(critic, criticError, 'Color', [0 1 0]);
hp = errorbaralpha(processModel, processModelError, 'Color', [0 0 1]);

legend([ha, hc, hp], {'Actor', 'Critic', 'Process Model'}, 'Location','east');
%xlabel(args.xlabel);
ylabel('Performance');
title('Log-scale memory sizes in Dyna-mlac using 2^6 updates/control step');
hold off;