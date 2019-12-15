package usersList;

public interface StatusCode {
    int SUCCESS = 0;
    int REGISTRATION_SUCCESSFUL = 1;
    int INVALID_USERNAME = 10;
    int INVALID_VALUE = 11;
    int NO_ACCOUNT_EXISTS = 12;
    int ACCOUNT_IS_DISABLED = 13;
    int ACCOUNT_ALREADY_EXISTS = 14;
    int FATAL_ERROR = 100;
}
