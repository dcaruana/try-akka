package caruana.try_akka;

/**
 * Request a transform
 */
public class Transform
{

    private final TransformKind kind;
    private final String content;

    public Transform(TransformKind kind, String content)
    {
        this.kind = kind;
        this.content = content;
    }

    public TransformKind getKind()
    {
        return kind;
    }

    public String getContent()
    {
        return content;
    }

}
