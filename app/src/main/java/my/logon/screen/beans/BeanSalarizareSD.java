package my.logon.screen.beans;

import java.util.List;

import my.logon.screen.beans.BeanSalarizareAgent;
import my.logon.screen.beans.SalarizareDetaliiCVS;

public class BeanSalarizareSD extends BeanSalarizareAgent {

	private List<SalarizareDetaliiCVS> detaliiCvs;

	public List<SalarizareDetaliiCVS> getDetaliiCvs() {
		return detaliiCvs;
	}

	public void setDetaliiCvs(List<SalarizareDetaliiCVS> detaliiCvs) {
		this.detaliiCvs = detaliiCvs;
	}

}
