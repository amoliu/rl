episodes = 15;
trials = 24;
step = 2^10;
    
cr_005 = zeros(trials,episodes);
for k=1:trials
   joblist(k).command = codes.dyna;
   joblist(k).arguments = {episodes, step, 0.005};
end
[results,finishtimes]  = runjobs(s,joblist,1);
for k=1:trials
   cr_005(k,:) = results{k};
end
save(strcat('cr_005', num2str(step)), 'cr_005');

cr_01 = zeros(trials,episodes);
for k=1:trials
   joblist(k).command = codes.dyna;
   joblist(k).arguments = {episodes, step, 0.01};
end
[results,finishtimes]  = runjobs(s,joblist,1);
for k=1:trials
   cr_01(k,:) = results{k};
end
save(strcat('cr_01', num2str(step)), 'cr_01');

cr_02 = zeros(trials,episodes);
for k=1:trials
   joblist(k).command = codes.dyna;
   joblist(k).arguments = {episodes, step, 0.02};
end
[results,finishtimes]  = runjobs(s,joblist,1);
for k=1:trials
   cr_02(k,:) = results{k};
end
save(strcat('cr_02', num2str(step)), 'cr_02');

crmlac_005 = zeros(trials,episodes);
for k=1:trials
   joblist(k).command = codes.dyna_mlac;
   joblist(k).arguments = {episodes, step, 0.005};
end
[results,finishtimes]  = runjobs(s,joblist,1);
for k=1:trials
   crmlac_005(k,:) = results{k};
end
save(strcat('crmlac_005', num2str(step)), 'crmlac_005');

crmlac_01 = zeros(trials,episodes);
for k=1:trials
   joblist(k).command = codes.dyna_mlac;
   joblist(k).arguments = {episodes, step, 0.01};
end
[results,finishtimes]  = runjobs(s,joblist,1);
for k=1:trials
   crmlac_01(k,:) = results{k};
end
save(strcat('crmlac_01', num2str(step)), 'crmlac_01');

crmlac_02 = zeros(trials,episodes);
for k=1:trials
   joblist(k).command = codes.dyna_mlac;
   joblist(k).arguments = {episodes, step, 0.02};
end
[results,finishtimes]  = runjobs(s,joblist,1);
for k=1:trials
   crmlac_02(k,:) = results{k};
end
save(strcat('crmlac_02', num2str(step)), 'crmlac_02');