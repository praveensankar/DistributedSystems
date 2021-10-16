import spread.*;

public class Listener implements AdvancedMessageListener {
  @Override
  public void regularMessageReceived(SpreadMessage message) {
      try {
          System.out.println("message : "+message.getObject().toString()+"\t sent by replica : "+
                  message.getSender().toString());
          // Todo: parse the incoming transaction and add it in the outstanding collection or execute it
          // design choice: we are executing the transactions as soon as it's received from other replicas
          // we are not going to add it in the outstanding collection
          Transaction transaction = (Transaction) message.getObject();
          String cmd = transaction.getCommand();
          if(cmd.startsWith("deposit")){
              double amount = Double.parseDouble(cmd.split(" ")[1]);
              // we are executing the transaction
              AccountReplica.deposit(amount);
             // AccountReplica.removeTransactionFromOutstandingCollection(transaction.getUnique_id());
              AccountReplica.removeTransactionFromOutstandingCollection();
              AccountReplica.addTransactionToExecutedList(transaction);
          //    System.out.println("deposited amount : "+ amount);
           //   AccountReplica.getQuickBalance();
          }
          else if(cmd.startsWith("addInterest")){

          }
          else
          {
              System.out.println("invalid command");
          }

      } catch (SpreadException e) {
          e.printStackTrace();
      }
  }

  @Override
  public void membershipMessageReceived(SpreadMessage message) {
      System.out.println("new membership message received");
     // System.out.println(spreadMessage.getMembershipInfo().getGroup());
  }
}
