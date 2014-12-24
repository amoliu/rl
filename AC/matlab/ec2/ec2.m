% Run SAC on EC2

clear all;
clc;

codes = messagecodes;

s = socket_client('/home/bruno/Documentos/aws/home.pem');
[s,servers] = updateserverlist(s);

trials = 40;
episodes = 600;
power_of_two = 20;

for k=1:trials
   joblist(k).command = codes.sac;
   joblist(k).arguments = {episodes};
end

[results,finishtimes]  = runjobs(s,joblist,1);

% Make sac.mat file
sac = zeros(trials,episodes);
for k=1:trials
   sac(k,:) = results{k};
end
save sac.mat sac;

%Dyna
for step=1:power_of_two
    for k=1:trials
       joblist(k).command = codes.dyna;
       joblist(k).arguments = {episodes, 2^step};
    end

    [results,finishtimes]  = runjobs(s,joblist,1);

    % Make dyna.mat file
    dyna = zeros(trials,episodes);
    for k=1:trials
       dyna(k,:) = results{k};
    end
    filename = strcat('dyna', num2str(step), '.mat');
    save(filename, 'dyna');
end

%Dyna-mlac
for step=1:power_of_two
    for k=1:trials
       joblist(k).command = codes.dyna_mlac;
       joblist(k).arguments = {episodes, 2^step};
    end

    [results,finishtimes]  = runjobs(s,joblist,1);

    % Make dyna.mat file
    dyna_mlac = zeros(trials,episodes);
    for k=1:trials
       dyna_mlac(k,:) = results{k};
    end
    filename = strcat('dyna-mlac', num2str(step), '.mat');
    save(filename, 'dyna_mlac');
end