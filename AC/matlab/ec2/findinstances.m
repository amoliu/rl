% FINDINSTANCES - find all running instances of EC2
% 
% instances = findinstances(s)

function instances = findinstances(s)
    
   [status,result] = system('ec2-describe-instances');
   if isempty(result)
       error('Could not find any instances');
   end
   
   % split into lines
   lines = strread(result,'%s','delimiter','\n');
   count = 1;  
   
   % We only want the lines that start with INSTANCE
   for k=1:numel(lines)
       if strncmp(lines{k},'INSTANCE',8)
           % break into columns
           columns = strread(lines{k},'%s','delimiter','\t');
           % extract the address (4th column)
           server = columns{4};
           isrunning = columns{6};
           if isrunning
               instances{count} = server;
               count = count+1;
           end
       end
   end
   

   
