import spread.*;

import java.sql.SQLOutput;
import java.util.ArrayList;

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

          //    System.out.println("deposited amount : "+ amount);
           //   AccountReplica.getQuickBalance();
          }
          else if(cmd.startsWith("addInterest")){
              double interestRate = Double.parseDouble(cmd.split(" ")[1]);
              AccountReplica.addInterest(interestRate);
          }
          else
          {
              System.out.println("invalid command");
              return;
          }
          AccountReplica.removeTransactionFromOutstandingCollection();
          AccountReplica.addTransactionToExecutedList(transaction);

      } catch (SpreadException e) {
          e.printStackTrace();
      }
  }

  @Override
  public void membershipMessageReceived(SpreadMessage message) {
      //System.out.println("new membership message received");
      //System.out.println("getGroup: " + message.getMembershipInfo().getGroup());
      //System.out.println("getMembers length: " + message.getMembershipInfo().getMembers().length);

      AccountReplica.updateMembers(message.getMembershipInfo().getMembers());

      //System.out.println("members: " + AccountReplica.members);

      //System.out.println(message.getMembershipInfo().getGroup());
  }
}
