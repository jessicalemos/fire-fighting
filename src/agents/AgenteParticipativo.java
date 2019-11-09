package agents;
import java.io.IOException;
import java.io.Serializable;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class AgenteParticipativo extends Agent implements Serializable{
	protected int combustivel;
	protected int agua;
	protected int pos_x;protected int pos_y;
	protected int dest_x;protected int dest_y;
	protected void setup() {
		super.setup();
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("combate");
		sd.setName(getLocalName());
		dfd.addServices(sd);
		this.addBehaviour(new EnviaPosicao());
		try {
			DFService.register(this, dfd);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
	}

	private class EnviaPosicao extends CyclicBehaviour {	
		public void action() {
			DFAgentDescription template = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType("central");
			template.addServices(sd);

			DFAgentDescription[] result;
			try {
				result = DFService.search(myAgent, template);
				DFAgentDescription central= result[0];
				AID idQuartel = central.getName();
				ACLMessage msg= new ACLMessage(ACLMessage.INFORM);
	
				try {
					msg.setContentObject(this);
					msg.addReceiver(idQuartel);
					AID local= this.myAgent.getAID();
					msg.setSender(local);
					send(msg);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (FIPAException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
