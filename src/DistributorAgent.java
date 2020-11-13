import jade.core.Agent;
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

import org.javatuples.Pair;

/*
 * Agent for the Distribution
 * */
public class DistributorAgent extends Agent {

	Supplier supplier = new Supplier();
    public void setup() {
        addBehaviour(new FIPARequestResp(this, MessageTemplate.MatchPerformative(ACLMessage.REQUEST)));
    }

    //Receives requests from the supplier
    class FIPARequestResp extends AchieveREResponder {

        public FIPARequestResp(Agent a, MessageTemplate mt) {
            super(a, mt);
        }

        protected ACLMessage handleRequest(ACLMessage request) {
            try {
                Pair<ArrayList<Order>, Location> message = (Pair<ArrayList<Order>, Location>)(request.getContentObject());
                ArrayList<Order> orders = message.getValue0();
                System.out.println("Got the orders, here is the first's date: " + orders.get(0).getDate());
                System.out.println("Location: " + message.getValue1().getLat() + ", " + message.getValue1().getLon());
                //send order array
                //addBehaviour( new FIPARequestSupplierInit(this, new ACLMessage(ACLMessage.REQUEST), orders ));

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