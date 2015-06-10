path = '../results/mat/';

%Dyna plot
load(strcat(path, 'llr_strategies.mat'));

h = figure;
set(h,'units','normalized','outerposition',[0 0 1 1]);
hAxes = axes('Parent',h,'Layer','top','FontSize',24);
hold on;
h_rand_2k = errorbaralpha(mean(random_2k_cr), 1.96.*std(random_2k_cr)./sqrt(trials), 'Rendering', 'alpha', 'Color', 'r');
h_rand_4k = errorbaralpha(mean(random_4k_cr), 1.96.*std(random_4k_cr)./sqrt(trials), 'Rendering', 'alpha', 'Color', 'g');
h_rand_6k = errorbaralpha(mean(random_6k_cr), 1.96.*std(random_6k_cr)./sqrt(trials), 'Rendering', 'alpha', 'Color', 'b');
legend([h_rand_2k, h_rand_4k, h_rand_6k], {'Random with 2000', 'Random with 4000', 'Random with 6000' }, 'Location', 'east');
xlabel('Episodes','FontSize',24);
ylabel('Performance','FontSize',24);
title('');
saveas( gcf, 'llr_random', 'png' ) 
hold off;

h = figure;
set(h,'units','normalized','outerposition',[0 0 1 1]);
hAxes = axes('Parent',h,'Layer','top','FontSize',24);
hold on;
h_prediction = errorbaralpha(mean(prediction_cr), 1.96.*std(prediction_cr)./sqrt(trials), 'Rendering', 'alpha', 'Color', 'g');
h_prediction_4k = errorbaralpha(mean(prediction_4k_cr), 1.96.*std(prediction_4k_cr)./sqrt(trials), 'Rendering', 'alpha', 'Color', 'b');
legend([h_prediction, h_prediction_4k], {'Prediction with 2000', 'Prediction with 4000' }, 'Location', 'east');
xlabel('Episodes','FontSize',24);
ylabel('Performance','FontSize',24);
title('');
saveas( gcf, 'llr_predict', 'png' ) 
hold off;

h = figure;
set(h,'units','normalized','outerposition',[0 0 1 1]);
hAxes = axes('Parent',h,'Layer','top','FontSize',24);
hold on;
h_uniform_500 = errorbaralpha(mean(uniform_500_cr), 1.96.*std(uniform_500_cr)./sqrt(trials), 'Rendering', 'alpha', 'Color', 'r');
h_uniform_1k = errorbaralpha(mean(uniform_1k_cr), 1.96.*std(uniform_1k_cr)./sqrt(trials), 'Rendering', 'alpha', 'Color', 'g');
h_uniform_2k = errorbaralpha(mean(uniform_cr), 1.96.*std(uniform_cr)./sqrt(trials), 'Rendering', 'alpha', 'Color', 'b');
legend([h_uniform_500, h_uniform_1k, h_uniform_2k], {'Uniform with 500', 'Uniform with 1000', 'Uniform with 2000' }, 'Location', 'east');
xlabel('Episodes','FontSize',24);
ylabel('Performance','FontSize',24);
title('');
saveas( gcf, 'llr_uniform', 'png' ) 
hold off;

h = figure;
set(h,'units','normalized','outerposition',[0 0 1 1]);
hAxes = axes('Parent',h,'Layer','top','FontSize',24);
hold on;
h_uniform = errorbaralpha(mean(uniform_cr), 1.96.*std(uniform_cr)./sqrt(trials), 'Rendering', 'alpha', 'Color', 'r');
h_prediction_4k = errorbaralpha(mean(prediction_4k_cr), 1.96.*std(prediction_4k_cr)./sqrt(trials), 'Rendering', 'alpha', 'Color', 'g');
h_rand_6k = errorbaralpha(mean(random_6k_cr), 1.96.*std(random_6k_cr)./sqrt(trials), 'Rendering', 'alpha', 'Color', 'b');
legend([h_uniform, h_prediction_4k, h_rand_6k], {'Uniform 2000', 'Prediction with 6000', 'Random with 6000' }, 'Location', 'east');
xlabel('Episodes','FontSize',24);
ylabel('Performance','FontSize',24);
title('');
saveas( gcf, 'llr_strategies', 'png' );
hold off;