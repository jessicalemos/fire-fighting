package agents;

import java.io.Serializable;

public class Drone extends AgenteParticipativo implements Serializable{
	private int velocidade;
	private int agua;
	private int combustivel;

	protected void setup() {
		super.setup();
		combustivel=5;
		agua=2;
		velocidade = 4;
	}
	
	public int getVelocidade() {
		return velocidade;
	}

	public int getAgua() {
		return agua;
	}

	public int getCombustivel() {
		return combustivel;
	}
}
