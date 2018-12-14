import java.net.*;


class Starter{

  public static void main(String[] args){ 
    
    System.out.println("A enviar ... ");
    
    try{
      // Get local IP address
      InetAddress ip_address = InetAddress.getByName("localhost");
      
      // Initialize sockets
      DatagramSocket client_socket = new DatagramSocket();


      String send_data = new String();
      send_data = "DATA_2";

      byte[] send_bytes = new byte[1024];
      send_bytes = send_data.getBytes();

      System.out.println(send_data);
      
      DatagramPacket send_packet;
      send_packet = new DatagramPacket(send_bytes, send_bytes.length, ip_address, 15001);
      client_socket.send(send_packet);
      
      System.out.println("Enviado com sucesso");
      
      client_socket.close();
      
    } catch (Exception e) { 
      System.out.println("Erro no envio");
      e.printStackTrace(); 
    } 


  }

}