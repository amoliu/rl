% Run SAC on EC2

clear all;
clc;

codes = messagecodes;

s = socket_client('/home/bruno/Documentos/aws/home.pem');
[s,servers] = updateserverlist(s);

trials = 16;
episodes = 200;
power_of_two = 11;

% Sac
for k=1:trials
   joblist(k).command = codes.sac;
   joblist(k).arguments = {episodes};
end

[results,finishtimes]  = runjobs(s,joblist,1);

cr = zeros(trials,episodes);
for k=1:trials
   cr(k,:) = results{k};
end
save sac.mat cr;

% Mlac
for k=1:trials
   joblist(k).command = codes.mlac;
   joblist(k).arguments = {episodes};
end

[results,finishtimes]  = runjobs(s,joblist,1);

cr = zeros(trials,episodes);
for k=1:trials
   cr(k,:) = results{k};
end
save mlac.mat cr;

dyna_episodes = [200, 200, 100, 100, 50, 30, 30, 30, 15, 15, 15];

%Dyna
for step=1:power_of_two
    for k=1:trials
       joblist(k).command = codes.dyna;
       joblist(k).arguments = {dyna_episodes(step), 2^step, 1};
    end

    [results,finishtimes]  = runjobs(s,joblist,1);

    % Make dyna.mat file
    cr = zeros(trials,dyna_episodes(step));
    for k=1:trials
       cr(k,:) = results{k};
    end
    filename = strcat('dyna-alpha1', num2str(step), '.mat');
    save(filename, 'cr');
end

%Dyna-mlac
for step=1:power_of_two
    for k=1:trials
       joblist(k).command = codes.dyna_mlac;
       joblist(k).arguments = {dyna_episodes(step), 2^step};
    end

    [results,finishtimes]  = runjobs(s,joblist,1);

    % Make dyna.mat file
    cr = zeros(trials,dyna_episodes(step));
    for k=1:trials
       cr(k,:) = results{k};
    end
    filename = strcat('dyna-mlac', num2str(step), '.mat');
    save(filename, 'cr');
end

% All standalone
episodes = 600;
dyna_episodes = 200;
start_power = 0;
end_power = 14;
for k=1:size(servers, 2)
   whoami = k;
   joblist(k).command = codes.all;
   joblist(k).arguments = {episodes, dyna_episodes, start_power, end_power, whoami};
end
runjobs(s,joblist,1);

% Alpha experiment
for k=1:size(servers, 2)
   whoami = k;
   joblist(k).command = codes.alpha_dyna;
   joblist(k).arguments = {whoami};
end
runjobs(s,joblist,1);

% Alpha ac experiment
for k=1:size(servers, 2)
   whoami = k;
   joblist(k).command = codes.alpha_ac;
   joblist(k).arguments = {whoami};
end
runjobs(s,joblist,1);