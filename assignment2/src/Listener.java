import spread.*;

import java.sql.SQLOutput;
import java.util.ArrayList;

public class Listener implements AdvancedMessageListener {

    @Override
    public void regularMessageReceived(SpreadMessage message) {
        try {

            System.out.print("message : " + message.getObject().toString());
            System.out.print("\t sent by replica : " + message.getSender().toString() + "\n");

            Transaction transaction = (Transaction) message.getObject();
            String cmd = transaction.getCommand();

            if (cmd.equals("exit")) {

                System.out.println("\nEXITING\n");
                AccountReplica.removeTransactionFromOutstandingCollection();
                AccountReplica.exit();

            } else if (cmd.equals("getSyncedBalance")) {

                AccountReplica.getQuickBalance();
                AccountReplica.removeTransactionFromOutstandingCollection();

            } else if (cmd.startsWith("deposit")) {

                double amount = Double.parseDouble(cmd.split(" ")[1]);
                AccountReplica.deposit(amount);

                AccountReplica.removeTransactionFromOutstandingCollection();
                AccountReplica.addTransactionToExecutedList(transaction);

            } else if (cmd.startsWith("addInterest")) {

                double interestRate = Double.parseDouble(cmd.split(" ")[1]);
                AccountReplica.addInterest(interestRate);

                AccountReplica.removeTransactionFromOutstandingCollection();
                AccountReplica.addTransactionToExecutedList(transaction);

            } else {
                System.out.println("invalid command");
            }

        } catch (SpreadException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void membershipMessageReceived(SpreadMessage message) {
        AccountReplica.updateMembers(message.getMembershipInfo().getMembers());
    }
}
