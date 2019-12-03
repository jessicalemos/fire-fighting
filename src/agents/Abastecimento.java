package agents;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.io.Serializable;

public class Abastecimento implements Serializable {
	private List<Posicao> local_agua;
	private List<Posicao> local_combustivel;

	public Abastecimento() {
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
	}

	public List<Posicao> get_agua() {
		return local_agua;
	}

	public List<Posicao> get_combustivel() {
		return local_combustivel;
	}
}
