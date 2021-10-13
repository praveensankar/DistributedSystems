import spread.*;

public class Listener implements AdvancedMessageListener {
  @Override
  public void regularMessageReceived(SpreadMessage spreadMessage) {
      try {
          System.out.println("test message received");
          System.out.println(spreadMessage.getObject().toString());
      } catch (SpreadException e) {
          e.printStackTrace();
      }
  }

  @Override
  public void membershipMessageReceived(SpreadMessage spreadMessage) {
      System.out.println(spreadMessage.getMembershipInfo().getGroup());
  }
}
