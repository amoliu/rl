function est = fa_estimate(state, params)
    active_tiles = GetTiles_Mex(params.grids, state, params.tiles, 1);
    est = 0;
    for i=1:params.grids
        est = est + params.weights(i,active_tiles(i));
    end
end