function path = make_save_folder()
    formatOut = 'yyyyddmm-hhMM';
    folder_name = datestr(now,formatOut);

    path = strcat('../results/', folder_name);
    
    mkdir(path);
end