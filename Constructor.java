import java.net.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.lang.Object;

/* java graphsteam libraries */
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;


class Constructor{

  private static final int main_port = 65535;  

  public static void main(String[] args){
    
    System.out.println("Constructor: initializing all threads");
    
    /* Create a linear topology */
    int sock_1 = 15001;
    int[] conn_1 = {15002, 15003};
    int sock_2 = 15002;
    int[] conn_2 = {15001, 15003, 15007};
    int sock_3 = 15003;
    int[] conn_3 = {15001, 15002, 15004};
    int sock_4 = 15004;
    int[] conn_4 = {15003, 15005};
    int sock_5 = 15005;
    int[] conn_5 = {15004, 15006};
    int sock_6 = 15006;
    int[] conn_6 = {15005, 15007};
    int sock_7 = 15007;
    int[] conn_7 = {15005, 15006, 15002};
    
    /* Create the threads according to the topology */
    Thread node_1 = new Gossip_node(sock_1, conn_1);
    node_1.start();
    Thread node_2 = new Gossip_node(sock_2, conn_2);
    node_2.start();
    Thread node_3 = new Gossip_node(sock_3, conn_3);
    node_3.start();
    Thread node_4 = new Gossip_node(sock_4, conn_4);
    node_4.start();
    Thread node_5 = new Gossip_node(sock_5, conn_5);
    node_5.start();
    Thread node_6 = new Gossip_node(sock_6, conn_6);
    node_6.start();
    Thread node_7 = new Gossip_node(sock_7, conn_7);
    node_7.start();
    

    System.out.println("Constructor: all threads initalized");
    
    
    /* Graph stream initializations */
    Graph graph = new SingleGraph("Graph Visualization");
    
    /* We can set the display in the beginning and we do not need to worry about it anymore */
    graph.display();
    
    /* Adding nodes */
    graph.addNode("15001");
    Node node = graph.getNode("15001");
    node.addAttribute("ui.style", "fill-color: rgb(0,100,255); size: 20px;");
    graph.addNode("15002");
    graph.addNode("15003");
    graph.addNode("15004");
    graph.addNode("15005");
    graph.addNode("15006");
    graph.addNode("15007");
    
    /* Adding edges */
    Edge edge;
    graph.addEdge("1500115002", "15001", "15002");
    graph.addEdge("1500115003", "15001", "15003");
    graph.addEdge("1500215003", "15002", "15003");
    graph.addEdge("1500215007", "15002", "15007");
    graph.addEdge("1500315004", "15003", "15004");
    graph.addEdge("1500415005", "15004", "15005");
    graph.addEdge("1500515006", "15005", "15006");
    graph.addEdge("1500615007", "15006", "15007");
    
    System.out.println("Constructor: graph initialized");
    
    
    /* Connection variables */
    DatagramSocket connection_socket;
    InetAddress ip_address;
    
    /* Setup socket to receive information */
    try{
      /* Get local IP address */
      ip_address = InetAddress.getByName("localhost");
      
      /* Initialize sockets
      * Having a client socket makes it so that we cannot naturally identify
      * the node who sent the message to us 
      * Thats why we will use only one socket */
      connection_socket = new DatagramSocket(main_port);
      
      
      /* Creating buffers */
      byte[] receive_bytes = new byte[1024];
      
      /* Creating the strings to read the data */
      String receive_data = new String(); 
      
      /* Graphstream objects to update */
      String link_to_update = new String();
      String node_to_update = new String();
      
      String node_that_sent = new String();
      String node_that_updated = new String();
      
      System.out.println("Constructor: receiver port configured");
      
      
      /* Loop to receive the information about the network */
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
          connection_socket.receive(receive_packet);
          receive_data = new String(receive_packet.getData()).trim();
          
          /* Get the information received about the structure */
          if(receive_data.length() > 5){
            node_to_update = receive_data.substring(receive_data.length() - 5);

            /* Guarantees that the order is correct in the link */
            node_that_sent = receive_data.substring(0, receive_data.length() - 5);
            node_that_updated = receive_data.substring(receive_data.length() - 5);
            
            if(Integer.parseInt(node_that_updated) > Integer.parseInt(node_that_sent)){
              link_to_update = receive_data;

            }
            else{
              link_to_update = node_that_updated + node_that_sent;
            }
            
            /* Good practices lesson:
             * Verify if the node/edge is found, else program will crash when it doesnt. 
             * Here we have the certainty that it exists so we progress forward */
            /* Coloring stuff 
            * Node */
            node = graph.getNode(node_to_update);
            node.addAttribute("ui.style", "fill-color: blue;");
            /* Coloring stuff 
            * Link */
            edge = graph.getEdge(link_to_update);
            edge.addAttribute("ui.style", "fill-color: rgb(255,69,0);");
            
            
          }
          
          System.out.println("Constructor: updated node " + node_that_updated + " and link " + link_to_update + node_that_sent);
          
        } catch (Exception e) { 
          e.printStackTrace(); 
        } 
        
      }
      
    
    } catch (Exception e) { 
      e.printStackTrace(); 
      
    } 


  }


}


class Gossip_node extends Thread{

  private static final int main_port = 65535;

  public DatagramSocket connection_socket;
  
  public InetAddress ip_address;

  public int server_port;
  public int[] client_ports;
  
  public int id = 0;
  public int missing_nodes = 0;

  public Random random_gen = new Random();

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
      
      /* Initialize sockets
       * Having a client socket makes it so that we cannot naturally identify
       * the node who sent the message to us 
       * Thats why we will use only one socket */
      this.connection_socket = new DatagramSocket(this.server_port);
      
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
        connection_socket.receive(receive_packet);
        receive_data = new String(receive_packet.getData()).trim();
        
        /* Check if it is a message or ack
        * This code checks if the received message is a new message to be difused or if it
        * is an acknowledge, positive or negative, from previous comunications */
        
        /* Positive acknowledge
        * If we receive a positive acknowledge our mission will be to keep difusing the message */
        if (receive_data.contains("YACK")) {
          if (missing_nodes > 0){
            /* Choose a random client from the stack of ports */
            int client_id = random_gen.nextInt(missing_nodes);
            int client = port_list.get(client_id);
            
            /* Setup the packet and send it
            * and propagates the message */
            send_bytes = to_propagate.getBytes();
            send_packet = new DatagramPacket(send_bytes, send_bytes.length, this.ip_address, client);
            this.connection_socket.send(send_packet);
            
            /* Delete the port that was sent the message */
            missing_nodes--;
            port_list.remove(client_id);
            
            System.out.println("Gossip node " + this.server_port + ": Positive ACKNOWLEDGE received from " +  receive_packet.getPort());
            
          }
          else{
            System.out.println("Gossip node " + this.server_port + ": Positive ACKNOWLEDGE received from " +  receive_packet.getPort());
            System.out.println("Gossip node " + this.server_port + ": No more nodes");
            
          }
          
        }
        
        /* Negative acknowledge
        * If we receive a negative acknowledge our mission will be to keep difusing the message
        * if and only if the probabilities allow us to do so */
        else if (receive_data.contains("NACK")) {
          if (missing_nodes > 0){
            /* Generate a random number between 0 and 100 and checks it it should continue difusing */
            if (random_gen.nextInt(100) < 90){
              /* Choose a random client from the stack of ports */
              int client_id = random_gen.nextInt(missing_nodes);
              int client = port_list.get(client_id);
              
              /* Setup the packet and send it
              * and propagates the message */
              send_bytes = to_propagate.getBytes();
              send_packet = new DatagramPacket(send_bytes, send_bytes.length, this.ip_address, client);
              this.connection_socket.send(send_packet);
              
              /* Delete the port that was sent the message */
              missing_nodes--;
              port_list.remove(client_id);
              
              System.out.println("Gossip node " + this.server_port + ": Received negative ACKNOWLEDGE - Continued transmission");
            }
            
            else{
              System.out.println("Gossip node " + this.server_port + ": Received negative ACKNOWLEDGE - Stopped transmission");
              
            }
            
          }
          else{
            System.out.println("Gossip node " + this.server_port + ": Received negative ACKNOWLEDGE");
            System.out.println("Gossip node " + this.server_port + ": No more nodes");
            
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
            this.connection_socket.send(send_packet);
            
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
            
            /* Remove the sender from the list */
            if (port_list.contains(receive_packet.getPort())){
              port_list.remove(port_list.indexOf(receive_packet.getPort()));

            }
            
            missing_nodes = port_list.size();
            
            /* Choose a random client from the stack of ports */
            int client_id = random_gen.nextInt(missing_nodes);
            int client = port_list.get(client_id);
            
            /* Update the list */
            missing_nodes--;
            port_list.remove(client_id);
            
            /* Setup the packet and send it
            * and propagates the message */
            send_bytes = to_propagate.getBytes();
            send_packet = new DatagramPacket(send_bytes, send_bytes.length, ip_address, client);
            this.connection_socket.send(send_packet);
            

            System.out.println("Gossip node " + this.server_port + ": Message received from " + receive_packet.getPort());
            
            /* Send the information to the main */
            send_data = Integer.toString(receive_packet.getPort()) + Integer.toString(this.server_port);
            send_bytes = send_data.getBytes();
            send_packet = new DatagramPacket(send_bytes, send_bytes.length, receive_packet.getAddress(), main_port);
            this.connection_socket.send(send_packet);


          }
          else{
            /* Send a negative acknowledge to the node that sent the message */
            send_data = "NACK";
            send_bytes = send_data.getBytes();
            send_packet = new DatagramPacket(send_bytes, send_bytes.length, receive_packet.getAddress(), receive_packet.getPort());
            this.connection_socket.send(send_packet);
            
            System.out.println("Gossip node " + this.server_port + ": Message received from " + receive_packet.getPort());
            
          }
          
        }      
        
      } catch (Exception e) { 
        e.printStackTrace(); 
      } 
      
    }
      
  }
    
}