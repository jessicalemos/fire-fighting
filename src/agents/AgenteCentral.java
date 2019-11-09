package agents;
import jade.core.Agent;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import jade.content.ContentElement;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

public class AgenteCentral extends Agent {
	private HashMap<AID,AgenteParticipativo> agentesCombate;
	private List<Incendio> incendiosAtivos;
	private int drones;
	private int aeronaves;
	private int camioes;

	protected void setup() {
		super.setup();
		drones=10;aeronaves=5;camioes=2;
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("central");
		sd.setName(getLocalName());
		dfd.addServices(sd);
		this.addBehaviour(new RecebePosicao());
		this.addBehaviour(new EnviaCombate());
		try {
			DFService.register(this, dfd);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
	}

	private class RecebePosicao extends CyclicBehaviour {	
		public void action() {
			ACLMessage mensagem= receive();
			if (mensagem!=null) {
				try {
					if(mensagem.getContentObject() instanceof AgenteParticipativo) {
						try {
							ContentElement content = getContentManager().extractContent(mensagem);
							AgenteParticipativo c=(AgenteParticipativo)((Action) content).getAction();
							AID sender=mensagem.getSender();
							agentesCombate.put(sender,c);					
						} catch (CodecException | OntologyException e) {
							e.printStackTrace();
						}
					}
					else if (mensagem.getContentObject() instanceof Incendio) {
						ContentElement content;
						try {
							content = getContentManager().extractContent(mensagem);
							Incendio c=(Incendio)((Action) content).getAction();
							incendiosAtivos.add(c);
						} catch (CodecException | OntologyException e) {
							e.printStackTrace();
						}

					}
				} catch (UnreadableException e) {
					e.printStackTrace();
				}

			}
		}
	}
	
	private class EnviaCombate extends OneShotBehaviour{
		@Override
		public void action() {
			
			
		}
		
	}
}
