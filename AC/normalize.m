function norm = normalize(obs, dims, min, max)
    norm = zeros(1, dims);
    for i=1:dims
        norm(i) = (obs(i) - min(i)) / (max(i) - min(i));
    end
end