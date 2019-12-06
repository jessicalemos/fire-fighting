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
import javax.swing.border.LineBorder;
import java.util.Map;
import jade.core.AID;
import java.util.stream.Collectors;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.chart.labels.*;
import org.jfree.chart.plot.*;
import java.text.DecimalFormat;


public class AgenteInterface extends Agent{
	private static JLabel[][] grid = new JLabel[100][100];
	private List<Incendio> incendios;
	protected List<Posicao> local_agua;
	protected List<Posicao> local_combustivel;
	protected Map<AID,Posicao> agentes_mapa;
	private static Object[][] mapa;
	private static int tamanho_mapa = 100;
	private final DefaultCategoryDataset model = new DefaultCategoryDataset();
	private DefaultPieDataset dataset = new DefaultPieDataset();
	private static Object[] agentes;
	private int total_combate;
	private JFrame f;
	
	protected void setup() {
		super.setup();
		this.total_combate = 0;
		Object[] args = getArguments();
		Mapa val = (Mapa) args[0];
		local_agua = val.get_agua();
		local_combustivel = val.get_combustivel();
		this.incendios = new ArrayList<Incendio>();
		this.agentes_mapa = new HashMap<AID, Posicao>();
		mapa = new Object[600][600];
		agentes = new Object[10];
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
		f = new JFrame("Estatísticas"); 
		frame.setLayout(new FlowLayout());
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		JFreeChart chart = ChartFactory.createBarChart("Incendios", "", "Nº", model, PlotOrientation.VERTICAL, false, false, false); 
		ChartPanel barPanel = new ChartPanel(chart);
		Update();
        JFreeChart chart2 = ChartFactory.createPieChart3D("Incendios extintos por agente",
                dataset, // data
                true,
                true, true);
        PieSectionLabelGenerator labelGenerator = new StandardPieSectionLabelGenerator("{0}: {2}", new DecimalFormat("0"), new DecimalFormat("0%"));
        PiePlot plot1 = (PiePlot) chart2.getPlot();
        plot1.setLabelGenerator(labelGenerator);
        ChartPanel chartPanel = new ChartPanel(chart2);
            
		f.getContentPane().add(barPanel,BorderLayout.WEST); 
		f.getContentPane().add(chartPanel,BorderLayout.EAST);
		f.pack(); 
		f.setLocationRelativeTo(null); 
		f.setVisible(true); 
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
		else {
			gridCell.setBackground(null);
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
	
	private void createSampleDataset() {
	    if (total_combate!=0) {
	    	int drones = (int)agentes[1];
	    	int camioes = (int)agentes[3];
	    	int aeronaves = (int)agentes[2];
	    	System.out.println(drones + " total " + total_combate + " PERCENTAGEM " + (double)drones/total_combate * 100);
		    dataset.setValue("Drones", new Double((double)drones/total_combate * 100));
		    dataset.setValue("Camiões", new Double((double)camioes/total_combate * 100));
		    dataset.setValue("Aeronaves", new Double((double)aeronaves/total_combate * 100));
	    }
	}
	
	public void Update() {
		String ROW_KEY = "Values"; 
		List<Incendio> incendios_extintos = incendios
				  .stream()
				  .filter(c -> c.getExtinto()==2)
				  .collect(Collectors.toList());
		List<Incendio> incendios_ativos = incendios
				  .stream()
				  .filter(c -> c.getExtinto()!=2)
				  .collect(Collectors.toList());
		List<Incendio> incendios_combate = incendios
				  .stream()
				  .filter(c -> c.getExtinto()==1)
				  .collect(Collectors.toList());
		model.setValue(incendios_ativos.size(), ROW_KEY, "Ativos"); 
		model.setValue(incendios_combate.size(), ROW_KEY, "Em combate"); 
		model.setValue(incendios_extintos.size(), ROW_KEY, "Extintos"); 
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
						Posicao pos_anterior = agentes_mapa.get(a.getAgente());
						if(pos_anterior!= null) {
							mapa[pos_anterior.getX()][pos_anterior.getY()] = "";
						}
						agentes_mapa.put(a.getAgente(), pos_atual);
						if (a.getTipo() == 1) {
							mapa[pos_x][pos_y] = "D";
							agentes[1] = 0;
						}
						else if (a.getTipo() == 2) {
							mapa[pos_x][pos_y] = "A";
							agentes[2] = 0;
						}
						else if (a.getTipo() == 3) {
							mapa[pos_x][pos_y] = "C";
							agentes[3] = 0;
						}
						System.out.println("Agente INTERFACE a receber "+pos_x+" "+pos_y+" do "+a.getAgente().getLocalName());
					} else if(msg.getPerformative() == ACLMessage.INFORM && msg.getContentObject() instanceof Incendio) {
						Incendio i = (Incendio) msg.getContentObject();
						incendios.add(i);
						Posicao incendio = i.getPos();
						mapa[incendio.getX()][incendio.getY()] = "I";
						Update();
					}
					else if(msg.getPerformative() == ACLMessage.CONFIRM) {
						String[] coordsIncendio = msg.getContent().split(";");
						int incendio=Integer.parseInt(coordsIncendio[0]);
						long duracao=Long.parseLong(coordsIncendio[1]);
						System.out.println("RECEBI QUE O INCENDIO TERMINOU" + incendio +"durou" + duracao);
						incendios.get(incendio).setTime(duracao);
						incendios.get(incendio).setExtinto(2);
						Incendio i = incendios.get(incendio);
						Posicao p = i.getPos();
						mapa[p.getX()][p.getY()] = "";
						Update();
					} else if(msg.getPerformative() == ACLMessage.REQUEST) {
						String[] incendio_info = msg.getContent().split(";");;
						int incendio = Integer.parseInt(incendio_info[0]);
						int agente_tipo = Integer.parseInt(incendio_info[1]);
						incendios.get(incendio).setExtinto(1);
						agentes[agente_tipo] = (int) agentes[agente_tipo] + 1;
						total_combate++;
						createSampleDataset();
					} 
					else block();
					fillGrid();
				}
			}catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}