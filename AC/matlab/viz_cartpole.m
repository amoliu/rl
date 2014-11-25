function h = viz_cartpole(x, h)
%VIZ_CARTPOLE Cart-pole visualization
%   H = VIZ_CARTPOLE(X) returns a handle to a new visualization of
%   state X.
%   H = VIZ_CARTPOLE(X, H) adjusts the visualization of H to the new
%   state X.
%
%   AUTHOR:
%      Wouter Caarls <wouter@caarls.org>

    pos = x(1);
    angle = x(3);
    
    cart = move(R(0.5*pi)*linkshape(-0.25), [pos-0.125, 0]);
    pendulum = move(R(-angle)*linkshape(-0.5), [pos, 0]);
    
    if nargin < 2
        h(1) = patch('FaceColor','b');
        h(2) = patch('FaceColor','r');
    end
    
    set(h(1),'Xdata',cart(1,:),'Ydata',cart(2,:));
    set(h(2),'Xdata',pendulum(1,:),'Ydata',pendulum(2,:));
    axis equal
    axis([-2.4 2.4 -0.6 0.6])
    
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
