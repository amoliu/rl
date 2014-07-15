#rl
==
##Points to do
1. Use S-AC on Simple Walker
2. Implement LWR in Matlab using Wouter's paper (Section III - C)
3. Try LWR-AC on Pendulum
4. Try LWR-AC on Simple Walker

=====
##New points to do using the model
1. Decouple the number of planning steps per control steps from the number of steps per planning episode. Keep track of the last model observation and start from there.

2. Border problem. 0 and 2PI are the same!

3. Test the model accuracy. RMSE

4. Different alpha for model. 1/10 of the regular alpha

5. No use ET for the model

6. We must use the transitions generate by the model until we are predicting the same trajectory from the model. Once we start predict another one,
  we should erase the model transitions and start again.

=====
##How to use past experience
1. To build a model
2. Replay it

###How to use the model
1. The generate samples and update using it;
2. To best update the actor/critic;
3. To remove the actor. Critic + model is the actor;

======
##References
* Learning rate free reinforcement learning. Grondman
