import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        try {
            List<InputTask> input = createInputTasksFromFile("naive_input.txt");
            //Iterates over the list of input tasks and sends a request for each task in the list
            for(InputTask task:input){

                System.out.println("task.zoneId: " + task.zoneId);

                Registry registry = LocateRegistry.getRegistry();
                LoadBalancerInterface lbstub = (LoadBalancerInterface) registry.lookup("loadbalancer");
                LoadBalancerResponse response = lbstub.fetchServer(task.zoneId);
                ServerInterface server = response.serverStub;
                // String response = server.sayHello();
                // System.out.println(response);
                // int n1 = 10, n2 = 15;
                // System.out.println(n1 + "+" + n2 + "=" + server.add(n1, n2));

                //Unsure about this:
                File output = createOutputFile("naive_server.txt");
                // ServerInterface sstub = (ServerInterface) registry.lookup(server);
                if(task.methodName.equals("getTimesPlayedByUser")){
                    int sResponse = server.getTimesPlayedByUser(task.argument1, task.argument2);
                    writeFile("naive_server.txt", "" + sResponse);
                }else if(task.methodName.equals("getTopThreeMusicByUser")){
                    String[] sResponse = server.getTopThreeMusicByUser(task.argument1);
                    writeFile("naive_server.txt", sResponse.toString());
                }else if(task.methodName.equals("getTopArtistsByUserGenre")){
                    String[] sResponse = server.getTopArtistsByMusicGenre(task.argument1, task.argument2);
                    writeFile("naive_server.txt", sResponse.toString());
                }else if(task.methodName.equals("getTimesPlayed")){
                    int sResponse = server.getTimesPlayed(task.argument1);
                    writeFile("naive_server.txt", "" + sResponse);
                }

            }


        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        } finally {
          // ServerSimulator.done();
          // Registry registry = LocateRegistry.getRegistry();
          //
          // for (int i = 1; i <= 5; i++)
          //   UnicastRemoteObject.unexportObject(registry.lookup("server" + i), false);
        }
    }
    //Reads the input file and creates for each line a new InputTask Object by using method createInputTask
    private static List<InputTask> createInputTasksFromFile(String fileName){
        List<InputTask> inputTasks = new ArrayList<>();

        try {
            File input = new File(fileName);
            Scanner myReader = new Scanner(input);
            int inputCounter = 1;
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                InputTask inputTask = createInputTask(data);
                inputTasks.add(inputTask);
            }
            myReader.close();
        } catch (FileNotFoundException e){
            System.out.println("An error occured");
            e.printStackTrace();
        }

        return inputTasks;

    }

    //Is called for each line in the input doc
    //InputTask Object has a method type, 1 - 2 arguments and a zoneId
    private static InputTask createInputTask(String data){

        InputTask inputTask = new InputTask();
        //Split the contents of each line and assign them to the new InputTask
        String[] arrSplit1 = data.split("\\(");
        String[] arrSplit2 = arrSplit1[1].split(" ");
        inputTask.methodName = arrSplit1[0];
        inputTask.argument1 = arrSplit2[0].substring(0,10);
        //Check if second argument is given and split it
        if(arrSplit2[0].length() > 15){
            String[] arrSplit3 = arrSplit2[0].split(",");
            //substring(1, length()-1) to seperate the , and )
            inputTask.argument2 = arrSplit3[1].substring(1,arrSplit3[1].length() - 1);
        }
        String zoneId = arrSplit2[1];
        if(zoneId.equals("Zone:1")){
            inputTask.zoneId = 1;
        } else if(zoneId.equals("Zone:2")){
            inputTask.zoneId = 2;
        } else if(zoneId.equals("Zone:3")) {
            inputTask.zoneId = 3;
        } else if(zoneId.equals("Zone:4")){
            inputTask.zoneId = 4;
        } else if(zoneId.equals("Zone:5")){
            inputTask.zoneId = 5;
        }

        return inputTask;
    }

    //Creates the output file
    //If a file with the given filename exists, no new file will be created
    private static File createOutputFile(String fileName){

        try{
            File outputFile = new File(fileName);
            if(outputFile.createNewFile()){
                System.out.println("File created: " + fileName);
                return outputFile;
            } else {
                System.out.println("File already exists: " + fileName);
            }
        } catch(IOException e){

            System.out.println("An error occured creating the file.");
            e.printStackTrace();
        }

        return null;
    }

    //Case A: response for Client is already the sentence to print:
    private static void writeFile(String fileName, String response) {
        try {
            FileWriter fileWriter = new FileWriter(fileName, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(response);
            bufferedWriter.newLine();
            bufferedWriter.close();
            System.out.println("Successfully written into the file");
        } catch (IOException e) {
            System.out.println("An error occured writing the file");
            e.printStackTrace();
        }
    }

}
