close all;
clear all;

% Critic-like
llr = br.ufrj.ppgi.rl.fa.LLR(2000, 2, 1, 15);

total_add = 20000;
total_query = 500000;

tic;
for i=1:total_add
    llr.add(rand(1,2), rand());
end
toc;

tic;
for i=1:total_query
    llr.query(rand(1,2));
end
toc;
