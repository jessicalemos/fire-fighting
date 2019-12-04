package agents;

import java.util.List;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.util.ArrayList;

public class AgenteInterface extends Agent{
	private List<Incendio> incendios;
	protected void setup() {
		super.setup();
		this.incendios = new ArrayList<Incendio>();
		this.addBehaviour(new ReceberMensagens());
	}
	
	private class ReceberMensagens extends CyclicBehaviour {
		public void action() {
			ACLMessage msg = receive();
			try {
				if(msg != null && msg.getPerformative() == ACLMessage.INFORM) {
					Incendio i = (Incendio) msg.getContentObject();
					incendios.add(i);
				}
				else if(msg != null && msg.getPerformative() == ACLMessage.CONFIRM) {
					System.out.println(msg.getContent());
					String[] coordsAviao = msg.getContent().split(";");
					int incendio=Integer.parseInt(coordsAviao[0]);
					long duracao=Long.parseLong(coordsAviao[1]);
					System.out.println("RECEBI QUE O INCENDIO TERMINOU" + incendio +"durou" + duracao);
					incendios.get(incendio).setTime(duracao);
				}else
					block();
			}catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}