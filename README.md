# EntityOnView
This plugin tries to optimize user experience with naturally spawned entities by restricting where an entity can spawn. Only entities that (can be seen)/seen by a player will be spawned.

**This is not tested out yet, I hope someone tests it out.**

cancel-spawn, and abort-spawn are seperated because abort-spawn is not explained well. We dont know whether a guardian will spawn in a temple(It should spawn if it is in cancel-spawn). If the guardian does not spawn in both cases, we may need a pend-spawn section(Missed temples shouldnt cause a lag).
