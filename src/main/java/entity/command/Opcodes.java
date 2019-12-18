package entity.command;

public interface Opcodes {
    int NONE = -1;
    int WELCOME = 0;
    int LOGIN_CLIENT = 1;
    int REGISTER_CLIENT = 2;
    int SIGN_UP_USER = 3;
    int MESSAGE_TO_CLIENT = 4;
    int MESSAGE_FROM_CLIENT = 5;
    int ACTION_FAILED = 6;
    int GET_AUCTION_LIST = 7;
    int AUCTION_LIST = 8;
    int ADD_BID = 9;
    int WINNER_ANNOUNCEMENT = 10;
    int CLIENT_CONNECTED = 11;
    int LOGIN_SUCCESS = 12;
    int GET_AUCTION_ITEM = 13;
    int AUCTION_ITEM = 14;
    int EXIT = 100;
    int DEBUG = 666;
}
