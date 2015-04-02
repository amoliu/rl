clear all;
close all;
clc;

path = '../results/mat/er/';

%Dyna plot
dyna = zeros(4, 1);
dynam = zeros(4, 1);
dyna_mlac = zeros(4, 1);
dyna_mlacm = zeros(4,1);
trial = 1;
for i=1:4
    load(strcat(path, 'dyna-er', num2str(trial)));
    m = mean(cr);
    dyna(i,:) = m(end-1);
    
    load(strcat(path, 'dyna-mer', num2str(trial)));
    m = mean(cr);
    dynam(i,:) = m(end-1);
    
    load(strcat(path, 'dyna-mlac-er', num2str(trial)));
    m = mean(cr);
    dyna_mlac(i,:) = m(end-1);
    
    load(strcat(path, 'dyna-mlac-mer', num2str(trial)));
    m = mean(cr);
    dyna_mlacm(i,:) = m(end-1);
    
    trial = trial * 2;
end

figure;
hold on;
plot(dyna,'--bo','LineWidth',2,'MarkerSize',10);
plot(dynam,'--co','LineWidth',2,'MarkerSize',10);
plot(dyna_mlac,'--ro','LineWidth',2,'MarkerSize',10);
plot(dyna_mlacm,'--go','LineWidth',2,'MarkerSize',10);
xlabel('Alpha fraction');
ylabel('End performance');
legend('Dyna', 'Dyna Model', 'Dyna-Mlac', 'Dyna-Mlac Model');
title('Different exploration rates in Dynas using 2^6 updates/control step');
grid on;
hold off;