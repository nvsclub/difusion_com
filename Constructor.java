import java.net.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/* java graphsteam libraries */
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.algorithm.generator.*;


class Constructor{

  private static final int main_port = 65535;  

  public static void main(String[] args){
    
    /* Initialize scanner to get data from terminal */
    Scanner sc = new Scanner(System.in);
    
    /* Get the graph generator to use */
    System.out.println("Starter: Choose the graph to be generated:");
    System.out.println("1 - Dorogovtsev mendes:");
    int algorithm_id;
    algorithm_id = sc.nextInt();
    sc.nextLine(); /* to skip the enter when entering the port */
    String algorithm_choosen = new String();
    if(algorithm_id == 1){
      algorithm_choosen = "Dorogovtsev mendes";

    }


    /* Get the size of the graph to generate */
    System.out.println("Starter: Enter the size of the graph to be generated: ");
    int size_of_graph;
    size_of_graph = sc.nextInt();
    sc.nextLine(); /* to skip the enter when entering the port */
    
    /* Generating DorogovtsevMendes type graph */
    Graph graph = new SingleGraph(algorithm_choosen);
    Generator gen = new DorogovtsevMendesGenerator();
    gen.addSink(graph);
    gen.begin();

    for(int i=0; i<size_of_graph; i++) {
      gen.nextEvents();
    }
    
    gen.end();
    graph.display();

    /* Paint the node 0 */
    Node node = graph.getNode("0");
    node.addAttribute("ui.style", "fill-color: rgb(0,100,255); size: 20px;");
    
    /* Get the size of the graph that might not match the size set */
    size_of_graph = graph.getNodeCount();
    
    Edge edge;
    
    System.out.println("Constructor: graph initialized");
    
    Pattern p = Pattern.compile("-?\\d+");
    Matcher m;

    int[] node_pair = {0, 0};
    List<Integer> list_of_nodes_connected = new ArrayList<Integer>();

    /* Initializing nodes */
    for(int i=0; i<size_of_graph; i++) {
      /* Get a list of nodes to which the node connects */
      for(Edge e:graph.getEachEdge()) {
        /* Get the pair of nodes connected */
        m = p.matcher(e.getId());
        m.find();
        if(Integer.parseInt(m.group()) > 0){
          node_pair[0] = Integer.parseInt(m.group());
          
        }
        else{
          node_pair[0] = -Integer.parseInt(m.group());

        }

        m.find();
        if(Integer.parseInt(m.group()) > 0){
          node_pair[1] = Integer.parseInt(m.group());

        }
        else{
          node_pair[1] = -Integer.parseInt(m.group());

        }

        /* Check if any of the nodes is the node being checked */
        if(node_pair[0] == i){
          list_of_nodes_connected.add(node_pair[1]);

        }
        else if(node_pair[1] == i){
          list_of_nodes_connected.add(node_pair[0]);

        }

      }

      /* Initialize threads for the node */
      Thread nodeThread = new Gossip_node(50000 + i, list_of_nodes_connected);
      nodeThread.start();

      list_of_nodes_connected.clear();
      
    }    

    System.out.println("Constructor: all threads initalized");
    
    
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
      
      int node_that_sent = 0;
      int node_that_updated = 0;
      
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
            node_that_sent = Integer.parseInt(receive_data.substring(0, receive_data.length() - 5)) - 50000;
            node_that_updated = Integer.parseInt(receive_data.substring(receive_data.length() - 5)) - 50000;
            
            if(node_that_updated > node_that_sent){
              link_to_update = node_that_sent + "-" + node_that_updated;

            }
            else{
              link_to_update = node_that_updated + "-" + node_that_sent;

            }
            
            /* Do not act on the starter connection */
            if(node_that_sent != -1 && node_that_updated != -1){
              /* Good practices lesson:
              * Verify if the node/edge is found, else program will crash when it doesnt. 
              * Here we have the certainty that it exists so we progress forward */
              /* Coloring stuff 
              * Node */
              node = graph.getNode(Integer.toString(node_that_updated));
              node.addAttribute("ui.style", "fill-color: blue;");
              /* Coloring stuff 
              * Link */
              edge = graph.getEdge(link_to_update);
              edge.addAttribute("ui.style", "fill-color: rgb(255,69,0);");
              
              System.out.println("Constructor: updated node " + node_that_updated + " and link " + link_to_update);
            
            }
            
          }
          
          
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
  public List<Integer> client_ports;
  
  public int id = 0;
  public int missing_nodes = 0;

  public Random random_gen = new Random();

  /* This initializing method allows us to send arguments to the class
   * whenever we are initalizing a new thread */
  public Gossip_node(int server_port, List<Integer> client_ports){
    this.server_port = server_port;
    this.client_ports= new ArrayList<Integer>(client_ports);

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
    port_list = new ArrayList<Integer>(this.client_ports);
    
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
            int client = port_list.get(client_id) + 50000;
            
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
              int client = port_list.get(client_id) + 50000;
              
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
            port_list = new ArrayList<Integer>(this.client_ports);
            
            /* Remove the sender from the list */
            if (port_list.contains(receive_packet.getPort())){
              port_list.remove(port_list.indexOf(receive_packet.getPort()));

            }
            
            missing_nodes = port_list.size();
            
            /* Choose a random client from the stack of ports */
            int client_id = random_gen.nextInt(missing_nodes);
            int client = port_list.get(client_id) + 50000;
            
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