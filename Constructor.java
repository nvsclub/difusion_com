import java.net.*;
import java.util.Random;

class Constructor{
  
  
  public static void main(String[] args){
    
    System.out.println("Comecei");
    
    int sock_1 = 6001;
    int[] conn_1 = {6002, 6003};
    int sock_2 = 6002;
    int[] conn_2 = {6001, 6003};
    int sock_3 = 6003;
    int[] conn_3 = {6001, 6002};
    

    Thread node_1 = new Gossip_node(sock_1, conn_1);
    node_1.start();
    Thread node_2 = new Gossip_node(sock_2, conn_2);
    node_2.start();
    Thread node_3 = new Gossip_node(sock_3, conn_3);
    node_3.start();
    


    System.out.println("Acabei");

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
    
    System.out.println("Entrei no nó" + server_port);

    try{
      // Get local IP address
      this.ip_address = InetAddress.getByName("localhost");

      // Initialize sockets
      this.server_socket = new DatagramSocket(this.server_port);
      this.client_socket = new DatagramSocket();
    } catch (Exception e) { 
      e.printStackTrace(); 
    } 


    // Sender buffer
    byte[] send_data = new byte[64];    
    // Receiver buffer
    byte[] receive_data = new byte[64];    
    
    while(true){
      try{
        // Waits for new messages
        DatagramPacket send_packet;
        DatagramPacket receive_packet = new DatagramPacket(receive_data, receive_data.length);
        receive_data = receive_packet.getData();
        
        // Check if it is a message or ack
        if (receive_data[0] == (0xFF)) {
          // Do ack stuff
          // When it receives the message
          //// Selects random port
          int client = new Random().nextInt(this.client_ports.length);
          //// Sets the packet and sends it
          send_packet = new DatagramPacket(receive_data, receive_data.length, ip_address, this.client_ports[client]);
          this.client_socket.send(send_packet);

        }      
        else if (receive_data[0] == (0xF0)) {
          // Do nack stuff
          // When it receives the message
          if (new Random().nextInt(100) < 90){
            //// Selects random port
            int client = new Random().nextInt(this.client_ports.length);
            //// Sets the packet and sends it
            send_packet = new DatagramPacket(receive_data, receive_data.length, ip_address, this.client_ports[client]);
            this.client_socket.send(send_packet);

          }
        }      
        else if (receive_data[0] == (0xAA)) {
          // When it receives the message
          if (receive_data[4] > id){
            //// Answers to the new message
            send_packet = new DatagramPacket(receive_data, receive_data.length, receive_packet.getAddress(), receive_packet.getPort());
            this.client_socket.send(send_packet);
            
            //// Selects random port
            int client = new Random().nextInt(this.client_ports.length);
            //// Sets the packet and sends it
            send_packet = new DatagramPacket(receive_data, receive_data.length, ip_address, this.client_ports[client]);
            this.client_socket.send(send_packet);

          }
          else{
            //// Answers to the new message
            send_packet = new DatagramPacket(receive_data, receive_data.length, receive_packet.getAddress(), receive_packet.getPort());
            this.client_socket.send(send_packet);

          }

        }      
      } catch (Exception e) { 
				e.printStackTrace(); 
			} 
    }
      
  }

}