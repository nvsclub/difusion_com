import java.net.*;
import java.util.Scanner;


class Starter{

  public static void main(String[] args){ 
    
    /* Initialize scanner to get data from terminal */
    Scanner sc = new Scanner(System.in);
    
    try{
      /* Get local IP address */
      InetAddress ip_address = InetAddress.getByName("localhost");
      
      /* Initialize sockets */
      DatagramSocket client_socket = new DatagramSocket();
      
      while(true){
        /* Get the port to send to */
        System.out.println("Starter: Enter the port to send the message to (enter 0 for default port):");
        int send_to_port;
        send_to_port = sc.nextInt();
        sc.nextLine(); /* to skip the enter when entering the port */
        if(send_to_port == 0){
          send_to_port = 15001;
        }
        
        /* Get the message to be sent */
        System.out.println("Starter: Enter the message to be propagated (enter nothing for default message): ");
        String send_data = new String();
        send_data = sc.nextLine();
        if(send_data.isEmpty()){
          send_data = "DATA_1";
          
        }
        
        
        System.out.println("Starter: Sending message ~ " + send_data + " ~ to port " + send_to_port);
        System.out.println("Starter: Sending ... ");
        
        
        /* Buffering the string into buffers so that we get it into a packet */
        byte[] send_bytes = new byte[1024];
        send_bytes = send_data.getBytes();
        
        /* Assembling the packet to be sent */
        DatagramPacket send_packet;
        send_packet = new DatagramPacket(send_bytes, send_bytes.length, ip_address, send_to_port);
        
        /* Sending the packet */
        client_socket.send(send_packet);
        

        System.out.println("Starter: Success!");

      }
      
      /* This is needed for a safe socket close but we cant do that here
       * because of the while(true) loop */
      //client_socket.close();
        
      } catch (Exception e) { 
        System.out.println("Starter: Sending error");
        e.printStackTrace(); 
      } 
      
      
    }
    
  }