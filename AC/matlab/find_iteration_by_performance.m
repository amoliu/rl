function [m, c] = find_iteration_by_performance(experiments, desired_performance, trials_in_a_row)
    [trials, episodes] = size(experiments);
    
    performance = zeros(trials, 1);
    
    for i=1:trials
        above = 0;
        for j=1:episodes
            if (experiments(i,j) >= desired_performance)
                above = above + 1;
            else
                above = 0;
            end
            
            if above >= trials_in_a_row
                break;
            end
        end
        performance(i) = j;
    end
    
    m = mean(performance);
    c = 1.96.*std(performance)./sqrt(trials);
end