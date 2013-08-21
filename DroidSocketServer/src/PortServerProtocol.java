/*
 * Keep track and returns the number of "Pokes" the server has seen
 */
public class PortServerProtocol {

    private static int currentPokes = 0;
 
    public String processInput() {
    	
    	// Return the number of times the server has been poked
        return String.format("Current Pokes: %d", ++currentPokes);
    }
}