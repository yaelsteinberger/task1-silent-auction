package MOCKs;

import entity.User;
import entity.command.Command;
import entity.command.Opcodes;
import entity.command.schemas.AddBidMessage;
import entity.command.schemas.LoginUserMessage;

public class MockCommands {
    public static Command getMockLoginCommand(User user){
        Integer opcode = Opcodes.LOGIN_CLIENT;
        LoginUserMessage message = new LoginUserMessage(user);

        return new Command(opcode,message);
    }

    public static Command getMockRegisterCommand(User user){
        Integer opcode = Opcodes.REGISTER_CLIENT;
        LoginUserMessage message = new LoginUserMessage(user);

        return new Command(opcode,message);
    }

    public static Command getMockAddBidCommand(Long AuctionItemId, Long bidValue){
        Integer opcode = Opcodes.ADD_BID;
        AddBidMessage message = new AddBidMessage(AuctionItemId,bidValue);

        return new Command(opcode,message);
    }
}
