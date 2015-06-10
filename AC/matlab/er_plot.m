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

h = figure;
axes('Parent',h,'Layer','top','FontSize',24);
hold on;
plot(dyna,'--bo','LineWidth',2,'MarkerSize',10);
plot(dynam,'--co','LineWidth',2,'MarkerSize',10);
plot(dyna_mlac,'--ro','LineWidth',2,'MarkerSize',10);
plot(dyna_mlacm,'--go','LineWidth',2,'MarkerSize',10);
xlabel('Exploration rate','FontSize',24);
ylabel('End performance','FontSize',24);
legend('Dyna-SAC', 'Dyna-SAC Model', 'Dyna-MLAC', 'Dyna-MLAC Model', 'Location','east')
%title('Different exploration rates in Dynas using 2^6 updates/control step');
set(gca,'XTick',[1 2 3 4])
set(gca, 'XTickLabel', {'1', '2', '4', '8'});
grid on;
saveas( gcf, 'exploration_rate_dynas', 'png' ) 
hold off;