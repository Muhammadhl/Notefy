package com.todobom.opennotescanner;
import android.net.Uri;
import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;

public class TCPClient {
    Socket MyClient = null;
    int PortNumber = 12345;
    DataInputStream br = null;
    DataOutputStream bw = null;

    public void OpenConnection() {
        try {
            MyClient = new Socket("104.248.19.125", PortNumber);
            br = new DataInputStream(MyClient.getInputStream());
            bw = new DataOutputStream(MyClient.getOutputStream());
            System.out.println("Connected to server");
        } catch (IOException e) {
            System.out.println("In Client: " + e);
            e.printStackTrace();
        }
    }


    public void SendFile(String path) {

        File image = new File(path);
        try {
            //BufferedReader br = new BufferedReader(new FileReader(file));
            int size = (int) image.length();
            byte[] fileContent = new byte[size];
            try {
                BufferedInputStream buf = new BufferedInputStream(new FileInputStream(image));
                DataInputStream dis = new DataInputStream(buf);
                dis.readFully(fileContent, 0, size);
                //buf.read(fileContent, 0, fileContent.length);
                buf.close();
                dis.close();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            //System.out.print(fileContent.toString());

            bw.writeInt(size);
            bw.writeUTF(image.getName());
            bw.write(fileContent);
            bw.flush();
            MyClient.getOutputStream().flush();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String ReceiveFile() {
        try {

            int size = br.readInt();
            String name = br.readUTF();

            FileOutputStream stream = new FileOutputStream(new File(Environment.getExternalStorageDirectory() + "/OpenNoteScanner/mxl/"+ name));

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

    public void CloseConnection() {
        try {
            bw.close();
            br.close();
            MyClient.close();
        } catch (IOException e) {
            System.out.println("In Client: " + e);
            e.printStackTrace();
        }
    }
}
