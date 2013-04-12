package caruana.try_akka;


/**
 * Result from successful content transformation.
 */
public class TransformResult
{

    private final String transformer;
    private final int timeToTransform;
    private final String content;
    private final String transformed;
    private final TransformKind kind;

    public TransformResult(String transformer, TransformKind kind, int timeToTransform, String content, String transformed)
    {
        this.transformer = transformer;
        this.kind = kind;
        this.timeToTransform = timeToTransform;
        this.content = content;
        this.transformed = transformed;
    }

    public String getTransformer()
    {
        return transformer;
    }

    public int getTimeToTransform()
    {
        return timeToTransform;
    }

    public String getContent()
    {
        return content;
    }

    public String getTransformed()
    {
        return transformed;
    }

    public TransformKind getKind()
    {
        return kind;
    }

}
