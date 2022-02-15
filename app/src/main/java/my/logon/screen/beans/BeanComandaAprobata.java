package my.logon.screen.beans;

import my.logon.screen.beans.BeanComandaCreata;

public class BeanComandaAprobata extends BeanComandaCreata {

	private String tipPersoanaClient;
	private String obsLivrare;

	public String getTipPersoanaClient() {
		return tipPersoanaClient;
	}

	public void setTipPersoanaClient(String tipPersoanaClient) {
		this.tipPersoanaClient = tipPersoanaClient;
	}

	public String getObsLivrare() {
		return obsLivrare;
	}

	public void setObsLivrare(String obsLivrare) {
		this.obsLivrare = obsLivrare;
	}

}
