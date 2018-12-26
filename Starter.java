import java.net.*;
import java.util.Scanner;
import java.util.Random;


class Starter{

  public static void main(String[] args){ 
    
    /* Initialize scanner to get data from terminal */
    Scanner sc = new Scanner(System.in);
    
    try{
      /* Get local IP address */
      InetAddress ip_address = InetAddress.getByName("localhost");
      
      /* Initialize sockets */
      DatagramSocket client_socket = new DatagramSocket(49999);
      
      /* Random number generator */
      Random random_gen = new Random();

      while(true){
        /* Get the port to send to */
        System.out.println("Starter: Enter the port to send the message to:");
        System.out.println("Enter 0 for initial node");
        System.out.println("Enter 1 for random node (0~100)");
        int send_to_port;
        send_to_port = sc.nextInt();
        sc.nextLine(); /* to skip the enter when entering the port */
        if(send_to_port == 0){
          send_to_port = 50000;
        }
        else if(send_to_port == 1){
          send_to_port = 50000 + random_gen.nextInt(100);
        }
        
        /* Get the message to be sent */
        System.out.println("Starter: Enter the message to be propagated (enter nothing for default message): ");
        System.out.println("Enter nothing for default message DATA_1_255,069,000 (ORANGE with ID 1)");
        System.out.println("Enter '1' for DATA_2_255,000,000 (RED with ID 2)");
        System.out.println("Enter '2' for DATA_3_000,255,000 (GREEN with ID 3)");
        System.out.println("Enter '3' for DATA_4_000,000,255 (BLUE with ID 4)");
        System.out.println("Enter '4' for DATA_5_000,255,128 (CYAN with ID 5)");
        System.out.println("Enter '5' for DATA_6_255,000,128 (PINK with ID 6)");
        System.out.println("Enter '6' for DATA_7_102,051,000 (BROWN with ID 7)");
        System.out.println("Enter '7' for DATA_8_127,000,255 (PURPLE with ID 8)");
        System.out.println("Enter '8' for DATA_9_255,255,000 (YELLOW with ID 9)");
        System.out.println("Enter '9' for DATA_10_000,128,255 (LBLUE with ID 10)");
        String send_data = new String();
        send_data = sc.nextLine();
        if(send_data.isEmpty()){
          send_data = "DATA_1_255,069,000";
          
        }
        else if(send_data.contains("1")){
          send_data = "DATA_2_255,000,000";
          
        }
        else if(send_data.contains("2")){
          send_data = "DATA_3_000,255,000";
          
        }
        else if(send_data.contains("3")){
          send_data = "DATA_4_000,000,255";
          
        }
        else if(send_data.contains("4")){
          send_data = "DATA_5_000,255,128";
          
        }
        else if(send_data.contains("5")){
          send_data = "DATA_6_255,000,128";
          
        }
        else if(send_data.contains("6")){
          send_data = "DATA_7_102,051,000";
          
        }
        else if(send_data.contains("7")){
          send_data = "DATA_8_127,000,255";
          
        }
        else if(send_data.contains("8")){
          send_data = "DATA_9_255,255,000";
          
        }
        else if(send_data.contains("9")){
          send_data = "DATA_10_000,128,255";
          
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