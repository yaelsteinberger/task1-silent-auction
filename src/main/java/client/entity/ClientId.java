package client.entity;

public class ClientId {
    private static String clientId;

    public static void setClientId(String clientId) {
        if(ClientId.clientId == null){
            ClientId.clientId = clientId;
        }
    }

    public static String getClientId() {
        return clientId;
    }
}
