package agents;
import java.io.IOException;
import java.io.Serializable;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class AgenteParticipativo extends Agent implements Serializable{
	protected int combustivel; protected boolean disponivel;
	protected int agua; protected int velocidade;
	protected int pos_x;protected int pos_y;
	protected int dest_x;protected int dest_y;
	public boolean getDisponivel() {
		return disponivel;
	}

	public void setDisponivel(boolean disponivel) {
		this.disponivel = disponivel;
	}
	public int getCombustivel() {
		return combustivel;
	}

	public void setCombustivel(int combustivel) {
		this.combustivel = combustivel;
	}

	public int getAgua() {
		return agua;
	}

	public void setAgua(int agua) {
		this.agua = agua;
	}

	public int getVelocidade() {
		return velocidade;
	}

	public void setVelocidade(int velocidade) {
		this.velocidade = velocidade;
	}

	public int getPos_x() {
		return pos_x;
	}

	public void setPos_x(int pos_x) {
		this.pos_x = pos_x;
	}

	public int getPos_y() {
		return pos_y;
	}

	public void setPos_y(int pos_y) {
		this.pos_y = pos_y;
	}

	public int getDest_x() {
		return dest_x;
	}

	public void setDest_x(int dest_x) {
		this.dest_x = dest_x;
	}

	public int getDest_y() {
		return dest_y;
	}

	public void setDest_y(int dest_y) {
		this.dest_y = dest_y;
	}
	protected void setup() {
		super.setup();
		pos_x=0; pos_y=0;
		disponivel=true;
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("combate");
		sd.setName(getLocalName());
		dfd.addServices(sd);
		this.addBehaviour(new EnviaInicio());
		this.addBehaviour(new EnviaPosicao(this,5000));
		try {
			DFService.register(this, dfd);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
	}

	private class EnviaInicio extends OneShotBehaviour{	
		public void action() {
	AID i= new AID();
	i.setLocalName("AgenteCentral");
	ACLMessage msg= new ACLMessage(ACLMessage.INFORM);
	try {
		msg.setContentObject(myAgent);
		msg.addReceiver(i);
		send(msg);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
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
			Agente a= new Agente(combustivel,agua,pos_x,pos_y,disponivel);
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
