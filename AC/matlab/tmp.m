path = '/home/bruno/Documentos/mestrado/rl/AC/results/mat/memory/';

episodes = 16;
trials = 20;

memory = [2000 1000 500 250 125 60 30 15];
actorNeighbors = [20 10 5 3 2 2 2 2];
criticNeighbors = [40 20 10 5 3 2 2 2];
processNeighbors = [20 10 5 3 2 2 2 2];

t_cr = zeros(episodes, trials);
for i=1:size(memory,2)    
    for j=1:episodes
        t = strcat('dyna-mlac-actor', num2str(memory(i)), '-', num2str(j));
        load(strcat(path,t), 'cr');
        
        t_cr(j,:) = cr;
    end
    cr = t_cr;
    t = strcat('dyna-mlac-actor', num2str(memory(i)));
    save(strcat(path, t), 'cr');
end

for i=1:size(memory,2)    
    for j=1:episodes
        t = strcat('dyna-mlac-critic', num2str(memory(i)), '-', num2str(j));
        load(strcat(path,t), 'cr');
    end
    cr = t_cr;
    t = strcat('dyna-mlac-critic', num2str(memory(i)));
    save(strcat(path, t), 'cr');
end

for i=1:size(memory,2)    
    for j=1:episodes
        t = strcat('dyna-mlac-process', num2str(memory(i)), '-', num2str(j));
        load(strcat(path,t), 'cr');
    end
    cr = t_cr;
    t = strcat('dyna-mlac-process', num2str(memory(i)));
    save(strcat(path, t), 'cr');
end

for i=1:size(memory,2)    
    for j=1:episodes
        t = strcat('dyna-actor', num2str(memory(i)), '-', num2str(j));
        load(strcat(path,t), 'cr');
    end
    cr = t_cr;
    t = strcat('dyna-actor', num2str(memory(i)));
    save(strcat(path, t), 'cr');
end

for i=1:size(memory,2)    
    for j=1:episodes
        t = strcat('dyna-critic', num2str(memory(i)), '-', num2str(j));
        load(strcat(path,t), 'cr');
    end
    cr = t_cr;
    t = strcat('dyna-critic', num2str(memory(i)));
    save(strcat(path, t), 'cr');
end

for i=1:size(memory,2)    
    for j=1:episodes
        t = strcat('dyna-process', num2str(memory(i)), '-', num2str(j));
        load(strcat(path,t), 'cr');
    end
    cr = t_cr;
    t = strcat('dyna-process', num2str(memory(i)));
    save(strcat(path, t), 'cr');
end