clear all;
clc;

codes = messagecodes;

s = socket_client('/home/bruno/Documentos/aws/home.pem');
restartallservers(s);
[s,servers] = updateserverlist(s);

episodes = 150;
trials = size(servers, 2);
step = 2^9;
alpha = 500;

cr100 = zeros(trials,episodes);
cr500 = zeros(trials,episodes);
cr1000 = zeros(trials,episodes);
cr2000 = zeros(trials,episodes);

for k=1:trials
    joblist(k).command = codes.dyna;
    joblist(k).arguments = {episodes, step, 100, 1};
end
results = runjobs(s,joblist,1);
for k=1:trials
   cr100(k,:) = results{k};
end

for k=1:trials
    joblist(k).command = codes.dyna;
    joblist(k).arguments = {episodes, step, 500, 1};
end
results = runjobs(s,joblist,1);
for k=1:trials
   cr500(k,:) = results{k};
end

for k=1:trials
    joblist(k).command = codes.dyna;
    joblist(k).arguments = {episodes, step, 1000, 1};
end
results = runjobs(s,joblist,1);
for k=1:trials
   cr1000(k,:) = results{k};
end

for k=1:trials
    joblist(k).command = codes.dyna;
    joblist(k).arguments = {episodes, step, 2000, 1};
end
results = runjobs(s,joblist,1);
for k=1:trials
   cr2000(k,:) = results{k};
end

plot_many('title', strcat('Dyna ', num2str(step), ' updates/control step using alpha = 500'), 'data', {er1, er3, er5, er10}, 'legend', {'Exploration rate 1', 'Exploration rate 3', 'Exploration rate 5', 'Exploration rate 10'});