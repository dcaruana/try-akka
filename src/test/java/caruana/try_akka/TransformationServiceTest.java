package caruana.try_akka;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scala.concurrent.Await;
import scala.concurrent.Future;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.Patterns;
import akka.util.Timeout;

public class TransformationServiceTest
{
    private static Logger logger = LoggerFactory.getLogger(TransformationServiceTest.class);

    private static int NUMBER_OF_TRANSFORMS = 1000;
    private static long STATS_FREQUENCY_MS = 5000;

    private Random kindgen = new Random();
    private Random lengen = new Random();
    private Random casegen = new Random();
    private Random chargen = new Random();

    private ActorRef transformationService;

    @Before
    public void init()
    {
        ActorSystem system = ActorSystem.create("TransformSystem");
        transformationService = system.actorOf(new Props(TransformationService.class), "transformationService");
    }

    @Test
    public void simulateTransforms() throws Exception
    {
        // fire off several transforms to transformation service
        for (int i = 0; i < NUMBER_OF_TRANSFORMS; i++)
        {
            TransformKind kind = TransformKind.values()[kindgen.nextInt(TransformKind.values().length)];
            String content = genContent();
            Transform msg = new Transform(kind, content);
            transformationService.tell(msg, transformationService);
        };

        // ask about transform statistics every now and then
        do
        {
            Future<Object> future = Patterns.ask(transformationService, new CalculateStats(), 10000);
            TransformStats stats = (TransformStats) Await.result(future, new Timeout(1000).duration());

            if (logger.isInfoEnabled())
            {
                String msg = "Completed: " + stats.getNumberOfCompletedTransforms()
                    + ", Failed: " + stats.getNumberOfFailures()
                    + ", Average ms: " + stats.getAvgTransformTime()
                    + ", Remaining: " + stats.getTransformsRemaining();
                logger.info(msg);
            }

            try
            {
                Thread.sleep(STATS_FREQUENCY_MS);
            } catch (Exception e) {}
        } while (true);
    }

    private String genContent()
    {
        int chars = lengen.nextInt(30) + 1;
        char[] content = new char[chars];
        for (int i = 0; i < chars; i++)
        {
            content[i] = (char) (chargen.nextInt(26) + (casegen.nextInt(2) == 0 ? 65 : 97));
        }
        return new String(content);
    }

}
