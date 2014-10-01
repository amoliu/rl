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
        initial_value;
        total_elements_for_tree;
        tree;
    end % properties
    methods(Access = private)
        function rel = update_relevance_for_point(llr, input, output)
            neighbors = get_neighbors(llr, input);
            
            for i=1:numel(neighbors)
                predict_value = calc_query_neighbors(llr, llr.data(neighbors(i),1:llr.input), neighbors);
                rel = calc_relevance(llr, llr.data(neighbors(i),llr.input+1:llr.input+llr.output), predict_value);
                
                llr.relevance(neighbors(i)) = llr.gamma*llr.relevance(neighbors(i)) + (1-llr.gamma)*rel;
                %llr.data(neighbors(i),llr.input+1:llr.input+llr.output) = predict_value;
            end
            
            predict_value = calc_query_neighbors(llr, input, neighbors);
            rel = calc_relevance(llr, output, predict_value);
        end
        
        function build_kdtree(llr)
            if llr.tree ~= 0
                kdtree_delete(llr.tree);
            end
            
            llr.tree = kdtree_build(llr.data(1:llr.last_llr-1,1:llr.input));
        end
        
        function rel = calc_relevance(llr, output, y_hat)
            if (llr.last_llr <= llr.k)
                rel = 0;
                return;
            end
            
            rel = norm(output - y_hat)^2;
        end
        
        function neighbors = get_neighbors(llr, query)
            if (llr.last_llr <= llr.k)
                neighbors = [];
                return;
            end
            
            %points = env.kdtree.knnsearch(query, 'K', env.k);
            %neighbors = knnsearch(query, llr.data(1:llr.last_llr-1,1:llr.input), llr.k);
            neighbors = kdtree_k_nearest_neighbors(llr.tree, query, llr.k);
        end
        
        function [y_hat, X] = calc_query_neighbors(llr, query, neighbors)
            if (llr.last_llr <= llr.k)
                y_hat = rand(1, llr.output) + llr.initial_value;
                X = rand(llr.output, llr.input + 1) + llr.initial_value;
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

            try
                y_hat = (X*[query 1]')';
            catch err
                disp(err);
            end
        end
    end
    methods
        function llr = LLR(memory, input, output, k, initial_value, tikhonov, gamma)
            llr.memory = memory;
            llr.input = input;
            llr.output = output;
            llr.k = k;
            llr.data = zeros([llr.memory llr.input + llr.output]);
            llr.relevance = zeros([llr.memory 1]);
            llr.last_llr = 1;
            llr.total_elements_for_tree = 0;
            llr.tree = 0;
            
            % Default values
            llr.initial_value = 0;
            llr.gamma = 0.9;
            llr.tikhonov = 0.000001;
            switch nargin
                case 5
                    llr.initial_value = initial_value;
                case 6
                    llr.initial_value = initial_value;
                    llr.tikhonov = tikhonov;
                case 7
                    llr.initial_value = initial_value;
                    llr.tikhonov = tikhonov;
                    llr.gamma = gamma;
            end
        end
        
        function pos = add(llr, input, output)
            rel = update_relevance_for_point(llr, input, output);
            
            if (llr.last_llr <= llr.memory)
                pos = llr.last_llr;
                llr.last_llr = llr.last_llr + 1;
            else
                [rel_min, pos] = min(llr.relevance);
                if (rel < rel_min)
                    return;
                end            
            end
            
            llr.relevance(pos,:) = rel;
            llr.data(pos,:) = [input output];
            
            llr.total_elements_for_tree = llr.total_elements_for_tree + 1;
            if mod(llr.total_elements_for_tree, 1) == 0
                llr.build_kdtree();
            end
        end
        
        function update(llr, delta, points, min_value, max_value)
            if nargin == 2
                llr.data(:,llr.input+1:llr.input+llr.output) = ...
                    llr.data(:,llr.input+1:llr.input+llr.output) + delta;
            else
                if numel(points)
                    llr.data(points, llr.input+1:llr.input+llr.output) = ...
                        min(max(llr.data(points,llr.input+1:llr.input+llr.output) + repmat(delta, [size(points), 1]), repmat(min_value, [size(points), 1])), repmat(max_value, [size(points), 1])); 
                end
            end
        end
        
        function [y_hat, X, neighbors] = query(llr, query)
            neighbors = get_neighbors(llr, query);
            [y_hat, X] = calc_query_neighbors(llr, query, neighbors);
        end
    end % methods
end