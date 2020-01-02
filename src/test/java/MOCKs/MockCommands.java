package MOCKs;

import entity.User;
import entity.command.Command;
import entity.command.Opcodes;
import entity.command.schemas.*;

public class MockCommands {
    public static Command getMockConnectedCommand(){
        Integer opcode = Opcodes.CLIENT_CONNECTED;
        EmptyMessage message = new EmptyMessage();

        return new Command(opcode,message);
    }

    public static Command getMockLoginCommand(User user){
        Integer opcode = Opcodes.LOGIN_CLIENT;
        LoginUserMessage message = new LoginUserMessage(user.getUserName());

        return new Command(opcode,message);
    }

    public static Command getMockRegisterCommand(User user){
        Integer opcode = Opcodes.REGISTER_CLIENT;
        RegisterUserMessage message = new RegisterUserMessage(user);

        return new Command(opcode,message);
    }

//    public static Command getMockAddBidCommand(Long AuctionItemId, Long bidValue){
//        Integer opcode = Opcodes.ADD_BID;
//        AddBidMessage message = new AddBidMessage(AuctionItemId,bidValue);
//
//        return new Command(opcode,message);
//    }

    public static Command getMockGetAuctionListCommand(){
        Integer opcode = Opcodes.GET_AUCTION_LIST;
        return new Command(opcode,new EmptyMessage());
    }

    public static Command getMockGetAuctionItemCommand(Long itemId){
        Integer opcode = Opcodes.GET_AUCTION_ITEM;
        GetAuctionItemMessage message = new GetAuctionItemMessage(itemId);

        return new Command(opcode,message);
    }
}
