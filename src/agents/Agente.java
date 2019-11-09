package agents;

import java.io.Serializable;

public class Agente implements Serializable {
private int combustivel;
private int agua;
private int pos_x;
private int pos_y;
private int tipo;
	public Agente() {
		int combustivel=0;
		int agua=0;
		int pos_x=0;
		int pos_y=0;
		int tipo=0;
	}
	public Agente(int combustivel,int agua,int pos_x,int pos_y,int tipo) {
		super();
		this.combustivel=combustivel;
		this.agua=agua;
		this.pos_x=pos_x;
		this.pos_y=pos_y;
		this.tipo=tipo;
	}
	public int getTipo() {
		return tipo;
	}
	public void setTipo(int tipo) {
		this.tipo = tipo;
	}
	public int getCombustivel() {
		return combustivel;
	}
	public void setCombustivel(int combustivel) {
		this.combustivel = combustivel;
	}
	public int getAgua() {
		return agua;
	}
	public void setAgua(int agua) {
		this.agua = agua;
	}
	public int getPos_x() {
		return pos_x;
	}
	public void setPos_x(int pos_x) {
		this.pos_x = pos_x;
	}
	public int getPos_y() {
		return pos_y;
	}
	public void setPos_y(int pos_y) {
		this.pos_y = pos_y;
	}
}
