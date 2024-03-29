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
import java.util.List;
import java.util.Map;
import java.util.Random;
import Apoio.*;

public class AgenteParticipativo extends Agent implements Serializable{
	protected double combustivel_atual;
	protected double combustivel_max;
	protected boolean disponivel;
	protected int agua_atual;
	protected int agua_max;
	protected int velocidade;
	protected Posicao pos;
	protected Posicao dest;
	protected List<Posicao> local_agua;
	protected List<Posicao> local_combustivel;
	protected double consumo;
	protected boolean incendio_extinto;
	protected boolean abastecer_agua;
	protected boolean abastecer_combustivel;
	protected Posicao agua_pos;
	protected Posicao combustivel_pos;
	protected int id_incendio;

	public boolean getDisponivel() {
		return disponivel;
	}

	public void setDisponivel(boolean disponivel) {
		this.disponivel = disponivel;
	}
	public double getCombustivel() {
		return combustivel_atual;
	}
	public void setCombustivel(double combustivel) {
		this.combustivel_atual = combustivel;
	}

	public int getAgua() {
		return agua_atual;
	}

	public void setAgua(int agua) {
		this.agua_atual = agua;
	}

	public int getVelocidade() {
		return velocidade;
	}

	public void setVelocidade(int velocidade) {
		this.velocidade = velocidade;
	}

	public Posicao getPos() {
		return pos;
	}

	public void setPos(Posicao pos) {
		this.pos=pos;
	}

	public Posicao getDest() {
		return dest;
	}

	public void setDest(Posicao dest) {
		this.dest.setX(dest.getX());
		this.dest.setY(dest.getY());
	}

	protected void setup() {
		super.setup();
		Object[] args = getArguments();
		Mapa val = (Mapa) args[0];
		Map<String, List<Posicao>> zonas = val.get_agente_zonas();
		List<Posicao> zona = zonas.get(getAID().getLocalName());
		int min_x = 500;
		int max_x = 0;
		int min_y = 500;
		int max_y = 0;
		for(Posicao p : zona) {
			min_x = Math.min(min_x,p.getX());
			max_x = Math.max(max_x,p.getX());
			min_y = Math.min(min_y,p.getX());
			max_y = Math.max(max_y,p.getX());			
		}
		Random randomizer = new Random();
		int x_rand = min_x + randomizer.nextInt(((max_x) - min_x) + 1);
		int y_rand = min_y + randomizer.nextInt(((max_y) - min_y) + 1);
		pos = new Posicao(x_rand,y_rand);
		local_agua = val.get_agua();
		local_combustivel = val.get_combustivel();
		disponivel = true;
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("combate");
		sd.setName(getLocalName());
		dfd.addServices(sd);
		this.addBehaviour(new EnviaInfo());
		this.addBehaviour(new EnviaPosicao(this,1000));
		this.addBehaviour(new Movimento(this, 1000));
		this.addBehaviour(new RecebePosição());
		try {
			DFService.register(this, dfd);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
	}

	public Posicao AbastecimentoMaisProximo(int tipo) {
		double min = 500;
		Posicao mais_proximo = null;
		List<Posicao> abastecimento;
		if (tipo == 0) {
			abastecimento = local_agua;
		} else {
			abastecimento = local_combustivel;
		}
		for(Posicao l:abastecimento) {
			double dist = Math.sqrt(Math.pow((l.getX()-pos.getX()),2) + Math.pow((l.getY()-pos.getY()),2));
			if(min > dist) {
				min = dist;
				mais_proximo = l;
			}
		}
		return mais_proximo;
	}

	public void VerificaAbastecimento() {
		if (combustivel_atual*2 < combustivel_max) {
			Posicao local_combustivel = AbastecimentoMaisProximo(1);
			combustivel_pos = new Posicao(local_combustivel.getX(),local_combustivel.getY());
			abastecer_combustivel = true;
		}
		if (agua_atual == 0) {
			Posicao local_agua = AbastecimentoMaisProximo(0);
			agua_pos = new Posicao(local_agua.getX(),local_agua.getY());
			abastecer_agua = true;
		}
	}

	private class Movimento extends TickerBehaviour {
		public Movimento(Agent a, long period) {
			super(a, period);
		}
		protected void onTick() {
			int agente_tipo = -1;
			if(disponivel==true) {
				if(myAgent instanceof Drone) agente_tipo = 1;
				else if (myAgent instanceof Aeronave) agente_tipo = 2;
				else agente_tipo = 3;
			}
			else {
				if (incendio_extinto == true && abastecer_agua == false && abastecer_combustivel == false) {
					disponivel = true;
					EnviaDisponivel();
				}
				else {
					double dist = Math.sqrt(Math.pow((dest.getX()-pos.getX()),2) + Math.pow((dest.getY()-pos.getY()),2));
					if (abastecer_combustivel == true) {
						dest.setX(combustivel_pos.getX());
						dest.setY(combustivel_pos.getY());
						dist = Math.sqrt(Math.pow((dest.getX()-pos.getX()),2) + Math.pow((dest.getY()-pos.getY()),2));
					}
					else if (abastecer_agua == true) {
						dest.setX(agua_pos.getX());
						dest.setY(agua_pos.getY());
						dist = Math.sqrt(Math.pow((dest.getX()-pos.getX()),2) + Math.pow((dest.getY()-dest.getY()),2));
					}
					int destino_dist = (int) dist;
					if (abastecer_combustivel == true && destino_dist <= velocidade) {
						pos.setX(dest.getX());
						pos.setY(dest.getY());
						EnviaMovimento(agente_tipo);
						combustivel_atual = combustivel_max;
						abastecer_combustivel = false;
						System.out.println("Abasteci " + getAID().getLocalName() + " tenho combustivel: " + combustivel_atual);
					}
					else if (abastecer_agua == true && abastecer_combustivel == false && destino_dist <= velocidade) {
						pos.setX(dest.getX());
						pos.setY(dest.getY());
						EnviaMovimento(agente_tipo);
						abastecer_agua = false;
						agua_atual = agua_max;
						System.out.println("Abasteci " + getAID().getLocalName() + " tenho agua: " + agua_atual);
					}
					else if (destino_dist <= velocidade && incendio_extinto==false) {
						pos.setX(dest.getX());
						pos.setY(dest.getY());
						agua_atual--;
						incendio_extinto = true;
						EnviaMovimento(agente_tipo);
						InformIncendioExtinto();
						System.out.println("Em abastacimento "+ getAID().getLocalName() + " agua "+agua_atual);
						VerificaAbastecimento();
					} else {
						int next_x = (int) (velocidade * (dest.getX() - pos.getX()) / dist) + 1;
						int next_y = (int) (velocidade * (dest.getY() - pos.getY()) / dist) + 1;
						pos.setX(pos.getX() + next_x);
						pos.setY(pos.getY() + next_y);
						EnviaMovimento(agente_tipo);
						System.out.println("Em movimento agente: " + getAID().getLocalName() + " pos_x " +pos.getX() + " pos_y " + pos.getY());
						int dist_percorrida = (int) Math.sqrt(Math.pow(next_x, 2) + Math.pow(next_y, 2));
						combustivel_atual -= dist_percorrida * consumo;
						System.out.println("Combustivel atual: " + combustivel_atual+" "+dist_percorrida);
					}
				}
			}
		}
	}
	
	protected void EnviaMovimento(int agente_tipo) {
		AID agente_interface = new AID();
		agente_interface.setLocalName("AgenteInterface");
		ACLMessage msgi = new ACLMessage(ACLMessage.INFORM);
		int tipo;
		Agente a = new Agente(getAID(), pos.getX(), pos.getY(),agente_tipo);
		try {
			msgi.setContentObject(a);
			msgi.addReceiver(agente_interface);
			send(msgi);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected class RecebePosição extends CyclicBehaviour{
		public void action() {
			ACLMessage mensagem= receive();
			if (mensagem!=null) {
				String[] coords = mensagem.getContent().split(";");
				dest = new Posicao(Integer.parseInt(coords[1]),Integer.parseInt(coords[2]));
				disponivel = false;
				incendio_extinto = false;
				System.out.println("Agente " + getAID().getLocalName() + " recebeu incêndio -> coords " + dest.getX() + " " + dest.getY());
				id_incendio = Integer.parseInt(coords[0]);
				System.out.println("Id incendio: "+id_incendio);
			}
		}
	}

	private class EnviaInfo extends OneShotBehaviour{
		public void action() {
			AID i= new AID();
			i.setLocalName("AgenteCentral");
			ACLMessage msg= new ACLMessage(ACLMessage.INFORM);
			try {
				int tipo;
				if(myAgent instanceof Drone) tipo = 1;
				else if (myAgent instanceof Aeronave) tipo = 2;
				else tipo = 3;
				Agente a = new Agente(getAID(), combustivel_atual, agua_atual, pos.getX(), pos.getY(), disponivel, velocidade, consumo, tipo);
				msg.setContentObject(a);
				msg.addReceiver(i);
				send(msg);
				EnviaMovimento(tipo);
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
			if (disponivel == false) {
				AID agente_central = new AID();
				agente_central.setLocalName("AgenteCentral");
				ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
				Agente a = new Agente(combustivel_atual, agua_atual, pos.getX(), pos.getY());
				try {
					msg.setContentObject(a);
					msg.addReceiver(agente_central);
					send(msg);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	private void InformIncendioExtinto() {
		ACLMessage msg = new ACLMessage(ACLMessage.CONFIRM);
		AID central = new AID();
		central.setLocalName("AgenteCentral");
		msg.addReceiver(central);
		msg.setContent(""+id_incendio);
		send(msg);
	}
	
	
	private void EnviaDisponivel() {
		ACLMessage msg= new ACLMessage(ACLMessage.INFORM_IF);
		AID central = new AID();
		central.setLocalName("AgenteCentral");
		msg.addReceiver(central);
		String s1 = this.getClass().toString();
		msg.setContent(""+disponivel+" "+(s1.substring(s1.indexOf(".")+1)));
		send(msg);
	}
}
