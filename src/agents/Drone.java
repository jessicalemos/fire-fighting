package agents;

import java.io.Serializable;

public class Drone extends AgenteParticipativo implements Serializable{
	protected void setup() {
		super.setup();
		combustivel_max = 5;
		agua_max = 2;
		velocidade = 4;
		consumo = 0.02;
		combustivel_atual = combustivel_max;
		agua_atual = agua_max;
	}
}
