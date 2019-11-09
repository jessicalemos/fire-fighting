package agents;

import java.io.Serializable;

public class Camiao extends AgenteParticipativo implements Serializable{

	protected void setup() {
		super.setup();
		combustivel=20;
		agua=10;
		velocidade= 1;
		tipo=1;
	}
}
