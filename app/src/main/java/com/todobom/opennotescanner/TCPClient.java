package com.todobom.opennotescanner;
import android.net.Uri;
import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class TCPClient {
    Socket MyClient = null;
    int PortNumber = 12345;
    DataInputStream br = null;
    DataOutputStream bw = null;

    public void OpenConnection() throws TCPException {
        try {
            MyClient = new Socket("104.248.47.116", PortNumber);
            br = new DataInputStream(MyClient.getInputStream());
            bw = new DataOutputStream(MyClient.getOutputStream());
            System.out.println("Connected to server");
        } catch (IOException e) {
            System.out.println("In Client: " + e);
            e.printStackTrace();
            throw new TCPException("Server is offline!");
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

    public String ReceiveFile() throws TCPException {
        try {
            String result = br.readUTF();
            if(result.equals("SUCCESS")) {
                int size = br.readInt();
                String name = br.readUTF();
                File file = new File(Environment.getExternalStorageDirectory() + "/Notefy/Scores/" + name);
                File xml_file = new File(Environment.getExternalStorageDirectory() + "/Notefy/Scores/" + name.substring(0, name.lastIndexOf('.')) + ".xml");
                FileOutputStream stream = new FileOutputStream(file);


                byte[] str = new byte[size];
                System.out.println("File " + name + " received!");
                br.readFully(str, 0, size);
                stream.write(str);
                stream.close();
                InputStream is;
                try {
                    is = new FileInputStream(file);
                    ZipInputStream zis = null;
                    try {
                        zis = new ZipInputStream(new BufferedInputStream(is));
                        ZipEntry ze;
                        while ((ze = zis.getNextEntry()) != null) {
                            if (!ze.getName().startsWith("META-INF") // ignore META-INF/ and container.xml
                                    && ze.getName() != "container.xml") {
                                // read from Zip into buffer and copy into ByteArrayOutputStream which is converted to byte array of whole file
                                ByteArrayOutputStream os = new ByteArrayOutputStream();
                                byte[] buffer = new byte[1024];
                                int count;
                                while ((count = zis.read(buffer)) != -1) { // load in 1K chunks
                                    os.write(buffer, 0, count);
                                }
                                stream = new FileOutputStream(xml_file);
                                stream.write(os.toByteArray());
                                stream.close();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (zis != null)
                            zis.close();
                        file.delete();
                    }
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return name;
            }
            else {
                throw new TCPException("File conversion failed!");
            }

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
