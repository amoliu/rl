function plot_model(model)   
    input = model.getLWR.getMatlabDataInput;
    output = model.getLWR.getMatlabDataOutput;
    
    diff = input - output;
    
    quiverc(input(:,1),input(:,2),diff(:,1),diff(:,2));
end