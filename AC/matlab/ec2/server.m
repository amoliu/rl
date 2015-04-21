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
                [~, ~, cr] = sac_pendulum('episodes', episodes, 'verbose', true);
                mssend(sock,cr);
              case {codes.mlac}
                if debug
                    fprintf('Received mlac command\n');
                end
                [episodes] = deal(received.arguments{:});
                [~, ~, cr] = mlac_pendulum('episodes', episodes, 'verbose', true);
                mssend(sock,cr);
              case {codes.dyna}
                if debug
                    fprintf('Received dyna command\n');
                end
                [episodes, steps, alpha] = deal(received.arguments{:});
                [~, ~, cr] = dyna_pendulum('episodes', episodes, 'steps', steps, 'alpha', alpha, 'verbose', true);
                mssend(sock,cr);
              case {codes.dyna_mlac}
                if debug
                    fprintf('Received dyna-mlac command\n');
                end
                [episodes, steps, actorScale, criticScale, processScale] = deal(received.arguments{:});
                [~, ~, cr] = dyna_mlac_pendulum('episodes', episodes, 'steps', steps, 'actorScale', actorScale, 'criticScale', criticScale, 'processScale', processScale, 'verbose', true);
                mssend(sock,cr);
              case {codes.all}
                if debug
                    fprintf('Received all command\n');
                end
                [episodes, dyna_episodes, start_power, end_power, whoami] = deal(received.arguments{:});
                mssend(sock,'1');
                msclose(sock);
       
                if episodes > 0
                    path = '/mnt/s3/sac/';
                    [~, ~, cr] = sac_pendulum('mode', 'episode', 'episodes', episodes, 'verbose', true);
                    t = strcat('sac-', num2str(episodes), '-episodes-', num2str(whoami));
                    save(strcat(path,t), 'cr');

                    path = '/mnt/s3/mlac/';
                    [~, ~, cr] = mlac_pendulum('mode', 'episode', 'episodes', episodes, 'verbose', true);
                    t = strcat('mlac-', num2str(episodes), '-episodes-', num2str(whoami));
                    save(strcat(path,t), 'cr');
                end
                
                if numel(dyna_episodes) > 0
                    path = '/mnt/s3/dyna/';
                    for power=start_power:end_power
                        steps = 2^power;
                        fprintf(strcat('Dyna ', num2str(steps), ' \n'));
                        [~, ~, cr] = dyna_pendulum('mode', 'episode', 'episodes', dyna_episodes(power), 'steps', steps, 'verbose', true);
                        t = strcat('dyna', num2str(steps), '-', num2str(whoami));
                        save(strcat(path,t), 'cr');
                    end

                    path = '/mnt/s3/dyna-mlac/';
                    for power=start_power:end_power
                        steps = 2^power;
                        fprintf(strcat('Dyna-mlac ', num2str(steps), ' \n'));
                        [~, ~, cr] = dyna_mlac_pendulum('mode', 'episode', 'episodes', dyna_episodes(power), 'steps', steps, 'verbose', true);
                        t = strcat('dyna-mlac', num2str(steps), '-', num2str(whoami));
                        save(strcat(path,t), 'cr');
                    end
                end
                return;
              case {codes.closesocket}
                msclose(sock);
                if debug
                    fprintf('Closed socket\n');
                end
                break;
              case {codes.alpha_dyna}
                if debug
                    fprintf('Received alpha dyna command\n');
                end
                [whoami] = deal(received.arguments{:});
                mssend(sock,'1');
                msclose(sock);
                
                path = '/mnt/s3/alpha/';
                alpha = 1;
                for i=1:4
                    [~, ~, cr] = dyna_mlac_pendulum('episodes', 20, 'steps', 64, 'alpha', alpha, 'verbose', true);
                    t = strcat('dyna-mlac-', num2str(alpha), '-', num2str(whoami));
                    save(strcat(path,t), 'cr');
                    
                    [~, ~, cr] = dyna_pendulum('episodes', 20, 'steps', 64, 'alpha', alpha, 'verbose', true);
                    t = strcat('dyna-', num2str(alpha), '-', num2str(whoami));
                    save(strcat(path,t), 'cr');
                    alpha = alpha * 2;
                end
              case {codes.alpha_ac}
                if debug
                    fprintf('Received alpha ac command\n');
                end
                [whoami] = deal(received.arguments{:});
                mssend(sock,'1');
                msclose(sock);
                
                path = '/mnt/s3/alpha-ac/';
                scale = 0.25;
                for i=1:5
                    [~, ~, cr] = dyna_mlac_pendulum('episodes', 20, 'steps', 64, 'actorAlpha', scale, 'verbose', true);
                    t = strcat('dyna-mlac-actor-', num2str(scale), '-', num2str(whoami), '.mat');
                    save(strcat(path,t), 'cr');
                    
                    [~, ~, cr] = dyna_mlac_pendulum('episodes', 20, 'steps', 64, 'criticAlpha', scale, 'verbose', true);
                    t = strcat('dyna-mlac-critic-', num2str(scale), '-', num2str(whoami), '.mat');
                    save(strcat(path,t), 'cr');
                    
                    [~, ~, cr] = dyna_pendulum('episodes', 20, 'steps', 64, 'actorAlpha', scale, 'verbose', true);
                    t = strcat('dyna-actor', num2str(scale), '-', num2str(whoami), '.mat');
                    save(strcat(path,t), 'cr');
                    
                    [~, ~, cr] = dyna_pendulum('episodes', 20, 'steps', 64, 'criticAlpha', scale, 'verbose', true);
                    t = strcat('dyna-critic', num2str(scale), '-', num2str(whoami), '.mat');
                    save(strcat(path,t), 'cr');
                    scale = scale * 2;
                end
            case {codes.er}
                if debug
                    fprintf('Received er command\n');
                end
                [whoami] = deal(received.arguments{:});
                mssend(sock,'1');
                msclose(sock);
                
                path = '/mnt/s3/er/';
                er = 1;
                for i=1:4
                    [~, ~, cr] = dyna_mlac_pendulum('episodes', 20, 'steps', 64, 'explorationRate', er, 'verbose', true);
                    t = strcat('dyna-mlac-er', num2str(er), '-', num2str(whoami));
                    save(strcat(path,t), 'cr');
                    
                    [~, ~, cr] = dyna_mlac_pendulum('episodes', 20, 'steps', 64, 'modelExplorationRate', er, 'verbose', true);
                    t = strcat('dyna-mlac-mer', num2str(er), '-', num2str(whoami));
                    save(strcat(path,t), 'cr');
                    
                    [~, ~, cr] = dyna_pendulum('episodes', 20, 'steps', 64, 'explorationRate', er, 'verbose', true);
                    t = strcat('dyna-er', num2str(er), '-', num2str(whoami));
                    save(strcat(path,t), 'cr');
                    
                    [~, ~, cr] = dyna_pendulum('episodes', 20, 'steps', 64, 'modelExplorationRate', er, 'verbose', true);
                    t = strcat('dyna-mer', num2str(er), '-', num2str(whoami));
                    save(strcat(path,t), 'cr');
                    er = er * 2;
                end
            case {codes.memory}
               if debug
                    fprintf('Received memory command\n');
                end
                [whoami] = deal(received.arguments{:});
                mssend(sock,'1');
                msclose(sock);
                
                path = '/mnt/s3/memory/';
                memory = [2000 1000 500 250 125 60 30 15];
                actorNeighbors = [20 10 5 3 2 2 2 2];
                criticNeighbors = [40 20 10 5 3 2 2 2];
                processNeighbors = [40 20 20 10 10 5 3 2];
                for i=1:size(memory,2)
                    [~, ~, cr] = dyna_mlac_pendulum('episodes', 20, 'steps', 64, 'actorMemory', memory(i), 'actorNeighbors', actorNeighbors(i), 'verbose', true);
                    t = strcat('dyna-mlac-actor', num2str(memory(i)), '-', num2str(whoami));
                    save(strcat(path,t), 'cr');
                                      
                    [~, ~, cr] = dyna_mlac_pendulum('episodes', 20, 'steps', 64, 'criticMemory', memory(i), 'criticNeighbors', criticNeighbors(i), 'verbose', true);
                    t = strcat('dyna-mlac-critic', num2str(memory(i)), '-', num2str(whoami));
                    save(strcat(path,t), 'cr');
                    
                    [~, ~, cr] = dyna_mlac_pendulum('episodes', 20, 'steps', 64, 'processMemory', memory(i), 'processNeighbors', processNeighbors(i), 'verbose', true);
                    t = strcat('dyna-mlac-process', num2str(memory(i)), '-', num2str(whoami));
                    save(strcat(path,t), 'cr');
                end      
            end
        end
    end