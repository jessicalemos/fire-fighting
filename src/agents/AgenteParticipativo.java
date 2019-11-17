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
import java.util.Random;

public class AgenteParticipativo extends Agent implements Serializable{
	protected double combustivel_atual;
	protected double combustivel_max;
	protected boolean disponivel;
	protected int agua_atual;
	protected int agua_max; 
	protected int velocidade;
	protected int pos_x;
	protected int pos_y;
	protected int dest_x = 100;
	protected int dest_y = 100;
	protected List<Posicao> local_agua;
	protected List<Posicao> local_combustivel;
	protected double consumo;
	protected boolean incendio_extinto;
	protected boolean abastecer_agua;
	protected boolean abastecer_combustivel;
	protected int agua_x;
	protected int agua_y;
	protected int combustivel_x;
	protected int combustivel_y;
	
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
		Object[] args = getArguments();
		Abastecimento val = (Abastecimento) args[0];
		local_agua = val.get_agua();
		local_combustivel = val.get_combustivel();
		Random randomizer = new Random();
		pos_x = randomizer.nextInt(500);
		pos_y = randomizer.nextInt(500);
		disponivel=true;
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("combate");
		sd.setName(getLocalName());
		dfd.addServices(sd);
		this.addBehaviour(new EnviaInicio());
		this.addBehaviour(new EnviaPosicao(this,5000));
		this.addBehaviour(new Movimento(this, 1000));
		//this.addBehaviour(new RecebePosição());
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
			double dist = Math.sqrt(Math.pow((l.getX()-pos_x),2) + Math.pow((l.getY()-pos_y),2));
			if(min > dist) {
				min = dist;
				mais_proximo = l;
			}
		}
		return mais_proximo;
	}

	public void VerificaAbastecimento() {
		System.out.println("Entrei");
		if (combustivel_atual*2 < combustivel_max) {
			System.out.println("No abastecimento");
			Posicao local_combustivel = AbastecimentoMaisProximo(1);
			combustivel_x = local_combustivel.getX();
			combustivel_y = local_combustivel.getY();
			abastecer_combustivel = true;
			System.out.println(combustivel_x + " " + combustivel_y);
		}
		if (agua_atual*2 <= agua_max) {
			System.out.println("Na agua");
			Posicao local_agua = AbastecimentoMaisProximo(0);
			agua_x = local_agua.getX();
			agua_y = local_agua.getY();
			abastecer_agua = true;
			System.out.println(agua_x + " " + agua_y);
		}
	}
	
	private class Movimento extends TickerBehaviour {
		public Movimento(Agent a, long period) {
			super(a, period);
		}
		protected void onTick() {
			//trocar para false -> apenas para o teste
			if(disponivel==true) {
				double dist = Math.sqrt(Math.pow((dest_x-pos_x),2) + Math.pow((dest_y-pos_y),2));
				if (abastecer_combustivel == true) {
					System.out.println("Vou para o combustível " + getAID().getLocalName());
					dest_x = combustivel_x;
					dest_y = combustivel_y;
					dist = Math.sqrt(Math.pow((dest_x-pos_x),2) + Math.pow((dest_y-pos_y),2));
				}
				else if (abastecer_agua == true) {
					System.out.println("Vou para a água " + getAID().getLocalName());
					dest_x = agua_x;
					dest_y = agua_y;
					dist = Math.sqrt(Math.pow((dest_x-pos_x),2) + Math.pow((dest_y-pos_y),2));
				}
				int destino_dist = (int) dist;
				if (abastecer_combustivel == true && destino_dist <= velocidade) {
					System.out.println("Cheguei ao combustível " + getAID().getLocalName());
					pos_x = dest_x;
					pos_y = dest_y;
					combustivel_atual = combustivel_max;
					abastecer_combustivel = false;
					//disponivel = true
					System.out.println("Abasteci " + getAID().getLocalName() + " tenho combustivel: " + combustivel_atual);
				}
				else if (abastecer_agua == true && abastecer_combustivel == false && destino_dist <= velocidade) {
					System.out.println("Cheguei à água " + getAID());
					pos_x = dest_x;
					pos_y = dest_y;
					abastecer_agua = false;
					agua_atual = agua_max;
					//disponivel = true
					System.out.println("Abasteci " + getAID().getLocalName() + " tenho agua: " + agua_atual);
				}
				if (destino_dist <= velocidade && incendio_extinto==false) {
					pos_x = dest_x;
					pos_y = dest_y;
					agua_atual--;
					incendio_extinto = true;
					System.out.println("Em abastacimento "+ getAID().getLocalName() + " agua "+agua_atual);
					VerificaAbastecimento();
				} else {
					int cenas_x = (int) (velocidade * (dest_x - pos_x) / dist);
					int cenas_y = (int) (velocidade * (dest_y - pos_y) / dist);
					pos_x = pos_x + cenas_x;
					pos_y = pos_y + cenas_y;
					System.out.println("Em movimento agente: " + getAID().getLocalName() + " pos_x " +pos_x + " pos_y " + pos_y);
					int dist_percorrida = (int) Math.sqrt(Math.pow(cenas_x, 2) + Math.pow(cenas_y, 2));
					combustivel_atual -= dist_percorrida * consumo;
					System.out.println("Combustivel atual: " + combustivel_atual);
				}
			}
		}
	}
	
	protected class RecebePosição extends CyclicBehaviour{
		public void action() {
			ACLMessage mensagem= receive();
			if (mensagem!=null) {
				String[] coords = mensagem.getContent().split(";");
				dest_x=Integer.parseInt(coords[0]);
				dest_y=Integer.parseInt(coords[1]);
				System.out.println(dest_x);
				disponivel = false;
				incendio_extinto = false;
				System.out.println("Agente " + getAID().getLocalName() + " recebeu incêndio -> coords " + dest_x + " " + dest_y );
			}
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
			Agente a= new Agente(combustivel_atual,agua_atual,pos_x,pos_y,disponivel);
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
