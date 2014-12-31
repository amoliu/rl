% Sin plot using LLR
% Only for test purposes

clear all;
close all;

llr = br.ufrj.ppgi.rl.fa.LWR.createLLR;
llr.setSize(50) ...
   .setInputDimension(1) ...
   .setOutputDimension(1) ...
   .setK(3) ...
   .setValuesToRebuildTheTree(3) ...
   .setMemoryManagement(br.ufrj.ppgi.rl.fa.LLRMemoryManagement.LLR_MEMORY_EVENLY);

figure;       
    
x = linspace(0,2*pi, 100);
for i=1:numel(x)
    llr.add(x(i), sin(x(i)));
end

for i=1:numel(x)
    llr.add(x(i), sin(x(i)));
    scatter(llr.getMatlabDataInput, llr.getMatlabDataOutput, 30, llr.getRelevance, 'fill');
    axis([0,8,-1.3,1.3]);
    M(i)=getframe;
end

x_hat = linspace(0,2*pi, 1000);
y_hat = zeros(1, numel(x_hat));
for i=1:numel(x_hat)
    y_hat(i) = llr.query(x_hat(i)).getMatlabResult;
end

hold on;
plot(x, sin(x), 'r');
plot(x_hat, y_hat, 'b');
scatter(llr.getMatlabDataInput, llr.getMatlabDataOutput);
