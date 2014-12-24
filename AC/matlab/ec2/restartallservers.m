% RESTARTALLSERVERS - restart all the servers

function s = restartallservers(s)
    
    killallservers(s);
    s = startallservers(s);


