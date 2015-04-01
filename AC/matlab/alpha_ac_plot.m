clear all;
close all;
clc;

x = logspace(-1,2,5);
path = '../results/mat/alpha-ac/';

%Dyna plot
actor = zeros(5, 1);
critic = zeros(5, 1);
alpha = 0.25;
for i=1:5
    load(strcat(path, 'dyna-actor', num2str(alpha), '.mat'));
    m = mean(cr);
    actor(i,:) = m(end-1);
    
    load(strcat(path, 'dyna-critic', num2str(alpha), '.mat'));
    m = mean(cr);
    critic(i,:) = m(end-1);
    
    alpha = alpha * 2;
end

figure;
hold on;
semilogx(x, actor,'--bo','LineWidth',2,'MarkerSize',10);
semilogx(x, critic,'--ro','LineWidth',2,'MarkerSize',10);
xlabel('Alpha fraction');
ylabel('End performance');
legend('Actor', 'Critic');
title('Different alpha values in Dyna using 2^6 updates/control step');
set(gca,'xscale','log');
grid on;
hold off;

%Dyna mlac plot
actor = zeros(5, 1);
critic = zeros(5, 1);
alpha = 0.25;
for i=1:5
    load(strcat(path, 'dyna-mlac-actor-', num2str(alpha), '.mat'));
    m = mean(cr);
    actor(i,:) = m(end-1);
    
    load(strcat(path, 'dyna-mlac-critic-', num2str(alpha), '.mat'));
    m = mean(cr);
    critic(i,:) = m(end-1);
    
    alpha = alpha * 2;
end

figure;
hold on;
semilogx(x, actor,'--bo','LineWidth',2,'MarkerSize',10);
semilogx(x, critic,'--ro','LineWidth',2,'MarkerSize',10);
xlabel('Alpha fraction');
ylabel('End performance');
legend('Actor', 'Critic');
title('Different alpha values in Dyna-Mlac using 2^6 updates/control step');
set(gca,'xscale','log');
grid on;
hold off;