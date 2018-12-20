import java.net.*;
import java.util.List;
import java.util.ArrayList;;
import java.util.Random;

class Constructor{
  
  
  public static void main(String[] args){
    
    System.out.println("Constructor: initializing all threads");
    
    /* Create a linear topology */
    int sock_1 = 15001;
    int[] conn_1 = {15002, 15003};
    int sock_2 = 15002;
    int[] conn_2 = {15001, 15003};
    int sock_3 = 15003;
    int[] conn_3 = {15001, 15002};
    
    /* Create the threads according to the topology */
    Thread node_1 = new Gossip_node(sock_1, conn_1);
    node_1.start();
    Thread node_2 = new Gossip_node(sock_2, conn_2);
    node_2.start();
    Thread node_3 = new Gossip_node(sock_3, conn_3);
    node_3.start();
    

    System.out.println("Constructor: all threads initalized");

  }


}

/* TODO 
 * When we receive a message remove the sender from the port_list so that we do not send the message back */

class Gossip_node extends Thread{

  public DatagramSocket server_socket;
  public DatagramSocket client_socket;
  
  public InetAddress ip_address;
  
  public int server_port;
  public int[] client_ports;
  
  
  public int id = 0;

  /* This initializing method allows us to send arguments to the class
   * whenever we are initalizing a new thread */
  public Gossip_node(int server_port, int[] client_ports){
    this.server_port = server_port;
    this.client_ports= client_ports;

  }
  
  public void run(){ 
    try{
      /* Get local IP address */
      this.ip_address = InetAddress.getByName("localhost");
      
      /* Initialize sockets */
      this.server_socket = new DatagramSocket(this.server_port);
      this.client_socket = new DatagramSocket();
      
    } catch (Exception e) { 
      e.printStackTrace(); 
    
    } 
    
    System.out.println("Gossip node " + this.server_port + ": Initialized");
    
    /* Creating buffers
     * 
     * These byte buffers are needed because one of the DatagramPacket 
     * arguments is a pointer for an array of bytes where the packet data
     * will be stored  */
    byte[] send_bytes = new byte[1024];
    byte[] receive_bytes = new byte[1024];

    /* Creating the strings to read the data */
    String send_data = new String(); 
    String receive_data = new String();   

    /* Create a list with the ports where the message was not sent yet
     * 
     * We use a list here because it uses dinamic memory
     * Had we used a normal array it would be way more complex to remove
     * a element because it statically allocates memory*/
    List<Integer> port_list = new ArrayList<Integer>();
    /* Populate the list with the clients */
    for(int i : client_ports){
      port_list.add(i);
    }
    
    /* Saver for the message to be propagated */
    String to_propagate = new String();

    DatagramPacket send_packet;
    
    while(true){
      try{
        /* Creating UDP packets to receive the messages
         * 
         * These packets are responsible in receiving the messages and storing it in memory
         * They are needed in order to store the data received in the socket */
        DatagramPacket receive_packet = new DatagramPacket(receive_bytes, receive_bytes.length);
        /* Receiving the message and transforming it into a string
         * 
         * The data is received from the packet into a bytes array and then we typecast it 
         * into a string in order to be readable */
        server_socket.receive(receive_packet);
        receive_data = new String(receive_packet.getData()).trim();
        
        System.out.println("Gossip node " + this.server_port + ": Message received ~ " + receive_data);

        /* Check if it is a message or ack
         * This code checks if the received message is a new message to be difused or if it
         * is an acknowledge, positive or negative, from previous comunications */
        
        /* Positive acknowledge
         * If we receive a positive acknowledge our mission will be to keep difusing the message */
        if (receive_data.contains("YACK")) {
          /* Choose a random client from the stack of ports */
          int client_id = new Random().nextInt(port_list.size());
          int client = port_list.get(client_id);
          
          /* Setup the packet and send it
           * and propagates the message */
          send_bytes = to_propagate.getBytes();
          send_packet = new DatagramPacket(send_bytes, send_bytes.length, this.ip_address, client);
          this.client_socket.send(send_packet);
          
          /* Delete the port that was sent the message */
          port_list.remove(client_id);

          System.out.println("Gossip node " + this.server_port + ": Positive ACKNOWLEDGE received");
          
        }

        /* Negative acknowledge
         * If we receive a negative acknowledge our mission will be to keep difusing the message
         * if and only if the probabilities allow us to do so */
        else if (receive_data.contains("NACK")) {
          /* Generate a random number between 0 and 100 and checks it it should continue difusing */
          if (new Random().nextInt(100) < 90){
            /* Choose a random client from the stack of ports */
            int client_id = new Random().nextInt(port_list.size());
            int client = port_list.get(client_id);
            
            /* Setup the packet and send it
            * and propagates the message */
            send_bytes = to_propagate.getBytes();
            send_packet = new DatagramPacket(send_bytes, send_bytes.length, this.ip_address, client);
            this.client_socket.send(send_packet);
          
            /* Delete the port that was sent the message */
            port_list.remove(client_id);

            System.out.println("Gossip node " + this.server_port + ": Negative ACKOWLEDGE received");
            System.out.println("Gossip node " + this.server_port + ": Continued transmission");
            
          }
          else{
            System.out.println("Gossip node " + this.server_port + ": Stopped transmission");
            
          }
        }

        /* Receives new instruction
         *
         *  Whenever we receive a new instruction we need to decide if we will be difusing it or
         * if not. The message to be transmited will be ordered by id and our goal will be to 
         * always transmit the most recent message */ 
        else if (receive_data.contains("DATA")) {

          /* Get message id */
          int new_message_id = Integer.parseInt(receive_data.replaceAll("\\D+", ""));

          /* If the message is more recent */
          if (new_message_id > id){
            /* Send a positive acknowledge to the node that sent the message */
            send_data = "YACK";
            send_bytes = send_data.getBytes();
            send_packet = new DatagramPacket(send_bytes, send_bytes.length, receive_packet.getAddress(), receive_packet.getPort());
            this.client_socket.send(send_packet);
            
            /* Save the new best id */
            id = new_message_id;

            /* Save the new message to be propagated */
            to_propagate = receive_data;

            /* Resets the list */
            while(!port_list.isEmpty()){
              port_list.remove(0);
            }
            /* Populate the list with the clients */
            for(int i : client_ports){
              port_list.add(i);
            }
            
            /* Choose a random client from the stack of ports */
            int client_id = new Random().nextInt(port_list.size());
            int client = port_list.get(client_id);
            
            /* Setup the packet and send it
            * and propagates the message */
            send_bytes = to_propagate.getBytes();
            send_packet = new DatagramPacket(send_bytes, send_bytes.length, ip_address, client);
            this.client_socket.send(send_packet);
            
            System.out.println("Gossip node " + this.server_port + ": Difunding new message from " + receive_packet.getPort() + " to " + client + " with id " + id);
            
          }
          else{
            /* Send a negative acknowledge to the node that sent the message */
            send_data = "NACK";
            send_bytes = send_data.getBytes();
            send_packet = new DatagramPacket(send_bytes, send_bytes.length, receive_packet.getAddress(), receive_packet.getPort());
            this.client_socket.send(send_packet);
            
            System.out.println("Gossip node " + this.server_port + ": Rejected new message from " + receive_packet.getPort() + " with id " + new_message_id);
            
          }
          
        }      
        
      } catch (Exception e) { 
        e.printStackTrace(); 
      } 
      
    }
      
  }
    
}