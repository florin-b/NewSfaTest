/**
 * @author florinb
 *
 */
package my.logon.screen.screens;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;

import my.logon.screen.R;
import my.logon.screen.adapters.ArticolModificareAdapter;
import my.logon.screen.adapters.ArticolePretTransport;
import my.logon.screen.adapters.ComandaModificareAdapter;
import my.logon.screen.beans.ArticolCalculDesc;
import my.logon.screen.beans.ArticolPalet;
import my.logon.screen.beans.BeanArticoleAfisare;
import my.logon.screen.beans.BeanComandaCreata;
import my.logon.screen.beans.BeanConditii;
import my.logon.screen.beans.BeanConditiiArticole;
import my.logon.screen.beans.BeanConditiiHeader;
import my.logon.screen.beans.CostDescarcare;
import my.logon.screen.beans.DateLivrareAfisare;
import my.logon.screen.dialogs.AprobariDialog;
import my.logon.screen.dialogs.CostMacaraDialog;
import my.logon.screen.dialogs.CostPaletiDialog;
import my.logon.screen.enums.EnumComenziDAO;
import my.logon.screen.enums.EnumPaleti;
import my.logon.screen.enums.EnumTipClientIP;
import my.logon.screen.helpers.HelperCostDescarcare;
import my.logon.screen.listeners.ArticolModificareListener;
import my.logon.screen.listeners.AsyncTaskListener;
import my.logon.screen.listeners.ComenziDAOListener;
import my.logon.screen.listeners.CostMacaraListener;
import my.logon.screen.listeners.PaletiListener;
import my.logon.screen.model.AlgoritmComandaGed;
import my.logon.screen.model.ArticolComanda;
import my.logon.screen.model.Comanda;
import my.logon.screen.model.ComenziDAO;
import my.logon.screen.model.Constants;
import my.logon.screen.model.DateLivrare;
import my.logon.screen.model.HelperTranspBuc;
import my.logon.screen.model.InfoStrings;
import my.logon.screen.model.ListaArticoleComandaGed;
import my.logon.screen.model.ListaArticoleModificareComanda;
import my.logon.screen.model.OperatiiArticolImpl;
import my.logon.screen.model.UserInfo;
import my.logon.screen.utils.UtilsComenzi;
import my.logon.screen.utils.UtilsComenziGed;
import my.logon.screen.utils.UtilsFormatting;
import my.logon.screen.utils.UtilsGeneral;
import my.logon.screen.utils.UtilsUser;

public class ModificareComanda extends Activity implements AsyncTaskListener, ComenziDAOListener, ArticolModificareListener, Observer, CostMacaraListener,
		PaletiListener {

	Button quitBtn, stocBtn, clientBtn, articoleBtn, livrareBtn, salveazaComandaBtn, stergeComandaBtn, btnCommentariiCond, aprobareBtn;
	String filiala = "", nume = "", cod = "", globalSubCmp = "0";
	public static String unitLogComanda = "";
	public static String numeDepart = "";
	public static String codDepart = "";

	private int listViewSelPos = -1;

	private Spinner spinnerComenzi;
	public SimpleAdapter adapterComenzi;

	public static String selectedCmd = "";
	private String selectedCmdSAP = "-1";
	private String selectedClientCode = "-1", selectedUnitLog = "-1";
	private BeanComandaCreata comandaSelectata;
	private TextView textTipPlata, textAdrLivr, textTotalCmd, textOras, textJudet;
	private TextView textPersContact, textTelefon, textCantar, textTransport, textFactRed, textPondereB, textTaxaVerde;

	private TextView textCondProcB, textCondNrFacturi, textCondComentarii;

	public static String codClientVar = "";
	public static String numeClientVar = "";
	public static String tipClientVar = "";
	public static String articoleComanda = "", numeArtSelContextMenu = "", codArtSelContextMenu = "";
	public static double totalComanda = 0, stocArtCond = 0;

	public static boolean isComandaDistrib = true;

	private static ArticolComanda[] objArticol = new ArticolComanda[70];

	public static String filialaAlternativaM = "NN10";

	private ProgressBar mProgress;
	private Timer myTimer;
	private int progressVal = 0;
	private Handler logonHandler = new Handler();

	private String comandaBlocata = "0";
	private String globalAlertSDKA = "", globalAlertDVKA = "";
	private String conditieID = "";
	private int idOperatieComanda = 3;

	private boolean alertSD = false;
	private boolean alertDV = false;

	public static String tipAcces;
	ListView listViewArticole, listViewArtCond;

	private LinearLayout layoutTaxaVerde, layoutConditiiHeader, layoutCondProcB, layoutCondNrFact, layoutCondObs;

	public static String depozitUnic = "";
	private String selectedCmdAdrLivr = "";
	private String mailAlertTipDest = "";

	public static String divizieComanda = "";

	private LinearLayout layoutDetaliiCmd;

	private Comanda comandaFinala;

	String serializedResult;
	private String comandaJson;
	private ComenziDAO operatiiComenzi;
	private List<BeanComandaCreata> listComenzi;
	private ArrayList<ArticolComanda> listArticoleComanda;
	private List<BeanConditiiArticole> conditiiComandaArticole;
	private ArticolModificareAdapter adapterArticole;
	private String codTipReducere = "-1";
	private LinearLayout layoutBV90;

	private CostDescarcare costDescarcare;
	private Button valTranspBtn;
	private TextView textAlertaMarja;

	private double valTransport = 0;
	private double valTransportSAP = 0;
	public static boolean permitArticoleDistribIP = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

		setTheme(R.style.LRTheme);
		setContentView(R.layout.modificarecomandaheader);

		operatiiComenzi = ComenziDAO.getInstance(this);
		operatiiComenzi.setComenziDAOListener(this);

		ActionBar actionBar = getActionBar();
		actionBar.setTitle("Modificare comanda");
		actionBar.setDisplayHomeAsUpEnabled(true);

		checkStaticVars();

		spinnerComenzi = (Spinner) findViewById(R.id.spinnerCmd);
		spinnerComenzi.setVisibility(View.INVISIBLE);
		addListenerSpinnerComenzi();

		layoutDetaliiCmd = (LinearLayout) findViewById(R.id.layoutDetaliiCmd);
		listViewArticole = (ListView) findViewById(R.id.listArtModif);

		layoutBV90 = (LinearLayout) findViewById(R.id.layoutBV90);
		layoutBV90.setVisibility(View.GONE);

		if (isUserCV())
			ListaArticoleComandaGed.getInstance().addObserver(this);
		else
			ListaArticoleModificareComanda.getInstance().addObserver(this);

		addListenerListArtModif();
		registerForContextMenu(listViewArticole);

		layoutTaxaVerde = (LinearLayout) findViewById(R.id.layoutTaxaVerde);
		layoutTaxaVerde.setVisibility(View.INVISIBLE);

		textTaxaVerde = (TextView) findViewById(R.id.textTaxaVerde);

		textTipPlata = (TextView) findViewById(R.id.textTipPlata);
		textAdrLivr = (TextView) findViewById(R.id.textAdrLivr);
		textPersContact = (TextView) findViewById(R.id.textPersContact);
		textTelefon = (TextView) findViewById(R.id.textTelefon);
		textCantar = (TextView) findViewById(R.id.textCantar);
		textTransport = (TextView) findViewById(R.id.textTransport);
		textTotalCmd = (TextView) findViewById(R.id.textTotalCmd);
		textFactRed = (TextView) findViewById(R.id.textFactRed);
		textPondereB = (TextView) findViewById(R.id.textPondereB);

		layoutConditiiHeader = (LinearLayout) findViewById(R.id.layoutConditiiHeader);
		layoutCondProcB = (LinearLayout) findViewById(R.id.layoutCondProcB);
		layoutCondNrFact = (LinearLayout) findViewById(R.id.layoutCondNrFact);
		layoutCondObs = (LinearLayout) findViewById(R.id.layoutCondObs);

		textCondProcB = (TextView) findViewById(R.id.textCondProcB);
		textCondNrFacturi = (TextView) findViewById(R.id.textCondNrFacturi);
		textCondComentarii = (TextView) findViewById(R.id.textCondComentarii);

		textOras = (TextView) findViewById(R.id.textOras);
		textJudet = (TextView) findViewById(R.id.textJudet);

		this.salveazaComandaBtn = (Button) findViewById(R.id.saveCmdBtn);
		this.salveazaComandaBtn.setVisibility(View.INVISIBLE);
		addListenerSaveCmdBtn();

		this.stergeComandaBtn = (Button) findViewById(R.id.delCmdBtn);

		aprobareBtn = (Button) findViewById(R.id.aprobareBtn);

		if (UtilsUser.isUserKA()) {
			aprobareBtn.setVisibility(View.VISIBLE);
			openAprobareDialog();
		}

		addListenerDelCmdBtn();

		mProgress = (ProgressBar) findViewById(R.id.progress_bar_savecmd);
		mProgress.setVisibility(View.INVISIBLE);

		valTranspBtn = (Button) findViewById(R.id.valTransp);
		addListenerValTranspBtn();
		textAlertaMarja = (TextView) findViewById(R.id.textAlertaMarja);

		loadListComenzi();

	}

	public void addListenerValTranspBtn() {
		valTranspBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showModifValTranspDialogBox();

			}
		});

	}

	private boolean isComandaClpGed() {
		return !isComandaDistrib && !DateLivrare.getInstance().getCodFilialaCLP().trim().isEmpty()
				&& DateLivrare.getInstance().getCodFilialaCLP().trim().length() == 4;
	}

	public void showModifValTranspDialogBox() {
		final Dialog dialogModifValTransp = new Dialog(ModificareComanda.this);
		dialogModifValTransp.setContentView(R.layout.modifvaltranspdialogbox);
		dialogModifValTransp.setTitle("Modificare valoare transport");
		dialogModifValTransp.setCancelable(false);
		dialogModifValTransp.show();

		final NumberFormat nf3 = NumberFormat.getInstance(new Locale("en", "US"));
		nf3.setMinimumFractionDigits(2);
		nf3.setMaximumFractionDigits(2);
		nf3.setGroupingUsed(false);

		final EditText textValTransp = (EditText) dialogModifValTransp.findViewById(R.id.txtValTransp);

		TextView txtTranspSAP = (TextView) dialogModifValTransp.findViewById(R.id.txtTranspSAP);
		txtTranspSAP.setText("SAP: " + nf3.format(valTransportSAP));

		ListView listViewArticoleTransp = (ListView) dialogModifValTransp.findViewById(R.id.listArticoleTransp);

		ArticolePretTransport adapterArticoleTransport = new ArticolePretTransport(ModificareComanda.this, listArticoleComanda);
		listViewArticoleTransp.setAdapter(adapterArticoleTransport);

		textValTransp.setText(nf3.format(valTransport));
		textValTransp.setSelection(textValTransp.getText().length(), textValTransp.getText().length());

		Button btnOkTransp = (Button) dialogModifValTransp.findViewById(R.id.btnOkTransp);
		btnOkTransp.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				if (textValTransp.getText().toString().trim().length() > 0) {
					if (Double.parseDouble(textValTransp.getText().toString().trim()) >= 0) {

						valTransport = Double.parseDouble(textValTransp.getText().toString().trim());

						if (valTransport < valTransportSAP) {
							Toast.makeText(getApplicationContext(), "Valoarea transportului nu poate fi mai mica decat cea din SAP!", Toast.LENGTH_SHORT)
									.show();
							valTransport = valTransportSAP;
							textValTransp.setText(nf3.format(valTransport));
						} else {
							valTranspBtn.setText("Transp: " + textValTransp.getText().toString().trim());
							calculProcente(listArticoleComanda);
							dialogModifValTransp.dismiss();
						}

						UtilsComenziGed.setValoareArticolTransport(listArticoleComanda, valTransport);
						adapterArticole.notifyDataSetChanged();

						DateLivrare.getInstance().setValTransport(valTransport);

					}

				}

			}
		});

		Button btnCancelTransp = (Button) dialogModifValTransp.findViewById(R.id.btnCancelTransp);
		btnCancelTransp.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialogModifValTransp.dismiss();

			}
		});

	}

	public void openAprobareDialog() {
		aprobareBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				try {

					AprobariDialog aprove = new AprobariDialog(ModificareComanda.this);
					aprove.getAproveData(selectedCmd);
					aprove.show();

				} catch (Exception e) {
					System.out.println(e.getMessage());
				}

			}
		});
	}

	private void CreateMenu(Menu menu) {

		if (!isUserCVExc()) {
			MenuItem mnu2 = menu.add(0, 0, 0, "Articole");
			mnu2.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		}
		MenuItem mnu3 = menu.add(0, 1, 1, "Livrare");
		mnu3.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case 0:
			if (codClientVar.length() > 0) {

				Intent nextScreen = null;
				if (isUserCV() || isComandaGed()) {
					nextScreen = new Intent(getApplicationContext(), SelectArtCmdGed.class);
					nextScreen.putExtra("totalNegociat", false);
					nextScreen.putExtra("codClientVar", selectedClientCode);
					nextScreen.putExtra("depozitUnic", depozitUnic);
					nextScreen.putExtra("tipComanda", "X");
					nextScreen.putExtra("rezervStoc", false);
					nextScreen.putExtra("filialaAlternativa", selectedUnitLog);
					nextScreen.putExtra("canalDistrib", ModificareComanda.isComandaDistrib ? "10" : "20");
					nextScreen.putExtra("tipPersClient", comandaSelectata.isCmdInstPublica() ? "IP" : "");

					new SelectArtCmdGed().addFilialaMathaus(selectedUnitLog, getBaseContext());

				} else {
					nextScreen = new Intent(getApplicationContext(), SelectArtModificareCmd.class);

				}
				startActivity(nextScreen);

			} else {
				Toast.makeText(getApplicationContext(), "Selectati o comanda!", Toast.LENGTH_SHORT).show();
			}

			return true;

		case 1:

			if (codClientVar.length() > 0) {

				Intent nextScreenLivr = null;
				if (isUserCV() || isComandaGed()) {
					nextScreenLivr = new Intent(getApplicationContext(), SelectAdrLivrCmdGed.class);
					nextScreenLivr.putExtra("codClient", selectedClientCode);
					nextScreenLivr.putExtra("parrentClass", "ModificareComanda");
					nextScreenLivr.putExtra("tipPlataContract", DateLivrare.getInstance().getTipPlata());

				} else {
					nextScreenLivr = new Intent(getApplicationContext(), SelectAdrLivrCmd.class);
					nextScreenLivr.putExtra("parrentClass", "ModificareComanda");
					nextScreenLivr.putExtra("tipPlataContract", DateLivrare.getInstance().getTipPlata());
				}

				selectedCmdAdrLivr = selectedCmd;
				startActivity(nextScreenLivr);

			} else {
				Toast.makeText(getApplicationContext(), "Selectati o comanda!", Toast.LENGTH_SHORT).show();
			}

			return true;

		case android.R.id.home:

			articoleComanda = "";
			numeClientVar = "";
			codClientVar = "";

			UserInfo.getInstance().setParentScreen("");
			clearAllData();

			Intent nextScreen = new Intent(getApplicationContext(), MainMenu.class);

			startActivity(nextScreen);

			finish();
			return true;

		}
		return false;
	}

	boolean isUserCV() {
		return UserInfo.getInstance().getTipUser().equals("CV") || UserInfo.getInstance().getTipUser().equals("CVR")
				|| UserInfo.getInstance().getTipUser().equals("SM") || UserInfo.getInstance().getTipUserSap().equals("KA3")
				|| UserInfo.getInstance().getTipUser().equals("SMR") || UserInfo.getInstance().getTipUser().equals("WOOD")
				|| UserInfo.getInstance().getTipUser().equals("SC");
	}

	private boolean isUserCVExc() {
		return UserInfo.getInstance().getTipUser().equals("CVR") || UserInfo.getInstance().getTipUserSap().equals("KA3")
				|| UserInfo.getInstance().getTipUser().equals("SMR") || UserInfo.getInstance().getTipUser().equals("WOOD");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		CreateMenu(menu);
		return true;
	}

	@Override
	public void onResume() {

		super.onResume();
		checkStaticVars();

		if (DateLivrare.getInstance().getDateLivrare().length() > 0) {

			DateLivrare dateLivrareInstance = DateLivrare.getInstance();

			textJudet.setText(dateLivrareInstance.getNumeJudet());
			textOras.setText(dateLivrareInstance.getOras());
			textAdrLivr.setText(dateLivrareInstance.getStrada());
			textPersContact.setText(dateLivrareInstance.getPersContact());
			textTelefon.setText(dateLivrareInstance.getNrTel());

			// ***************cantar
			if (dateLivrareInstance.getCantar().equals("NU"))
				textCantar.setText("Nu");
			else
				textCantar.setText("Da");
			// ***************sf. cantar

			textFactRed.setText(UtilsGeneral.getTipReducere(dateLivrareInstance.getRedSeparat()));
			textTipPlata.setText(UtilsGeneral.getDescTipPlata(dateLivrareInstance.getTipPlata()));
			textTransport.setText(UtilsGeneral.getDescTipTransport(dateLivrareInstance.getTransport()));

			if (!isUserCV() && !isComandaGed()) {

				if (dateLivrareInstance.getZonaBucuresti() != null) {

					HelperTranspBuc.eliminaCostTransportZoneBuc(listArticoleComanda);

					if (HelperTranspBuc.isCondTranspZonaBuc(dateLivrareInstance, dateLivrareInstance.getZonaBucuresti())) {
						HelperTranspBuc.adaugaTransportBucuresti(listArticoleComanda, dateLivrareInstance.getZonaBucuresti());

					}

					adapterArticole.setListArticole(listArticoleComanda);
					adapterArticole.notifyDataSetChanged();
					listViewArticole.setAdapter(adapterArticole);

				}

			}

		}

	}

	private double getTotalComanda() {

		double localTotalComanda = 0;

		if (listArticoleComanda != null) {
			for (ArticolComanda articol : listArticoleComanda) {

				localTotalComanda += articol.getPretUnit() * articol.getCantUmb();

			}
		}

		return localTotalComanda;

	}

	public void calculPondereB() {

		double totalArtB = 0, procentB = 0, localTotalComanda = 0;

		for (ArticolComanda articol : listArticoleComanda) {

			if (articol.getTipArt().equalsIgnoreCase("B"))
				totalArtB += articol.getPret();

			localTotalComanda += articol.getPretUnit() * articol.getCantUmb();

		}

		if (localTotalComanda == 0) {
			procentB = 0;
		} else {
			procentB = totalArtB / localTotalComanda * 100;
		}

		textPondereB.setText(String.format("%.02f", procentB) + "%");
		textTotalCmd.setText(String.format("%.02f", localTotalComanda));
		totalComanda = localTotalComanda;

	}

	public void addListenerSaveCmdBtn() {
		salveazaComandaBtn.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {

				try {

					switch (event.getAction()) {

					case MotionEvent.ACTION_DOWN:

						if (listArticoleComanda.size() == 0) {
							Toast.makeText(getApplicationContext(), "Comanda nu contine articole!", Toast.LENGTH_SHORT).show();
							return false;
						}

						if (!selectedCmdAdrLivr.equals(selectedCmd)) {
							Toast.makeText(getApplicationContext(), "Verificati datele de livrare!", Toast.LENGTH_SHORT).show();
							return false;
						}

						if (!isConditiiAcceptate()) {
							Toast.makeText(getApplicationContext(), "Preluati toate conditiile!", Toast.LENGTH_SHORT).show();
							return false;
						}

						if (!isConditiiCmdAccept()) {
							Toast.makeText(getApplicationContext(), "Comanda nu are toate aprobarile!", Toast.LENGTH_SHORT).show();
							return false;
						}

						mProgress.setVisibility(View.VISIBLE);
						mProgress.setProgress(0);
						progressVal = 0;
						myTimer = new Timer();
						myTimer.schedule(new UpdateProgress(), 40, 15);

						return true;

					case MotionEvent.ACTION_UP:

						if (listArticoleComanda.size() > 0 && myTimer != null) {
							myTimer.cancel();
							mProgress.setVisibility(View.INVISIBLE);
						}
						return true;

					}
				} catch (Exception ex) {
					Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_SHORT).show();
				}

				return false;
			}

		});

	}

	class UpdateProgress extends TimerTask {
		public void run() {
			progressVal++;
			if (mProgress.getProgress() == 50) {
				logonHandler.post(new Runnable() {
					public void run() {

						DateLivrare dateLivrareInstance = DateLivrare.getInstance();

						prepareArtForDelivery();

						if (dateLivrareInstance.getTipPlata().equals("E") && totalComanda > 5000 && tipClientVar.equals("PJ")) {
							Toast.makeText(getApplicationContext(), "Pentru plata in numerar valoarea maxima este de 5000 RON!", Toast.LENGTH_SHORT).show();
							return;
						}

						dateLivrareInstance.setTotalComanda(String.valueOf(totalComanda));
						dateLivrareInstance.setCodAgent(UserInfo.getInstance().getCod());
						dateLivrareInstance.setFactRed("-1");

						String alerteKA = globalAlertSDKA + "!" + globalAlertDVKA;

						// aprobare adr. livrare noua doar pentru agenti
						if (!UserInfo.getInstance().getTipAcces().equals("27")) {
							if (dateLivrareInstance.isAdrLivrNoua())
								comandaBlocata = "1";
						}

						String localRedSeparat = codTipReducere.equals("-1") ? dateLivrareInstance.getRedSeparat() : codTipReducere;

						if (isComandaGed())
							localRedSeparat = "NU";

						if (isReducere())
							localRedSeparat = "X";

						NumberFormat nf3 = NumberFormat.getInstance();
						nf3.setMinimumFractionDigits(2);
						nf3.setMaximumFractionDigits(2);

						if (dateLivrareInstance.getObsPlata().equals("SO") && dateLivrareInstance.getTipPlata().equals("E")) {
							if (!dateLivrareInstance.isValIncModif()) {
								dateLivrareInstance.setValoareIncasare(nf3.format(ModificareComanda.totalComanda * Constants.TVA));
							}
						}

						String userSiteMail = " ", isValIncModif = " ", codJ = "", adrLivrareGED = "";

						if (dateLivrareInstance.isValIncModif())
							isValIncModif = "X";

						comandaFinala = new Comanda();
						comandaFinala.setCodClient(selectedClientCode);
						comandaFinala.setComandaBlocata(comandaBlocata);
						comandaFinala.setNrCmdSap(selectedCmdSAP);
						comandaFinala.setConditieID(conditieID);

						comandaFinala.setAlerteKA(alerteKA);
						comandaFinala.setFactRedSeparat(localRedSeparat);
						comandaFinala.setFilialaAlternativa(ModificareComanda.filialaAlternativaM);
						comandaFinala.setUserSite(UserInfo.getInstance().getUserSite());
						comandaFinala.setUserSiteMail(dateLivrareInstance.getMail());
						comandaFinala.setIsValIncModif(isValIncModif);
						comandaFinala.setCodJ(codJ);
						comandaFinala.setAdresaLivrareGed(serializeDateLivrareGed());
						comandaFinala.setNumeClient(dateLivrareInstance.getNumeClient());
						comandaFinala.setCnpClient(dateLivrareInstance.getCnpClient());
						comandaFinala.setNecesarAprobariCV(comandaSelectata.getAprobariNecesare());

						verificaPretMacara();

					}
				});

				myTimer.cancel();
			} else {
				mProgress.setProgress(progressVal);
			}

		}
	}

	private void verificaPretMacara() {

		HelperCostDescarcare.eliminaCostDescarcare(listArticoleComanda);

		if ((DateLivrare.getInstance().getTransport().equalsIgnoreCase("TRAP") || DateLivrare.getInstance().getTransport().equalsIgnoreCase("TCLI"))
				&& !isExceptieComandaIP() && !UtilsUser.isAV_SD_01()) {

			String codFurnizor = " ";

			if (DateLivrare.getInstance().getFurnizorComanda() != null
					&& !DateLivrare.getInstance().getFurnizorComanda().getCodFurnizorMarfa().trim().isEmpty())
				codFurnizor = DateLivrare.getInstance().getFurnizorComanda().getCodFurnizorMarfa();
			else if (!DateLivrare.getInstance().getCodFilialaCLP().trim().isEmpty())
				codFurnizor = DateLivrare.getInstance().getCodFilialaCLP();

			List<ArticolCalculDesc> artCalcul = HelperCostDescarcare.getDateCalculDescarcare(listArticoleComanda);

			String listArtSer = operatiiComenzi.serializeArtCalcMacara(artCalcul);

			HashMap<String, String> params = new HashMap<String, String>();

			params.put("unitLog", DateLivrare.getInstance().getUnitLog());
			params.put("codAgent", DateLivrare.getInstance().getCodAgent());
			params.put("codClient", comandaFinala.getCodClient());
			params.put("codFurnizor", codFurnizor);
			params.put("listArt", listArtSer);

			operatiiComenzi.getCostMacara(params);
		} else
			performSaveCmd();

	}

	private boolean isExceptieComandaIP() {
		return UtilsUser.isUserIP() && comandaSelectata.getTipClientInstPublica() == EnumTipClientIP.CONSTR;
	}

	private void afiseazaPretMacaraDialog(String result) {

		costDescarcare = HelperCostDescarcare.deserializeCostMacara(result);

		verificaPaletiComanda(costDescarcare.getArticolePaleti());

		if (!costDescarcare.getArticolePaleti().isEmpty()) {
			costDescarcare.getArticoleDescarcare().get(0).setCantitate(0);

			int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.65);
			int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.55);

			CostPaletiDialog costPaleti = new CostPaletiDialog(this, costDescarcare.getArticolePaleti(), DateLivrare.getInstance().getTransport());
			costPaleti.setPaletiDialogListener(this);
			costPaleti.getWindow().setLayout(width, height);
			costPaleti.show();

		} else if (costDescarcare.getSePermite() && costDescarcare.getValoareDescarcare() > 0
				&& DateLivrare.getInstance().getTransport().equalsIgnoreCase("TRAP")) {

			CostMacaraDialog macaraDialog = new CostMacaraDialog(this, costDescarcare, isComandaGed());
			macaraDialog.setCostMacaraListener(this);
			macaraDialog.show();

		} else {
			if (!costDescarcare.getSePermite())
				DateLivrare.getInstance().setMasinaMacara(false);

			performSaveCmd();

		}

	}

	private void verificaPaletiComanda(List<ArticolPalet> listPaleti) {

		Iterator<ArticolComanda> articolIterator = listArticoleComanda.iterator();

		boolean paletExista;

		while (articolIterator.hasNext()) {
			ArticolComanda articol = articolIterator.next();

			paletExista = true;

			if (articol.isUmPalet()) {
				paletExista = false;
			}

			for (ArticolPalet palet : listPaleti) {

				if (palet.getCodPalet().equals(articol.getCodArticol()) && (palet.getCantitate() == (int) articol.getCantitate())) {
					paletExista = true;
				}

			}

			if (!paletExista) {
				articolIterator.remove();
				adapterArticole.notifyDataSetChanged();
			}

		}

		Iterator<ArticolPalet> paletIterator = listPaleti.iterator();

		for (ArticolComanda articol : listArticoleComanda) {

			while (paletIterator.hasNext()) {
				ArticolPalet palet = paletIterator.next();

				if (palet.getCodPalet().equals(articol.getCodArticol()) && (palet.getCantitate() == (int) articol.getCantitate())) {
					paletIterator.remove();
				}

			}

			paletIterator = listPaleti.iterator();

		}

	}

	private void trateazaPretMacara(boolean acceptaPret, double valoarePret) {

		if (acceptaPret) {
			DateLivrare.getInstance().setMasinaMacara(true);

			List<ArticolComanda> articoleDescarcare = HelperCostDescarcare.getArticoleDescarcare(costDescarcare, valoarePret, UserInfo.getInstance()
					.getUnitLog(), listArticoleComanda);

			listArticoleComanda.addAll(articoleDescarcare);

		} else {
			DateLivrare.getInstance().setMasinaMacara(false);
		}

		performSaveCmd();

	}

	private boolean isReducere() {
		return globalSubCmp.equals("1") && !UserInfo.getInstance().getCodDepart().equals("07") && !UserInfo.getInstance().getCodDepart().equals("04");
	}

	private void performSaveCmd() {
		try {

			// comanda cv cu conditii se salveaza direct
			if (isComandaGed() && UserInfo.getInstance().getTipUser().equals("CV") && !UserInfo.getInstance().getTipUserSap().equals("CONS-GED")) {
				alertSD = false;
				alertDV = false;
			}

			HashMap<String, String> params = new HashMap<String, String>();

			String tipUser = "";
			if (UserInfo.getInstance().getTipAcces().equals("27"))
				tipUser = "KA";
			else if (isComandaGed())
				tipUser = "CV";
			else if (UserInfo.getInstance().getTipAcces().equals("62"))
				tipUser = "AV";
			else
				tipUser = UserInfo.getInstance().getTipUser();

			params.put("comanda", " ");
			params.put("tipUser", tipUser);
			params.put("JSONArt", serializeArticole());
			params.put("JSONComanda", serializeComanda(comandaFinala));
			params.put("JSONDateLivrare", serializeDateLivrare());
			params.put("alertSD", String.valueOf(alertSD));
			params.put("alertDV", String.valueOf(alertDV));
			params.put("tipUserSap", UserInfo.getInstance().getTipUserSap());
			params.put("idCmdAmob", "-1");

			operatiiComenzi.salveazaComandaDistrib(params);

		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
		}
	}

	private boolean isComandaGed() {

		String tempDistribUL = InfoStrings.getDistribUnitLog(selectedUnitLog);

		if (InfoStrings.getClientGenericGed(tempDistribUL, "PF").equals(selectedClientCode)
				|| InfoStrings.getClientGenericGed(tempDistribUL, "PJ").equals(selectedClientCode)
				|| InfoStrings.getClientGenericGedWood(tempDistribUL, "PF").equals(selectedClientCode)
				|| InfoStrings.getClientGenericGedWood(tempDistribUL, "PJ").equals(selectedClientCode)
				|| InfoStrings.getClientGenericGedWood_faraFact(tempDistribUL, "PF").equals(selectedClientCode)
				|| InfoStrings.getClientGenericGed_CONSGED_faraFactura(tempDistribUL, "PF").equals(selectedClientCode)
				|| InfoStrings.getClientCVO_cuFact_faraCnp(tempDistribUL, "").equals(selectedClientCode)
				|| InfoStrings.getClientGed_FaraFactura(tempDistribUL).equals(selectedClientCode) || !isComandaDistrib)

			return true;
		else
			return false;
	}

	public String prepareArtForDelivery() {
		String retVal = "";

		String[] tokPret;
		double valCondPret = 0;
		double taxaVerde = 0;
		boolean hasTaxaVerde = false;

		alertSD = false;
		alertDV = false;
		comandaBlocata = "0";
		globalAlertSDKA = "?";
		globalAlertDVKA = "?";
		globalSubCmp = "0";

		totalComanda = 0;

		Collections.sort(listArticoleComanda, ArticolComanda.DepartComparator);
		Iterator<ArticolComanda> iterator = listArticoleComanda.iterator();
		ArticolComanda articolComanda = null;

		while (iterator.hasNext()) {

			articolComanda = iterator.next();

			if (articolComanda.getInfoArticol().contains(";")) {
				String[] condPret = articolComanda.getInfoArticol().split(";");

				for (int ii = 0; ii < condPret.length; ii++) {
					tokPret = condPret[ii].split(":");
					valCondPret = Double.valueOf(tokPret[1].replace(',', '.').trim());
					if (valCondPret != 0) {
						if (tokPret[0].toUpperCase(Locale.getDefault()).contains("VERDE")) {
							taxaVerde += valCondPret;
						}

					}
				}
			}

			if (articolComanda.getAlteValori().toString().equals("1"))
				globalSubCmp = "1";

			if (isUserExceptie()) {
				if (articolComanda.getTipAlert().contains("SD")) {
					comandaBlocata = "1";
					alertSD = true;

					if (!globalAlertSDKA.contains(articolComanda.getDepart()))
						globalAlertSDKA += articolComanda.getDepart() + "?";
				}

				if (articolComanda.getTipAlert().contains("DV")) {
					comandaBlocata = "1";
					alertDV = true;

					if (!globalAlertDVKA.contains(articolComanda.getDepart()))
						globalAlertDVKA += articolComanda.getDepart() + "?";
				}

			}

			if (isUserCV()) {
				comandaBlocata = "0";
				alertSD = false;
				alertDV = false;

			}

			if (articolComanda.getCodArticol().equals("00000000")) {
				hasTaxaVerde = true;
			}

			totalComanda += articolComanda.getPretUnit() * articolComanda.getCantUmb();

		}

		if (ModificareComanda.isComandaDistrib) {
			if (hasTaxaVerde) {
				updateTaxaVerde(taxaVerde);
			} else {
				if (taxaVerde > 0)
					addTaxaVerde(taxaVerde);
			}
		}

		return retVal;
	}

	// userul este agent, sd sau ka
	boolean isUserExceptie() {
		return UserInfo.getInstance().getTipAcces().equals("9") || UserInfo.getInstance().getTipAcces().equals("10")
				|| UserInfo.getInstance().getTipAcces().equals("27") || UserInfo.getInstance().getTipAcces().equals("62");
	}

	private void updateTaxaVerde(double taxaVerde) {
		Iterator<ArticolComanda> iterator = listArticoleComanda.iterator();
		ArticolComanda articol = null;

		while (iterator.hasNext()) {
			articol = iterator.next();

			if (articol.getCodArticol().equals("00000000")) {
				articol.setTaxaVerde(taxaVerde);
				break;
			}
		}
	}

	private void addTaxaVerde(double taxaVerde) {

		ArticolComanda articol = new ArticolComanda();
		articol.setCodArticol("000000000000000000");
		articol.setCantitate(1.0);
		articol.setDepozit(listArticoleComanda.get(0).getDepozit());
		articol.setPretUnit(taxaVerde);
		articol.setProcent(0);
		articol.setUm("BUC");
		articol.setProcentFact(0);
		articol.setConditie(false);
		articol.setDiscClient(0);
		articol.setProcAprob(0);
		articol.setMultiplu(1);
		articol.setPret(taxaVerde);
		articol.setInfoArticol(" ");
		articol.setCantUmb(1);
		articol.setUmb("BUC");
		articol.setDepart(listArticoleComanda.get(0).getDepart());
		articol.setObservatii("");
		articol.setIstoricPret("");
		listArticoleComanda.add(articol);

	}

	private String serializeArticole() {
		JSONArray myArray = new JSONArray();
		TreeSet<String> aprobariCV = new TreeSet<String>();
		JSONObject obj = null;

		if (!ModificareComanda.isComandaDistrib && !UtilsUser.isAgentOrSDorKA() && !isExceptieComandaIP() && valTransport > 0) {
			UtilsComenziGed.setValoareArticolTransport(listArticoleComanda, valTransport);
		}

		try {
			for (int i = 0; i < listArticoleComanda.size(); i++) {

				if (listArticoleComanda.get(i).getStatus() != null && listArticoleComanda.get(i).getStatus().toLowerCase().contains("respins"))
					continue;

				obj = new JSONObject();
				obj.put("codArticol", listArticoleComanda.get(i).getCodArticol());
				obj.put("cantitate", listArticoleComanda.get(i).getCantitate());
				obj.put("depozit", listArticoleComanda.get(i).getDepozit());
				obj.put("pretUnit", listArticoleComanda.get(i).getPretUnit());
				obj.put("procent", listArticoleComanda.get(i).getProcent());
				obj.put("um", listArticoleComanda.get(i).getUm());
				obj.put("procentFact", listArticoleComanda.get(i).getProcentFact());
				obj.put("conditie", listArticoleComanda.get(i).getConditie());
				obj.put("discClient", listArticoleComanda.get(i).getDiscClient());
				obj.put("procAprob", listArticoleComanda.get(i).getProcAprob());
				obj.put("multiplu", listArticoleComanda.get(i).getMultiplu());
				obj.put("pret", listArticoleComanda.get(i).getPret());
				obj.put("infoArticol", listArticoleComanda.get(i).getInfoArticol());
				obj.put("cantUmb", listArticoleComanda.get(i).getCantUmb());
				obj.put("Umb", listArticoleComanda.get(i).getUmb());
				obj.put("depart", listArticoleComanda.get(i).getDepart());
				obj.put("ponderare", listArticoleComanda.get(i).getPonderare());

				obj.put("observatii", listArticoleComanda.get(i).getTipAlert());
				obj.put("departAprob", listArticoleComanda.get(i).getDepartAprob());
				obj.put("istoricPret", listArticoleComanda.get(i).getIstoricPret());
				obj.put("valTransport", listArticoleComanda.get(i).getValTransport());
				obj.put("filialaSite", listArticoleComanda.get(i).getFilialaSite());
				obj.put("dataExp", listArticoleComanda.get(i).getDataExpPret());
				obj.put("listCabluri", new OperatiiArticolImpl(this).serializeCabluri05(listArticoleComanda.get(i).getListCabluri()));

				if (!UtilsUser.isAgentOrSDorKA()) {
					if ((listArticoleComanda.get(i).getNumeArticol() != null && listArticoleComanda.get(i).getPonderare() == 1)
							|| comandaSelectata.isCmdInstPublica()) {
						alertDV = true;
						if (!comandaFinala.getComandaBlocata().equals("21"))
							comandaFinala.setComandaBlocata("1");

						aprobariCV.add(listArticoleComanda.get(i).getDepartSintetic());
					}
				}

				myArray.put(obj);
			}
		} catch (Exception ex) {
			Toast.makeText(this, ex.toString(), Toast.LENGTH_SHORT).show();
		}

		serializedResult = myArray.toString();

		if (!UtilsUser.isAgentOrSDorKA()) {
			String strAprobariCV = new String(aprobariCV.toString());
			comandaFinala.setNecesarAprobariCV(strAprobariCV.substring(1, strAprobariCV.length() - 1));
		}

		return serializedResult;

	}

	private String serializeComanda(Comanda comanda) {
		JSONObject obj = new JSONObject();

		try {
			obj.put("codClient", comanda.getCodClient());
			obj.put("numeClient", comanda.getNumeClient());
			obj.put("persoanaContact", comanda.getPersoanaContact());
			obj.put("telefon", comanda.getTelefon());
			obj.put("cantarire", comanda.getCantarire());
			obj.put("metodaPlata", comanda.getMetodaPlata());
			obj.put("tipTransport", comanda.getTipTransport());
			obj.put("comandaBlocata", comanda.getComandaBlocata());
			obj.put("nrCmdSap", comanda.getNrCmdSap());
			obj.put("alerteKA", comanda.getAlerteKA());
			obj.put("factRedSeparat", comanda.getFactRedSeparat());
			obj.put("filialaAlternativa", comanda.getFilialaAlternativa());
			obj.put("userSite", comanda.getUserSite());
			obj.put("userSiteMail", comanda.getUserSiteMail());
			obj.put("isValIncModif", comanda.getIsValIncModif());
			obj.put("codJ", comanda.getCodJ());
			obj.put("cnpClient", comanda.getCnpClient());
			obj.put("adresaLivrareGed", comanda.getAdresaLivrareGed());
			obj.put("adresaLivrare", comanda.getAdresaLivrare());
			obj.put("valoareIncasare", comanda.getValoareIncasare());
			obj.put("conditieID", comanda.getConditieID());
			obj.put("canalDistrib", ModificareComanda.isComandaDistrib ? "10" : "20");
			obj.put("necesarAprobariCV", comanda.getNecesarAprobariCV());
			obj.put("valTransportSap", "0");

		} catch (Exception ex) {
			Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
		}

		return obj.toString();
	}

	private String serializeDateLivrare() {

		JSONObject obj = new JSONObject();

		try {

			obj.put("codJudet", DateLivrare.getInstance().getCodJudet());
			obj.put("numeJudet", DateLivrare.getInstance().getNumeJudet());
			obj.put("Oras", DateLivrare.getInstance().getOras());
			obj.put("Strada", DateLivrare.getInstance().getStrada());
			obj.put("persContact", DateLivrare.getInstance().getPersContact());
			obj.put("nrTel", DateLivrare.getInstance().getNrTel());
			obj.put("redSeparat", DateLivrare.getInstance().getRedSeparat());
			obj.put("Cantar", DateLivrare.getInstance().getCantar());
			obj.put("tipPlata", UtilsComenzi.getTipPlataClient(DateLivrare.getInstance().getTipPlata(), CreareComandaGed.tipPlataContract.concat(CreareComanda.tipPlataContract).trim()));
			obj.put("Transport", DateLivrare.getInstance().getTransport());
			obj.put("dateLivrare", DateLivrare.getInstance().getDateLivrare());
			obj.put("termenPlata", DateLivrare.getInstance().getTermenPlata());
			obj.put("obsLivrare", DateLivrare.getInstance().getObsLivrare());
			obj.put("dataLivrare", DateLivrare.getInstance().getDataLivrare());
			obj.put("adrLivrNoua", DateLivrare.getInstance().isAdrLivrNoua());
			obj.put("tipDocInsotitor", DateLivrare.getInstance().getTipDocInsotitor());
			obj.put("obsPlata", DateLivrare.getInstance().getObsPlata());
			obj.put("addrNumber", DateLivrare.getInstance().getAddrNumber());
			obj.put("valoareIncasare", DateLivrare.getInstance().getValoareIncasare());
			obj.put("isValIncModif", DateLivrare.getInstance().isValIncModif());
			obj.put("mail", DateLivrare.getInstance().getMail());
			obj.put("totalComanda", getTotalComanda());
			obj.put("unitLog", DateLivrare.getInstance().getUnitLog());
			obj.put("codAgent", DateLivrare.getInstance().getCodAgent());
			obj.put("idObiectiv", DateLivrare.getInstance().getIdObiectiv());
			obj.put("isAdresaObiectiv", DateLivrare.getInstance().isAdresaObiectiv());
			obj.put("coordonateGps", getCoordAdresa());
			obj.put("tonaj", DateLivrare.getInstance().getTonaj());
			obj.put("prelucrare", DateLivrare.getInstance().getPrelucrare());
			obj.put("clientRaft", DateLivrare.getInstance().isClientRaft());
			obj.put("meserias", DateLivrare.getInstance().getCodMeserias());

			if (isComandaGed())
				obj.put("factRed", "NU");
			else
				obj.put("factRed", codTipReducere.equals("-1") ? DateLivrare.getInstance().getFactRed() : codTipReducere);
			obj.put("macara", DateLivrare.getInstance().isMasinaMacara() ? "X" : " ");

			obj.put("factPaletiSeparat", DateLivrare.getInstance().isFactPaletSeparat());

			obj.put("furnizorMarfa", DateLivrare.getInstance().getFurnizorComanda().getCodFurnizorMarfa());
			obj.put("furnizorProduse", DateLivrare.getInstance().getFurnizorComanda().getCodFurnizorProduse());
			obj.put("isCamionDescoperit", DateLivrare.getInstance().isCamionDescoperit());
			obj.put("programLivrare", DateLivrare.getInstance().getProgramLivrare());
			obj.put("livrareSambata", DateLivrare.getInstance().getLivrareSambata());
			obj.put("codSuperAgent", UserInfo.getInstance().getCodSuperUser());
			obj.put("filialaCLP", DateLivrare.getInstance().getCodFilialaCLP());
			obj.put("numeDelegat", DateLivrare.getInstance().getDelegat().getNume());
			obj.put("ciDelegat", DateLivrare.getInstance().getDelegat().getSerieNumarCI());
			obj.put("autoDelegat", DateLivrare.getInstance().getDelegat().getNrAuto());
			obj.put("refClient", DateLivrare.getInstance().getRefClient());

		} catch (Exception ex) {
			Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
		}

		return obj.toString();

	}

	private String serializeDateLivrareGed() {

		JSONObject jsonAdresa = new JSONObject();

		try {
			jsonAdresa.put("codJudet", DateLivrare.getInstance().getCodJudetD());
			jsonAdresa.put("oras", DateLivrare.getInstance().getOrasD());
			jsonAdresa.put("strada", DateLivrare.getInstance().getAdresaD());

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return jsonAdresa.toString();
	}

	private String getCoordAdresa() {
		if (DateLivrare.getInstance().getCoordonateAdresa() != null)
			return DateLivrare.getInstance().getCoordonateAdresa().latitude + "#" + DateLivrare.getInstance().getCoordonateAdresa().longitude;
		else
			return "0#0";
	}

	private boolean isConditiiAcceptate() {
		boolean isConditiiAcceptate = true;

		for (int i = 0; i < listArticoleComanda.size(); i++) {

			if (listArticoleComanda.get(i).hasConditii()) {
				isConditiiAcceptate = false;
				break;
			}
		}

		return isConditiiAcceptate;

	}

	private boolean isConditiiCmdAccept() {
		if ((UtilsUser.isAgentOrSD() && isComandaGed()) || comandaSelectata.isCmdInstPublica())
			return isCmdGEDOkToSave();
		else
			return isCommandaOkToSave();
	}

	private boolean isCmdGEDOkToSave() {

		for (ArticolComanda articol : listArticoleComanda) {
			if (articol.getConditie())
				return false;

		}

		return true;
	}

	private boolean isCommandaOkToSave() {
		boolean isOkToSave = true;
		String aprobariNecesare = comandaSelectata.getAprobariNecesare();
		String aprobariPrimite = comandaSelectata.getAprobariPrimite();
		String conditiiImpuse = comandaSelectata.getConditiiImpuse();

		for (int i = 0; i < listArticoleComanda.size(); i++) {

			if (aprobariNecesare.contains(listArticoleComanda.get(i).getDepartSintetic())) {
				if (!aprobariPrimite.contains(listArticoleComanda.get(i).getDepartSintetic())) {

					if (conditiiImpuse.contains(listArticoleComanda.get(i).getDepartSintetic()) && !listArticoleComanda.get(i).hasConditii()) {
						continue;
					} else {
						isOkToSave = false;
						break;
					}

				}

			}

		}

		return isOkToSave;
	}

	private void saveCmdStatus(String saveResponse) {
		if (!saveResponse.equals("-1")) {
			try {

				if (!saveResponse.equals("9")) {
					if (alertSD) {
						sendMailAlert(0);
					}
					if (alertDV) {

					}

					if (UserInfo.getInstance().getTipAcces().equals("27"))// alerta
																			// director
																			// ka
					{
						// este nevoie de cel putin o aprobare
						if (globalAlertSDKA.contains("0") || globalAlertDVKA.contains("0")) {
							sendMailAlert(3);
						}
					}

				}

				Toast.makeText(getApplicationContext(), InfoStrings.statusSAPMsg(Integer.parseInt(saveResponse)), Toast.LENGTH_LONG).show();

				clearAllData();
				loadListComenzi();

			} catch (Exception e) {
				Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
			}

		} else {
			Toast.makeText(getApplicationContext(), "Comanda NU a fost salvata!", Toast.LENGTH_LONG).show();
		}
	}

	public void sendMailAlert(int tip) {
		try {

			if (tip == 1) // alerta dv
			{
				mailAlertTipDest = "2";
			}

			if (tip == 3) // alerta director KA
			{
				mailAlertTipDest = "4";
			}

			if (tip == 1 || tip == 3) {
				HashMap<String, String> params = new HashMap<String, String>();

				params.put("ul", UserInfo.getInstance().getUnitLog());
				params.put("depart", UserInfo.getInstance().getCodDepart());
				params.put("dest", mailAlertTipDest); // tip alert
				params.put("agent", UserInfo.getInstance().getNume()); // agent
				params.put("clnt", numeClientVar); //
				params.put("suma", String.format("%.02f", totalComanda)); //

				AsyncTaskWSCall call = new AsyncTaskWSCall(this, "sendMailAlert", params);
				call.getCallResults();

			}

		} catch (Exception ex) {
			Log.e("Error", ex.toString());

		}
	}

	private void clearAllData() {

		DateLivrare.getInstance().resetAll();

		articoleComanda = "";
		numeClientVar = "";
		codClientVar = "";
		selectedCmd = "";
		totalComanda = 0;
		codTipReducere = "-1";
		permitArticoleDistribIP = true;
		CreareComanda.tipPlataContract = " ";
		CreareComandaGed.tipPlataContract = " ";

		ListaArticoleComandaGed.getInstance().clearArticoleComanda();
		ListaArticoleComandaGed.getInstance().deleteObserver(this);

		ListaArticoleModificareComanda.getInstance().clearArticoleComanda();
		ListaArticoleModificareComanda.getInstance().deleteObserver(this);

	}

	public void addListenerDelCmdBtn() {
		stergeComandaBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				if (isUserCV()) {

					if (!isConditiiAcceptate()) {
						Toast.makeText(getApplicationContext(), "Preluati toate conditiile!", Toast.LENGTH_SHORT).show();
						return;
					}

					if (!isCommandaOkToSave()) {
						Toast.makeText(getApplicationContext(), "Comanda nu are toate aprobarile!", Toast.LENGTH_SHORT).show();
						return;
					}

				}

				showConfirmationAlert();

			}
		});

	}

	public void showConfirmationAlert() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Stergeti comanda?").setCancelable(false).setPositiveButton("Da", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {

				idOperatieComanda = 3;
				opereazaComanda();

			}
		}).setNegativeButton("Nu", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();

			}
		}).setTitle("Confirmare").setIcon(R.drawable.warning96);

		AlertDialog alert = builder.create();
		alert.show();

	}

	public void opereazaComanda() {

		try {

			HashMap<String, String> params = new HashMap<String, String>();

			NumberFormat nf3 = new DecimalFormat("00000000");
			String fullCode = nf3.format(Integer.parseInt(UserInfo.getInstance().getCod())).toString();

			params.put("nrCmd", selectedCmd);
			params.put("nrCmdSAP", selectedCmdSAP);
			params.put("tipOp", String.valueOf(idOperatieComanda));
			params.put("codUser", fullCode);

			operatiiComenzi.opereazaComanda(params);

		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
		}
	}

	private void loadListComenzi() {

		HashMap<String, String> params = new HashMap<String, String>();

		params.put("filiala", UserInfo.getInstance().getUnitLog());
		params.put("codUser", UserInfo.getInstance().getCod());
		params.put("tipCmd", "1");
		params.put("depart", UserInfo.getInstance().getCodDepart());
		params.put("tipUser", UserInfo.getInstance().getTipUser());
		params.put("tipUserSap", UserInfo.getInstance().getTipUserSap());

		displayCmdDetails(false);
		operatiiComenzi.getListComenzi(params);

	}

	private void getArticoleComanda() {

		HashMap<String, String> params = new HashMap<String, String>();

		params.put("nrCmd", selectedCmd);
		params.put("afisCond", "1");
		params.put("tipUser", UserInfo.getInstance().getTipUser());

		operatiiComenzi.getArticoleComandaJSON(params);

	}

	private void afiseazaArticoleComanda(BeanArticoleAfisare articoleComanda) {

		DateLivrareAfisare dateLivrare = articoleComanda.getDateLivrare();

		DateLivrare.getInstance().setDateLivrareAfisare(dateLivrare);
		DateLivrare.getInstance().setClientBlocat(articoleComanda.getDateLivrare().isClientBlocat());
		

		listArticoleComanda = articoleComanda.getListArticole();

		BeanConditii conditiiComanda = articoleComanda.getConditii();
		afisConditiiHeader(conditiiComanda.getHeader());

		conditieID = String.valueOf(conditiiComanda.getHeader().getId());

		conditiiComandaArticole = conditiiComanda.getArticole();

		if (isUserCV() || isComandaGed()) {
			ListaArticoleComandaGed.getInstance().setListaArticole(listArticoleComanda);
			ListaArticoleComandaGed.getInstance().addObserver(this);
			getPretTransport();
		} else {
			ListaArticoleModificareComanda.getInstance().setListaArticole(listArticoleComanda);
			ListaArticoleModificareComanda.getInstance().addObserver(this);
		}

		ListaArticoleComandaGed.getInstance().setConditiiArticole(conditiiComandaArticole);

		adapterArticole = new ArticolModificareAdapter(this, listArticoleComanda, conditiiComandaArticole, comandaSelectata);

		adapterArticole.setArticolModificareListener(this);
		listViewArticole.setAdapter(adapterArticole);

		textJudet.setText(dateLivrare.getNumeJudet());
		textOras.setText(dateLivrare.getOras());
		textAdrLivr.setText(dateLivrare.getDateLivrare());
		textPersContact.setText(dateLivrare.getPersContact());
		textTelefon.setText(dateLivrare.getNrTel());
		textCantar.setText(UtilsGeneral.getTipCantarire(dateLivrare.getCantar()));
		textTipPlata.setText(UtilsGeneral.getDescTipPlata(dateLivrare.getTipPlata()));
		textTransport.setText(UtilsGeneral.getDescTipTransport(dateLivrare.getTransport()));
		textFactRed.setText(UtilsGeneral.getTipReducere(dateLivrare.getRedSeparat()));

		tipClientVar = dateLivrare.getTipPersClient();

		calculPondereB();

		if (listArticoleComanda.get(0).getUnitLogAlt().equals("NN10")) {
			filialaAlternativaM = UserInfo.getInstance().getUnitLog();
		} else {
			filialaAlternativaM = listArticoleComanda.get(0).getUnitLogAlt();
		}

		if (listArticoleComanda.get(0).getUnitLogAlt().contains("BV9")) {
			layoutBV90.setVisibility(View.VISIBLE);
		} else {
			layoutBV90.setVisibility(View.GONE);
		}

		displayCmdDetails(true);

		adapterArticole.notifyDataSetChanged();

		calculValTransport(listArticoleComanda);
		calculProcente(listArticoleComanda);
	}

	private void getPretTransport() {

		for (int i = 0; i < listArticoleComanda.size(); i++) {
			if (listArticoleComanda.get(i).getNumeArticol().toLowerCase().contains("servicii")
					&& listArticoleComanda.get(i).getNumeArticol().toLowerCase().contains("transport")) {
				DateLivrare.getInstance().setValTransport(listArticoleComanda.get(i).getPretUnit());
				break;
			}

		}

	}

	private void afisConditiiHeader(BeanConditiiHeader conditiiHeader) {

		layoutConditiiHeader.setVisibility(View.GONE);
		boolean isHeaderVisible = false;
		NumberFormat nf2 = new DecimalFormat("#0.00");

		if (conditiiHeader.getConditiiCalit() > 0) {
			textCondProcB.setText(nf2.format(conditiiHeader.getConditiiCalit()));
			layoutCondProcB.setVisibility(View.VISIBLE);
			isHeaderVisible = true;
		} else {
			layoutCondProcB.setVisibility(View.GONE);
		}

		if (conditiiHeader.getNrFact() > 0) {
			textCondNrFacturi.setText(getDescNrFacturi(conditiiHeader.getNrFact()));
			layoutCondNrFact.setVisibility(View.VISIBLE);
			isHeaderVisible = true;
		} else {
			layoutCondNrFact.setVisibility(View.GONE);
		}

		if (!conditiiHeader.getObservatii().equals("null")) {
			textCondComentarii.setText(conditiiHeader.getObservatii());
			layoutCondObs.setVisibility(View.VISIBLE);
			isHeaderVisible = true;
		} else {
			layoutCondObs.setVisibility(View.GONE);
		}

		if (isHeaderVisible)
			layoutConditiiHeader.setVisibility(View.VISIBLE);

	}

	private String getDescNrFacturi(int codNrFacturi) {
		String descNrFacturi = "";
		switch (codNrFacturi) {
		case 1:
			descNrFacturi = "1 fact (red in pret)";
			codTipReducere = " ";
			break;
		case 2:
			descNrFacturi = "2 facturi";
			codTipReducere = "X";
			break;
		case 3:
			descNrFacturi = "1 fact (red separat)";
			codTipReducere = "R";
			break;

		}

		return descNrFacturi;

	}

	private void afiseazaListaComenzi(List<BeanComandaCreata> listComenzi) {

		if (listComenzi.size() > 0) {
			ComandaModificareAdapter adapter = new ComandaModificareAdapter(listComenzi, this);
			spinnerComenzi.setAdapter(adapter);
			displayComenziControl(true);

		} else {
			displayComenziControl(false);
		}

	}

	private void displayComenziControl(boolean isVisible) {
		if (isVisible) {
			spinnerComenzi.setVisibility(View.VISIBLE);
			salveazaComandaBtn.setVisibility(View.VISIBLE);
			stergeComandaBtn.setVisibility(View.VISIBLE);

			if (UtilsUser.isUserKA())
				aprobareBtn.setVisibility(View.VISIBLE);

		} else {
			spinnerComenzi.setVisibility(View.INVISIBLE);
			salveazaComandaBtn.setVisibility(View.INVISIBLE);
			stergeComandaBtn.setVisibility(View.INVISIBLE);
			aprobareBtn.setVisibility(View.INVISIBLE);

		}

	}

	public void addListenerListArtModif() {
		listViewArticole.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

				listViewSelPos = position;
				return false;

			}
		});

		listViewArticole.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

				listViewSelPos = position;

				if ((listViewArticole.getFirstVisiblePosition() == listViewSelPos) || (listViewArticole.getFirstVisiblePosition() + 1 == listViewSelPos)) {
					listViewArticole.smoothScrollToPositionFromTop(listViewSelPos - 1, 0);
				}

				if ((listViewArticole.getLastVisiblePosition() == listViewSelPos) || (listViewArticole.getLastVisiblePosition() - 1 == listViewSelPos)) {
					listViewArticole.smoothScrollToPositionFromTop(listViewArticole.getFirstVisiblePosition() + 1, 0);
				}

			}
		});

	}

	private void checkStaticVars() {

		String locLang = getBaseContext().getResources().getConfiguration().locale.getLanguage();

		if (!locLang.toLowerCase(Locale.getDefault()).equals("en")) {

			String languageToLoad = "en";
			Locale locale = new Locale(languageToLoad);
			Locale.setDefault(locale);
			Configuration config = new Configuration();
			config.locale = locale;
			getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
		}

		// restart app la idle
		if (UserInfo.getInstance().getCod().equals("")) {

			Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
		}

	}

	public double calculTaxaVerde() {

		double totalTaxaVerde = 0;

		try {

			String[] tokVal;
			Double valCondPret = 0.0;

			if (ModificareComanda.isComandaDistrib) {

				prepareArtForDelivery();

				for (int i = 0; i < objArticol.length; i++) {
					if (objArticol[i] != null) {
						if (!objArticol[i].getCodArticol().equals("-1")) {

							if (objArticol[i].getInfoArticol().contains(";")) {
								String[] tokInfoArt = objArticol[i].getInfoArticol().split(";");

								for (int ii = 0; ii < tokInfoArt.length; ii++) {
									tokVal = tokInfoArt[ii].split(":");
									valCondPret = Double.valueOf(tokVal[1].replace(',', '.').trim());
									if (valCondPret != 0) {
										if (tokVal[0].toUpperCase(Locale.getDefault()).contains("VERDE")) {
											totalTaxaVerde += valCondPret;
										}

									}

								}// for
							}// if

						}
					}
				}// sf. for

				totalComanda = getTotalComanda();
				totalComanda += totalTaxaVerde;

				textTotalCmd.setText(String.format("%.02f", totalComanda));

				if (totalTaxaVerde > 0) {
					layoutTaxaVerde.setVisibility(View.VISIBLE);
					textTaxaVerde.setText(String.valueOf(totalTaxaVerde) + ")");
				}

			}// sf. if

		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
		}

		return totalTaxaVerde;

	}

	public void onBackPressed() {

		UserInfo.getInstance().setParentScreen("");
		clearAllData();

		Intent nextScreen = new Intent(getApplicationContext(), MainMenu.class);
		startActivity(nextScreen);

		finish();
		return;
	}

	public void onTaskComplete(String methodName, Object result) {

		if (methodName.equals("sendMailAlert")) {
			clearAllData();
		}

	}

	private void displayCmdDetails(boolean isVisible) {
		if (isVisible) {
			layoutDetaliiCmd.setVisibility(View.VISIBLE);
			listViewArticole.setVisibility(View.VISIBLE);
			stergeComandaBtn.setVisibility(View.VISIBLE);
			salveazaComandaBtn.setVisibility(View.VISIBLE);
		} else {
			layoutDetaliiCmd.setVisibility(View.INVISIBLE);
			listViewArticole.setVisibility(View.INVISIBLE);
			stergeComandaBtn.setVisibility(View.INVISIBLE);
			salveazaComandaBtn.setVisibility(View.INVISIBLE);
		}

	}

	private void afiseazaArticoleComanda(BeanComandaCreata comanda) {

		textTipPlata.setText("");
		textAdrLivr.setText("");
		textPersContact.setText("");
		textTelefon.setText("");
		textCantar.setText("");
		textTransport.setText("");

		textTotalCmd.setText("0.00");

		comandaSelectata = comanda;

		unitLogComanda = comandaSelectata.getFiliala();

		selectedCmd = comanda.getId();

		selectedCmdSAP = comanda.getCmdSap();

		totalComanda = getTotalComanda();

		textTotalCmd.setText(String.format("%.02f", totalComanda));

		selectedClientCode = getCodClient(comanda);

		selectedUnitLog = comanda.getFiliala();

		if (selectedUnitLog.substring(2, 3).equals("1"))
			isComandaDistrib = true;
		else
			isComandaDistrib = false;

		if (!isComandaDistrib)
			DateLivrare.getInstance().setFilialeExtraMathaus("");

		codClientVar = comanda.getCodClient();
		numeClientVar = comanda.getNumeClient();

		if (!comanda.getDocInsotitor().equals("-1")) {
			DateLivrare.getInstance().setTipDocInsotitor(comanda.getDocInsotitor());
		} else {
			DateLivrare.getInstance().setTipDocInsotitor("1");
		}

		getArticoleComanda();

		textTipPlata.setVisibility(View.VISIBLE);
		textAdrLivr.setVisibility(View.VISIBLE);
		textPersContact.setVisibility(View.VISIBLE);
		textTelefon.setVisibility(View.VISIBLE);
		textCantar.setVisibility(View.VISIBLE);
		textTransport.setVisibility(View.VISIBLE);

	}

	private String getCodClient(BeanComandaCreata comanda) {

		return UtilsFormatting.isNumeric(comanda.getCodClient()) ? comanda.getCodClient() : comanda.getCodClientGenericGed();

	}

	private void addListenerSpinnerComenzi() {
		spinnerComenzi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				displayCmdDetails(false);
				afiseazaArticoleComanda(listComenzi.get(position));

			}

			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}

	@SuppressWarnings("unchecked")
	public void operationComenziComplete(EnumComenziDAO methodName, Object result) {
		switch (methodName) {
		case GET_LIST_COMENZI:
			this.listComenzi = (List<BeanComandaCreata>) result;
			afiseazaListaComenzi(this.listComenzi);
			break;
		case GET_ARTICOLE_COMANDA_JSON:
			afiseazaArticoleComanda(operatiiComenzi.deserializeArticoleComanda((String) result));
			break;
		case OPERATIE_COMANDA:
			loadListComenzi();
			break;
		case SALVEAZA_COMANDA_DISTRIB:
			saveCmdStatus((String) result);
			break;
		case GET_COST_MACARA:
			afiseazaPretMacaraDialog((String) result);
			break;
		default:
			break;
		}

	}

	public void articolModificat() {
		calculPondereB();
	}

	private void calculValTransport(ArrayList<ArticolComanda> listArticole) {

		if (UtilsUser.isAgentOrSDorKA() || UtilsUser.isConsWood() || comandaSelectata.isCmdInstPublica() || UtilsUser.isOIVPD()) {
			return;
		}

		NumberFormat nf3 = NumberFormat.getInstance(new Locale("en", "US"));
		nf3.setMinimumFractionDigits(2);
		nf3.setMaximumFractionDigits(2);
		nf3.setGroupingUsed(false);

		valTransportSAP = Double.valueOf(nf3.format(UtilsComenziGed.getValoareTransportSap(listArticole)));
		valTransport = Double.valueOf(nf3.format(UtilsComenziGed.getValoareTransportComanda(listArticole)));

		if (valTransport < valTransportSAP)
			valTransport = valTransportSAP;

		if (!DateLivrare.getInstance().getTransport().equals("TCLI")) {
			UtilsComenziGed.setValoareArticolTransport(listArticole, valTransport);
			adapterArticole.notifyDataSetChanged();
		}

		if (DateLivrare.getInstance().getTransport().equals("TRAP") || DateLivrare.getInstance().getTransport().equals("TERT")) {
			valTranspBtn.setVisibility(View.VISIBLE);
			valTranspBtn.setText("Transp: " + nf3.format(valTransport));
		} else {
			valTranspBtn.setVisibility(View.INVISIBLE);
			valTransport = 0;
		}

	}

	public boolean esteModificatPretulGed(ArrayList<ArticolComanda> listArticole) {
		boolean esteModificat = false;

		ArticolComanda articol = null;

		for (int i = 0; i < listArticole.size(); i++) {

			articol = listArticole.get(i);

			if (articol.getProcent() > 0) {
				esteModificat = true;
				break;
			}

		}

		if (!esteModificat) {
			AlgoritmComandaGed algoritm = new AlgoritmComandaGed();
			algoritm.inlaturaToateAlertelePret(listArticoleComanda);
		}

		return esteModificat;
	}

	public static double round(double value, int places) {

		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, BigDecimal.ROUND_HALF_UP);
		return bd.doubleValue();
	}

	private void adaugaPalet(ArticolPalet articolPalet, EnumPaleti status) {

		String depozitPalet = HelperCostDescarcare.getDepozitPalet(listArticoleComanda, articolPalet.getCodArticol());

		ArticolComanda articol = HelperCostDescarcare.getArticolPalet(articolPalet, depozitPalet);

		listArticoleComanda.add(articol);
		adapterArticole.setListArticole(listArticoleComanda);
		adapterArticole.notifyDataSetChanged();

		costDescarcare.getArticoleDescarcare().get(0).setCantitate(costDescarcare.getArticoleDescarcare().get(0).getCantitate() + articol.getCantitate());

	}

	private void respingePalet() {
		if (costDescarcare.getSePermite() && costDescarcare.getValoareDescarcare() > 0 && DateLivrare.getInstance().getTransport().equalsIgnoreCase("TRAP")) {

			CostMacaraDialog macaraDialog = new CostMacaraDialog(this, costDescarcare, isComandaGed());
			macaraDialog.setCostMacaraListener(this);
			macaraDialog.show();

		} else {
			if (!costDescarcare.getSePermite())
				DateLivrare.getInstance().setMasinaMacara(false);

		}
	}

	private void calculProcente(ArrayList<ArticolComanda> listArticole) {
		if (UtilsUser.isAgentOrSDorKA() || UtilsUser.isConsWood() || comandaSelectata.isCmdInstPublica()) {
			valTranspBtn.setVisibility(View.GONE);
			return;
		}

		textAlertaMarja.setVisibility(View.GONE);

		NumberFormat nf = NumberFormat.getInstance();
		nf.setMinimumFractionDigits(2);
		nf.setMaximumFractionDigits(2);

		AlgoritmComandaGed algoritm = new AlgoritmComandaGed();
		algoritm.calculProcenteComanda(listArticole, esteModificatPretulGed(listArticole));

		double formulaTotalAdaosClientCorectat = algoritm.getTotalAdaosClientCorectat();
		double valTransportAlgoritm = valTransport - valTransportSAP;

		if (DateLivrare.getInstance().getTransport().equals("TRAP") || DateLivrare.getInstance().getTransport().equals("TERT")) {
			formulaTotalAdaosClientCorectat = algoritm.getTotalAdaosClientCorectat() + valTransportAlgoritm;
		}

		if (formulaTotalAdaosClientCorectat < algoritm.getTotalAdaosMinimReper()) {

			double deficitComanda = (algoritm.getTotalAdaosMinimReper() - formulaTotalAdaosClientCorectat);

			textAlertaMarja.setText("Cresteti val. cmd. cu minim " + nf.format(deficitComanda) + " RON");
			textAlertaMarja.setVisibility(View.VISIBLE);
			algoritm.redistribuireMarja(listArticole, valTransportAlgoritm);

		} else {
			algoritm.schimbaAlertaArticol(listArticole);
			textAlertaMarja.setText("");
		}
	}

	public void update(Observable observable, Object data) {
		if (observable instanceof ListaArticoleModificareComanda) {
			listArticoleComanda = ListaArticoleModificareComanda.getInstance().getListArticoleComanda();
			conditiiComandaArticole = ListaArticoleModificareComanda.getInstance().getConditiiArticole();
			adapterArticole.setListArticole(listArticoleComanda);
			adapterArticole.notifyDataSetChanged();

		}

		if (observable instanceof ListaArticoleComandaGed) {
			listArticoleComanda = ListaArticoleComandaGed.getInstance().getListArticoleComanda();
			conditiiComandaArticole = ListaArticoleComandaGed.getInstance().getConditiiArticole();
			adapterArticole.setListArticole(listArticoleComanda);
			adapterArticole.notifyDataSetChanged();
			calculValTransport(listArticoleComanda);
			calculProcente(listArticoleComanda);
		}

	}

	@Override
	public void acceptaCostMacara(boolean acceptaCost, double valoareCost) {
		trateazaPretMacara(acceptaCost, valoareCost);

	}

	@Override
	public void articolSters() {
		calculValTransport(listArticoleComanda);
		calculProcente(listArticoleComanda);

	}

	@Override
	public void paletiStatus(EnumPaleti status, ArticolPalet palet) {
		switch (status) {
		case ACCEPTA:
			adaugaPalet(palet, status);
			break;
		case RESPINGE:
		case FINALIZEAZA:
			respingePalet();
			break;
		default:
			break;
		}

	}
}
