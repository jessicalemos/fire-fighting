package agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.io.Serializable;

public class Mapa implements Serializable {
	private List<Posicao> local_agua;
	private List<Posicao> local_combustivel;
	private Map<String, List<Posicao>> agente_zonas;
	
	public Mapa() {
		this.local_agua = new ArrayList<>();
		this.local_combustivel = new ArrayList<>();
		Random randomizer = new Random();
		int x_rand, y_rand;
		for (int y=0; y<500; y+=125) {
			for(int x=0; x<500; x+=125) {
				for(int w=0; w<2; w++) {
					x_rand = x + randomizer.nextInt(((x+125) - x) + 1);
					y_rand = y + randomizer.nextInt(((y+125) - y) + 1);
					local_agua.add(new Posicao(x_rand,y_rand));
					x_rand = x + randomizer.nextInt(((x+125) - x) + 1);
					y_rand = y + randomizer.nextInt(((y+125) - y) + 1);
					local_combustivel.add(new Posicao(x_rand,y_rand));
				}
			}
		}
		this.agente_zonas = new HashMap<String, List<Posicao>>()
		{
		    {
		    	
		    	List<Posicao> zona1, zona2, zona3, zona4, centro;
		    	zona1 = new ArrayList<Posicao>();
		    	zona2 = new ArrayList<Posicao>();
		    	zona3 = new ArrayList<Posicao>();
		    	zona4 = new ArrayList<Posicao>();
		    	centro = new ArrayList<Posicao>();
		    	Posicao p1, p2, p3, p4, p5, p6, p7, p8, p9;
		    	p1 = new Posicao(0,0);
		    	p2 = new Posicao(250,0);
		    	p3 = new Posicao(500,0);
		    	p4 = new Posicao(0,250);
		    	p5 = new Posicao(250,250);
		    	p6 = new Posicao(500,250);
		    	p7 = new Posicao(0,500);
		    	p8 = new Posicao(250,500);
		    	p9 = new Posicao(500,500);
		    	//zona1 -> p4,p5,p7,p8
		    	zona1.add(p4);
		    	zona1.add(p5);
		    	zona1.add(p7);
		    	zona1.add(p8);
		    	//zona2 -> p5,p6,p8,p9
		    	zona2.add(p5);
		    	zona2.add(p6);
		    	zona2.add(p8);
		    	zona2.add(p9);
		    	//zona3 -> p1,p2,p4,p5
		    	zona3.add(p1);
		    	zona3.add(p2);
		    	zona3.add(p4);
		    	zona3.add(p5);
		    	//zona4 -> p2,p3,p5,p6
		    	zona4.add(p2);
		    	zona4.add(p3);
		    	zona4.add(p5);
		    	zona4.add(p6);
		    	//centro -> p5
		    	centro.add(p5);
		    	String agent = "AgenteParticipativo ";
		    	//drones
		    	for(int i = 0; i<2; i++) {
		    		put(agent + i, zona1);
		    		put(agent + (i+2), zona2);
		    		put(agent + (i+4), zona3);
		    		put(agent + (i+6), zona4);
		    		put(agent + (i+8), centro);
		    	}
		    	//aeronaves
		    	put(agent + 12, zona1);
		    	put(agent + 13, zona2);
		    	put(agent + 14, zona3);
		    	put(agent + 15, zona4);
		    	put(agent + 16, centro);
		    	//camioes
		    	put(agent + 10, zona1);
		    	put(agent + 11, zona4);
		    }
		};
	}

	public List<Posicao> get_agua() {
		return local_agua;
	}

	public List<Posicao> get_combustivel() {
		return local_combustivel;
	}
	
	public Map<String, List<Posicao>> get_agente_zonas(){
		return agente_zonas;
	}
}
