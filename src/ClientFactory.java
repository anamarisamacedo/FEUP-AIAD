import jade.core.Agent;
import jade.wrapper.StaleProxyException;

public class ClientFactory extends Agent {
    private int totalClients = 1;
    public void setup()
    {
//        try {
//            getContainerController().createNewAgent("OFAgent", "OrderFactoryAgent", null);
//        }
//        catch(StaleProxyException e) {e.printStackTrace();}

        for(int i = 0; i < totalClients; i++)
        {
            try {
                getContainerController().createNewAgent("Client-" + Integer.toString(i), "ClientAgent", null).activate();
            }
            catch(StaleProxyException e) {e.printStackTrace();}
        }
        System.out.println("Created " + Integer.toString(totalClients) + " agents");
    }
}
