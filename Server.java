import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;


/**
 * Created by Maysan on 13-Aug-18.
 */
public class Server {

    ServerSocket MyServer = null;
    Socket ClientSocket = null;
    int PortNumber = 12345;
    DataInputStream br = null;
    DataOutputStream bw = null;

    public void OpenConnection() {
        try {
            MyServer = new ServerSocket(PortNumber);
            System.out.println("Waiting for connection...");
            ClientSocket = MyServer.accept();
            br=new DataInputStream(new BufferedInputStream(ClientSocket.getInputStream()));
            bw=new DataOutputStream(new BufferedOutputStream(ClientSocket.getOutputStream()));
            System.out.println("Client connected.");

        } catch (IOException e) {
            System.out.println("In Socket: " + e);
            e.printStackTrace();
        }
    }


    public String ReceiveFile() {
        try {
            int size = br.readInt();
            String name = br.readUTF();

            //TODO: change directory!
            FileOutputStream stream = new FileOutputStream(name);

            byte[] str = new byte[size];
            System.out.println("File " + name + " received!");
            br.readFully(str, 0, size);
            stream.write(str);
            stream.close();

            return name;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void ConvertFile(String filename) {
        try {
            String quote = "\"";
            String pingResult = "";
            //String[] params = ("run -PcmdLineArgs=" + quote + " -batch,-export,-output,my output folder,--, " + filename  + quote).split(" ");
            //System.out.println("Command: " + "gradle" );
            Process p = Runtime.getRuntime().exec("./Audiveris -batch -export -output output -- " + filename);
            p.waitFor();
            BufferedReader in = new BufferedReader(new
            InputStreamReader(p.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println(inputLine);
                pingResult += inputLine;
            }
            in.close();
        
        } catch (IOException e) {
            System.out.println("Exception: " + e);
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.out.println("Exception: " + e);
            e.printStackTrace();
        }
    }

    public void SendFile(String filename) {
        System.out.println("Debug: filename = " + filename + " split = " + filename.split("\\.")[0]);
        String newName = filename.split("\\.")[0] + ".mxl";
        File file = new File("output//"+ filename.split("\\.")[0] + "//" + newName);

        System.out.println("Sending file " + newName);

        try {
            byte[] fileContent = Files.readAllBytes(file.toPath());

            bw.writeInt(fileContent.length);
            bw.writeUTF(file.getName());
            bw.write(fileContent);
            bw.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void CloseConnection() {
        try {
            bw.close();
            br.close();
            ClientSocket.close();
            MyServer.close();
        } catch (IOException e) {
            System.out.println("In Socket: " + e);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        while(true) {
            server.OpenConnection();
            String filename = server.ReceiveFile();
            server.ConvertFile(filename);
            server.SendFile(filename);
            server.CloseConnection();
        }
    }
}
