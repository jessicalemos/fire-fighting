package agents;

import java.io.Serializable;

import jade.core.AID;

public class Agente implements Serializable {
	private AID agente;
	private double combustivel;
	private int agua;
	private Posicao pos;
	private boolean disponibilidade;
	private int velocidade;
	private double consumo;
	private int tipo; //1-drone, 2-aeronave, 3-camiao

	public Agente() {
		this.agente = null;
		this.combustivel=0;
		this.agua=0;
		this.pos = new Posicao(0,0);
		this.velocidade = -1;
		this.consumo = -1;
		this.disponibilidade = true;
		this.tipo = 0;		
	}
	
	public Agente(AID agente, double combustivel, int agua, int pos_x, int pos_y, boolean disp, int vel, double consumo, int tipo) {
		super();
		this.agente = agente;
		this.combustivel=combustivel;
		this.agua=agua;
		this.pos = new Posicao(pos_x,pos_y);
		this.disponibilidade = disp;
		this.velocidade = vel;
		this.consumo = consumo;
		this.tipo = tipo;
	}
	
	public Agente(double combustivel,int agua,int pos_x,int pos_y) {
		super();
		this.combustivel=combustivel;
		this.agua=agua;
		this.pos = new Posicao(pos_x,pos_y);
	}
	
	public AID getAgente() {
		return agente;
	}

	public void setAgente(AID agente) {
		this.agente = agente;
	}

	public double getConsumo() {
		return consumo;
	}

	public void setConsumo(double consumo) {
		this.consumo = consumo;
	}

	public double getCombustivel() {
		return combustivel;
	}

	public void setCombustivel(double combustivel) {
		this.combustivel = combustivel;
	}

	public int getAgua() {
		return agua;
	}

	public boolean isDisponibilidade() {
		return disponibilidade;
	}

	public void setDisponibilidade(boolean disponibilidade) {
		this.disponibilidade = disponibilidade;
	}

	public int getVelocidade() {
		return velocidade;
	}

	public void setVelocidade(int velocidade) {
		this.velocidade = velocidade;
	}

	public int getTipo() {
		return tipo;
	}

	public void setTipo(int tipo) {
		this.tipo = tipo;
	}

	public void setAgua(int agua) {
		this.agua = agua;
	}

	public Posicao getPos() {
		return pos;
	}

	public int getPos_x() {return pos.getX();}
	public int getPos_y() {return pos.getY();}

	public void setPos(Posicao post) {
		this.pos.setX(pos.getX());
		this.pos.setY(post.getY());
	}

}
