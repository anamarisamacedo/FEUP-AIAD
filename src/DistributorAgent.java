import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.AchieveREInitiator;
import jade.proto.AchieveREResponder;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

/*
 * Agent for the Distribution
 * */
public class DistributorAgent extends Agent {
	public List<Pair<Order, Double>> time_per_order = new ArrayList<Pair<Order, Double>>();
	private DistributorAgent distAgent = this;

    public void setup() {
        addBehaviour(new FIPARequestResp(this, MessageTemplate.MatchPerformative(ACLMessage.REQUEST)));
        System.out.println("Distributor active!!");
    }

    //Receives requests from the supplier
    class FIPARequestResp extends AchieveREResponder {

        public FIPARequestResp(Agent a, MessageTemplate mt) {
            super(a, mt);
        }

        protected ACLMessage handleRequest(ACLMessage request) {
            try {
                List<Order> orders = (ArrayList<Order>)(request.getContentObject());
                Distributor dist = new Distributor(orders);
                time_per_order = dist.allocate();
                Arrays.toString(orders.toArray());
                System.out.println("Got the orders, here is the first's date: " + orders.get(0).getDate());
                //send order array
                //addBehaviour( new FIPARequestSupplierInit(this, new ACLMessage(ACLMessage.REQUEST), orders ));
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


    }
    
    class FIPARequestClientInit extends AchieveREInitiator {

		public FIPARequestClientInit(Agent a, ACLMessage msg) {
			super(a, msg);
		}

		protected Vector<ACLMessage> prepareRequests(ACLMessage msg) {
			Vector<ACLMessage> v = new Vector<ACLMessage>();
			
			for(int i = 0; i < DistributorAgent.this.time_per_order.size(); i++) {
				AID clientId = time_per_order.get(i).getFirst().getClientID();
				msg.addReceiver(clientId);
				
				double time = time_per_order.get(i).getSecond();
				String date = time_per_order.get(i).getFirst().getDate();
				msg.setContent(String.format("%s , your order was successfully delivered in %f and was ordered at %s", clientId, time, date));
				v.add(msg);
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