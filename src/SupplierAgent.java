import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;

import java.util.Vector;

public class SupplierAgent extends Agent {

	public void setup() {
		addBehaviour(new FIPARequestResp(this, MessageTemplate.MatchPerformative(ACLMessage.REQUEST)));
	}
	
	class FIPARequestResp extends AchieveREResponder {

		public FIPARequestResp(Agent a, MessageTemplate mt) {
			super(a, mt);
		}
		
		protected ACLMessage handleRequest(ACLMessage request) {
			ACLMessage reply = request.createReply();
			// ...
			reply.setPerformative(ACLMessage.AGREE);
			return reply;
		}
		
		protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) {
			ACLMessage result = request.createReply();
			// ...
			//result.setPerformative(ACLMessage.INFORM);
			//result.setContentObject(); <-- any object (vectors, lists, etc...)
			//result.setContent("Welelele");
			return result;
		}

	}
	

}
