package agents;

import java.io.Serializable;

public class Camiao extends AgenteParticipativo implements Serializable{
	private int velocidade;
	private int agua;
	private int combustivel;

	protected void setup() {
		super.setup();
		combustivel=20;
		agua=10;
		velocidade = 1;
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
