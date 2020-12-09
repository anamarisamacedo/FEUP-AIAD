import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import sajas.core.Agent;
import sajas.proto.AchieveREInitiator;
import sajas.proto.AchieveREResponder;
import uchicago.src.sim.network.DefaultDrawableNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


/*
 * Agent for the Distribution
 * */
public class DistributorAgent extends Agent {
	
	public List<Pair<Order, Double>> time_per_order = new ArrayList<Pair<Order, Double>>();
    private DistributorAgent distAgent;
    private DefaultDrawableNode myNode;

    public void setup() {
    	distAgent = this;
        addBehaviour(new FIPARequestResp(this, MessageTemplate.MatchPerformative(ACLMessage.REQUEST)));
        System.out.println("Distributor active!!");
        HelperClass.registerAgent(this, "Distributor");
    }

    //TODO: Receive argument to determine which algorithm to use for the allocation
    public DistributorAgent()
    {

    }

    public void setNode(DefaultDrawableNode node) {
        this.myNode = node;
    }

    public DefaultDrawableNode getNode() {
        return this.myNode;
    }

    //TODO: complete function to follow path created by the distributor
    public void nextPos()
    {
        myNode.setX(myNode.getX() + 1);
        myNode.setY(myNode.getY() + 1);
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
                System.out.println("Got the pickup location: " + requestMessage.getSecond().getLat() + ", " + requestMessage.getSecond().getLon());
               
                Distributor dist = new Distributor();
                time_per_order = dist.allocate(orders, pickup);
                
                addBehaviour( new FIPARequestClientInit(distAgent, new ACLMessage(ACLMessage.REQUEST)));

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


        class FIPARequestClientInit extends AchieveREInitiator {

    		public FIPARequestClientInit(Agent a, ACLMessage msg) {
    			super(a, msg);
    		}

    		protected Vector<ACLMessage> prepareRequests(ACLMessage msg) {
    			Vector<ACLMessage> v = new Vector<ACLMessage>();
    			
    			for(int i = 0; i < time_per_order.size(); i++) {
    				ACLMessage reply = new ACLMessage(ACLMessage.REQUEST);
    				AID clientId = time_per_order.get(i).getFirst().getClientID();
    				reply.addReceiver(clientId);
    				
    				double time = time_per_order.get(i).getSecond();
    				String date = time_per_order.get(i).getFirst().getDate();
    				reply.setContent(String.format("%s , your order was successfully delivered in %f and was ordered at %s", clientId, time, date));
    				v.add(reply);
    			}
    			
    			return v;
    		}
    		
    		protected void handleAgree(ACLMessage agree) {
    			System.out.println(agree);
    		}
    		
    		protected void handleRefuse(ACLMessage refuse) {
    			System.out.println(refuse);
    			}
    		
    		protected void handleInform(ACLMessage inform) {
    			System.out.println(inform);
    			}
    		
    		protected void handleFailure(ACLMessage failure) {
    			System.out.println(failure);
    		}

    	}
    }
}