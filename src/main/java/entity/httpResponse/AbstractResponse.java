package entity.httpResponse;

public abstract class AbstractResponse {

    /* forcing all implementation to have "isError" method */
    public abstract Boolean isError();
}