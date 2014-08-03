classdef LLR < handle
    properties
        data;
        memory;
        relevance;
        input;
        output;
        k;
        tikhonov;
        gamma;
        last_llr;
    end % properties
    methods(Access = private)
        function rel = calc_relevance(llr, input, output, neighbors)
            if (llr.last_llr <= llr.k)
                rel = norm(output)^2;
                return;
            end
            
            y_hat = calc_query_neighbors(llr, input, neighbors);
            rel = norm(output - y_hat')^2;
        end
        
        function neighbors = get_neighbors(llr, query)
            if (llr.last_llr <= llr.k)
                neighbors = [];
                return;
            end
            
            %points = env.kdtree.knnsearch(query, 'K', env.k);
            neighbors = knnsearch(query, llr.data(1:llr.last_llr-1,1:llr.input), llr.k);
        end
        
        function [y_hat, X] = calc_query_neighbors(llr, query, neighbors)
            if (llr.last_llr <= llr.k)
                y_hat = rand(1, llr.output);
                X = rand(llr.output, llr.input + 1);
                return;
            end
            
            N = llr.data(neighbors, :);

            A = N(:,1:llr.input)';
            A(llr.input+1,:) = 1; % bias

            B = N(:,llr.input+1:llr.input + llr.output)';

            % Using Cholesky
            % A = U'U
            %inv(A) = inv(U)*inv(U)'

            U = chol(A*A' + eye(llr.input+1)*llr.tikhonov);
            iU = inv(U);
            temp_inv = iU*iU';

            X = B*A'*temp_inv;

            y_hat = X*[query 1]';
        end
    end
    methods
        function llr = LLR(memory, input, output, k, tikhonov, gamma)
            llr.memory = memory;
            llr.input = input;
            llr.output = output;
            llr.k = k;
            llr.gamma = gamma;
            llr.tikhonov = tikhonov;
            llr.data = zeros([llr.memory llr.input + llr.output]);
            llr.relevance = zeros([llr.memory 1]);
            llr.last_llr = 1;
        end
        
        function add(llr, input, output)
            neighbors = get_neighbors(llr, input);
            rel = calc_relevance(llr, input, output, neighbors);
            
            for i=1:numel(neighbors)
                llr.relevance(neighbors(i)) = llr.gamma*llr.relevance(neighbors(i)) ...
                        + (1-llr.gamma)*calc_relevance(llr, llr.data(neighbors(i),1:llr.input), ...
                            llr.data(neighbors(i),llr.input+1:llr.input+llr.output), neighbors);
            end
            
            if (llr.last_llr <= llr.memory)
                pos = llr.last_llr;
                llr.last_llr = llr.last_llr + 1;
            else
                [~, pos] = min(llr.relevance);
            end
            
            llr.relevance(pos,:) = rel;
            llr.data(pos,:) = [input output];
        end
        
        function update(llr, delta, points, min_value, max_value)
            if nargin == 2
                llr.data(:,llr.input+1:llr.input+llr.output) = ...
                    llr.data(:,llr.input+1:llr.input+llr.output) + delta;
            else
               llr.data(points, llr.input+1:llr.input+llr.output) = ...
                min(max(llr.data(points,llr.input+1:llr.input+llr.output) + delta, min_value), max_value); 
            end
        end
        
        function [y_hat, X, neighbors] = query(llr, query)
            neighbors = get_neighbors(llr, query);
            [y_hat, X] = calc_query_neighbors(llr, query, neighbors);
        end
    end % methods
end