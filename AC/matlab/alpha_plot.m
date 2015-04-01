clear all;
close all;
clc;

path = '../results/mat/alpha/';

%Dyna plot
dyna = zeros(4, 1);
dyna_mlac = zeros(4, 1);
trial = 1;
for i=1:4
    load(strcat(path, 'dyna-', num2str(trial)));
    m = mean(cr);
    dyna(i,:) = m(end-1);
    
    load(strcat(path, 'dyna-mlac-', num2str(trial)));
    m = mean(cr);
    dyna_mlac(i,:) = m(end-1);
    
    trial = trial * 2;
end

figure;
hold on;
plot(dyna,'--bo','LineWidth',2,'MarkerSize',10);
plot(dyna_mlac,'--ro','LineWidth',2,'MarkerSize',10);
xlabel('Alpha fraction');
ylabel('End performance');
legend('Dyna', 'Dyna-Mlac');
title('Different alpha values in Dynas using 2^6 updates/control step');
grid on;
hold off;