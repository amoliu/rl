function m = mean_without_outlier(data)
    meann = mean(data);
    stdd = std(data);
    idx = bsxfun(@lt, meann + 2*stdd, data) | bsxfun(@gt, meann - 2*stdd, data);
    m = mean(data(~idx));
end