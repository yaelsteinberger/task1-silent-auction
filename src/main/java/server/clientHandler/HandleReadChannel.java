package server.clientHandler;

import auctionList.AuctionItem;
import auctionList.AuctionItemsList;
import authenticate.HttpAuthApi;
import entity.User;
import entity.command.Opcodes;
import entity.command.schemas.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import usersList.AbstractUsersList;
import entity.StatusCode;

import java.io.IOException;
import java.net.Socket;
import java.util.Optional;

public class HandleReadChannel {
    private final static Logger logger = LoggerFactory.getLogger(HandleReadChannel.class);
//    private final AbstractUsersList usersList;
    private final AuctionItemsList auctionItemsList;
    private final ChannelServices channelServices;

    public HandleReadChannel( Socket socket,
                              AbstractUsersList usersList,
                              AuctionItemsList auctionItemsList) {
//        this.usersList = usersList;
        this.auctionItemsList = auctionItemsList;
        this.channelServices = new ChannelServices(socket,usersList,auctionItemsList);
    }

    public int handleReadCommand(int opcode, BaseMessage message) throws IOException {

        int statusCode = StatusCode.FATAL_ERROR;

        switch(opcode){
            case Opcodes.CLIENT_CONNECTED:{
                logger.debug("Handling read command: CLIENT_CONNECTED");

                /* when client is connected, reply with Welcome message */
                statusCode = channelServices.sendMessageToClient(
                        Opcodes.WELCOME,
                        this.channelServices.getWelcomeMessage());
                break;
            }

            case Opcodes.LOGIN_CLIENT:{
                logger.debug("Handling read command: LOGIN_USER");
                statusCode = this.channelServices.handleLoginClient((LoginUserMessage)message);
                break;
            }

            case Opcodes.REGISTER_CLIENT:{
                logger.debug("Handling read command: REGISTER_CLIENT");
                User user = ((RegisterUserMessage)message).getUser();
                HttpAuthApi httpApi = new HttpAuthApi();
                statusCode = this.channelServices.handleRegisterActionResult(httpApi.registerUser(user));
                break;
            }

            case Opcodes.GET_AUCTION_LIST:{
                logger.debug("Handling read command: GET_AUCTION_LIST");
                String listString = this.auctionItemsList.itemsListToPrettyString();
                this.channelServices.sendMessageToClient(Opcodes.AUCTION_LIST, listString);
                statusCode = StatusCode.SUCCESS;
                break;
            }

            case Opcodes.GET_AUCTION_ITEM:{
                logger.debug("Handling read command: GET_AUCTION_ITEM");
                Long itemId = ((GetAuctionItemMessage)message).getItemId();
                AuctionItem item = this.auctionItemsList.findById(itemId);
                this.channelServices.sendMessageToClient(Opcodes.AUCTION_ITEM, item.toPrettyString());
                statusCode = StatusCode.SUCCESS;
                break;
            }

            case Opcodes.ADD_BID:{
                logger.debug("Handling read command: ADD_BID");
                statusCode = this.channelServices.handleAddBid((AddBidMessage) message);
                break;
            }

            case Opcodes.WINNER_ANNOUNCEMENT:{
                //TODO
                break;
            }
        }

        return statusCode;
    }

    public Optional<User> getUser(){
        return this.channelServices.getUser();
    }
}
