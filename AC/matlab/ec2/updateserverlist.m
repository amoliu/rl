% FINDSERVERS - find all running servers on instances on EC2
%
% It first finds all your running EC2 instances, then tries the ports
% 9001-9016 on each to see if they respond.

function [s,servers] = updateserverlist(s)
    
    codes = messagecodes;
    instances = findinstances(s);
    count = 1;
    
    
    for k=1:length(instances)
        for port=9001:9020
            sock = msconnect(instances{k},port);
            if sock ~= -1
                % Try to make a connection
                received = msrecv(sock,5);
                if isstruct(received) && isfield(received,'accepted') && received.accepted==1
                    %success, so add it to the list
                    % close it
                    m.command = codes.closesocket;
                    success = mssend(sock,m);
                    servers(count).address = instances{k};
                    servers(count).port = port;
                    count = count+1;
                end
                msclose(sock);
            end
        end
    end
    if count==1
        servers = [];
    end
    
    s.servers = servers;
