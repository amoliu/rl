% SOCKET_CLIENT - A class for connecting to socket clients running elsewhere (such as on Amazon EC2)

function s = socket_client(keylocation);
    
    s.servers = [];
    s.keylocation = [];
    
    if nargin<1 || isempty(keylocation)
        s.keylocation = '~/.ec2/singapore-key.pem';
    else
        s.keylocation = keylocation;
    end
