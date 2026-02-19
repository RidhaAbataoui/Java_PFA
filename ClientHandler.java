import java.net.*;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
public class ClientHandler implements Runnable {
    public Socket socket;

    public ClientHandler(Socket socket){
        this.socket = socket;
    }
    public void run(){
        try{
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            //reveive jar 
            long jarSize = in.readLong();
            byte[] jarBytes = in.readNBytes((int)jarSize);
            Files.write(Path.of("task.jar"), jarBytes);
            
            //receive option
            String option = in.readUTF();

            //reveive files
            int fileCount = in.readInt();

            for(int i=0;i<fileCount;i++){
                String name = in.readUTF();
                long size = in.readLong();
                byte[] data = in.readNBytes((int)size);
                Files.write(Path.of(name), data);
            }


            // read options
          int optCount = in.readInt();
          List<String> command = new ArrayList<>();

          command.add("java");
          command.add("-jar");
          command.add("task.jar");
   
          for(int i=0;i<optCount;i++){
             command.add(in.readUTF());
            }

       // read files
             int fileCounts = in.readInt();

for(int i=0;i<fileCounts;i++){
    String name = in.readUTF();
    long size = in.readLong();
    byte[] data = in.readNBytes((int)size);
    Files.write(Path.of(name), data);

    command.add(name);
}

            //execute jar
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.inheritIO();
            Process p = pb.start();
            p.waitFor();

            //send ouput
            byte[] result = Files.readAllBytes(Path.of("Result.txt"));
            out.writeLong(result.length);
            out.write(result);
            socket.close();  
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    
}
