public interface ReplicaManagerInterface {
    public void setUpClients(int number_of_clients);
    public void sendMessage(int clientId, byte[] message);

}
