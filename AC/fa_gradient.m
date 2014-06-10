function est = fa_gradient(state, params)
    active_tiles = GetTiles_Mex(params.grids, state, params.tiles, 1);
    est = zeros([params.grids params.tiles]);
    for i=1:params.grids
        est(i, active_tiles(i)) = 1;
    end
end