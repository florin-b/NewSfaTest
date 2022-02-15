package my.logon.screen.beans;

import java.util.List;

import my.logon.screen.model.UserInfo;

import my.logon.screen.enums.EnumTipClientIP;

public class BeanClient {

	private String numeClient;
	private String codClient;
	private String tipClient;
	private String agenti;
	private List<String> termenPlata;
	private String codCUI;
	private EnumTipClientIP tipClientIP = EnumTipClientIP.CONSTR;
	private String filialaClientIP = UserInfo.getInstance().getFiliala();
	private boolean clientBlocat;
	private String tipPlata;

	public BeanClient() {

	}

	public String getNumeClient() {
		return numeClient;
	}

	public void setNumeClient(String numeClient) {
		this.numeClient = numeClient;
	}

	public String getCodClient() {
		return codClient;
	}

	public void setCodClient(String codClient) {
		this.codClient = codClient;
	}

	public String getTipClient() {
		return tipClient;
	}

	public void setTipClient(String tipClient) {
		this.tipClient = tipClient;
	}

	@Override
	public String toString() {
		return numeClient;
	}

	public String getAgenti() {
		return agenti;
	}

	public void setAgenti(String agenti) {
		this.agenti = agenti;
	}

	public List<String> getTermenPlata() {
		return termenPlata;
	}

	public void setTermenPlata(List<String> termenPlata) {
		this.termenPlata = termenPlata;
	}

	public String getCodCUI() {
		return codCUI;
	}

	public void setCodCUI(String codCUI) {
		this.codCUI = codCUI;
	}

	public EnumTipClientIP getTipClientIP() {
		return tipClientIP;
	}

	public void setTipClientIP(EnumTipClientIP tipClientIP) {
		this.tipClientIP = tipClientIP;
	}

	public String getFilialaClientIP() {
		return filialaClientIP;
	}

	public void setFilialaClientIP(String filialaClientIP) {
		this.filialaClientIP = filialaClientIP;
	}

	public boolean isClientBlocat() {
		return clientBlocat;
	}

	public void setClientBlocat(boolean clientBlocat) {
		this.clientBlocat = clientBlocat;
	}

	public String getTipPlata() {
		return tipPlata;
	}

	public void setTipPlata(String tipPlata) {
		this.tipPlata = tipPlata;
	}

	
}
