package my.logon.screen.beans;

import java.util.List;

import my.logon.screen.beans.ArticolCalculDesc;

public class CalculDescarcare {
	private String filiala;
	private List<ArticolCalculDesc> listArticole;

	public String getFiliala() {
		return filiala;
	}

	public void setFiliala(String filiala) {
		this.filiala = filiala;
	}

	public List<ArticolCalculDesc> getListArticole() {
		return listArticole;
	}

	public void setListArticole(List<ArticolCalculDesc> listArticole) {
		this.listArticole = listArticole;
	}

}
