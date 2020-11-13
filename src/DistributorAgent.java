import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.AchieveREResponder;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


/*
 * Agent for the Distribution
 * */
public class DistributorAgent extends Agent {

	Supplier supplier = new Supplier();
    public void setup() {
        addBehaviour(new FIPARequestResp(this, MessageTemplate.MatchPerformative(ACLMessage.REQUEST)));

        HelperClass.registerAgent(this, "Distributor");
    }

    //Receives requests from the supplier
    class FIPARequestResp extends AchieveREResponder {

        public FIPARequestResp(Agent a, MessageTemplate mt) {
            super(a, mt);
        }

        protected ACLMessage handleRequest(ACLMessage request) {
            try {
            	//Get the content (pair with the orders array and the pickup location) of the received message
                Pair<ArrayList<Order>, Location> requestMessage = (Pair<ArrayList<Order>, Location>)(request.getContentObject());
                ArrayList<Order> orders = requestMessage.getFirst();
                Location pickup = requestMessage.getSecond();
                
                System.out.println("Got the orders, here is the first's date: " + orders.get(0).getDate());
                System.out.println("Got the location: " + requestMessage.getSecond().getLat() + ", " + requestMessage.getSecond().getLon());
               

            } catch (UnreadableException e) {
                e.printStackTrace();
            }
            ACLMessage reply = request.createReply();
            reply.setPerformative(ACLMessage.AGREE);
            return reply;
        }

        protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) {
            ACLMessage result = request.createReply();
            result.setPerformative(ACLMessage.INFORM);
            result.setContent("Distr: Request received! We will start distributing the orders!");
            return result;
        }


    }
}