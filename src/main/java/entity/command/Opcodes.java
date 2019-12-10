package entity.command;

public interface Opcodes {
    int WELCOME = 0;
    int LOGIN_CLIENT = 1;
    int REGISTER_CLIENT = 2;
    int SIGN_UP_USER = 3;
    int MESSAGE_TO_CLIENT = 4;
    int MESSAGE_FROM_CLIENT = 5;
    int ACTION_FAILED = 5;
    int EXIT = 6;
    int DEBUG = 666;
}
