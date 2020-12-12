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
    private Distributor distributor;
    private DefaultDrawableNode myNode;
    private Location location;
    private ArrayList<Order> orders;
    private Location pickup;
    private int nClients;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }


    public void setup() {
    	distAgent = this;
        addBehaviour(new FIPARequestResp(this, MessageTemplate.MatchPerformative(ACLMessage.REQUEST)));
        System.out.println("Distributor active!!");
        HelperClass.registerAgent(this, "Distributor");
    }

    public DistributorAgent(DistributorMethod method, int nrClients)
    {
        this.location = new Location(300, 300);
        this.nClients = nrClients;
        this.distributor = new Distributor(new Location(300, 300), method, nrClients);
    }

    public List<Vehicle> getFleet()
    {
        return distributor.getFleet();
    }

    public void moveVehicles()
    {
        if(distributor != null)
        {
            distributor.moveVehicles();
        }
    }

    public double getTotalTripsCost()
    {
        return distributor.getTotalTripsCost();
    }

    public void setNode(DefaultDrawableNode node) {
        this.myNode = node;
    }

    public DefaultDrawableNode getNode() {
        return this.myNode;
    }
    
    public List<Pair<Order, Double>> getTimeRegular(){
    	Distributor distributorRegular = new Distributor(new Location(300, 300), DistributorMethod.regular, this.nClients);
    	List<Pair<Order, Double>> time_order_regular = distributorRegular.allocateRegular(orders, pickup);
    	return time_order_regular;
    }
    
    public List<Pair<Order, Double>> getTimeRandom(){
    	Distributor distributorRandom = new Distributor(new Location(300, 300), DistributorMethod.random, this.nClients);
    	List<Pair<Order, Double>> time_order_random = distributorRandom.allocateRandom(orders, pickup);
    	return time_order_random;
    }
    
    public List<Pair<Order, Double>> getTimeEven(){
    	Distributor distributorEven = new Distributor(new Location(300, 300), DistributorMethod.even, this.nClients);
    	List<Pair<Order, Double>> time_order_even = distributorEven.allocateEven(orders, pickup);
    	return time_order_even;
    }
    
    public List<Pair<Order, Double>> getTimeReduceCost(){
    	Distributor distributorRC = new Distributor(new Location(300, 300), DistributorMethod.reduceCost, this.nClients);
    	List<Pair<Order, Double>> time_order_reduce_cost = distributorRC.allocateReduceCost(orders, pickup);
    	return time_order_reduce_cost;
    }
    
    public double getCostRegular(){
    	Distributor distributorRegular = new Distributor(new Location(300, 300), DistributorMethod.regular, this.nClients);
    	List<Pair<Order, Double>> time_order_regular = distributorRegular.allocateRegular(orders, pickup);
    	return distributorRegular.getTotalTripsCost();
    }
    
    public double getCostRandom(){
    	Distributor distributorRandom = new Distributor(new Location(300, 300), DistributorMethod.random, this.nClients);
    	List<Pair<Order, Double>> time_order_random = distributorRandom.allocateRandom(orders, pickup);
    	return distributorRandom.getTotalTripsCost();
    }
    
    public double getCostEven(){
    	Distributor distributorEven = new Distributor(new Location(300, 300), DistributorMethod.even, this.nClients);
    	List<Pair<Order, Double>> time_order_even = distributorEven.allocateEven(orders, pickup);
    	return distributorEven.getTotalTripsCost();
    }
    
    public double getCostReduceCost(){
    	Distributor distributorRC = new Distributor(new Location(300, 300), DistributorMethod.reduceCost, this.nClients);
    	List<Pair<Order, Double>> time_order_reduce_cost = distributorRC.allocateReduceCost(orders, pickup);
    	return distributorRC.getTotalTripsCost();
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
                orders = requestMessage.getFirst();
                pickup = requestMessage.getSecond();

                time_per_order = distributor.allocate(orders, pickup);
                
                addBehaviour(new FIPARequestClientInit(distAgent, new ACLMessage(ACLMessage.REQUEST)));

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