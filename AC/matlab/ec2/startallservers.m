% STARTALLSERVERS - start servers on all instances

function s = startallservers(s)
    
    instances = findinstances(s);
    
    for k=1:length(instances)
        system(sprintf('ssh -oStrictHostKeyChecking=no -i %s ubuntu@%s matlab/runserver &',...
                       s.keylocation,instances{k}));
    end
   
    pause(10); %give the servers some time to start
    
    s = updateserverlist(s);
