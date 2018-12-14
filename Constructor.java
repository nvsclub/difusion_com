import java.net.*;
import java.util.Random;

class Constructor{
  
  
  public static void main(String[] args){
    
    System.out.println("Constructor: initializing all threads");
    
    int sock_1 = 15001;
    int[] conn_1 = {15002, 15003};
    int sock_2 = 15002;
    int[] conn_2 = {15001, 15003};
    int sock_3 = 15003;
    int[] conn_3 = {15001, 15002};
    

    Thread node_1 = new Gossip_node(sock_1, conn_1);
    node_1.start();
    Thread node_2 = new Gossip_node(sock_2, conn_2);
    node_2.start();
    Thread node_3 = new Gossip_node(sock_3, conn_3);
    node_3.start();
    


    System.out.println("Constructor: all threads initalized");

  }


}

class Gossip_node extends Thread{

  public DatagramSocket server_socket;
  public DatagramSocket client_socket;
  
  public InetAddress ip_address;
  
  public int server_port;
  public int[] client_ports;
  
  
  public int id = 0;

    
  public Gossip_node(int server_port, int[] client_ports){
    this.server_port = server_port;
    this.client_ports= client_ports;

  }
  
  public void run(){ 
    try{
      // Get local IP address
      this.ip_address = InetAddress.getByName("localhost");
      
      // Initialize sockets
      this.server_socket = new DatagramSocket(this.server_port);
      this.client_socket = new DatagramSocket();
      
    } catch (Exception e) { 
      e.printStackTrace(); 
    
    } 
    
    System.out.println("Gossip node: Initialized node " + server_port);
    
    // Creating buffers
    /* These byte buffers are needed because one of the DatagramPacket 
    arguments is a pointer for an array of bytes where the packet data
    will be stored  */
    byte[] send_bytes = new byte[1024];
    byte[] receive_bytes = new byte[1024];

    // Creating the strings to read the data
    String send_data = new String(); 
    String receive_data = new String();   
    String prev_receive_data = new String();

    DatagramPacket send_packet;
    
    while(true){
      try{
        // Creating UDP packets to receive the messages
        /* These packets are responsible in receiving the messages and storing it in memory
        They are needed in order to store the data received in the socket */
        DatagramPacket receive_packet = new DatagramPacket(receive_bytes, receive_bytes.length);
        // Receiving the message and transforming it into a string
        /* The data is received from the packet into a bytes array and then we typecast it 
        into a string in order to be readable */
        server_socket.receive(receive_packet);
        receive_data = new String(receive_packet.getData()).trim();
        
        System.out.println("Gossip node: Message received ~ " + receive_data);

        prev_receive_data = receive_data;
        
        // Check if it is a message or ack
        /* This code checks if the received message is a new message to be difused or if it
        is an acknowledge, positive or negative, from previous comunications */
        // Positive acknowledge
        /* If we receive a positive acknowledge our mission will be to keep difusing the message */
        if (receive_data.contains("YACK")) {
          // Choose a random client from the ports list
          // ************************ TO DO ******************************** //
          /* WE NEED TO CHOOSE THE TARGET PORT IN A MORE EFICIENT WAY, AND STOP WHEN WE HAVE TRANSMITED TO THEM ALL */
          int client = new Random().nextInt(this.client_ports.length);
          
          // Setup the packet and send it
          // ************************ TO DO ******************************** //
          /* This datagram packet needs to transmit the received_data to a new port, and not
          the data that it receives back */
          send_packet = new DatagramPacket(receive_data.getBytes(), 64, this.ip_address, this.client_ports[client]);
          this.client_socket.send(send_packet);
          
          System.out.println("Gossip node: Positive ACKNOWLEDGE received at " + server_port);
          
        }      
        // Negative acknowledge
        /* If we receive a negative acknowledge our mission will be to keep difusing the message
        if and only if the probabilities allow us to do so */
        else if (receive_data.contains("NACK")) {
          // Generate a random number between 0 and 100 and checks it it should continue difusing
          if (new Random().nextInt(100) < 90){
            // Choose a random client from the ports list
            // ************************ TO DO ******************************** //
            /* WE NEED TO CHOOSE THE TARGET PORT IN A MORE EFICIENT WAY, AND STOP WHEN WE HAVE TRANSMITED TO THEM ALL */
            int client = new Random().nextInt(this.client_ports.length);
            
            // Setup the packet and send it
            // ************************ TO DO ******************************** //
            /* This datagram packet needs to transmit the received_data to a new port, and not
            the data that it receives back */
            send_packet = new DatagramPacket(receive_data.getBytes(), 64, this.ip_address, this.client_ports[client]);
            this.client_socket.send(send_packet);
            
            System.out.println("Gossip node: Negative ACKOWLEDGE received at " + server_port);
            
          }
        }     
        // Receives new instruction
        /* Whenever we receive a new instruction we need to decide if we will be difusing it or
        if not. The message to be transmited will be ordered by id and our goal will be to 
        always transmit the most recent message */ 
        else if (receive_data.contains("DATA")) {
          // If the message is more recent
          if (Integer.parseInt(receive_data.replaceAll("\\D+", "")) > id){
            // Send a positive acknowledge to the node that sent the message
            send_data = "YACK";
            send_bytes = send_data.getBytes();
            send_packet = new DatagramPacket(send_bytes, send_bytes.length, receive_packet.getAddress(), receive_packet.getPort());
            this.client_socket.send(send_packet);
            
            
            // Choose a random client from the ports list
            // ************************ TO DO ******************************** //
            /* WE NEED TO CHOOSE THE TARGET PORT IN A MORE EFICIENT WAY, AND STOP WHEN WE HAVE TRANSMITED TO THEM ALL 
            WE NEED TO CLEAR THE BUFFER FROM THE ALREADY SENT */
            int client = new Random().nextInt(this.client_ports.length);
            
            // Setup the packet and send it
            // ************************ TO DO ******************************** //
            /* This datagram packet needs to transmit the received_data to a new port, and not
            the data that it receives back */
            send_packet = new DatagramPacket(receive_bytes, receive_bytes.length, ip_address, this.client_ports[client]);
            this.client_socket.send(send_packet);
            
            System.out.println("Gossip node: Difunding new message from " + server_port + " with id " + id);
            
          }
          else{
            // Sends a negative acknowledge to the sender
            send_packet = new DatagramPacket(receive_data.getBytes(), 64, receive_packet.getAddress(), receive_packet.getPort());
            this.client_socket.send(send_packet);
            
            System.out.println("Gossip node: Rejected new message from " + server_port);
            
          }
          
          System.out.println("Recebi mensagem nova em " + receive_data + " with id " + id);
          
        }      
        
      } catch (Exception e) { 
        e.printStackTrace(); 
      } 
      
    }
      
  }
    
}