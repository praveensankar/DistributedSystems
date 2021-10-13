import spread.*;

public class Listener implements AdvancedMessageListener {
  @Override
  public void regularMessageReceived(SpreadMessage spreadMessage) {
      try {
          System.out.println("message : "+spreadMessage.getObject().toString()+"\t sent by replica : "+
                  spreadMessage.getSender().toString());
      } catch (SpreadException e) {
          e.printStackTrace();
      }
  }

  @Override
  public void membershipMessageReceived(SpreadMessage spreadMessage) {
      System.out.println("new membership message received");
     // System.out.println(spreadMessage.getMembershipInfo().getGroup());
  }
}
