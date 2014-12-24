% RUNJOBS - run a set of jobs on the server(s)
% [results,finishtime] = runjobs(s,joblist,verbose)
%
% finishtime is only provided if verbose=1

function [results,finishtime] = runjobs(s,joblist,verbose)

    if nargin<3 || isempty(verbose)
        verbose = 0;
    end
    
    if numel(s.servers)==0
        error('There are no servers - run updateserverlist first');
    end
    
    codes = messagecodes;
   
    % Job status
    NOT_STARTED = 1;
    PROCESSING = 2;
    FINISHED = 3;
    
    % Server status
    FREE = -1;
    BUSY = -2;
    FAULTY = -10;
    % positive numbers indicate the job number it is working on
    
    jobstatus = NOT_STARTED * ones(1,numel(joblist));
    serverstatus = FREE * ones(1,numel(s.servers));
    sock = -1 * ones(1,numel(joblist));
    
    % The results go in a global variable, for recovery in case of a crash.
    global results;

    while ~all(jobstatus==FINISHED)
        % find the first not started
        nextjob = find(jobstatus==NOT_STARTED,1,'first');
        nextfreeserver = find(serverstatus==FREE,1,'first');
        % If there is a waiting job and a free server, connect to a
        % server and assign it
        if ~isempty(nextjob) && ~isempty(nextfreeserver)
            server = s.servers(nextfreeserver);
            sock(nextfreeserver) = msconnect(server.address,server.port);
            if sock(nextfreeserver)==-1
                if verbose
                    fprintf('Could not connect to server');
                end
                % Mark the server as faulty, and continue
                serverstatus(nextfreeserver) = FAULTY;
                continue;
            end
            
            m.command = joblist(nextjob).command;
            m.arguments = joblist(nextjob).arguments;
            success = mssend(sock(nextfreeserver),m);
            
            if success<0
                if verbose
                    fprintf('Could not send job to server');
                end
                % Mark the server as faulty, and continue
                serverstatus(nextfreeserver) = FAULTY;
                continue;
            end
            [thisrv,success] = msrecv(sock(nextfreeserver),5);
            if success<0 || ~isfield(thisrv,'accepted') || thisrv.accepted ~=1
                if verbose
                    fprintf('Did not get acceptance from server');
                end
                % Mark the server as faulty, and continue
                serverstatus(nextfreeserver) = FAULTY;
                continue;
            end
            % Otherwise, the command has been successfully sent, so mark
            % the server as busy and job as processing
            serverstatus(nextfreeserver) = nextjob ;
            jobstatus(nextjob) = PROCESSING;
            
            if verbose
                tstart(nextjob) = tic;
                fprintf('Sent job %d to server %d\n',nextjob,nextfreeserver);
            end
            continue;
        end
        % Check all the busy servers to see if they have finished
        % serverstatus > 0 indicates running a job
        busyservers = find(serverstatus>0);
        for servernum = busyservers
            [thisrv,success] = msrecv(sock(servernum),0.01);
            if success>=0
                jobnum = serverstatus(servernum);
                results{jobnum} = thisrv;
                jobstatus(jobnum) = FINISHED;
                if verbose
                    finishtime(jobnum) = toc(tstart(jobnum));
                    fprintf('Job %d finished after %.2f s\n',jobnum,finishtime(jobnum));
                end
                
                % close the server
                m.command = codes.closesocket;
                m.arguments = [];
                success = mssend(sock(servernum),m);
                if success<0
                    if verbose
                        fprintf('Could not close socket %d\n',servernum);
                    end
                    % Mark socket as faulty
                    serverstatus(servernum) = FAULTY;
                    continue;
                end
                if verbose
                    fprintf('Closed socket %d\n',servernum);
                end
                % Mark the socket as free
                serverstatus(servernum) = FREE;
            end
        end
    end
    
    % TODO: Kill and restart faulty servers
