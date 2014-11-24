function path = make_save_folder(type)
    formatOut = 'yyyyddmm';
    folder_name = datestr(now,formatOut);

    path = strcat('../results/', folder_name);
    mkdir(path);
    
    path = strcat(path, '/', type, '/');
    mkdir(path);
end