package entity;

public interface StatusCode {
    int SUCCESS = 1;
    int REGISTRATION_SUCCESSFUL = 2;
    int INVALID_USERNAME = 3;
    int INVALID_VALUE = 4;
    int NO_ACCOUNT_EXISTS = 5;
    int ACCOUNT_IS_DISABLED = 6;
    int ACCOUNT_ALREADY_EXISTS = 7;
    int MENU = 8;
    int START_PROCESS = 9;
    int IN_PROCESS_ASK = 10;
    int IN_PROCESS_ANSWER = 11;
    int END_PROCESS = 12;
    int TERMINATE_SESSION = 13;
    int FATAL_ERROR = 14;
}
