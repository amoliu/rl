function plot_model(model)
    figure;
    
    %%%%%%%%%%
    % Memory %
    input = model.getLWR.getMatlabDataInput;
    output = model.getLWR.getMatlabDataOutput;
        
    d = input(:,1:2) - output(:,1:2);
    for i=1:size(d, 1)
        d(i,:) = d(i,:) ./ norm(d(i,:));
    end

    
    subplot(2,3,1);
    quiver(input(:,1),input(:,2),d(:,1),d(:,2));
    xlabel('Angle');
    ylabel('Angular speed');
    title('Model memory');
    
    %%%%%%%%%%%%%%%
    % Zero action %
    input = model.getLWR.getMatlabDataInput;
    output = zeros(size(input ,1), 2);
    for i=1:size(input ,1)
        output(i, :) = model.query(input(i, 1:2), 0).getLWRQueryVO.getMatlabResult;
    end
    d = input(:,1:2) - output(:,1:2);
    for i=1:size(d, 1)
        d(i,:) = d(i,:) ./ norm(d(i,:));
    end
    
    subplot(2,3,2);
    quiver(input(:,1),input(:,2),d(:,1),d(:,2));
    xlabel('Angle');
    ylabel('Angular speed');
    title('Model query using action zero');
    
    %%%%%%%%%%%%%%
    % Min action %
    input = model.getLWR.getMatlabDataInput;
    output = zeros(size(input ,1), 2);
    for i=1:size(input ,1)
        output(i, :) = model.query(input(i, 1:2), -3).getLWRQueryVO.getMatlabResult;
    end
    d = input(:,1:2) - output(:,1:2);
    for i=1:size(d, 1)
        d(i,:) = d(i,:) ./ norm(d(i,:));
    end
    
    subplot(2,3,3);
    quiver(input(:,1),input(:,2),d(:,1),d(:,2));
    xlabel('Angle');
    ylabel('Angular speed');
    title('Model query using min action');
    
    %%%%%%%%%%%%%%
    % Max action %
    input = model.getLWR.getMatlabDataInput;
    output = zeros(size(input ,1), 2);
    for i=1:size(input ,1)
        output(i, :) = model.query(input(i, 1:2), 3).getLWRQueryVO.getMatlabResult;
    end
    d = input(:,1:2) - output(:,1:2);
    for i=1:size(d, 1)
        d(i,:) = d(i,:) ./ norm(d(i,:));
    end
    
    subplot(2,3,4);
    quiver(input(:,1),input(:,2),d(:,1),d(:,2));
    xlabel('Angle');
    ylabel('Angular speed');
    title('Model query using max action');
    
    %%%%%%%%%%%%%%%%%%
    % Random actions %
    input = model.getLWR.getMatlabDataInput;
    output = zeros(size(input ,1), 2);
    for i=1:size(input ,1)
        output(i, :) = model.query(input(i, 1:2), rand * 6 - 3).getLWRQueryVO.getMatlabResult;
    end
    d = input(:,1:2) - output(:,1:2);
    for i=1:size(d, 1)
        d(i,:) = d(i,:) ./ norm(d(i,:));
    end
    
    subplot(2,3,5);
    quiver(input(:,1),input(:,2),d(:,1),d(:,2));
    xlabel('Angle');
    ylabel('Angular speed');
    title('Model query using random actions');
    
    %%%%%%%%%%%%%%%%%%%%%%%%
    % Random state/actions %
    input = zeros(size(input ,1), 2);
    for i=1:size(input ,1)
        input(i, :) = rand(1,2) .* [20 20] - [0 10];
    end
    
    output = zeros(size(input ,1), 2);
    for i=1:size(input ,1)
        output(i, :) = model.query(input(i, 1:2), rand * 6 - 3).getLWRQueryVO.getMatlabResult;
    end
    d = input(:,1:2) - output(:,1:2);
    for i=1:size(d, 1)
        d(i,:) = d(i,:) ./ norm(d(i,:));
    end
    
    subplot(2,3,6);
    quiver(input(:,1),input(:,2),d(:,1),d(:,2));
    xlabel('Angle');
    ylabel('Angular speed');
    title('Model query using random inputs and actions');
end