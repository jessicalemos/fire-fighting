package agents;
import jade.core.Agent;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
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
		this.incendiosAtivos = new ArrayList<Incendio>();
		this.agentesCombate= new HashMap<AID,AgenteParticipativo>();
		this.addBehaviour(new RecebePosicao());
	}

	private class RecebePosicao extends CyclicBehaviour {	
		public void action() {
			ACLMessage mensagem= receive();
			if (mensagem!=null) {
					try {
						if(mensagem.getContentObject() instanceof Agente) {
							Agente c= (Agente) mensagem.getContentObject();
							AID sender=mensagem.getSender();
							AgenteParticipativo x=agentesCombate.get(sender);
							x.setAgua(c.getAgua());x.setCombustivel(c.getCombustivel());x.setPos_x(c.getPos_x());x.setPos_y(c.getPos_y());x.setDisponivel(c.isDisponivel());
							agentesCombate.put(sender,x);
							System.out.println("Guardei informacao do "+sender.getLocalName());
						}
						else if (mensagem.getContentObject() instanceof Incendio) {
							
								Incendio c = (Incendio) mensagem.getContentObject();
								System.out.println("Vou registar o incendio:" + c.getGravidade() + " " + c.getpos_x() + " " + c.getpos_y() + "\n");
								incendiosAtivos.add(c);
						}
						else if (mensagem.getContentObject() instanceof AgenteParticipativo) {
							AgenteParticipativo c= (AgenteParticipativo) mensagem.getContentObject();
							AID sender=mensagem.getSender();
							agentesCombate.put(sender,c);
							System.out.println("Guardei informacao do "+sender.getLocalName());
						}
					} catch (UnreadableException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		}
	}	
