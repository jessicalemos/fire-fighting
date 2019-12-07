package agents;

import java.util.List;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.awt.*;
import javax.swing.*;
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
	private List<Incendio> incendios;
	protected List<Posicao> local_agua;
	protected List<Posicao> local_combustivel;
	protected Map<AID,Object[]> agentes_mapa;
	private static int tamanho_mapa;
	private final DefaultCategoryDataset model = new DefaultCategoryDataset();
	private final DefaultCategoryDataset m = new DefaultCategoryDataset();
	private DefaultPieDataset dataset = new DefaultPieDataset();
	private static Object[] agentes;
	private int total_combate;
	private long tempo_minimo;
	private long tempo_maximo;
	private long tempo_medio;
	private JFrame f;
	private Work work;
	
	protected void setup() {
		super.setup();
		tamanho_mapa = 500;
		this.total_combate = 0;
		this.tempo_maximo = 0;
		this.tempo_minimo = 1000000;
		this.tempo_medio = 0;
		Object[] args = getArguments();
		Mapa val = (Mapa) args[0];
		local_agua = val.get_agua();
		local_combustivel = val.get_combustivel();
		this.incendios = new ArrayList<Incendio>();
		this.agentes_mapa = new HashMap<AID, Object[]>();
		agentes = new Object[10];
		JFrame frame = new JFrame(getClass().getSimpleName());
        work = new Work();
        JScrollPane scroll = new JScrollPane(work);
        frame.add(scroll);
        work.setPreferredSize(new Dimension(500*10,500*10));
        frame.pack();
	    frame.setVisible(true);
		f = new JFrame("Estatísticas");
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
            
        JFreeChart chart3 = ChartFactory.createBarChart("Tempo Medio", "", "Tempo", m, PlotOrientation.VERTICAL, false, false, false); 
		ChartPanel barPanel2 = new ChartPanel(chart3);
		barPanel2.setPreferredSize(new Dimension(600,400));
		chartPanel.setPreferredSize(new Dimension(600,400));
		barPanel.setPreferredSize(new Dimension(600,400));
		f.getContentPane().add(barPanel,BorderLayout.WEST); 
		f.getContentPane().add(chartPanel,BorderLayout.EAST);
		f.getContentPane().add(barPanel2,BorderLayout.CENTER);
		f.pack();
		f.setLocationRelativeTo(null); 
		f.setVisible(true);
		this.addBehaviour(new ReceberMensagens());
	}
	
	public class Work extends JPanel{
	   public void paintComponent(Graphics g){
	    	int x, y;
	    	super.paintComponent(g);
	        for (int row = 0; row < tamanho_mapa; row++) { 
	            for (int col = 0; col < tamanho_mapa; col++) { 
	                x = row * 10;
	                y = col * 10;
                	g.drawRect(x, y, 10, 10);
	            } 
	        }
	        for(Posicao pa : local_agua) {
				g.setColor(Color.BLUE);
				g.fillRect(pa.getX() * 10, pa.getY() * 10, 10, 10);
			}
			for(Posicao pc : local_combustivel) {
				g.setColor(Color.ORANGE);
				g.fillRect(pc.getX() * 10, pc.getY() * 10, 10, 10);
			}
			for(Incendio i : incendios) {
				if(i.getExtinto()!=2) {
					g.setColor(Color.RED);
					g.fillRect(i.getPos().getX() * 10, i.getPos().getY() * 10, 10, 10);
				} else {
					g.setColor(Color.BLACK);
					g.drawRect(i.getPos().getX() * 10, i.getPos().getY() * 10, 10, 10);
				}
			}
			for(Object[] o : agentes_mapa.values()) {
				Posicao pos_atual = (Posicao) o[1];
				Posicao pos_ant = (Posicao) o[0];
				g.setColor(Color.GREEN);
				g.fillRect(pos_atual.getX() * 10, pos_atual.getY() * 10, 10, 10);
				if(pos_ant != null) {
					g.setColor(Color.BLACK);
					g.drawRect(pos_ant.getX() * 10, pos_ant.getY() * 10, 10, 10);
				}
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
	
	public void TimeUpdate(long duracao) {
		String ROW_KEY = "Values"; 
		if(duracao<tempo_minimo) tempo_minimo = duracao;
		if (duracao>tempo_maximo) tempo_maximo = duracao;
		List<Incendio> incendios_extintos = incendios
				  .stream()
				  .filter(c -> c.getExtinto()==2)
				  .collect(Collectors.toList());
		int size = incendios_extintos.size() - 1;
		long total =  (tempo_medio * size) + duracao;
		size++;
		tempo_medio = total/size;
		m.setValue(tempo_minimo, ROW_KEY, "Mínimo"); 
		m.setValue(tempo_medio, ROW_KEY, "Médio"); 
		m.setValue(tempo_maximo, ROW_KEY, "Máximo"); 
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
						Object[] pos;
						if (!agentes_mapa.containsKey(a.getAgente())) {
							pos = new Object[3];
							pos[0] = null;
							pos[1] = new Posicao(pos_x,pos_y);
						} else {
							pos = agentes_mapa.get(a.getAgente());
							pos[0] = pos[1];
							pos[1] = new Posicao(pos_x,pos_y);
						}

						agentes_mapa.put(a.getAgente(),pos);
						if (a.getTipo() == 1) {
							agentes[1] = 0;
						}
						else if (a.getTipo() == 2) {
							agentes[2] = 0;
						}
						else if (a.getTipo() == 3) {
							agentes[3] = 0;
						}
						System.out.println("Agente INTERFACE a receber "+pos_x+" "+pos_y+" do "+a.getAgente().getLocalName());
					} else if(msg.getPerformative() == ACLMessage.INFORM && msg.getContentObject() instanceof Incendio) {
						Incendio i = (Incendio) msg.getContentObject();
						incendios.add(i);
						work.updateUI();
						Update();
					}
					else if(msg.getPerformative() == ACLMessage.CONFIRM) {
						String[] coordsIncendio = msg.getContent().split(";");
						int incendio=Integer.parseInt(coordsIncendio[0]);
						long duracao=Long.parseLong(coordsIncendio[1]);
						System.out.println("RECEBI QUE O INCENDIO TERMINOU" + incendio +"durou" + duracao);
						incendios.get(incendio).setTime(duracao);
						incendios.get(incendio).setExtinto(2);
						Update();
						TimeUpdate(duracao);
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
				}
			}catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}