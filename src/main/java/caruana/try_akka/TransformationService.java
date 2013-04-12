package caruana.try_akka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.actor.UntypedActor;
import akka.actor.SupervisorStrategy.Directive;
import akka.japi.Function;
import akka.routing.RoundRobinRouter;


/**
 * Transformation service responsible for accepting transform requests and reporting
 * on transform statistics.
 * 
 * Service utilises multiple transformers where transform requests are sent in a round-robin
 * manner. A supervisor strategy is employed for handling failed transforms, retries and
 * restarts of transformers.
 * 
 * Statistics are maintained on successful transform.
 */
public class TransformationService extends UntypedActor
{
    private static Logger logger = LoggerFactory.getLogger(TransformationService.class);

    private int remainingTransforms = 0;
    private int numberOfTransforms = 0;
    private int numberOfFailures = 0;
    private int totalTransformTime = 0;

    private final ActorRef transformRouter;
    private final RoundRobinRouter router;

    public TransformationService()
    {
        SupervisorStrategy strategy = new OneForOneStrategy(2, Duration.create("2 seconds"),
            new Function<Throwable, Directive>()
            {
                @Override
                public Directive apply(Throwable t)
                {
                    if (logger.isErrorEnabled())
                        logger.error("Caught Transformer exception " + t.getMessage(), t);
                    
                    numberOfFailures++;
                    remainingTransforms--;
                    if (t instanceof TransformException)
                    {
                        if (logger.isInfoEnabled())
                            logger.info("Restart " + t.getMessage());

                        return OneForOneStrategy.restart();
                    }
                    else
                    {
                        return OneForOneStrategy.escalate();
                    }
                }
            });

        router = new RoundRobinRouter(8).withSupervisorStrategy(strategy);
        transformRouter = this.getContext().actorOf(new Props(Transformer.class).withRouter(router), "transformRouter");
    }

    public void onReceive(Object message)
    {
        if (message instanceof Transform)
        {
            remainingTransforms++;
            transformRouter.tell(message, getSelf());
        }
        else if (message instanceof TransformResult)
        {
            TransformResult result = (TransformResult) message;
            remainingTransforms--;
            numberOfTransforms++;
            totalTransformTime += result.getTimeToTransform();

            if (logger.isDebugEnabled())
            {
                String msg = result.getTransformer() + " transformed " + result.getContent() + " to "
                        + result.getTransformed() + " [" + result.getKind() + "]"
                        + " (in " + result.getTimeToTransform() + " ms)";
                logger.debug(msg);
            }
        }
        else if (message instanceof CalculateStats)
        {
            TransformStats stats = new TransformStats(remainingTransforms, numberOfTransforms, 
                    numberOfFailures, totalTransformTime == 0 ? 0 : totalTransformTime / numberOfTransforms);
            getSender().tell(stats, getSelf());
        }
        else
        {
            unhandled(message);
        }
    }

}
