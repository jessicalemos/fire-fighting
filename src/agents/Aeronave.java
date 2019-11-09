package agents;

import java.io.Serializable;

public class Aeronave extends AgenteParticipativo implements Serializable{
	private int velocidade;
	private int agua;
	private int combustivel;

	protected void setup() {
		super.setup();
		combustivel=10;
		agua=5;
		velocidade = 2;
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
