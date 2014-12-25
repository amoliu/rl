% SOCKET_SERVER - run a server that listens on a socket for computations to perform 
function server(port,debug)
    if isdeployed
	port = str2num(port);
    end
    
    if nargin==2 && isdeployed
        debug = str2num(debug);
    end
    
    if nargin<2 || isempty(debug)
        debug = 0;
    end
    
    codes = messagecodes;
    
    socket = mslisten(port);
    
    if debug
        fprintf('Opened a socket on port %d with number %d\n',port,socket);
    end
    
    while 1
        % Keep listening until a connection is received
        sock = -1;
        while sock == -1
            sock = msaccept(socket,0.0000001);
            drawnow;
        end
        fprintf('Accepted a connection\n');
        % Send an acknowledgement
        m.accepted = 1;
        mssend(sock,m);
        
        while 1
            success = -1;
            clear rv;
            while success<0
                [received,success] = msrecv(sock,0.0000001);
                %WaitSecs(0.000001);
                drawnow;
            end
            switch received.command
              case {codes.dummy}
                % do nothing
                if debug
                    fprintf('Received dummy command\n');
                end
              case {codes.sac}
                if debug
                    fprintf('Received sac command\n');
                end
                [episodes] = deal(received.arguments{:});
                [~, ~, cr] = sac_pendulum('mode', 'episode', 'episodes', episodes, 'verbose', true);
                mssend(sock,cr);
              case {codes.mlac}
                if debug
                    fprintf('Received mlac command\n');
                end
                [episodes] = deal(received.arguments{:});
                [~, ~, cr] = mlac_pendulum('mode', 'episode', 'episodes', episodes, 'verbose', true);
                mssend(sock,cr);
              case {codes.dyna}
                if debug
                    fprintf('Received dyna command\n');
                end
                [episodes, steps] = deal(received.arguments{:});
                [~, ~, cr] = dyna_pendulum('mode', 'episode', 'episodes', episodes, 'steps', steps, 'verbose', true);
                mssend(sock,cr);
              case {codes.dyna_mlac}
                if debug
                    fprintf('Received dyna-mlac command\n');
                end
                [episodes, steps] = deal(received.arguments{:});
                [~, ~, cr] = dyna_mlac_pendulum('mode', 'episode', 'episodes', episodes, 'steps', steps, 'verbose', true);
                mssend(sock,cr);
              case {codes.all}
                if debug
                    fprintf('Received all command\n');
                end
                [episodes, start_power, end_power, whoami] = deal(received.arguments{:});
                mssend(sock,'1');
                msclose(sock);
                
                path = '/mnt/s3/';
                for power=start_power:end_power
                    steps = 2^power;
                    fprintf(strcat('Dyna-mlac ', num2str(steps), ' \n'));
                    [~, ~, cr] = dyna_mlac_pendulum('mode', 'episode', 'episodes', episodes, 'steps', steps, 'verbose', true);
                    t = strcat('dyna-mlac', num2str(steps), '-', num2str(episodes), '-episodes-', num2str(whoami));
                    save(strcat(path,t), 'cr');
                end
                
                for power=start_power:end_power
                    steps = 2^power;
                    fprintf(strcat('Dyna ', num2str(steps), ' \n'));
                    [~, ~, cr] = dyna_pendulum('mode', 'episode', 'episodes', episodes, 'steps', steps, 'verbose', true);
                    t = strcat('dyna-', num2str(steps), '-', num2str(episodes), '-episodes-', num2str(whoami));
                    save(strcat(path,t), 'cr');
                end
              case {codes.closesocket}
                msclose(sock);
                if debug
                    fprintf('Closed socket\n');
                end
                break;
            end
        end
    end