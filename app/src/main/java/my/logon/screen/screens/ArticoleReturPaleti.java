package my.logon.screen.screens;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;


import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import my.logon.screen.R;
import my.logon.screen.adapters.ArticoleReturAdapter;
import my.logon.screen.beans.Address;
import my.logon.screen.beans.BeanArticolRetur;
import my.logon.screen.beans.BeanComandaRetur;
import my.logon.screen.enums.EnumRetur;
import my.logon.screen.enums.EnumTipClientIP;
import my.logon.screen.enums.EnumTipComanda;
import my.logon.screen.enums.EnumTipRetur;
import my.logon.screen.listeners.ListaArtReturListener;
import my.logon.screen.listeners.OperatiiReturListener;
import my.logon.screen.model.OperatiiReturMarfa;
import my.logon.screen.model.UserInfo;
import my.logon.screen.utils.MapUtils;
import my.logon.screen.utils.UtilsGeneral;
import my.logon.screen.utils.UtilsUser;

public class ArticoleReturPaleti extends Fragment implements ListaArtReturListener, OperatiiReturListener {

	TextView textDocument;
	ListView listArticoleRetur;
	TextView textNumeArticol, textCodArticol, textCantArticol, textUmArticol;
	Button saveArtRetur, cancelArtRetur, saveReturBtn;
	EditText textReturArticol;
	List<BeanArticolRetur> listArticole;
	BeanArticolRetur selectedArticol;
	LinearLayout layoutRetur;

	String[] tipTransport = { "Selectati tip transport", "TRAP - Transport Arabesque", "TCLI - Transport client", "TERT - Transport curier" };
	ProgressBar saveReturProgress;
	private int progressVal = 0;
	private Timer myTimer;
	private Handler myHandler = new Handler();
	private String nrDocument, codClient, numeClient;
	private OperatiiReturMarfa opRetur;
	TextView selectIcon;
	private static final double TAXA_RETUR_PALET = 8.4;
	private static final String codArtTranspPalet = "99999999";

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.articole_retur_marfa, container, false);

		opRetur = new OperatiiReturMarfa(getActivity());
		opRetur.setOperatiiReturListener(this);

		layoutRetur = (LinearLayout) v.findViewById(R.id.layoutRetur);
		layoutRetur.setVisibility(View.GONE);

		textDocument = (TextView) v.findViewById(R.id.textDocument);
		listArticoleRetur = (ListView) v.findViewById(R.id.listArticoleRetur);
		addListenerListArticole();

		textNumeArticol = (TextView) v.findViewById(R.id.textNumeArticol);
		textCodArticol = (TextView) v.findViewById(R.id.textCodArticol);
		textCantArticol = (TextView) v.findViewById(R.id.textCantArticol);
		textUmArticol = (TextView) v.findViewById(R.id.textUmArticol);

		saveArtRetur = (Button) v.findViewById(R.id.saveArtRetur);
		addListenerSaveArt();

		cancelArtRetur = (Button) v.findViewById(R.id.cancelArtRetur);
		addListenerCancelArt();

		textReturArticol = (EditText) v.findViewById(R.id.textReturArticol);

		saveReturBtn = (Button) v.findViewById(R.id.saveReturBtn);
		saveReturBtn.setVisibility(View.INVISIBLE);
		addListenerSaveReturBtn();

		selectIcon = (TextView) v.findViewById(R.id.selectIcon);
		selectIcon.setVisibility(View.INVISIBLE);

		saveReturProgress = (ProgressBar) v.findViewById(R.id.progress_bar_retur);
		saveReturProgress.setVisibility(View.INVISIBLE);

		return v;
	}

	public static ArticoleReturPaleti newInstance() {
		ArticoleReturPaleti frg = new ArticoleReturPaleti();
		Bundle bdl = new Bundle();
		frg.setArguments(bdl);
		return frg;
	}

	private void addListenerSaveArt() {
		saveArtRetur.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (isCantValid()) {
					selectedArticol.setCantitateRetur(Double.valueOf(textReturArticol.getText().toString()));
					updateListArticole(selectedArticol);

					showPanel("selectItem");
				}
			}
		});
	}

	private boolean isCondTranspPaleti() {

		if (isTipTranspPretPaleti() && DateLivrareReturPaleti.tipClientIP != null && DateLivrareReturPaleti.tipClientIP == EnumTipClientIP.NONCONSTR)
			return true;
		else
			return isTipTranspPretPaleti() && (UtilsUser.isCV() || DateLivrareReturPaleti.tipCmdRetur == EnumTipComanda.GED);

	}

	private boolean isValReturPaleti() {

		double valTaxaRetur = getNrPaletiRetur() * TAXA_RETUR_PALET;
		double valPaletiRetur = getValoarePaletiRetur();

		if (valTaxaRetur > valPaletiRetur) {
			showInfoTaxaDialog(valTaxaRetur, valPaletiRetur);
			return false;
		}

		return true;

	}

	public void showInfoTaxaDialog(double valTaxa, double valPaleti) {

		NumberFormat nf = new DecimalFormat("#0.00");

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(
				"\nValoarea taxei de retur (" + nf.format(valTaxa) + " lei) este mai mare decat valoarea paletilor returnati (" + nf.format(valPaleti)
						+ " lei). \nAceasta comanda nu se poate salva.\n").setCancelable(false)
				.setPositiveButton("Inchide", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();

					}
				});
		AlertDialog alert = builder.create();
		alert.setCancelable(false);
		alert.show();

	}

	private boolean isTipTranspPretPaleti() {
		return DateLivrareReturPaleti.tipTransport.equals("TRAP");
	}

	private void addListenerCancelArt() {
		cancelArtRetur.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showPanel("selectItem");
			}
		});
	}

	private void addListenerSaveReturBtn() {
		saveReturBtn.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					if (isValidInput()) {

						saveReturProgress.setVisibility(View.VISIBLE);
						saveReturProgress.setProgress(0);
						progressVal = 0;
						myTimer = new Timer();
						myTimer.schedule(new UpdateProgress(), 40, 15);
					}
					return true;
				case MotionEvent.ACTION_UP:
					if (saveReturProgress.getVisibility() == View.VISIBLE) {
						myTimer.cancel();
						saveReturProgress.setVisibility(View.INVISIBLE);
						return true;
					}
				}
				return false;
			}
		});
	}

	private boolean isValidInput() {
		return isDateReturValide() && hasArticolReturCant();
	}

	private boolean isDateReturValide() {

		if (DateLivrareReturPaleti.dataRetur.length() == 0) {
			Toast.makeText(getActivity(), "Selectati data retur", Toast.LENGTH_SHORT).show();
			return false;
		}

		if (DateLivrareReturPaleti.tipTransport.length() == 0) {
			Toast.makeText(getActivity(), "Selectati tipul de transport", Toast.LENGTH_SHORT).show();
			return false;
		}

		if (DateLivrareReturPaleti.motivRetur.length() == 0) {
			Toast.makeText(getActivity(), "Selectati motivul de retur", Toast.LENGTH_SHORT).show();
			return false;
		}

		if (DateLivrareReturPaleti.adresaCodJudet.length() == 0) {
			Toast.makeText(getActivity(), "Selectati judetul", Toast.LENGTH_SHORT).show();
			return false;
		}

		if (DateLivrareReturPaleti.adresaOras.length() == 0) {
			Toast.makeText(getActivity(), "Selectati orasul", Toast.LENGTH_SHORT).show();
			return false;
		}

		if (isCondTranspPaleti() && !isValReturPaleti())
			return false;		
		
		if (!isAdresaCorecta()) {
			Toast.makeText(getActivity(), "Completati adresa corect sau pozitionati adresa pe harta.", Toast.LENGTH_LONG).show();
			return false;
		}
		


		return true;
	}

	private boolean isAdresaCorecta() {
		if (DateLivrareReturPaleti.tipTransport.toUpperCase().equals("TRAP") && DateLivrareReturPaleti.isAltaAdresa)
			return isAdresaGoogleOk();
		else
			return true;

	}

	private boolean isAdresaGoogleOk() {
		return MapUtils.geocodeAddress(getAddressFromForm(), getActivity()).isAdresaValida();

	}

	private Address getAddressFromForm() {
		Address address = new Address();

		address.setCity(DateLivrareReturPaleti.adresaOras);
		address.setStreet(DateLivrareReturPaleti.adresaStrada);
		address.setSector(UtilsGeneral.getNumeJudet(DateLivrareReturPaleti.adresaCodJudet));

		return address;
	}

	private boolean hasArticolReturCant() {
		boolean retCant = false;

		for (int i = 0; i < listArticole.size(); i++)
			if (listArticole.get(i).getCantitateRetur() > 0)
				retCant = true;

		if (!retCant)
			Toast.makeText(getActivity(), "Adaugati o cantitate de retur", Toast.LENGTH_SHORT).show();
		return retCant;
	}

	class UpdateProgress extends TimerTask {
		public void run() {
			progressVal++;
			if (saveReturProgress.getProgress() == 50) {
				myHandler.post(new Runnable() {
					public void run() {

						showWarningDialog();

					}
				});
				myTimer.cancel();
			} else {
				saveReturProgress.setProgress(progressVal);
			}
		}
	}

	private void performSaveRetur() {

		BeanComandaRetur comandaRetur = new BeanComandaRetur();

		removeArticolTransport();

		if (isCondTranspPaleti()) {
			listArticole.add(getTransportRetur());
		}

		comandaRetur.setNrDocument(nrDocument);
		comandaRetur.setDataLivrare(DateLivrareReturPaleti.dataRetur);
		comandaRetur.setTipTransport(DateLivrareReturPaleti.tipTransport);
		comandaRetur.setCodAgent(UserInfo.getInstance().getCod());
		comandaRetur.setTipAgent(UserInfo.getInstance().getTipUser());
		comandaRetur.setMotivRespingere(DateLivrareReturPaleti.motivRetur);
		comandaRetur.setNumePersContact(DateLivrareReturPaleti.numePersContact);
		comandaRetur.setTelPersContact(DateLivrareReturPaleti.telPersContact);
		comandaRetur.setAdresaCodJudet(DateLivrareReturPaleti.adresaCodJudet);
		comandaRetur.setAdresaOras(DateLivrareReturPaleti.adresaOras);
		comandaRetur.setAdresaStrada(DateLivrareReturPaleti.adresaStrada);
		comandaRetur.setAdresaCodAdresa(DateLivrareReturPaleti.adresaCodAdresa);
		comandaRetur.setListArticole(opRetur.serializeListArticole(listArticole));
		comandaRetur.setObservatii(DateLivrareReturPaleti.observatii);
		comandaRetur.setCodClient(codClient);
		comandaRetur.setNumeClient(numeClient);

		HashMap<String, String> params = UtilsGeneral.newHashMapInstance();
		params.put("dateRetur", opRetur.serializeComandaRetur(comandaRetur));
		params.put("tipRetur", EnumTipRetur.PALETI.getTipRetur());

		opRetur.saveComandaRetur(params);

	}

	private void removeArticolTransport() {

		Iterator<BeanArticolRetur> listIterator = listArticole.iterator();

		while (listIterator.hasNext()) {
			BeanArticolRetur artRetur = listIterator.next();

			if (artRetur.getCod().equals(codArtTranspPalet))
				listIterator.remove();
		}

	}

	private boolean isCantValid() {
		if (textReturArticol.getText().toString().trim().length() == 0) {
			Toast.makeText(getActivity(), "Cantitate invalida", Toast.LENGTH_LONG).show();
			return false;
		}

		double cantitate = Double.valueOf(textCantArticol.getText().toString());
		double retur = Double.valueOf(textReturArticol.getText().toString().trim());

		if (retur > cantitate) {
			Toast.makeText(getActivity(), "Cantitate invalia", Toast.LENGTH_LONG).show();
			return false;
		}

		return true;
	}

	private void updateListArticole(BeanArticolRetur articol) {

		int artPosition = 0;
		for (int i = 0; i < listArticole.size(); i++) {
			if (articol == listArticole.get(i)) {
				artPosition = i;
				break;
			}
		}

		listArticole.set(artPosition, articol);
		populateListArticole(listArticole);
		listArticoleRetur.setSelection(artPosition);
		clearArtFields();

	}

	private int getNrPaletiRetur() {

		int nrPaleti = 0;

		for (BeanArticolRetur artRetur : listArticole) {
			if (artRetur.getCantitateRetur() > 0)
				nrPaleti += artRetur.getCantitateRetur();
		}

		return nrPaleti;
	}

	private double getValoarePaletiRetur() {

		double valPaleti = 0;

		for (BeanArticolRetur artRetur : listArticole) {
			valPaleti += artRetur.getCantitateRetur() * artRetur.getPretUnitPalet();
		}

		return valPaleti;

	}

	public void showWarningDialog() {

		StringBuilder alertMsg = new StringBuilder();
		alertMsg.append("\n");
		alertMsg.append("Te rugam sa te asiguri ca ai discutat cu clientul pentru pregatirea numarului si tipurilor de paleti ce trebuiesc returnati!");
		alertMsg.append("\n");
		alertMsg.append("\n");
		alertMsg.append("Este responsabilitatea ta! ");
		alertMsg.append("\n");

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(alertMsg.toString()).setCancelable(false).setPositiveButton("Salveaza", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				performSaveRetur();
			}
		}).setNegativeButton("Anuleaza", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		}).setTitle("Atentie!").setIcon(R.drawable.warning96).setCancelable(false);

		AlertDialog alert = builder.create();
		alert.show();

	}

	private void clearArtFields() {
		textNumeArticol.setText("");
		textCodArticol.setText("");
		textCantArticol.setText("");
		textUmArticol.setText("");
		textReturArticol.setText("");
	}

	private void addListenerListArticole() {
		listArticoleRetur.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				BeanArticolRetur articol = (BeanArticolRetur) arg0.getAdapter().getItem(arg2);
				selectedArticol = articol;

				if (!articol.getCod().equals(codArtTranspPalet)) {
					showArticolData(articol);
					showPanel("cantitateRetur");
				}

			}
		});
	}

	private BeanArticolRetur getTransportRetur() {

		BeanArticolRetur transpRetur = new BeanArticolRetur();
		transpRetur.setCod(codArtTranspPalet);
		transpRetur.setNume("PRESTARI SERVICII TRANSPORT");
		transpRetur.setCantitate(getNrPaletiRetur());
		transpRetur.setCantitateRetur(getNrPaletiRetur());
		transpRetur.setUm("BUC");

		return transpRetur;

	}

	private void showPanel(String panel) {
		if (panel.equals("cantitateRetur")) {
			layoutRetur.setVisibility(View.VISIBLE);
			selectIcon.setVisibility(View.GONE);
		}

		if (panel.equals("selectItem")) {
			layoutRetur.setVisibility(View.GONE);
			selectIcon.setVisibility(View.VISIBLE);
		}

	}

	private void emptyScreen() {
		layoutRetur.setVisibility(View.GONE);
		selectIcon.setVisibility(View.GONE);
		saveReturBtn.setVisibility(View.INVISIBLE);
		populateListArticole(new ArrayList<BeanArticolRetur>());

	}

	private void showArticolData(BeanArticolRetur articol) {
		textNumeArticol.setText(articol.getNume());
		textCodArticol.setText(articol.getCod());
		textCantArticol.setText(String.valueOf(articol.getCantitate()));
		textUmArticol.setText(articol.getUm());

		if (articol.getCantitateRetur() > 0)
			textReturArticol.setText(String.valueOf(articol.getCantitateRetur()));

	}

	public void setListArtRetur(String nrDocument, List<BeanArticolRetur> listArticole, String codClient, String numeClient) {
		this.listArticole = listArticole;
		this.nrDocument = nrDocument;
		this.codClient = codClient;
		this.numeClient = numeClient;

		saveReturBtn.setVisibility(View.VISIBLE);
		showPanel("selectItem");
		textDocument.setText("Articole");
		populateListArticole(listArticole);
	}

	private void populateListArticole(List<BeanArticolRetur> listArticole) {
		ArticoleReturAdapter adapter = new ArticoleReturAdapter(getActivity(), listArticole);
		listArticoleRetur.setAdapter(adapter);
	}

	private void showCmdStatus(String response) {
		if (response.equals("0")) {
			Toast.makeText(getActivity(), "Date salvate", Toast.LENGTH_SHORT).show();
			emptyScreen();
		} else {
			Toast.makeText(getActivity(), "Eroare: " + response, Toast.LENGTH_LONG).show();
		}

	}

	public void operationReturComplete(EnumRetur methodName, Object result) {
		switch (methodName) {
		case SAVE_COMANDA_RETUR:
			showCmdStatus((String) result);
			break;
		default:
			break;
		}

	}

}
