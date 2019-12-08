package agents;
import Apoio.*;
import java.io.Serializable;

public class Camiao extends AgenteParticipativo implements Serializable{

	protected void setup() {
		super.setup();
		combustivel_max = 20;
		agua_max = 10;
		velocidade = 1;
		consumo = 0.05;
		combustivel_atual = combustivel_max;
		agua_atual = agua_max;
	}
}
