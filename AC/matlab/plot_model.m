function plot_model(model)   
    input = model.getLWR.getMatlabDataInput;
    output = model.getLWR.getMatlabDataOutput;
        
    d = input(:,1:2) - output(:,1:2);
    for i=1:size(d, 1)
        d(i,:) = d(i,:) ./ norm(d(i,:));
    end
        
    quiver3(input(:,1),input(:,2),input(:,3),d(:,1),d(:,2),ones(size(output, 1),1));
    view(45,45);
end