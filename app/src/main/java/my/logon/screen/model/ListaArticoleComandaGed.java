package my.logon.screen.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;

import my.logon.screen.beans.BeanConditiiArticole;
import my.logon.screen.beans.BeanParametruPretGed;
import my.logon.screen.beans.ComandaExtraMathaus;
import my.logon.screen.beans.PretArticolGed;
import my.logon.screen.beans.StocMathaus;
import my.logon.screen.enums.EnumArticoleDAO;
import my.logon.screen.listeners.OperatiiArticolListener;
import my.logon.screen.screens.CreareComandaGed;
import my.logon.screen.screens.SelectArtCmdGed;

public class ListaArticoleComandaGed extends Observable implements OperatiiArticolListener {

	private int articolIndex = -1;
	private static ListaArticoleComandaGed instance = new ListaArticoleComandaGed();
	private ArrayList<ArticolComanda> listArticoleComanda = new ArrayList<ArticolComanda>();
	private List<BeanConditiiArticole> conditiiComandaArticole;
	private double valoareNegociata;

	private ArticolComandaGed articolMathaus;
	private OperatiiArticol opArticol;
	private ArticolComandaGed articolComandaGed;
	private ComandaExtraMathaus comandaExtraMathaus;

	private ListaArticoleComandaGed() {

	}

	public static ListaArticoleComandaGed getInstance() {
		return instance;
	}

	public void setListaArticole(ArrayList<ArticolComanda> listaArticole) {
		this.listArticoleComanda = listaArticole;
	}

	public void addArticoleMathaus(ArticolComandaGed articolComanda, ComandaExtraMathaus comandaMathaus, Context context) {

		articolComandaGed = articolComanda;
		comandaExtraMathaus = comandaMathaus;
		updateDatePret(articolComanda, comandaMathaus, context);

		for (StocMathaus stoc : comandaMathaus.getListArticole()) {

			articolMathaus = new ArticolComandaGed();

			articolMathaus.setNumeArticol(articolComanda.getNumeArticol());
			articolMathaus.setCodArticol(articolComanda.getCodArticol());
			articolMathaus.setCantitate(stoc.getCantitate());
			articolMathaus.setPretUnitGed(articolComanda.getPretUnitGed());
			articolMathaus.setUm(articolComanda.getUm());
			articolMathaus.setDepozit(articolComanda.getDepozit());
			articolMathaus.setPretUnitarClient(articolComanda.getPretUnitarClient());
			articolMathaus.setPretUnit(articolComanda.getPretUnit());
			articolMathaus.setTipAlert(articolComanda.getTipAlert());
			articolMathaus.setPromotie(articolComanda.getPromotie());
			articolMathaus.setPonderare(articolComanda.getPonderare());
			articolMathaus.setProcent(articolComanda.getProcent());
			articolMathaus.setDiscClient(articolComanda.getDiscClient());
			articolMathaus.setProcAprob(articolComanda.getProcAprob());
			articolMathaus.setMultiplu(articolComanda.getMultiplu());
			articolMathaus.setPret(articolComanda.getPret());
			articolMathaus.setInfoArticol(articolComanda.getInfoArticol());
			articolMathaus.setUmb(articolComanda.getUmb());
			articolMathaus.setCantUmb(stoc.getCantitate());
			articolMathaus.setAlteValori(articolComanda.getAlteValori());
			articolMathaus.setDepart(articolComanda.getDepart());
			articolMathaus.setDepartSintetic(articolComanda.getDepartSintetic());
			articolMathaus.setCmp(articolComanda.getCmp());
			articolMathaus.setCoefCorectie(articolComanda.getCoefCorectie());
			articolMathaus.setPretMediu(articolComanda.getPretMediu());
			articolMathaus.setAdaosMediu(articolComanda.getAdaosMediu());
			articolMathaus.setTipArt(articolComanda.getTipArt());
			articolMathaus.setValTransport(articolComanda.getValTransport());
			articolMathaus.setProcTransport(articolComanda.getProcTransport());
			articolMathaus.setDiscountAg(articolComanda.getDiscountAg());
			articolMathaus.setDiscountSd(articolComanda.getDiscountSd());
			articolMathaus.setUmPalet(articolComanda.isUmPalet());
			articolMathaus.setFilialaSite(stoc.getUl());
			articolMathaus.setLungime(articolComanda.getLungime());
			articolMathaus.setIstoricPret(articolComanda.getIstoricPret());
			articolMathaus.setDataExpPret(articolComanda.getDataExpPret());


		}

	}

	private void updateDatePret(ArticolComandaGed articol, ComandaExtraMathaus comandaMathaus, Context context) {

		String uLog = UserInfo.getInstance().getUnitLog().substring(0, 2) + "2" + UserInfo.getInstance().getUnitLog().substring(3, 4);
		String tipUser = UserInfo.getInstance().getTipUser();
		String cantitati = "";

		for (StocMathaus stoc : comandaMathaus.getListArticole()) {
			if (cantitati.isEmpty())
				cantitati = String.valueOf(stoc.getCantitate());
			else
				cantitati += "#" + String.valueOf(stoc.getCantitate());
		}

		if (isWood()) {
			uLog = UserInfo.getInstance().getUnitLog().substring(0, 2) + "4" + UserInfo.getInstance().getUnitLog().substring(3, 4);
			tipUser = "CV";
		}

		opArticol = OperatiiArticolFactory.createObject("OperatiiArticolImpl", context);
		opArticol.setListener(this);

		HashMap<String, String> params = new HashMap<String, String>();

		BeanParametruPretGed paramPret = new BeanParametruPretGed();
		paramPret.setClient(SelectArtCmdGed.codClientVar);
		paramPret.setArticol(articol.getCodArticol());
		paramPret.setCantitate(String.valueOf(articol.getCantitate()));
		paramPret.setDepart(articol.getDepart());
		paramPret.setUm(articol.getUm());
		paramPret.setUl(uLog);
		paramPret.setDepoz(" ");
		paramPret.setCodUser(UserInfo.getInstance().getCod());
		paramPret.setCanalDistrib("20");
		paramPret.setTipUser(tipUser);
		paramPret.setMetodaPlata(DateLivrare.getInstance().getTipPlata());
		paramPret.setTermenPlata(DateLivrare.getInstance().getTermenPlata());
		paramPret.setCodJudet(getCodJudetPret());
		paramPret.setLocalitate(getLocalitatePret());
		paramPret.setFilialaAlternativa(CreareComandaGed.filialaAlternativa);
		paramPret.setCodClientParavan(CreareComandaGed.codClientParavan);

		params.put("parametruPret", opArticol.serializeParamPretGed(paramPret));
		params.put("cantitati", cantitati);

		opArticol.getInfoPretMathaus(params);

	}

	private void updateInfoArticol(String result) {

		String[] objArticole = result.split("#");

		for (String infoArticol : objArticole) {

			PretArticolGed pretArticol = opArticol.deserializePretGed(infoArticol);

			for (StocMathaus stoc : comandaExtraMathaus.getListArticole()) {

				if (Double.valueOf(pretArticol.getCantitate()) == stoc.getCantitate()) {

					articolMathaus = new ArticolComandaGed();

					articolMathaus.setNumeArticol(articolComandaGed.getNumeArticol());
					articolMathaus.setCodArticol(articolComandaGed.getCodArticol());
					articolMathaus.setCantitate(Double.parseDouble(pretArticol.getCantitate()));
					articolMathaus.setPretUnitGed(articolComandaGed.getPretUnitGed());
					articolMathaus.setUm(articolComandaGed.getUm());
					articolMathaus.setDepozit(articolComandaGed.getDepozit());
					articolMathaus.setPretUnitarClient(articolComandaGed.getPretUnitarClient());
					articolMathaus.setPretUnit(articolComandaGed.getPretUnit());
					articolMathaus.setTipAlert(articolComandaGed.getTipAlert());
					articolMathaus.setPromotie(articolComandaGed.getPromotie());
					articolMathaus.setPonderare(articolComandaGed.getPonderare());
					articolMathaus.setProcent(articolComandaGed.getProcent());
					articolMathaus.setDiscClient(articolComandaGed.getDiscClient());
					articolMathaus.setProcAprob(articolComandaGed.getProcAprob());
					articolMathaus.setMultiplu(articolComandaGed.getMultiplu());
					articolMathaus.setPret(articolComandaGed.getPret());
					articolMathaus.setInfoArticol(pretArticol.getConditiiPret().replace(',', '.'));
					articolMathaus.setUmb(articolComandaGed.getUmb());
					articolMathaus.setCantUmb(Double.parseDouble(pretArticol.getCantitate()));
					articolMathaus.setAlteValori(articolComandaGed.getAlteValori());
					articolMathaus.setDepart(articolComandaGed.getDepart());
					articolMathaus.setDepartSintetic(articolComandaGed.getDepartSintetic());
					articolMathaus.setCmp(articolComandaGed.getCmp());
					articolMathaus.setCoefCorectie(articolComandaGed.getCoefCorectie());
					articolMathaus.setPretMediu(articolComandaGed.getPretMediu());
					articolMathaus.setAdaosMediu(articolComandaGed.getAdaosMediu());
					articolMathaus.setTipArt(articolComandaGed.getTipArt());
					articolMathaus.setValTransport(articolComandaGed.getValTransport());
					articolMathaus.setProcTransport(articolComandaGed.getProcTransport());
					articolMathaus.setDiscountAg(articolComandaGed.getDiscountAg());
					articolMathaus.setDiscountSd(articolComandaGed.getDiscountSd());
					articolMathaus.setUmPalet(articolComandaGed.isUmPalet());
					articolMathaus.setFilialaSite(stoc.getUl());
					articolMathaus.setLungime(articolComandaGed.getLungime());
					articolMathaus.setIstoricPret(articolComandaGed.getIstoricPret());
					articolMathaus.setDataExpPret(articolComandaGed.getDataExpPret());
					
					addArticolComanda(articolMathaus);

				}

			}

		}


	}

	boolean isWood() {
		return UserInfo.getInstance().getTipUser().equals("WOOD");
	}

	private String getCodJudetPret() {

		if (DateLivrare.getInstance().isAltaAdresa()) {
			return DateLivrare.getInstance().getCodJudetD();
		} else {
			return DateLivrare.getInstance().getCodJudet();
		}

	}

	private String getLocalitatePret() {

		if (DateLivrare.getInstance().isAltaAdresa()) {
			return DateLivrare.getInstance().getOrasD();
		} else {
			return DateLivrare.getInstance().getOras();
		}

	}

	public void addArticolComanda(ArticolComanda articolComanda) {
		if (!articolExists(articolComanda))
			listArticoleComanda.add(articolComanda);
		else {
			listArticoleComanda.remove(articolIndex);
			listArticoleComanda.add(articolIndex, articolComanda);
			removeConditiiArticol(articolComanda);
		}

		triggerObservers();

	}

	private boolean articolExists(ArticolComanda articolComanda) {
		Iterator<ArticolComanda> iterator = listArticoleComanda.iterator();

		int pos = 0;
		articolIndex = -1;
		while (iterator.hasNext()) {

			if (isSameArticol(iterator.next(), articolComanda)) {
				articolIndex = pos;
				return true;
			}
			pos++;
		}

		return false;
	}

	// pentru total negociat
	public void calculProcentReducere() {

		double totalComanda = getTotalNegociatComanda();

		double procentGlobal = 0;

		if (valoareNegociata <= totalComanda) {
			procentGlobal = (1 - valoareNegociata / totalComanda) * 100;
		} else
			procentGlobal = 0;

		if (procentGlobal > 0) {

			double procentLocal = 0;

			String tipAlert = "";
			ArticolComanda articol = null;
			Iterator<ArticolComanda> iterator = listArticoleComanda.iterator();
			while (iterator.hasNext()) {
				articol = iterator.next();

				articol.setPretUnitarClient(articol.getPretUnitarClient() * (1 - procentGlobal / 100));
				articol.setPretUnit(articol.getPretUnitarClient());
				articol.setPret(articol.getPretUnitarClient() * articol.getCantUmb());

				procentLocal = (1 - articol.getPretUnitarClient() / articol.getPretUnitarGed()) * 100;

				if (procentLocal > articol.getDiscountAg()) {
					tipAlert = "SD";
				}
				if (procentLocal > articol.getDiscountSd()) {
					tipAlert += ";DV";
				}

				articol.setProcent(procentLocal);
				articol.setProcAprob(procentLocal);

				articol.setValTransport((articol.getPretUnitarClient() * articol.getCantUmb()) * (articol.getProcTransport() / 100));
				articol.setTipAlert(tipAlert);
				articol.setPonderare(0);

			}
		}

	}

	private boolean isSameArticol(ArticolComanda articol1, ArticolComanda articol2) {
		return articol1.getCodArticol().equals(articol2.getCodArticol()) && articol1.getDepozit().equals(articol2.getDepozit())
				&& articol1.getFilialaSite().equals(articol2.getFilialaSite());
	}

	public void removeArticolComanda(String codArticol) {

		Iterator<ArticolComanda> iterator = listArticoleComanda.iterator();

		while (iterator.hasNext()) {
			if (iterator.next().getCodArticol().equals(codArticol)) {
				iterator.remove();
				break;
			}
		}

		triggerObservers();

	}

	private void removeConditiiArticol(ArticolComanda articolComanda) {
		if (conditiiComandaArticole != null) {
			Iterator<BeanConditiiArticole> iterator = conditiiComandaArticole.iterator();

			while (iterator.hasNext()) {
				if (iterator.next().getCod().equals(articolComanda.getCodArticol())) {
					iterator.remove();
					break;
				}

			}

		}
	}

	public void removeArticolComanda(int articolIndex) {
		listArticoleComanda.remove(articolIndex);
		triggerObservers();

	}

	public ArrayList<ArticolComanda> getListArticoleComanda() {
		return listArticoleComanda;
	}

	public ArticolComanda getArticolComanda(int position) {
		return listArticoleComanda.get(position);
	}

	public double getTotalNegociatComanda() {

		double totalComanda = 0;
		Iterator<ArticolComanda> iterator = listArticoleComanda.iterator();
		ArticolComanda articol = null;
		while (iterator.hasNext()) {
			articol = iterator.next();
			totalComanda += articol.getPretUnitarClient() * articol.getCantUmb();
		}

		return totalComanda;

	}

	public double getTotalComanda() {

		double totalComanda = 0;
		Iterator<ArticolComanda> iterator = listArticoleComanda.iterator();

		while (iterator.hasNext()) {
			totalComanda += iterator.next().getPret();
		}

		return totalComanda;

	}

	public int getNrArticoleComanda() {
		return listArticoleComanda.size();
	}

	public void setConditiiArticole(List<BeanConditiiArticole> conditiiComandaArticole) {
		this.conditiiComandaArticole = conditiiComandaArticole;
	}

	public List<BeanConditiiArticole> getConditiiArticole() {
		return this.conditiiComandaArticole;
	}

	public double getValoareNegociata() {
		return valoareNegociata;
	}

	public void setValoareNegociata(double valoareNegociata) {
		this.valoareNegociata = valoareNegociata;
	}

	public void clearArticoleComanda() {
		if (listArticoleComanda != null)
			listArticoleComanda.clear();

		if (conditiiComandaArticole != null)
			conditiiComandaArticole.clear();

		valoareNegociata = 0;
	}

	private void triggerObservers() {

		setChanged();
		notifyObservers(listArticoleComanda);
	}

	@Override
	public void operationComplete(EnumArticoleDAO methodName, Object result) {
		switch (methodName) {
		case GET_INFOPRET_MATHAUS:
			updateInfoArticol((String) result);
			break;
		default:
			break;
		}
	}

}
