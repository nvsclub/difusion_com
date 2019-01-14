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


/* CLASS DESCRIPTION  -  Constructor
* This class runs the main of the program. It is responsible for building the network of nodes on top of a predifined graph.
* 
* STEPS:
* 1 - Scan information from the terminal about the graph to be generated
* 2 - Generate the graph according to the demands
* 3 - Initialize all nodes of the network according to the graph
* 4 - Setup connection to receive information from the nodes 
* 5 - Monitor the nodes and change the graph according to changes on the network
* 
* */
/* @TODO
* Fix graphs on README.md
* */
class Constructor{

  private static final int port_offset = 60000;
  private static final int main_port = 65535;  
  private static final boolean enablePrints = false;  

  public static void main(String[] args){
    
    int algorithm_id;
    int size_of_graph;

    if (args.length != 2){

    /* STEP 1 - Scan information from the terminal about the graph to be generated */

    /* Initialize scanner to get data from terminal */
    Scanner sc = new Scanner(System.in);
    /* Get the graph generator to use */
    System.out.println("Starter: Choose the graph to be generated: (* not working properly)");
    System.out.println("1 - Barabasi Albert");
    System.out.println("2 - Chain*");
    System.out.println("3 - Chvatal");
    System.out.println("4 - Dorogovtsev-Mendes");
    System.out.println("5 - Flower Snark*");
    System.out.println("6 - Full Graph");
    System.out.println("7 - Full Grid*");
    System.out.println("8 - Incomplete Grid*");
    System.out.println("9 - Lobster");
    System.out.println("10 - Petersen");
    System.out.println("11 - RandomEuclidean");
    algorithm_id = sc.nextInt();
    sc.nextLine(); /* to skip the enter */

    /* Get the size of the graph to generate */
    System.out.println("Starter: Enter the size of the graph to be generated: ");
    size_of_graph = sc.nextInt();
    sc.nextLine(); /* to skip the enter when */

    }
    else{
      algorithm_id = Integer.parseInt(args[0]);
      size_of_graph = Integer.parseInt(args[1]);
    }
    /* STEP 2 - Generate the graph according to the demands */

    Graph graph = new SingleGraph("Graph");
    /* Defined as default in case of error */
    Generator gen = new DorogovtsevMendesGenerator(); /* ID x, links x-x */
    if(algorithm_id == 1){
      gen = new BarabasiAlbertGenerator(); /* ID x, links x_x */

    }
    else if(algorithm_id == 2){
      gen = new ChainGenerator(); /* @TODO ID x, links x_x */

    }
    else if(algorithm_id == 3){
      gen = new ChvatalGenerator(); /* ID xx+1, links xx_xx */

    }
    else if(algorithm_id == 5){
      gen = new FlowerSnarkGenerator(); /* @TODO ID Axxxx, AxxxxAxxxx */
      
    }
    else if(algorithm_id == 6){
      gen = new FullGenerator(); /* ID x, links x_x */
      
    }
    else if(algorithm_id == 7){
      gen = new GridGenerator(); /* @TODO ID x_y, links x */
      
    }
    else if(algorithm_id == 8){
      gen = new IncompleteGridGenerator(); /* @TODO ID x_y, links x_y-x_y */
      
    }
    else if(algorithm_id == 9){
      gen = new LobsterGenerator(); /* ID xxxx, links xxxx-xxxx */

    }
    else if(algorithm_id == 10){
      /* This algorithm is a special case
       * it always has 10 nodes */
      gen = new PetersenGraphGenerator(); /* ID xx, links (xx;xx) */
      
    }
    else if(algorithm_id == 11){
      gen = new RandomEuclideanGenerator(); /* ID x, links x-x */

    }

    gen.addSink(graph);
    gen.begin();
    
    for(int i=0; i<size_of_graph; i++) {
      gen.nextEvents();
    }
    
    gen.end();
    graph.display();

    /* Use for graph debugging */
    if(enablePrints){
      for(Node n:graph) {
        System.out.println(n.getId());
      }
      for(Edge e:graph.getEachEdge()) {
        System.out.println(e.getId());
      }

    }
      

    /* Get length of the ID for padding */
    int id_length = String.valueOf(size_of_graph).length();

    /* Detect the initial node based on the algorithm */
    String node_zero = new String();
    if(algorithm_id == 3){
      node_zero = "01";
    
    }
    else if(algorithm_id == 9){
      node_zero = "0000";
    
    }
    else if(algorithm_id == 10){
      node_zero = "00";
    }
    else{
      node_zero = "0";
    }
    
    /* Paint the node 0 (initial node) for identification */
    Node node = graph.getNode(node_zero);
    node.addAttribute("ui.style", "fill-color: rgb(0,100,255); size: 20px;");
    
    /* Get the size of the graph that might not match the size set */
    size_of_graph = graph.getNodeCount();
    
    if(enablePrints){
      System.out.println("Constructor: graph initialized");
      
    }
    
    /* STEP 3 - Initialize all nodes of the network according to the graph */
    
    /* Classes that allow us to separate numbers from strings */
    Pattern p = Pattern.compile("-?\\d+");
    Matcher m;
    
    Edge edge;
    int[] node_pair = {0, 0};
    List<Integer> list_of_nodes_connected = new ArrayList<Integer>();

    /* Initializing nodes */
    /* For bidirectional algorithms we need to remove one of the directional links */
    if(algorithm_id == 2){
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
          
          
          /* Filter one of the directions */
          if(node_pair[0] > node_pair[1]){
            /* Check if any of the nodes is the node being checked */
            if(node_pair[0] == i){
              list_of_nodes_connected.add(node_pair[1]);
              
            }
            else if(node_pair[1] == i){
              list_of_nodes_connected.add(node_pair[0]);
              
            }

          }
            
        }
        
        /* Initialize threads for the node */
        Thread nodeThread = new Gossip_node(port_offset + i, list_of_nodes_connected);
        nodeThread.start();
        
        list_of_nodes_connected.clear();
        
      }
     
    }
    /* For algorithms where count starts at 1 */
    if(algorithm_id == 3){
      for(int i=1; i<=size_of_graph; i++) {
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
            list_of_nodes_connected.add(node_pair[1]-1);
            
          }
          else if(node_pair[1] == i){
            list_of_nodes_connected.add(node_pair[0]-1);
            
          }
          
        }
        
        /* Initialize threads for the node */
        Thread nodeThread = new Gossip_node(port_offset + i - 1, list_of_nodes_connected);
        nodeThread.start();
        
        list_of_nodes_connected.clear();
        
      }
     
    }
    /* For all the other algorithms */
    else{
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
        Thread nodeThread = new Gossip_node(port_offset + i, list_of_nodes_connected);
        nodeThread.start();
        
        list_of_nodes_connected.clear();
        
      }    

    }
      
    if(enablePrints){
      System.out.println("Constructor: all threads initalized");
    }
    
    /* STEP 4 - Setup connection to receive information from the nodes */

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
      

      /* STEP 5 - Monitor the nodes and change the graph according to changes on the network */
      
      /* Creating buffers */
      byte[] receive_bytes = new byte[1024];
      
      /* Creating the strings to read the data */
      String receive_data = new String(); 
      
      /* Graphstream objects to update */
      String link_to_update = new String();
      String node_to_update = new String();
      String color_to_update = new String();
      
      int node_that_sent = 0;
      int node_that_updated = 0;

      if(enablePrints){
        System.out.println("Constructor: receiver port enabled");
      }
      
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
          
          /* Process the information received about the structure and modify the graph accordingly */
          if(receive_data.length() > 5){
            node_to_update = receive_data.substring(receive_data.length() - 16, receive_data.length() - 11);

            /* Guarantees that the order is correct in the link */
            node_that_sent = Integer.parseInt(receive_data.substring(0, receive_data.length() - 16)) - port_offset;
            node_that_updated = Integer.parseInt(receive_data.substring(receive_data.length() - 16, receive_data.length() - 11)) - port_offset;
            
            color_to_update = receive_data.substring(receive_data.length() - 11, receive_data.length());

            /* Change id in case ID = x+1 */
            if (algorithm_id == 3){
              node_that_sent++;
              node_that_updated++;
            }
            /* Check for correct order */
            if(node_that_updated > node_that_sent){
              /* Check algorithm for separation character */
              if(algorithm_id == 1){
                link_to_update = node_that_sent + "_" + node_that_updated;
                /* If the order of the edge coordinates isnt correct, correct it */
                if(graph.getEdge(link_to_update) == null){
                  link_to_update = node_that_updated + "_" + node_that_sent;
                  
                }
                node_to_update = node_that_updated + "";
                
              }
              else if(algorithm_id == 2){
                link_to_update = node_that_sent + "_" + node_that_updated;
                node_to_update = node_that_updated + "";
                
              }
              else if(algorithm_id == 3){
                /* Make sure that the padding is done correctly */
                link_to_update = String.format("%02d", node_that_sent) + "_" +  String.format("%02d", node_that_updated);
                node_to_update = String.format("%02d", node_that_updated);
                
              }
              else if(algorithm_id == 6){
                link_to_update = node_that_updated + "_" + node_that_sent;
                node_to_update = node_that_updated + "";
                
              }
              else if(algorithm_id == 9){
                /* Make sure that the padding is done correctly */
                link_to_update = String.format("%04d", node_that_sent) + "--" +  String.format("%04d", node_that_updated);
                /* If the order of the edge coordinates isnt correct, correct it */
                if(graph.getEdge(link_to_update) == null){
                  link_to_update = String.format("%04d", node_that_updated) + "--" +  String.format("%04d", node_that_sent);
                  
                }
                
                node_to_update = String.format("%04d", node_that_updated);
                
              }
              else if(algorithm_id == 10){
                /* Make sure that the padding is done correctly */
                link_to_update = "(" + String.format("%02d", node_that_sent) + ";" +  String.format("%02d", node_that_updated) + ")";
                node_to_update = String.format("%02d", node_that_updated);
                
              }
              else if(algorithm_id == 11){
                link_to_update = node_that_sent + "-" + node_that_updated;
                /* If the order of the edge coordinates isnt correct, correct it */
                if(graph.getEdge(link_to_update) == null){
                  link_to_update = node_that_updated + "-" + node_that_sent;
                  
                }
                node_to_update = node_that_updated + "";

              }
              else{
                link_to_update = node_that_sent + "-" + node_that_updated;
                node_to_update = node_that_updated + "";
                
              }
              
            }
            else{
              /* Check algorithm for separation character */
              if(algorithm_id == 1){
                link_to_update = node_that_updated + "_" + node_that_sent;
                /* If the order of the edge coordinates isnt correct, correct it */
                if(graph.getEdge(link_to_update) == null){
                  link_to_update = node_that_sent + "_" + node_that_updated;
                  
                }
                node_to_update = node_that_updated + "";
                
              }
              else if(algorithm_id == 2){
                link_to_update = node_that_updated + "_" + node_that_sent;
                node_to_update = node_that_updated + "";
                
              }
              else if(algorithm_id == 3){
                /* Make sure that the padding is done correctly */
                link_to_update = String.format("%02d", node_that_updated) + "_" +  String.format("%02d", node_that_sent);
                node_to_update = String.format("%02d", node_that_updated);
                
              }
              else if(algorithm_id == 6){
                link_to_update = node_that_sent + "_" + node_that_updated;
                node_to_update = node_that_updated + "";
                
              }
              else if(algorithm_id == 9){
                /* Make sure that the padding is done correctly */
                link_to_update = String.format("%04d", node_that_updated) + "--" +  String.format("%04d", node_that_sent);
                /* If the order of the edge coordinates isnt correct, correct it */
                if(graph.getEdge(link_to_update) == null){
                  link_to_update = String.format("%04d", node_that_sent) + "--" +  String.format("%04d", node_that_updated);
                  
                }
                node_to_update = String.format("%04d", node_that_updated);
                
              }
              else if(algorithm_id == 10){
                /* Make sure that the padding is done correctly */
                link_to_update = "(" + String.format("%02d", node_that_updated) + ";" +  String.format("%02d", node_that_sent) + ")";
                node_to_update = String.format("%02d", node_that_updated);
                
              }
              else if(algorithm_id == 11){
                link_to_update = node_that_updated + "-" + node_that_sent;
                /* If the order of the edge coordinates isnt correct, correct it */
                if(graph.getEdge(link_to_update) == null){
                  link_to_update = node_that_sent + "-" + node_that_updated;
                  
                }
                node_to_update = node_that_updated + "";

              }
              else{
                link_to_update = node_that_updated + "-" + node_that_sent;
                node_to_update = node_that_updated + "";

              }
              
            }
            
            /* Changing the connections afected *
            * Do not act on the starter connection */
            if(node_that_sent != -1 && node_that_updated != -1){
              /* Good practices lesson:
              * Verify if the node/edge is found, else program will crash when it doesnt. 
              * Here we have the certainty that it exists so we progress forward */
              /* Coloring stuff 
              * Node */
              node = graph.getNode(node_to_update);
              node.addAttribute("ui.style", "fill-color: rgb(" + color_to_update + "); size: 15px;");
              /* Coloring stuff 
              * Link */
              edge = graph.getEdge(link_to_update);
              edge.addAttribute("ui.style", "fill-color: rgb(" + color_to_update + "); size: 3px;");
              
              if(enablePrints){
                System.out.println("Constructor: updated node " + node_that_updated + " and link " + link_to_update);
              }

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


/* CLASS DESCRIPTION  -  Gossip_node
* This class implements a node that should be run in a thread in order to simulate the newtork.
* The class inputs are the socket id where it will create the server and the socket ids from the nodes 
* that are connected to the node.
*
* STEPS:
* 1 - Initialize the server
* 2 - Monitor the messages received by the server and answer to them acording to the algorithm
*
* ALGORITHM
* 1 - When a new message is received check if the message is a new message or an acknowledge (positive or negative).
* 1.1 - If it is a positive acknowledge continue difunding the message
* 1.2 - If it is a negative acknowledge
* 1.2.1 - Continue difunding the message if the probability allows it to continue
* 1.2.2 - Stop difunding the message
* 1.3 - If it is a new message, verify if it has a more recent ID than the previously difunded message
* 1.3.1 - If it does send a positive acknowledge and start difunding the new message
* 1.3.2 - Else send a negative acknowledge and continue the normal behaviour.
*
* */
/* @TODO 
* */
class Gossip_node extends Thread{

  private static final long delay_per_cycle = 600;
  private static final int port_offset = 60000;
  private static final int main_port = 65535;
  private static final boolean enablePrints = false;

  public DatagramSocket connection_socket;
  
  public InetAddress ip_address;

  public int server_port;
  public List<Integer> client_ports;
  
  public int id = 0;
  public float k = 200;
  public int missing_nodes = 0;

  public Random random_gen = new Random();

  /* This initializing method allows us to send arguments to the class
   * whenever we are initalizing a new thread */
  public Gossip_node(int server_port, List<Integer> client_ports){
    this.server_port = server_port;
    this.client_ports= new ArrayList<Integer>(client_ports);

  }
  
  public void run(){ 
    /* STEP 1 - Initialize the server */

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
    
    if(enablePrints){
      System.out.println("Gossip node " + this.server_port + ": Initialized");
    
    }

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
    
    /* STEP 2 - Monitor the messages received by the server and answer to them acording to the algorithm */

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
            /* Sleep for visualization */
            Thread.sleep(delay_per_cycle);
            /* Choose a random client from the stack of ports */
            int client_id = random_gen.nextInt(missing_nodes);
            int client = port_list.get(client_id) + port_offset;
            
            /* Setup the packet and send it
            * and propagates the message */
            send_bytes = to_propagate.getBytes();
            send_packet = new DatagramPacket(send_bytes, send_bytes.length, this.ip_address, client);
            this.connection_socket.send(send_packet);
            
            /* Delete the port that was sent the message */
            missing_nodes--;
            port_list.remove(client_id);

            if(enablePrints){
              System.out.println("Gossip node " + this.server_port + ": Positive ACKNOWLEDGE received from " +  receive_packet.getPort());
            
            }

          }
          else{
            if(enablePrints){
              System.out.println("Gossip node " + this.server_port + ": Positive ACKNOWLEDGE received from " +  receive_packet.getPort());
              System.out.println("Gossip node " + this.server_port + ": No more nodes");
              
            }
            
          }
          
        }
        
        /* Negative acknowledge
        * If we receive a negative acknowledge our mission will be to keep difusing the message
        * if and only if the probabilities allow us to do so */
        else if (receive_data.contains("NACK")) {
          if (missing_nodes > 0){
            k=k/2;
            /* Generate a random number between 0 and 100 and checks it it should continue difusing */
            if (random_gen.nextInt(100) < k){
              /* Sleep for visualization */
              Thread.sleep(delay_per_cycle);
              /* Choose a random client from the stack of ports */
              int client_id = random_gen.nextInt(missing_nodes);
              int client = port_list.get(client_id) + port_offset;
              
              /* Setup the packet and send it
              * and propagates the message */
              send_bytes = to_propagate.getBytes();
              send_packet = new DatagramPacket(send_bytes, send_bytes.length, this.ip_address, client);
              this.connection_socket.send(send_packet);
              
              /* Delete the port that was sent the message */
              missing_nodes--;
              port_list.remove(client_id);

              if(enablePrints){
                System.out.println("Gossip node " + this.server_port + ": Received negative ACKNOWLEDGE - Continued transmission");

              }

            }
            
            else{
              if(enablePrints){
                System.out.println("Gossip node " + this.server_port + ": Received negative ACKNOWLEDGE - Stopped transmission");
              
              }

            }
            
          }
          else{
            if(enablePrints){
              System.out.println("Gossip node " + this.server_port + ": Received negative ACKNOWLEDGE");
              System.out.println("Gossip node " + this.server_port + ": No more nodes");
            
            }

          }
          
        }
        
        /* Receives new instruction
        *
        *  Whenever we receive a new instruction we need to decide if we will be difusing it or
        * if not. The message to be transmited will be ordered by id and our goal will be to 
        * always transmit the most recent message */ 
        else if (receive_data.contains("DATA")) {
          
          /* Get message id */
          int new_message_id = Integer.parseInt(receive_data.substring(0, receive_data.length() - 11).replaceAll("\\D+", ""));

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
            for(int i = 0; i < port_list.size(); i++){
              if(port_list.get(i) + port_offset == receive_packet.getPort()){
                port_list.remove(i);

              }

            }
            
            missing_nodes = port_list.size();
            
            /* Check if node is terminal 
             * if not terminal, propagate message */
            if(missing_nodes > 0){
              /* Sleep for visualization */
              Thread.sleep(delay_per_cycle);
              /* Choose a random client from the stack of ports */
              int client_id = random_gen.nextInt(missing_nodes);
              int client = port_list.get(client_id) + port_offset;
              
              /* Update the list */
              missing_nodes--;
              port_list.remove(client_id);
              
              /* Setup the packet and send it
              * and propagates the message */
              send_bytes = to_propagate.getBytes();
              send_packet = new DatagramPacket(send_bytes, send_bytes.length, ip_address, client);
              this.connection_socket.send(send_packet);

            }
              
            if(enablePrints){
              System.out.println("Gossip node " + this.server_port + ": Message received from " + receive_packet.getPort());
              
            }
            
            /* Send the information to the main */
            send_data = Integer.toString(receive_packet.getPort()) + Integer.toString(this.server_port) + receive_data.substring(receive_data.length() - 11, receive_data.length());
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
            if(enablePrints){
              System.out.println("Gossip node " + this.server_port + ": Message received from " + receive_packet.getPort());
            
            }

          }
          
        }      
        
      } catch (Exception e) { 
        e.printStackTrace(); 
      } 
      
    }
      
  }
    
}