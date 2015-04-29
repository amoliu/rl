clear all;
close all;
clc;

% MLAC
[critic, actor, cr, rmse, model, actorUpdates, criticUpdates] = mlac_pendulum('episodes', 600, 'verbose', true);
figure;
hold on;
plot(cr, 'r');
plot(criticUpdates, 'b');
plot(actorUpdates, 'g');
legend('Performance', 'TD-Error', 'Actor updates');
xlabel('Iterations');
title('Performance and TD-Error for MLAC on regular environment');
mlac_cr = cr;
mlac_cu = criticUpdates;
mlac_au = actorUpdates;
hold off;

% SAC
[critic, actor, cr, actorUpdates, criticUpdates, episodes] = sac_pendulum('episodes', 600, 'verbose', true);
figure;
hold on;
plot(cr, 'r');
plot(criticUpdates, 'b');
plot(actorUpdates, 'g');
legend('Performance', 'TD-Error', 'Actor updates');
xlabel('Iterations');
title('Performance and TD-Error for SAC on harder environment');
hold off;

% Both
figure;
hold on;
plot(cr, 'r')
plot(actorUpdates, 'b')
plot(mlac_cr, 'k')
plot(mlac_au', 'g')
legend('SAC Performance', 'SAC Actor Updates', 'MLAC Performance', 'MLAC Actor Updates')
title('Comparing SAC and MLAC on a harder environment')
xlabel('Iterations')
hold off;

figure;
hold on;
plot(abs(mlac_au) - abs(actorUpdates), 'b')
refline([0 0])