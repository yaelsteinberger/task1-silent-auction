package usersList;

public interface StatusCode {
    int SUCCESS = 0;
    int INVALID_USERNAME = 1;
    int NO_ACCOUNT_EXISTS = 2;
    int ACCOUNT_IS_DISABLED = 3;
    int ACCOUNT_ALREADY_EXISTS = 4;
    int FATAL_ERROR = 4;
}
