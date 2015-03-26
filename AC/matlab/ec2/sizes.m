clear all;
close all;
clc;

codes = messagecodes;

s = socket_client('/home/bruno/Documentos/aws/home.pem');
restartallservers(s);
[s,servers] = updateserverlist(s);

episodes = 20;
step = 2^6;
trials = 15;

cr = zeros(trials, episodes);

actorScale = 0.25;
criticScale = 1;
processScale = 1;
for a=1:4
    for k=1:trials
       joblist(k).command = codes.dyna_mlac;
       joblist(k).arguments = {episodes, step, actorScale, criticScale, processScale};
    end

    [results,finishtimes]  = runjobs(s,joblist,1);

    % Make .mat file
    for k=1:trials
       cr(k,:) = results{k};
    end
    filename = strcat('dyna-mlac-', num2str(actorScale), '-', num2str(criticScale), '-', num2str(processScale), '.mat');
    save(filename, 'cr');
    actorScale = actorScale * 2;
end

actorScale = 1;
criticScale = 0.25;
processScale = 1;
for c=1:5
    for k=1:trials
       joblist(k).command = codes.dyna_mlac;
       joblist(k).arguments = {episodes, step, actorScale, criticScale, processScale};
    end

    [results,finishtimes]  = runjobs(s,joblist,1);

    % Make .mat file
    for k=1:trials
       cr(k,:) = results{k};
    end
    filename = strcat('dyna-mlac-', num2str(actorScale), '-', num2str(criticScale), '-', num2str(processScale), '.mat');
    save(filename, 'cr');
    criticScale = criticScale * 2;
end

actorScale = 1;
criticScale = 1;
processScale = 0.25;
for p=1:5
    for k=1:trials
       joblist(k).command = codes.dyna_mlac;
       joblist(k).arguments = {episodes, step, actorScale, criticScale, processScale};
    end

    [results,finishtimes]  = runjobs(s,joblist,1);

    % Make .mat file
    for k=1:trials
       cr(k,:) = results{k};
    end
    filename = strcat('dyna-mlac-', num2str(actorScale), '-', num2str(criticScale), '-', num2str(processScale), '.mat');
    save(filename, 'cr');
    processScale = processScale * 2;
end