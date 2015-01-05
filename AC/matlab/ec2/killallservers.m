% KILLALLSERVERS - kill all servers

function s= killallservers(s)

    instances = findinstances(s);
    
    for k=1:length(instances)
        run_on_ec2(' sudo killall server', k);
        run_on_ec2(' sudo rm /tmp/tmp.*', k);
        run_on_ec2(' sudo umount /mnt/s3', k);
        run_on_ec2(' sudo mount -a', k);
        run_on_ec2(' sudo chmod 777 /mnt/s3/sac', k);
        run_on_ec2(' sudo chmod 777 /mnt/s3/mlac', k);
        run_on_ec2(' sudo chmod 777 /mnt/s3/dyna', k);
        run_on_ec2(' sudo chmod 777 /mnt/s3/dyna-mlac', k);
    end
    
    function run_on_ec2(cmd, k)
        system(sprintf(strcat('ssh -oStrictHostKeyChecking=no -i %s ubuntu@%s ', cmd), s.keylocation, instances{k}));
    end
    
    s.servers = [];
end
