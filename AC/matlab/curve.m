close all;
clear all;
clc;

desired_performance = -1000;
times_in_row = 3;
power_of_two=11;

x = linspace(0, power_of_two, power_of_two+1);

folder = '../results/mat/';

% sac dashed line
load(strcat(folder, 'sac.mat'));
[m_sac, c_sac, p_sac] = find_iteration_by_performance(cr, desired_performance, times_in_row);
disp(strcat('SAC: ', num2str(p_sac * 100), '%'));

% mlac dashed line
load(strcat(folder, 'mlac.mat'));
[m_mlac, c_mlac, p_mlac] = find_iteration_by_performance(cr, desired_performance, times_in_row);
disp(strcat('MLAC: ', num2str(p_mlac * 100), '%'));

% dyna dashed line
m_dyna = zeros(1, power_of_two+1);
c_dyna = zeros(1, power_of_two+1);
p_dyna = zeros(1, power_of_two+1);

m_dyna(1) = m_sac;
c_dyna(1) = c_sac;
p_dyna(1) = p_sac;
for i=1:power_of_two
    load(strcat(folder, 'dyna', num2str(i), '.mat'));
    [m_dyna(i+1), c_dyna(i+1), p_dyna(i+1)] = find_iteration_by_performance(cr, desired_performance, times_in_row);
end

% dyna-mlac dashed line
m_dyna_mlac = zeros(1, power_of_two+1);
c_dyna_mlac = zeros(1, power_of_two+1);
p_dyna_mlac = zeros(1, power_of_two+1);

m_dyna_mlac(1) = m_mlac;
c_dyna_mlac(1) = c_mlac;
p_dyna_mlac(1) = p_mlac;
for i=1:power_of_two
    load(strcat(folder, 'dyna-mlac', num2str(i), '.mat'));
    [m_dyna_mlac(i+1), c_dyna_mlac(i+1), p_dyna_mlac(i+1)] = find_iteration_by_performance(cr, desired_performance, times_in_row);
end

%path = make_save_folder('curve');
h = figure;
hold on;
axis_limits = [0,power_of_two,0,350];
h_sac = errorbaralpha(repmat(m_sac, 1, power_of_two+1), repmat(c_sac, 1, power_of_two+1), 'Rendering', 'alpha', 'Axis', axis_limits, 'Color', 'r');
h_mlac = errorbaralpha(repmat(m_mlac, 1, power_of_two+1), repmat(c_mlac, 1, power_of_two+1), 'Rendering', 'alpha', 'Axis', axis_limits, 'Color', 'b');
h_dyna = errorbaralpha(m_dyna, c_dyna, 'Rendering', 'alpha', 'Axis', axis_limits, 'Color', 'g');
h_dyna_mlac = errorbaralpha(m_dyna_mlac, c_dyna_mlac, 'Rendering', 'alpha', 'Axis', axis_limits, 'Color', 'm');
legend([h_sac, h_mlac, h_dyna, h_dyna_mlac], {'SAC', 'MLAC', 'DYNA', 'DYNA-MLAC' });
xlabel('Computation time');
ylabel('Rise Time');
title('Performance of Dyna against SAC and MLAC');
%filename = strcat('trials-', num2str(trials), '-performance-', num2str(performance), '-times_in_row-', num2str(times_in_row), '-power_of_two-', num2str(power_of_two));
%saveas(h, strcat(path, filename), 'png');
hold off;

h = figure;
hold on;
plot(x, p_dyna .* 100, 'g');
plot(x, p_dyna_mlac .* 100, 'm');
legend('DYNA', 'DYNA-MLAC','Location','west');
xlabel('Computation time');
ylabel('Convergence rate %');
title('Convergence rate of Dyna and Dyna-MLAC');