import java.io.*;
import java.net.*;

// Server and client running in the same class

public class Thread_test{
  public static final int number_of_nodes = 5;

  public static void main(String[] argv){
    // Server
    new Thread(new Runnable(){
      public void run(){
        try{
          ServerSocket ss = new ServerSocket(6666);
          
          Socket s = null;
        
          s = ss.accept();

          System.out.println("Client connected: " + s);

          DataInputStream dis = new DataInputStream(s.getInputStream());
          DataOutputStream dos = new DataOutputStream(s.getOutputStream());

          while(true){
            // Listen to the clients
            String received = dis.readUTF();

            System.out.println(received);

            // Write answer
            dos.writeUTF("Doo");
          }
        }catch (Exception e){
          e.printStackTrace();
        }
      }
    }).start();

    // Client
    new Thread(new Runnable(){
      public void run() {
        try{
          InetAddress ip = InetAddress.getByName("localhost");

          Socket s = new Socket(ip, 6666);

          DataInputStream dis = new DataInputStream(s.getInputStream());
          DataOutputStream dos = new DataOutputStream(s.getOutputStream());

          while(true){
            // Request client
            dos.writeUTF("Dee");
            
            // Receive answer
            String received = dis.readUTF();

            System.out.println(received);

          }
        }catch(Exception e){
          e.printStackTrace();
        }
      }
      
    }).start();


  }
}