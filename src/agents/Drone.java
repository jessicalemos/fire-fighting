package agents;

import java.io.Serializable;

public class Drone extends AgenteParticipativo implements Serializable{
	protected void setup() {
		super.setup();
		combustivel=5;
		agua=2;
		velocidade = 4;
	}
}
