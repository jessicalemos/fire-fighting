package agents;
import jade.core.Agent;

import java.io.IOException;
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
	private HashMap<AID,Agente> agentesParticipativos;
	private List<Incendio> incendiosAtivos;
	private int drones;
	private int aeronaves;
	private int camioes;

	protected void setup() {
		super.setup();
		drones=10;aeronaves=5;camioes=2;
		this.incendiosAtivos = new ArrayList<Incendio>();
		this.agentesParticipativos = new HashMap<AID,Agente>();
		this.addBehaviour(new RecebePosicao());
		this.addBehaviour(new EnviaCombate());
	}

	private class RecebePosicao extends CyclicBehaviour {
		public void action() {
			ACLMessage msg = receive();
			if (msg !=null) {
				try {
					if(msg.getPerformative() == ACLMessage.INFORM && msg.getContentObject() instanceof Agente) {
						Agente c = (Agente) msg.getContentObject();
						AID sender = msg.getSender();
						if(agentesParticipativos.containsKey(sender)) {
							Agente x = agentesParticipativos.get(sender);
							x.setAgua(c.getAgua());
							x.setCombustivel(c.getCombustivel());
							x.setPos(c.getPos());
							agentesParticipativos.put(sender,x);
							System.out.println("Guardei informacao do "+sender.getLocalName()+ " ");
						}
						else {
							agentesParticipativos.put(sender, c);
							System.out.println("Guardei informacao do " + sender.getLocalName());
						}
					}
					else if (msg.getPerformative() == ACLMessage.INFORM && msg.getContentObject() instanceof Incendio) {
						Incendio c = (Incendio) msg.getContentObject();
						System.out.println("Vou registar o incendio:" + c.getGravidade() + " " + c.getPos().getX() + " " + c.getPos().getY() + "\n");
						incendiosAtivos.add(c);
					}
					else if (msg.getPerformative() == ACLMessage.CONFIRM) {
						int incendio = Integer.parseInt(msg.getContent());
						System.out.println("Confirmado extinção incendio " + incendio);
						//tirar comentário quando o arraylist estiver correto
						//incendiosAtivos.get(incendio).setExtinto(true);
					}
					else if (msg.getPerformative() == ACLMessage.INFORM_IF) {
						String a= msg.getContent();
						AID sender= msg.getSender();
						Agente x = agentesParticipativos.get(sender);
						String[] lista = a.split(" ");
						if (lista[0].equals("true")) {
							x.setDisponibilidade(true);
							atualizaContadores(lista[1].trim());
						}
						else x.setDisponibilidade(false);
						agentesParticipativos.put(sender,x);
						System.out.println("Disponivel"+ sender.getLocalName()+" "+x.isDisponibilidade()+" "+a.trim());
					}
				} catch (UnreadableException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	private class EnviaCombate extends CyclicBehaviour {
		public void action() {
			int incendios=incendiosAtivos.size();
			int i=0;
			if (incendios > 0) {
				Incendio a = incendiosAtivos.get(i);
				int xinc = a.getPos().getX();
				int yinc = a.getPos().getY();
				Agente m = DisponivelMaisRapido(xinc,yinc,a.getGravidade());
				while (m == null && (drones>0||camioes>0||aeronaves>0) && i+1<incendios) {
					i++;
					a = incendiosAtivos.get(i);
					xinc = a.getPos().getX();
					yinc = a.getPos().getY();
					m = DisponivelMaisRapido(xinc, yinc,a.getGravidade());
				}
				if (m != null) {
					ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
					msg.setContent(i + ";" + xinc + ";" + yinc);
					msg.addReceiver(m.getAgente());
					send(msg);
					incendiosAtivos.remove(a);
					System.out.println("Enviei combate " + i + " " + xinc + " " + yinc + " " +  m.getAgente().getLocalName());
					m.setDisponibilidade(false);
					agentesParticipativos.put(m.getAgente(), m);
					decrementaContador(m);
				}
			}
		}
	}

	public Agente DisponivelMaisRapido(int x, int y,int gravidade) {
		Agente erro = null;
		double min = 1000;
		for (Agente l : agentesParticipativos.values()) {
			if (l.isDisponibilidade() == true && consegueChegar(l, x, y)) {
				double dist = Math.sqrt(Math.pow((l.getPos().getX() - x), 2) + Math.pow((l.getPos().getY() - y), 2));
				double tempo = (dist / l.getVelocidade());
				if ((min > tempo && ((this.drones > 5) || (gravidade>=3))) || (min > tempo && this.drones <= 5 &&  (l.getTipo() == 2 || l.getTipo() == 3))) {
					erro = new Agente();
					min = tempo;
					erro = l;
				}
			}
		}
		return erro;
	}

	public Boolean consegueChegar(Agente agent, int x, int y) {
		int xagent = agent.getPos().getX();
		int yagent = agent.getPos().getY();
		boolean fim = false;
		double dist = Math.sqrt(Math.pow((xagent - x), 2) + Math.pow((yagent - y), 2));
		if (dist * agent.getConsumo() < agent.getCombustivel()) fim = true;
		return fim;
	}
	private void decrementaContador(Agente x){
		if (x.getTipo() == 1) drones--;
		else if (x.getTipo() == 3) camioes--;
		else if (x.getTipo() == 2) aeronaves--;
	}
	private void atualizaContadores(String a){
		if (a.equals("Drone")) drones++;
		else if (a.equals("Camiao")) camioes++;
		else if (a.equals("Aeronave")) aeronaves++;
	}

}
