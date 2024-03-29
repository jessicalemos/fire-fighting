package agents;
import jade.core.Agent;
import java.util.List;

import Apoio.*;

import java.util.ArrayList;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jess.*;

public class AgenteCentral extends Agent {
	private List<Incendio> incendios;
	private Rete engine;
	
	protected void setup() {
		super.setup();
		this.incendios = new ArrayList<Incendio>();
		this.addBehaviour(new RecebePosicao());
		this.engine= new Rete();
		try {
			engine.batch("Apoio/ex2.clp");
			engine.reset();
		} catch (JessException e) {
			e.printStackTrace();
		}
	}

	private class RecebePosicao extends CyclicBehaviour {
		public void action() {
			ACLMessage msg = receive();
			if (msg !=null) {
				try {
					if(msg.getPerformative() == ACLMessage.INFORM && msg.getContentObject() instanceof Agente) {
						Agente c = (Agente) msg.getContentObject();
						AID sender = msg.getSender();
							String local[]=sender.getLocalName().split(" ");
							if (c.isDisponibilidade()==null)
							{
								engine.executeCommand("(assert (agente (x "+c.getPos_x()+")(y "+c.getPos_y()+")(id "+local[1]+")(agua "+c.getAgua()+")(combustivel "+c.getCombustivel()+")))");
								engine.run();
							}
							else {
								engine.executeCommand("(assert (combate (velocidade "+c.getVelocidade()+")(consumo "+c.getConsumo()+")(tipo "+c.getTipo()+")(disponivel "+c.isDisponibilidade()+")(x "+c.getPos_x()+")(y "+c.getPos_y()+")(id "+local[1]+")(agua "+c.getAgua()+")(combustivel "+c.getCombustivel()+")))");
								engine.run();
							}
							System.out.println("Guardei informacao do "+sender.getLocalName());				
					}
					else if (msg.getPerformative() == ACLMessage.INFORM && msg.getContentObject() instanceof Incendio) {
						Incendio c = (Incendio) msg.getContentObject();
						long inicio = System.currentTimeMillis();
						c.setTime(inicio);
						System.out.println("Vou registar o incendio:" + c.getGravidade() + " " + c.getPos().getX() + " " + c.getPos().getY() + "\n");
						incendios.add(c);
						myAgent.addBehaviour(new EnviaCombate());
					}
					else if (msg.getPerformative() == ACLMessage.CONFIRM) {
						int incendio = Integer.parseInt(msg.getContent());
						Incendio i = incendios.get(incendio);
						long fim = System.currentTimeMillis();
						long duracao = fim - i.getTime();
						System.out.println("Confirmado extinção incendio " + incendio + " duracao em ms " + duracao);
						incendios.get(incendio).setExtinto(2);
						informaExtinto(duracao, incendio);
					}
					else if (msg.getPerformative() == ACLMessage.INFORM_IF) {
						String a= msg.getContent();
						AID l=msg.getSender();
						System.out.println("Disponivel "+ l.getLocalName()+" "+a.trim());
						String numero[]=l.getLocalName().split(" ");
						engine.executeCommand("(assert (disponivel (valor "+true+")(id "+numero[1]+")))");
						engine.run();
						myAgent.addBehaviour(new EnviaCombate());
					}
				} catch (UnreadableException | JessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	private class EnviaCombate extends OneShotBehaviour {
		public void action() {
			if (incendios.size()!=0) {
				int i=0;
				while(i<incendios.size()) {
					Incendio a = incendios.get(i);
					if(a.getExtinto()==0) {
						int xinc = a.getPos().getX();
						int yinc = a.getPos().getY();
						ArrayList agentes=new ArrayList<Agente>();
						try {
							QueryResult rs= engine.runQueryStar("procuraAgenteCombate", new ValueVector());
							while(rs.next())
							{
								AID b=new AID(); b.setLocalName("AgenteParticipativo "+rs.getString("id"));
								Agente pp= new Agente(b,Double.valueOf(rs.getString("c")),Integer.valueOf(rs.getString("a")),Integer.valueOf(rs.getString("x")),Integer.valueOf(rs.getString("y")),Boolean.valueOf(rs.getString("d")),Integer.valueOf(rs.getString("v")),Double.valueOf(rs.getString("co")),Integer.valueOf(rs.getString("t")));
								agentes.add(pp);
							}
						} catch (JessException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						Agente m = DisponivelMaisRapido(xinc,yinc,a.getGravidade(),agentes);
						if (m != null) {
							ACLMessage msgi = new ACLMessage(ACLMessage.REQUEST);
							AID agente_interface = new AID();
							agente_interface.setLocalName("AgenteInterface");
							msgi.addReceiver(agente_interface);
							msgi.setContent(""+i+";"+m.getTipo());
							send(msgi);
							ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
							msg.setContent(i + ";" + xinc + ";" + yinc);
							msg.addReceiver(m.getAgente());
							send(msg);
							System.out.println("Enviei combate " + i + " " + xinc + " " + yinc + " " +  m.getAgente().getLocalName());
							m.setDisponibilidade(false);
							a.setExtinto(1);
							String numero[]=m.getAgente().getLocalName().split(" ");
							try {
								engine.executeCommand("(assert (disponivel (valor "+m.isDisponibilidade()+")(id "+numero[1]+")))");
								engine.run();			
								engine.executeCommand("(facts)");
							} catch (JessException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						} 
						i++;
					} else i++;
				}
			}
		}
	}
	
	public boolean agente_reserva(Agente l) {
		String agente = "AgenteParticipativo ";
		String local_name = l.getAgente().getLocalName();
		return local_name.equals(agente+9) || local_name.equals(agente+10) || local_name.equals(agente+16);
	}

	public Agente DisponivelMaisRapido(int x, int y,int gravidade,ArrayList<Agente> agentes) {
		Agente erro = null;
		double min = 1000;
		for (Agente l : agentes) {
			if (l.isDisponibilidade() == true && consegueChegar(l, x, y)) {
				double dist = Math.sqrt(Math.pow((l.getPos().getX() - x), 2) + Math.pow((l.getPos().getY() - y), 2));
				double tempo = (dist / l.getVelocidade());
				if (min > tempo && gravidade<3 && !agente_reserva(l)) {
					erro = new Agente();
					min = tempo;
					erro = l;
				} else if (min > tempo && gravidade==3) {
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
	
	private void informaExtinto(long duracao, int idIncendio){
		ACLMessage msg= new ACLMessage(ACLMessage.CONFIRM);
		AID agente_interface = new AID();
		agente_interface.setLocalName("AgenteInterface");
		msg.addReceiver(agente_interface);
		msg.setContent("" + idIncendio + ";" + duracao);
		send(msg);
	}
}
