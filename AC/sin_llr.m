% Sin plot using LLR
% Only for test purposes

clear all;
close all;

llr = LLR(30, 1, 1, 5);
figure;       
    
x = linspace(0,2*pi, 100);
for i=1:numel(x)
    llr.add(x(i), sin(x(i)));
end

for i=1:numel(x)
    llr.add(x(i), sin(x(i)));
    scatter(llr.data(:,1), llr.data(:,2), 30, llr.relevance, 'fill');
    axis([0,8,-1.3,1.3]);
    M(i)=getframe;
end

x_hat = linspace(0,2*pi, 1000);
y_hat = zeros(1, numel(x_hat));
for i=1:numel(x_hat)
    y_hat(i) = llr.query(x_hat(i));
end

hold on;
plot(x, sin(x), 'r');
plot(x_hat, y_hat, 'b');
scatter(llr.data(:,1), llr.data(:,2));