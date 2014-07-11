function est = fa_estimate(state, params)
    active_tiles = GetTiles_Mex(params.grids, state, params.tiles, 1);
    est = sum(params.weights(active_tiles + params.offset));
end