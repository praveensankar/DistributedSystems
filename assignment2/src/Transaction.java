import java.io.Serializable;

public class Transaction implements Serializable {

    String command;
    String unique_id;

    public Transaction(String command, String unique_id) {
        this.command = command;
        this.unique_id = unique_id;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getUnique_id() {
        return unique_id;
    }

    public void setUnique_id(String unique_id) {
        this.unique_id = unique_id;
    }

    @Override
    public String toString() {
        return "(" + command + ", " + unique_id + ")";
    }
}
