import jade.core.Agent;
import jade.wrapper.StaleProxyException;

public class ClassTesterAgent extends Agent {
    public void setup()
    {
        System.out.println("I was born!");
        try {
            getContainerController().createNewAgent("Test", "ClientAgent", null);
        }
        catch(StaleProxyException e) {e.printStackTrace();}
    }
}
