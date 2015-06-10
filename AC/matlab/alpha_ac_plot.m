clear all;
close all;
clc;

x = logspace(-2,2,5);
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

h = figure;
axes('Parent',h,'Layer','top','FontSize',24);
hold on;
semilogx(x, actor,'--bo','LineWidth',2,'MarkerSize',10);
semilogx(x, critic,'--ro','LineWidth',2,'MarkerSize',10);
xlabel('Alpha fraction','FontSize',24);
ylabel('End performance','FontSize',24);
legend('Actor', 'Critic');
%title('Different alpha values in Dyna using 2^6 updates/control step');
set(gca,'xscale','log');
grid on;
saveas( gcf, 'dyna_ac', 'png' ) 
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

h = figure;
axes('Parent',h,'Layer','top','FontSize',24);
hold on;
semilogx(x, actor,'--bo','LineWidth',2,'MarkerSize',10);
semilogx(x, critic,'--ro','LineWidth',2,'MarkerSize',10);
xlabel('Alpha fraction','FontSize',24);
ylabel('End performance','FontSize',24);
legend('Actor', 'Critic');
%title('Different alpha values in Dyna-Mlac using 2^6 updates/control step');
set(gca,'xscale','log');
grid on;
%saveas( gcf, 'dyna_mlac_ac', 'png' ) 
hold off;