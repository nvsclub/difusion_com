import java.util.concurrent.TimeUnit;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;

public class Tutorial {
	public static void main(String args[]) {
    Graph graph = new SingleGraph("Tutorial 1");
    
    /* We can set the display in the beginning and we do not need to worry about it anymore */
    graph.display();
    
    /* Adding nodes */
    graph.addNode("15001");
    graph.addNode("15002");
    graph.addNode("15003");

    /* Adding edges */
    graph.addEdge("1500115002", "15001", "15002");
    graph.addEdge("1500115003", "15001", "15003");
    graph.addEdge("1500215003", "15002", "15003");
    
    /* Little timer: use Thread.sleep(ms) inside threads */
    try{
      TimeUnit.SECONDS.sleep(3);

    } 
    catch(Exception e){
      System.out.println("Error in timer");
    }

    /* Good practices lesson:
     * Verify if the node/edge is found, else program will crash when it doesnt. */


    /* Coloring stuff 
     * Node */
    Node node = graph.getNode("15001");
    node.addAttribute("ui.style", "fill-color: blue;");
    
    /* Coloring stuff 
    * Link */
    Edge edge = graph.getEdge("1500115002");
    edge.addAttribute("ui.style", "fill-color: red;");


	}
}
