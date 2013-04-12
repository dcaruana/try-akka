package caruana.try_akka;


/**
 * Transform exception
 */
public class TransformException extends RuntimeException
{
    private static final long serialVersionUID = -1569524533455254623L;

    public TransformException(String message)
    {
        super(message);
    }
}
