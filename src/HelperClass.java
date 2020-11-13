import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

public class HelperClass {
    public static AID getAIDbyType(Agent agent, String type)
    {
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd  = new ServiceDescription();
        //name of agent to search for (registered in setup)

        sd.setType(type);
        dfd.addServices(sd);
        DFAgentDescription[] result = null;
        try{
            result = DFService.search(agent, dfd);
        }
        catch(FIPAException fipaException){
            System.out.println("Got an exception");
            return null;
        }

        if(result.length == 0)
        {
            System.out.format("Couldn't find any %s agent\n", type);
            return null;
        }
        return result[0].getName();
    }

    public static void registerAgent(Agent agent, String type)
    {
        //Register agent in directory facilitator
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName( agent.getAID() );
        ServiceDescription sd  = new ServiceDescription();
        sd.setType( type );
        sd.setName(agent.getLocalName() );
        dfd.addServices(sd);

        try {
            DFService.register(agent, dfd );
        }
        catch (FIPAException fe) { fe.printStackTrace(); }
    }
}
