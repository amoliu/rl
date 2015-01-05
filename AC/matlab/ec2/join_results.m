function join_results( experiment, episodes, step, expected_files )

    path = '/home/bruno/Documentos/s3/';
    path = strcat(path, experiment, '/');
    
    name = strcat(experiment, num2str(step), '-');
       
    files = ls(path);
    found = strfind(files, name);
    trials = numel(found);
    
    if trials ~= expected_files
        disp('Different number of files found!');
        return;
    end
    
    file = strcat(path, name, num2str(episodes), '-episodes-');
    
    join_cr = zeros(trials, episodes);
    for i = 1:trials
        to_load = strcat(file, num2str(i));
        load(to_load);
        
        join_cr(i,:) = cr;
    end
    
    cr = join_cr;
    
    save_path = make_save_folder(experiment);
    axis_limits = [0,episodes,-6000,0];

    t = strcat(experiment, '-', num2str(step), '-', num2str(trials), '-iterations-', num2str(episodes), '-episodes');
    h = errorbaralpha(mean(join_cr), 1.96.*std(cr)./sqrt(trials), 'Title', t, 'Rendering', 'opaque', 'Axis', axis_limits);
    saveas(h, strcat(save_path, t), 'png');
    save(strcat(save_path, t), 'cr');

    h = figure;
    t = strcat(experiment, '-', num2str(step), '-', num2str(trials), '-iterations-', num2str(episodes), '-episodes-curves');
    title(t);
    axis(axis_limits);
    xlabel('Trials');
    ylabel('Average reward');
    hold on;
    for i=1:trials
        plot(cr(i,:));
    end
    hold off;
    saveas(h, strcat(save_path, t), 'png');
end

