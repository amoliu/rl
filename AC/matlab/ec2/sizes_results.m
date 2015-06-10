folder = '../../results/mat/memory/';

episodes = 20;
step = 2^6;
trials = 15;

memory = [2000 1000 500 250 125 60 30 15];
memory_size = size(memory,2);

actor = zeros(1, memory_size);
actorError = zeros(1, memory_size);

critic = zeros(1, memory_size);
criticError = zeros(1, memory_size);

processModel = zeros(1, memory_size);
processModelError = zeros(1, memory_size);

for a=1:memory_size
    filename = strcat(folder, 'dyna-mlac-actor', num2str(memory(a)), '.mat');
    load(filename);
    
    m = mean(cr);
    er = 1.96.*std(cr)./sqrt(trials);
    
    actor(a) = m(1,end);
    actorError(a) = er(1, end);
end
actor = fliplr(actor);
actorError = fliplr(actorError);

for c=1:memory_size
    filename = strcat(folder, 'dyna-mlac-critic', num2str(memory(c)), '.mat');
    load(filename);
    
    m = mean(cr);
    er = 1.96.*std(cr)./sqrt(trials);
    
    critic(c) = m(1,end);
    criticError(c) = er(1,end);
end
critic = fliplr(critic);
criticError = fliplr(criticError);

for p=1:memory_size
    filename = strcat(folder, 'dyna-mlac-process', num2str(memory(p)), '.mat');
    load(filename);
    
    m = mean(cr);
    er = 1.96.*std(cr)./sqrt(trials);
    
    processModel(p) = m(1,end);
    processModelError(p) = er(1,end);
end
processModel = fliplr(processModel);
processModelError = fliplr(processModelError);

h = figure;
axes('Parent',h,'Layer','top','FontSize',24);
hold on;
ha = errorbaralpha(actor, actorError, 'Color', [1 0 0]);
hc = errorbaralpha(critic, criticError, 'Color', [0 1 0]);
hp = errorbaralpha(processModel, processModelError, 'Color', [0 0 1]);

legend([ha, hc, hp], {'Actor', 'Critic', 'Process Model'}, 'Location','east');
ylabel('End performance','FontSize',24);
xlabel('Memory size','FontSize',24);
%title('Memory sizes in Dyna-mlac using 2^6 updates/control step');
title('')
grid on;
set(gca, 'XTickLabel', sprintf('%3.0f|', fliplr(memory)));
saveas( gcf, 'logplot_memory_size', 'png' )  
hold off;