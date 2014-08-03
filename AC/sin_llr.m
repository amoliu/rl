% Sin plot using LLR
% Only for test purposes

llr = LLR(1000, 1, 1, 3, 0.01, 0.9);

x = linspace(0,2*pi, 1000);
for i=1:numel(x)
    llr.add(x(i), sin(x(i)));
end

query = rand();

x_hat = linspace(0,2*pi, 1000);
y_hat = zeros(1, numel(x_hat));
for i=1:numel(x_hat)
    y_hat(i) = llr.query(x_hat(i));
end

hold on;
plot(x, sin(x), 'r*');
plot(x_hat, y_hat, 'b+');
