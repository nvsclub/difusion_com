import java.net.*;
import java.util.Scanner;
import java.util.Random;

/* CLASS DESCRIPTION  -  Starter
* This class sends a UDP message to a local port in order to initiate the network
*
* */
class Starter_data{

  private static final int port_offset = 60000;

  public static void main(String[] args){ 
    
    /* Initialize scanner to get data from terminal */
    Scanner sc = new Scanner(System.in);
    
    try{
      /* Get local IP address */
      InetAddress ip_address = InetAddress.getByName("localhost");
      
      /* Initialize sockets */
      DatagramSocket client_socket = new DatagramSocket(port_offset - 1);
      
      /* Random number generator */
      Random random_gen = new Random();
      
      String send_data = new String();
      String id_data = null;
      String color_data = null;
      int send_to_port = port_offset;
      long timer = System.currentTimeMillis();
      
      for(int i = 0; i < 100; i++){

        if(i<10){
          id_data = "0" + Integer.toString(i);
          color_data = "0" + Integer.toString(i*21%90+10) + ",0" + Integer.toString(i*27%90+10) + ",0" + Integer.toString(i*9%90+10);
        }
        else{
          id_data = Integer.toString(i);
          color_data = "0" + Integer.toString(i*21%90+10) + ",0" + Integer.toString(i*27%90+10) + ",0" + Integer.toString(i*9%90+10);
        }

        send_data = "DATA_" + id_data + "_" + color_data;
        System.out.println(send_data);

        
        /* Buffering the string into buffers so that we get it into a packet */
        byte[] send_bytes = new byte[1024];
        send_bytes = send_data.getBytes();
        
        /* Assembling the packet to be sent */
        DatagramPacket send_packet;
        send_packet = new DatagramPacket(send_bytes, send_bytes.length, ip_address, send_to_port);
        
        /* Sending the packet */
        client_socket.send(send_packet);

        while(System.currentTimeMillis() - timer < 20000);
        timer = System.currentTimeMillis();

      }
        
    } catch (Exception e) { 
      System.out.println("Starter: Sending error");
      e.printStackTrace(); 
    } 
      
      
  }
    
}