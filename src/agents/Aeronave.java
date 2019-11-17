package agents;

import java.io.Serializable;

public class Aeronave extends AgenteParticipativo implements Serializable{

	protected void setup() {
		super.setup();
		combustivel_max = 10;
		agua_max = 5;
		velocidade = 2;
		consumo = 0.08;
		combustivel_atual = combustivel_max;
		agua_atual = agua_max;
	}
}
