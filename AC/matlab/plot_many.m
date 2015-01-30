function plot_many(varargin)
    
    p = inputParser;
    p.addParameter('title','', @isstr);
    p.addParameter('xlabel','Iterations', @isstr);
    p.addParameter('ylabel','Average reward', @isstr);
    
    p.addParameter('data', {}, @iscell);
    p.addParameter('legend', {}, @iscell);
    
    p.parse(varargin{:});
    args = p.Results;

    data_size = size(args.data, 2);
    
    h = figure;
    hold on;
    handles = [];
    cmap = hsv(data_size);
    for i=1:data_size
        d = args.data{i};
        c = cmap(i, :);
        handles = [handles errorbaralpha(mean(d), 1.96.*std(d)./sqrt(size(d, 1)), 'Color', c)];
    end
    
    legend(handles, args.legend);
    xlabel(args.xlabel);
    ylabel(args.ylabel);
    title(args.title);

    hold off;
    
end