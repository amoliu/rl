function [y_hat, X] = calc_query_neighbors(tikhonov, input, output, data, query, neighbors)
    N = data(neighbors, :);
    
    A = zeros(input+1, numel(neighbors));
    A(1:input,:) = N(:,1:input)';
    A(input+1,:) = 1; % bias

    B = N(:,input+1:input + output)';

    % Using Cholesky
    % A = U'U
    %inv(A) = inv(U)*inv(U)'

    U = chol(A*A' + eye(input+1)*tikhonov);

    iU = inv(U);
    temp_inv = iU*iU';

    X = B*A'*temp_inv;

    y_hat = [query 1]*X';
end