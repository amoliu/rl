function h = viz_mops_sim(x, h)
%VIZ_MOPS_SIM Simulated pendulum visualization
%   H = VIZ_MOPS_SIM(X) returns a handle to a new visualization of
%   state X.
%   H = VIZ_MOPS_SIM(X, H) adjusts the visualization of H to the new
%   state X.
%
%   AUTHOR:
%      Wouter Caarls <wouter@caarls.org>

    angle = x(1);
    
    pendulum = R(angle)*linkshape(-1);
    
    if nargin < 2
        h(1) = patch('FaceColor','r');
    end
    
    set(h(1),'Xdata',pendulum(1,:),'Ydata',pendulum(2,:));
    axis equal
    axis([-1.1 1.1 -1.1 1.1])
    
    function shape = linkshape(l)
        link_width = 0.1;
        n   = linspace(pi/2,-pi/2,20);
        top_arc    = (link_width/2)*[sin(n);cos(n)];
        bottom_arc = (link_width/2)*[-sin(n);-cos(n)];
        if l<0
            bottom_arc(2,:) = bottom_arc(2,:)+l;
        else
            top_arc(2,:) = top_arc(2,:)+l;
        end
        shape = [top_arc, bottom_arc];
    end

    function rot = R(phi)
        rot = [cos(phi)  -sin(phi);
               sin(phi)   cos(phi)];
    end

    function c = move(a, b)
        c(1,:) = a(1,:) + b(1);
        c(2,:) = a(2,:) + b(2);
    end

end
