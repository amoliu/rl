function norm = normalize(obs, dims, min_obs, max_obs, min_goal, max_goal)
    norm = zeros(1, dims);
    for i=1:dims
        norm(i) = ((max_goal(i) - min_goal(i))*(obs(i) - min_obs(i))) ...
            / (max_obs(i) - min_obs(i)) + min_goal(i);
    end
end