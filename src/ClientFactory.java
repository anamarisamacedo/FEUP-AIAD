import jade.core.Agent;
import jade.wrapper.StaleProxyException;

public class ClientFactory extends Agent {
    private int totalClients = 10;
    public void setup()
    {
        for(int i = 0; i < totalClients; i++)
        {
            try {
                getContainerController().createNewAgent("Client-" + Integer.toString(i), "ClientAgent", null).start();
            }
            catch(StaleProxyException e) {e.printStackTrace();}
        }
        System.out.println("ClientFactory: Created " + Integer.toString(totalClients) + " agents");
    }
}
