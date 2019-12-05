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
				if (msg !=null) {
					if (msg.getPerformative() == ACLMessage.INFORM && msg.getContentObject() instanceof Agente) {
						Agente a = (Agente) msg.getContentObject();
						int pos_x = a.getPos_x();
						int pos_y = a.getPos_y();
						System.out.println("Agente interface a receber "+pos_x+" "+pos_y+" do "+a.getAgente().getLocalName());
					} else if(msg.getPerformative() == ACLMessage.INFORM && msg.getContentObject() instanceof Incendio) {
						Incendio i = (Incendio) msg.getContentObject();
						incendios.add(i);
					}
					else if(msg.getPerformative() == ACLMessage.CONFIRM) {
						System.out.println(msg.getContent());
						String[] coordsIncendio = msg.getContent().split(";");
						int incendio=Integer.parseInt(coordsIncendio[0]);
						long duracao=Long.parseLong(coordsIncendio[1]);
						System.out.println("RECEBI QUE O INCENDIO TERMINOU" + incendio +"durou" + duracao);
						incendios.get(incendio).setTime(duracao);
					} else block();
				}
			}catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}