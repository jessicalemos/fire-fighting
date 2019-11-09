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

public class AgenteSecundario extends Agent {
	private int gravidade;
	private int pos_x;private int pos_y;
	protected void setup() {
		super.setup();
		this.addBehaviour(new EnviaIncendio());
	}

	private class EnviaIncendio extends CyclicBehaviour {	
		public void action() {
			DFAgentDescription template = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType("central");
			template.addServices(sd);

			DFAgentDescription[] result;
			try {
				result = DFService.search(myAgent, template);
				DFAgentDescription central= result[0];
				AID receiver = central.getName();
				ACLMessage posicao= new ACLMessage(ACLMessage.INFORM);
				posicao.addReceiver(receiver);
				Incendio atual= new Incendio(gravidade,pos_x,pos_y);
				try {
					posicao.setContentObject(atual);
					send(posicao);
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
