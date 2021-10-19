import spread.*;

public class Listener implements AdvancedMessageListener {

    @Override
    public void regularMessageReceived(SpreadMessage message) {
        try {

            Transaction transaction = (Transaction) message.getObject();
            String cmd = transaction.getCommand();

            if (cmd.equals("getState")) {
                AccountReplica.multicastState();

            } else if(cmd.equals("stateInfo")) {

                double balance =Double.parseDouble(transaction.getUnique_id());
                AccountReplica.setState(balance);

            } else if (cmd.equals("getSyncedBalance")) {

                AccountReplica.getSyncedBalanceAdvanced();
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
                System.out.println("INVALID COMMAND");
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
