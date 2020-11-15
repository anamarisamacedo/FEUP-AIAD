import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.wrapper.StaleProxyException;
import java.util.concurrent.TimeUnit;

public class ClientFactory extends Agent {
    private int totalClients = 15;
    public void setup()
    {
        HelperClass.registerAgent(this, "ClientFactory");
        //Generate clients
        for(int i = 0; i < totalClients; i++)
        {
            try {
                TimeUnit.SECONDS.sleep(3);
                getContainerController().createNewAgent("Client-" + Integer.toString(i), "ClientAgent", null).start();
            }
            catch(StaleProxyException | InterruptedException e) {e.printStackTrace();}
        }
        System.out.println("ClientFactory: Created " + Integer.toString(totalClients) + " agents");
    }
}
