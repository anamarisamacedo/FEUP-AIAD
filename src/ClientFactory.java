import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.wrapper.StaleProxyException;

public class ClientFactory extends Agent {
    private int totalClients = 10;
    public void setup()
    {
        HelperClass.registerAgent(this, "ClientFactory");
        //Generate clients
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
