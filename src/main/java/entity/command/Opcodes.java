package entity.command;

import java.util.concurrent.atomic.AtomicInteger;

public interface Opcodes {
    /* INFO */
    int NONE = 0;
    int WELCOME = 1;
    int AUCTION_ITEM = 2;
    int AUCTION_LIST = 3;
    int CLIENT_CONNECTED = 4;
    int LOGIN_SUCCESS = 5;

    /* ACTIONS */
    int ACTION_FAILED = 100;
    int WINNER_ANNOUNCEMENT = 101;
    int LOGIN_CLIENT = 102;
    int REGISTER_CLIENT = 103;
    int GET_AUCTION_ITEM = 104;
    int GET_AUCTION_LIST = 105;
    int ADD_BID = 106;
    int EXIT = 107;
}
