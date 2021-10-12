public interface ClientInterface {
    public void connectToSpreadDaemon(String dameon_address, int port);
    public void disconnectFromSpreadDaemon();
    public void joinGroup(String group_name);
    public void configureMessage(String group_name);
    public void sendMessage(byte[] data);

}
