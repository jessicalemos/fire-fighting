package agents;

import java.util.List;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import javax.swing.JScrollPane;
import java.util.ArrayList;
import java.util.HashMap;
import java.awt.*;
import javax.swing.*;
import java.awt.Dimension;
import java.awt.geom.*;
import javax.swing.border.LineBorder;
import java.util.Map;
import jade.core.AID;

public class AgenteInterface extends Agent{
	private static JLabel[][] grid = new JLabel[100][100];
	private List<Incendio> incendios;
	protected List<Posicao> local_agua;
	protected List<Posicao> local_combustivel;
	protected Map<AID,Posicao> agentes_mapa;
	private static Object[][] mapa;
	private static int tamanho_mapa = 100;
	
	protected void setup() {
		super.setup();
		Object[] args = getArguments();
		Mapa val = (Mapa) args[0];
		local_agua = val.get_agua();
		local_combustivel = val.get_combustivel();
		this.incendios = new ArrayList<Incendio>();
		this.agentes_mapa = new HashMap<AID, Posicao>();
		mapa = new Object[600][600];
		AdicionaMapa();
		JFrame frame = new JFrame(getClass().getSimpleName());
		frame.setSize(600,600);
		JPanel panel = new JPanel();
		panel.setBounds(0, 0, 500, 500);
		frame.getContentPane().setLayout(null);
		frame.getContentPane().add(panel);
		panel.setLayout(new GridLayout(100, 100));
		/*JScrollPane scrollPane = new JScrollPane();
		panel.add(scrollPane); 
		panel.setPreferredSize(new Dimension(100,100));*/
	    for (int i = 0; i < tamanho_mapa; i++){
	        for (int j = 0; j < tamanho_mapa; j++){
	            grid[j][i] = new JLabel();
	            grid[j][i].setBorder(new LineBorder(Color.BLACK));
	            grid[j][i].setHorizontalAlignment(SwingConstants.CENTER);
	            grid[j][i].setVerticalAlignment(SwingConstants.CENTER);
	            grid[j][i].setOpaque(true);
	            fillCell(mapa[j][i],grid[j][i]);
	            panel.add(grid[j][i]);
	        }
	    }
	    frame.setBounds(0, 0, 500, 500);
	    frame.setVisible(true);
	    //frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		this.addBehaviour(new ReceberMensagens());
	}
	
	public void AdicionaMapa() {
		for (int i = 0; i < 500; i++){
	        for (int j = 0; j < 500; j++){
	        	mapa[i][j] = "";
	        }
		}
		for(Posicao pa : local_agua) {
			mapa[pa.getX()][pa.getY()] = "AA";
		}
		for(Posicao pc : local_combustivel) {
			mapa[pc.getX()][pc.getY()] = "AC";
		}
	}
	
	private static void fillCell(Object cell, JLabel gridCell) {
		if (cell.equals("AA")) {
			gridCell.setBackground(Color.green);
		}
		else if (cell.equals("AC")) {
			gridCell.setBackground(Color.orange);
		} 
		else if (cell.equals("I")) {
			gridCell.setBackground(Color.red);
		}
		else if (cell.equals("D") || cell.equals("A") || cell.equals("C")) {
			gridCell.setBackground(Color.blue);
		}
	}
	
	public static void fillGrid() {
		for (int i = 0; i < tamanho_mapa ; i++){
		    for (int j = 0; j < tamanho_mapa; j++){
		        grid[j][i].setOpaque(true);
		        fillCell(mapa[j][i], grid[j][i]);
		    }
		}
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
						Posicao pos_atual = new Posicao(pos_x,pos_y);
						agentes_mapa.put(a.getAgente(), pos_atual);
						if (a.getTipo() == 1) {
							mapa[pos_x][pos_y] = "D";
						}
						else if (a.getTipo() == 2) {
							mapa[pos_x][pos_y] = "A";
						}
						else if (a.getTipo() == 3) {
							mapa[pos_x][pos_y] = "C";
						}
						System.out.println("Agente interface a receber "+pos_x+" "+pos_y+" do "+a.getAgente().getLocalName());
					} else if(msg.getPerformative() == ACLMessage.INFORM && msg.getContentObject() instanceof Incendio) {
						Incendio i = (Incendio) msg.getContentObject();
						incendios.add(i);
						Posicao incendio = i.getPos();
						mapa[incendio.getX()][incendio.getY()] = "I";
					}
					else if(msg.getPerformative() == ACLMessage.CONFIRM) {
						System.out.println(msg.getContent());
						String[] coordsIncendio = msg.getContent().split(";");
						int incendio=Integer.parseInt(coordsIncendio[0]);
						long duracao=Long.parseLong(coordsIncendio[1]);
						System.out.println("RECEBI QUE O INCENDIO TERMINOU" + incendio +"durou" + duracao);
						incendios.get(incendio).setTime(duracao);
						Incendio i = incendios.get(incendio);
						Posicao p = i.getPos();
						mapa[p.getX()][p.getY()] = "";
					} else block();
					fillGrid();
				}
			}catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}