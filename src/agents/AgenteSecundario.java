package agents;
import java.io.IOException;
import java.util.Random;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class AgenteSecundario extends Agent {
	private int gravidade;
	private int pos_x;
	private int pos_y;
	private AID agente_central;

	protected void setup() {
		super.setup();
		agente_central = new AID();
		agente_central.setLocalName("AgenteCentral");
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Partcipativo");
		sd.setName(getLocalName());
		dfd.addServices(sd);

		try {
			DFService.register(this, dfd);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
		this.addBehaviour(new GerarIncendio(this,5000));
	}

	private class GerarIncendio extends TickerBehaviour{
		public GerarIncendio(Agent a, long period) {
			super(a, period);
		}
		
		protected void onTick() {
			Random randomizer = new Random();
			gravidade = randomizer.nextInt(3);
			pos_x = randomizer.nextInt(500);
			pos_y = randomizer.nextInt(500);
			myAgent.addBehaviour(new EnviaIncendio());	
		}
	}
	
	private class EnviaIncendio extends OneShotBehaviour {	
		public void action() {
				ACLMessage msg= new ACLMessage(ACLMessage.INFORM);
				msg.addReceiver(agente_central);
				Incendio atual= new Incendio(gravidade,pos_x,pos_y);
				try {
					msg.setContentObject(atual);
				}catch (IOException e) {
					// TODO Auto-generated catch blockSyz
					e.printStackTrace();
				}
				myAgent.send(msg);
				System.out.print("Gerei o incendio:" + gravidade + " " + pos_x + " " + pos_y + "\n");
				
		}
	}
	
	protected void takeDown() {
		try {
			DFService.deregister(this);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
		super.takeDown();
	}
}
