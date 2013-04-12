try-akka
========

Example content transformation service developed on Akka.

Research into following:
- scale out transformations (cores and CPUs)
- recoverable transformers (on error etc)
- transformation statistics
- (future) cluster support (should be inherently available with Akka cluster support)

With Akka, the transformation service implementation turns out to be pretty simple. The service is backed by a pool of Transformers (each an actor). Transformation requests are managed by a round-robin router allowing requests to farmed out across the pool. A supervisor strategy associated with the router handles error cases and timeouts and ensures that a transformer is restarted when necessary.

Statistics are maintained by the transformation service as each transform successfully completes.
