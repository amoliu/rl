% KILLALLSERVERS - kill all servers

function s= killallservers(s)

    instances = findinstances(s);
    
    for k=1:length(instances)
        system(sprintf('ssh -oStrictHostKeyChecking=no -i %s ubuntu@%s sudo killall server',...
                       s.keylocation,instances{k}));
    end
    
    s.servers = []; 
