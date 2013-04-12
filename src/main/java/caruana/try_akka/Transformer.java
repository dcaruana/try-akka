package caruana.try_akka;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scala.Option;
import akka.actor.UntypedActor;


/**
 * Transformer responsible for actual transform of content.
 */
public class Transformer extends UntypedActor
{
    private static Logger logger = LoggerFactory.getLogger(Transformer.class);
    
    private Random fail = new Random();
    private int numberOfTransforms = 0;

    @Override
    public void preStart()
    {
        if (logger.isInfoEnabled())
            logger.info("Transformer " + getSelf().path().name() + ": preStart");
    }

    @Override
    public void postRestart(Throwable reason)
    {
        if (logger.isInfoEnabled())
            logger.info("Transformer " + getSelf().path().name() + ": postRestart: transformed " + numberOfTransforms);
    }

    @Override
    public void preRestart(Throwable reason, Option<Object> message)
    {
        if (logger.isInfoEnabled())
            logger.info("Transformer " + getSelf().path().name() + ": preRestart: transformed " + numberOfTransforms + " times before error");
    }

    @Override
    public void postStop()
    {
        if (logger.isInfoEnabled())
            logger.info("Transformer " + getSelf().path().name() + ": postStop");
    }

    public void onReceive(Object message)
    {
        if (message instanceof Transform)
        {
            // simulate a failure now and then
            if (fail.nextInt(100) == 1)
            {
                throw new TransformException("Transformer " + getSelf().path().name() + " failed");
            }

            Transform transform = (Transform) message;
            String transformed = null;
            
            switch (transform.getKind())
            {
            case UPPER:
                transformed = transform.getContent().toUpperCase();
                break;
            case LOWER:
                transformed = transform.getContent().toLowerCase();
                break;
            case TRIM:
                transformed = transform.getContent().substring(0, 1);
                break;
            case REPEAT:
                transformed = "";
                for (int i = 0; i < 3; i++)
                {
                    transformed += transform.getContent();
                }
            default:
                break;
            }

            if (transformed == null)
            {
                unhandled(message);
            }
            else
            {
                int timeTaken = new Random().nextInt(2000);
                long quit = System.currentTimeMillis() + timeTaken;

                // do some actual work to demonstrate all cpu power is taken (regardless of number of CPUs/cores)
                Random tan = new Random();
                do
                {
                    Math.tan(tan.nextDouble());
                }
                while (System.currentTimeMillis() < quit);

                TransformResult result = new TransformResult(getSelf().path().name(), transform.getKind(), timeTaken, transform.getContent(), transformed);
                getSender().tell(result, getSelf());

                numberOfTransforms++;
            }
        }
    }
}
