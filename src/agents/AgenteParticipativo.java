package agents;
import java.io.IOException;
import java.io.Serializable;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class AgenteParticipativo extends Agent implements Serializable{
	protected int combustivel;
	protected int agua; protected int velocidade;
	protected int pos_x;protected int pos_y;
	protected int dest_x;protected int dest_y;
	protected int tipo;
	protected void setup() {
		super.setup();
		pos_x=0; pos_y=0;
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("combate");
		sd.setName(getLocalName());
		dfd.addServices(sd);
		this.addBehaviour(new EnviaPosicao(this,5000));
		try {
			DFService.register(this, dfd);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
	}

	protected class EnviaPosicao extends TickerBehaviour{	
		public EnviaPosicao(Agent a, long period) {
			super(a, period);
		}
		protected void onTick() {
			AID i= new AID();
			i.setLocalName("AgenteCentral");
			ACLMessage msg= new ACLMessage(ACLMessage.INFORM);
			Agente a= new Agente(combustivel,agua,pos_x,pos_y,tipo);
			try {
				msg.setContentObject(a);
				msg.addReceiver(i);
				send(msg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
