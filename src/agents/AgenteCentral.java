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
		this.addBehaviour(new EnviaCombate());
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
						x.setAgua(c.getAgua());
						x.setCombustivel(c.getCombustivel());
						x.setPos(c.getPos());
						agentesCombate.put(sender,x);
						System.out.println("Guardei informacao do "+sender.getLocalName());
					}
					else if (mensagem.getContentObject() instanceof Incendio) {

						Incendio c = (Incendio) mensagem.getContentObject();
						System.out.println("Vou registar o incendio:" + c.getGravidade() + " " + c.getPos().getX() + " " + c.getPos().getY() + "\n");
						incendiosAtivos.add(c);
					}
					else if (mensagem.getContentObject() instanceof AgenteParticipativo) {
						AgenteParticipativo c= (AgenteParticipativo) mensagem.getContentObject();
						AID sender=mensagem.getSender();
						agentesCombate.put(sender,c);
						System.out.println("Guardei informacao do "+sender.getLocalName());
					}
				} catch (Exception e) {
					String a= mensagem.getContent();
					AID sender= mensagem.getSender();
					AgenteParticipativo x = agentesCombate.get(sender);
					String[] lista = a.split(" ");
					if (lista[0].equals("true")) {
						x.setDisponivel(true);
						atualizaContadores(lista[1].trim());
					}
					else x.setDisponivel(false);
					agentesCombate.put(sender,x);
					System.out.println("disponivel"+ x.getLocalName()+" "+x.disponivel+" "+a.trim());
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
				AgenteParticipativo m = DisponivelMaisRapido(xinc,yinc,a.getGravidade());
				while (m.getPos().getX()==-99 && (drones>0||camioes>0||aeronaves>0) && i+1<incendios) {
					i++;
					a = incendiosAtivos.get(i);
					xinc = a.getPos().getX();
					yinc = a.getPos().getY();
					m = DisponivelMaisRapido(xinc, yinc,a.getGravidade());
				}
				if (m.getPos().getX()!=-99) {
					ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
					msg.setContent(xinc + ";" + yinc);
					msg.addReceiver(m.getAID());
					send(msg);
					incendiosAtivos.remove(a);
					System.out.println("enviei combate " + xinc + " " + yinc + " " + m.getLocalName());
					m.disponivel = false;
					agentesCombate.put(m.getAID(), m);
					eliminaAgenteContador(m);
				}
			}
		}
	}


	public AgenteParticipativo DisponivelMaisRapido(int x, int y,int gravidade) {
		AgenteParticipativo erro =new AgenteParticipativo();
		Posicao k = new Posicao(-99,0);
		erro.setPos(k);
		int dron = this.drones;
		double min = 10000;
		for (AgenteParticipativo l : agentesCombate.values()) {
			if (l.disponivel==true && consegueChegar(l, x, y)) {
				double dist = Math.sqrt(Math.pow((l.getPos().getX() - x), 2) + Math.pow((l.getPos().getY() - y), 2));
				double tempo = (dist / l.getVelocidade());
				if ((min > tempo && ((dron > 5) || (gravidade>=3))) || (min > tempo && dron <= 5 &&  (l instanceof Camiao || l instanceof Aeronave))) {
					min = tempo;
					erro = l;
				}
			}
		}
		return erro;
	}

	public Boolean consegueChegar(AgenteParticipativo agent, int x, int y) {
		int xagent = agent.getPos().getX();
		int yagent = agent.getPos().getY();
		boolean fim = false;
		double dist = Math.sqrt(Math.pow((xagent - x), 2) + Math.pow((yagent - y), 2));
		if (dist * agent.consumo < agent.getCombustivel()) fim = true;
		return fim;
	}
	private void eliminaAgenteContador(AgenteParticipativo x){
		if (x instanceof Drone) drones--;
		else if (x instanceof Camiao) camioes--;
		else if (x instanceof  Aeronave) aeronaves--;
	}
	private void atualizaContadores(String a){
		if (a.equals("Drone")) drones++;
		else if (a.equals("Camiao")) camioes++;
		else if (a.equals("Aeronave")) aeronaves++;
	}

}
