function [m, c, p] = find_iteration_by_performance(experiments, desired_performance, trials_in_a_row)
    [trials, episodes] = size(experiments);
    
    performance = [];
    
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
        
        if j ~= episodes
            performance = [performance; j];
        end
    end
    
    p = numel(performance) / trials;
    m = mean(performance);
    c = 1.96.*std(performance)./sqrt(numel(performance));
end