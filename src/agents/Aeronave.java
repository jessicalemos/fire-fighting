package agents;

import java.io.Serializable;

public class Aeronave extends AgenteParticipativo implements Serializable{

	protected void setup() {
		super.setup();
		combustivel=10;
		agua=5;
		velocidade = 2;
	}
}
