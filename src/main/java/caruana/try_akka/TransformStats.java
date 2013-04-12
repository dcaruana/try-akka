package caruana.try_akka;


/**
 * Transformation statistics.
 */
public class TransformStats
{

    private final int remainingTransforms;
    private final int numberOfTransforms;
    private final int numberOfFailures;
    private final int avgTransformTime;

    public TransformStats(int remainingTransforms, int numberOfTransforms, int numberOfFailures, int avgTransformTime)
    {
        this.remainingTransforms = remainingTransforms;
        this.numberOfTransforms = numberOfTransforms;
        this.numberOfFailures = numberOfFailures;
        this.avgTransformTime = avgTransformTime;
    }

    public int getTransformsRemaining()
    {
        return this.remainingTransforms;
    }

    public int getNumberOfCompletedTransforms()
    {
        return this.numberOfTransforms;
    }

    public int getNumberOfFailures()
    {
        return this.numberOfFailures;
    }

    public int getAvgTransformTime()
    {
        return this.avgTransformTime;
    }

}
