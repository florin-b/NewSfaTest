package my.logon.screen.beans;

import java.util.List;

import my.logon.screen.beans.AntetComandaCLP;

public class ComandaCreataCLP {
	private AntetComandaCLP antetComandaCLP;
	private List<ArticolCLP> listaArticoleComanda;

	public AntetComandaCLP getAntetComandaCLP() {
		return antetComandaCLP;
	}

	public void setAntetComandaCLP(AntetComandaCLP antetComandaCLP) {
		this.antetComandaCLP = antetComandaCLP;
	}

	public List<ArticolCLP> getListaArticoleComanda() {
		return listaArticoleComanda;
	}

	public void setListaArticoleComanda(List<ArticolCLP> listaArticoleComanda) {
		this.listaArticoleComanda = listaArticoleComanda;
	}

}
