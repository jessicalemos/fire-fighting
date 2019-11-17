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
		for(int i=0; i<5; i++) {
			Random randomizer = new Random();
			local_agua.add(new Posicao(randomizer.nextInt(500),randomizer.nextInt(500)));
			local_combustivel.add(new Posicao(randomizer.nextInt(500),randomizer.nextInt(500)));
		}
	}

	public List<Posicao> get_agua() {
		return local_agua;
	}

	public List<Posicao> get_combustivel() {
		return local_combustivel;
	}
}
