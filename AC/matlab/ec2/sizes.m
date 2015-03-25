clear all;
close all;
clc;

codes = messagecodes;

s = socket_client('/home/bruno/Documentos/aws/home.pem');
restartallservers(s);
[s,servers] = updateserverlist(s);

episodes = 10;
step = 2^6;
trials = 16;

cr = zeros(trials, episodes);

actorScale = 0.25;
for a=1:5
    criticScale = 0.25;
    for c=1:5
        processScale = 0.25;
        for p=1:5
            % Do it!
            for k=1:trials
               joblist(k).command = codes.dyna_mlac;
               joblist(k).arguments = {dyna_episodes(step), step, actorScale, criticScale, processScale};
            end
            
            [results,finishtimes]  = runjobs(s,joblist,1);

            % Make .mat file
            for k=1:trials
               cr(k,:) = results{k};
            end
            filename = strcat('dyna-mlac-', num2str(actorScale), '-', num2str(criticScale), '-', num2str(processScale), '.mat');
            save(filename, 'cr');
            
            %disp(strcat('Actor: ', num2str(actorScale), '. Critic: ', num2str(criticScale), '. Process: ', num2str(processScale)));
            processScale = processScale * 2;
        end
        criticScale = criticScale * 2;
    end
    actorScale = actorScale * 2;
end