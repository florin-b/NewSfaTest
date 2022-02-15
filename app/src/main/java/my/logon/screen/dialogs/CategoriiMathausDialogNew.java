package my.logon.screen.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import my.logon.screen.R;
import my.logon.screen.adapters.AdapterCategoriiMathaus;
import my.logon.screen.adapters.ArticolMathausAdapter;
import my.logon.screen.beans.ArticolMathaus;
import my.logon.screen.beans.CategorieMathaus;
import my.logon.screen.beans.RezultatArtMathaus;
import my.logon.screen.enums.EnumOperatiiMathaus;
import my.logon.screen.listeners.AdapterMathausListener;
import my.logon.screen.listeners.ArticolMathausListener;
import my.logon.screen.listeners.OperatiiMathausListener;
import my.logon.screen.model.Constants;
import my.logon.screen.model.OperatiiMathaus;
import my.logon.screen.model.UserInfo;
import my.logon.screen.screens.CreareComanda;

public class CategoriiMathausDialogNew extends Dialog implements OperatiiMathausListener, AdapterMathausListener {

	private Context context;
	private Button okButton;
	private OperatiiMathaus opMathaus;

	private Spinner spinnerCategorii;
	private List<CategorieMathaus> listCategorii;

	private GridView gridView;
	private CategorieMathaus selectedCat;
	private ArticolMathausListener listener;
	private LinearLayout layoutSubcategorii;
	private TreeSet<String> setParinti;
	private Button cautaArticoleBtn;
	private RadioButton radioCod, radioNume;
	private EditText textCodArticol;
	private LinearLayout layoutPagination;
	private TextView textNrPagini;
	private int nrPagini;
	private int paginaCurenta;
	private ImageButton btnFirst, btnPrev, btnNext, btnLast;
	private String categorieCurenta;
	private RadioGroup radioTipArt;

	private enum AfisArtMathaus {
		CATEGORIE, CAUTARE
	};

	private AfisArtMathaus tipAfisArticole;

	private enum TipArticolAfis {
		SITE, ND
	};

	private TipArticolAfis tipArticolAfis;

	public CategoriiMathausDialogNew(Context context) {
		super(context);
		this.context = context;

		setContentView(R.layout.dialog_categorii_mathaus_new);
		setTitle("Categorii produse");
		setCancelable(true);
		opMathaus = new OperatiiMathaus(context);
		opMathaus.setOperatiiMathausListener(this);

		paginaCurenta = 1;

		setUpLayout();

	}

	private void setUpLayout() {

		opMathaus.getCategorii(new HashMap<String, String>());
		setParinti = new TreeSet<String>();

		radioTipArt = (RadioGroup) findViewById(R.id.radio_tip_art);
		setRadioTipArtListener();
		tipArticolAfis = TipArticolAfis.SITE;

		layoutSubcategorii = (LinearLayout) findViewById(R.id.layoutSubcategorii);

		layoutPagination = (LinearLayout) findViewById(R.id.layoutPagination);
		textNrPagini = (TextView) findViewById(R.id.textNrPagini);

		btnFirst = (ImageButton) findViewById(R.id.btnFirst);
		btnPrev = (ImageButton) findViewById(R.id.btnPrev);
		btnNext = (ImageButton) findViewById(R.id.btnNext);
		btnLast = (ImageButton) findViewById(R.id.btnLast);

		setListenerBtnFirst();
		setListenerBtnPrev();
		setListenerBtnNext();
		setListenerBtnLast();

		spinnerCategorii = (Spinner) findViewById(R.id.spinnerCategorii);
		gridView = (GridView) findViewById(R.id.gridItems);
		int parentHeight = (int) (context.getResources().getDisplayMetrics().heightPixels * 0.77);

		gridView.getLayoutParams().height = parentHeight;
		gridView.requestLayout();

		okButton = (Button) findViewById(R.id.btnOk);
		addOkButtonListener();

		cautaArticoleBtn = (Button) findViewById(R.id.btnCauta);
		addCautaBtnListener();

		radioCod = (RadioButton) findViewById(R.id.radio_cod);
		radioNume = (RadioButton) findViewById(R.id.radio_nume);

		setListenerRadio(radioCod);
		setListenerRadio(radioNume);
		textCodArticol = (EditText) findViewById(R.id.textCodArticol);
		textCodArticol.setHint("Introduceti cod articol");
	}

	private void setRadioTipArtListener() {

		radioTipArt.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {

				paginaCurenta = 1;
				clearCautaView();

				switch (checkedId) {
				case R.id.radio_site:
					tipArticolAfis = TipArticolAfis.SITE;
					getArticoleSite();
					break;
				case R.id.radio_nd:
					tipArticolAfis = TipArticolAfis.ND;
					getArticoleND();
					break;
				}

			}

		});

	}

	private void setListenerBtnFirst() {
		btnFirst.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (paginaCurenta > 1) {
					paginaCurenta = 1;

					if (tipAfisArticole == AfisArtMathaus.CATEGORIE)
						getArticole(categorieCurenta, paginaCurenta);
					else
						cautaArticoleMathaus();
				}

			}
		});
	}

	private void setListenerBtnPrev() {
		btnPrev.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (paginaCurenta > 1) {
					paginaCurenta--;

					if (tipAfisArticole == AfisArtMathaus.CATEGORIE)
						getArticole(categorieCurenta, paginaCurenta);
					else
						cautaArticoleMathaus();
				}

			}
		});
	}

	private void setListenerBtnNext() {
		btnNext.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (paginaCurenta < nrPagini) {
					paginaCurenta++;

					if (tipAfisArticole == AfisArtMathaus.CATEGORIE)
						getArticole(categorieCurenta, paginaCurenta);
					else
						cautaArticoleMathaus();
				}

			}
		});
	}

	private void setListenerBtnLast() {
		btnLast.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (paginaCurenta < nrPagini) {
					paginaCurenta = nrPagini;

					if (tipAfisArticole == AfisArtMathaus.CATEGORIE)
						getArticole(categorieCurenta, paginaCurenta);
					else
						cautaArticoleMathaus();
				}

			}
		});
	}

	private void addOkButtonListener() {
		okButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dismiss();

			}
		});

	}

	private void setListenerRadio(final RadioButton radioButton) {
		radioButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {

					if (radioButton.getText().toString().equals("Cod"))
						textCodArticol.setHint("Introduceti cod articol");
					else
						textCodArticol.setHint("Introduceti nume articol");

					textCodArticol.setText("");
					gridView.setAdapter(null);
				}

			}
		});
	}

	private void addCautaBtnListener() {
		cautaArticoleBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				tipAfisArticole = AfisArtMathaus.CAUTARE;
				paginaCurenta = 1;
				cautaArticoleMathaus();

			}

		});
	}

	private void cautaArticoleMathaus() {

		spinnerCategorii.setSelection(0);
		layoutSubcategorii.removeAllViews();

		gridView.setAdapter(null);
		String tipCautare;

		if (radioCod.isChecked())
			tipCautare = "c";
		else
			tipCautare = "n";

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("codArticol", textCodArticol.getText().toString().trim().toLowerCase());
		params.put("tipCautare", tipCautare);
		params.put("filiala", CreareComanda.filialaLivrareMathaus);
		params.put("depart", UserInfo.getInstance().getCodDepart());
		params.put("pagina", String.valueOf(this.paginaCurenta));

		if (this.categorieCurenta != null && this.categorieCurenta.equals("0")) // ND-uri neclasificate
			opMathaus.cautaArticoleLocal(params);
		else
			opMathaus.cautaArticole(params);

	}

	private void afisCategorii(String result) {

		listCategorii = opMathaus.deserializeCategorii(result);

		List<CategorieMathaus> categNodes = new ArrayList<CategorieMathaus>();

		for (CategorieMathaus cat : listCategorii) {
			if (cat.getCodParinte().trim().isEmpty()) {

				CategorieMathaus categ = new CategorieMathaus();
				categ.setNume(cat.getNume());
				categ.setCod(cat.getCod());
				categ.setCodParinte(cat.getCodParinte());
				categ.setCodHybris(cat.getCodHybris());
				categNodes.add(categ);

			}
		}

		Collections.sort(categNodes);

		CategorieMathaus categ = new CategorieMathaus();
		categ.setNume("Selectati o categorie");
		categ.setCod("-1");
		categ.setCodParinte("-1");
		categNodes.add(0, categ);

		AdapterCategoriiMathaus adapter = new AdapterCategoriiMathaus(context, categNodes);
		spinnerCategorii.setAdapter(adapter);
		setCategoriiListener(spinnerCategorii);

	}

	private List<CategorieMathaus> getListCategorii(String codParinte) {
		List<CategorieMathaus> listSubcategorii = new ArrayList<CategorieMathaus>();

		for (CategorieMathaus cat : listCategorii) {

			if (cat.getCodParinte().equals(codParinte))
				listSubcategorii.add(cat);

		}

		CategorieMathaus categ = new CategorieMathaus();
		categ.setNume("Selectati o categorie");
		categ.setCod("-1");
		categ.setCodParinte("-1");
		listSubcategorii.add(0, categ);

		return listSubcategorii;
	}

	private void setCategoriiListener(final Spinner spinnerCategorii) {
		spinnerCategorii.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

				if (position > 0) {
					selectedCat = (CategorieMathaus) spinnerCategorii.getSelectedItem();
					setTitle(selectedCat.getNume());
					addSpinner(selectedCat);

				} else {
					selectedCat = new CategorieMathaus();

				}

			}

			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
	}

	private void clearCautaView() {

		textCodArticol.setText("");
		radioCod.setChecked(true);
	}

	private void addSpinner(CategorieMathaus categorie) {

		setParinti.clear();

		List<CategorieMathaus> listCategorii = getListCategorii(categorie.getCod());

		Spinner spinner = new Spinner(context);
		spinner.setContentDescription(categorie.getCod());

		AdapterCategoriiMathaus adapter = new AdapterCategoriiMathaus(context, listCategorii);
		spinner.setAdapter(adapter);

		if (categorie.getCodParinte().trim().isEmpty())
			layoutSubcategorii.removeAllViews();

		setParinti.add(categorie.getCod());
		getParinti(categorie);

		int numChild = layoutSubcategorii.getChildCount();
		List<View> listSpinners = new ArrayList<View>();

		for (int i = 0; i < numChild; i++) {

			View childView = layoutSubcategorii.getChildAt(i);

			boolean isParent = false;
			for (String parent : setParinti) {

				if (parent.equals(childView.getContentDescription())) {
					isParent = true;
				}

			}

			if (!isParent)
				listSpinners.add(childView);

		}

		for (View childSpinner : listSpinners)
			layoutSubcategorii.removeView(childSpinner);

		if (listCategorii.size() > 1) {
			setCategoriiListener(spinner);
			radioTipArt.setVisibility(View.INVISIBLE);
			layoutSubcategorii.addView(spinner);
		}

		if (listCategorii.size() == 1) {
			this.categorieCurenta = categorie.getCodHybris();
			this.paginaCurenta = 1;
			clearCautaView();
			tipAfisArticole = AfisArtMathaus.CATEGORIE;

			/*
			 * if (!this.categorieCurenta.equals("0")) // diverse
			 * radioTipArt.setVisibility(View.VISIBLE); else
			 * radioTipArt.setVisibility(View.INVISIBLE);
			 */

			getArticole(categorie.getCodHybris(), paginaCurenta);

		}

	}

	private void getArticoleSite() {
		getArticole(categorieCurenta, paginaCurenta);
	}

	private void getArticoleND() {
		getArticole(categorieCurenta, paginaCurenta);
	}

	private void getParinti(CategorieMathaus categorie) {

		if (categorie.getCodParinte().trim().isEmpty()) {
			setParinti.add(categorie.getCod());
		} else

			for (CategorieMathaus cat : listCategorii) {
				if (cat.getCod().equals(categorie.getCodParinte())) {
					setParinti.add(cat.getCod());
					getParinti(cat);
				}

			}

	}

	private void getArticole(String codCategorie, int nrPagina) {

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("codCategorie", codCategorie);
		params.put("filiala", UserInfo.getInstance().getUnitLog());
		params.put("depart", UserInfo.getInstance().getCodDepart());
		params.put("pagina", String.valueOf(nrPagina));
		params.put("tipArticol", tipArticolAfis.toString());
		opMathaus.getArticole(params);

	}

	private void afisArticole(String result) {

		RezultatArtMathaus rezultat = opMathaus.deserializeArticole(result);

		if (Integer.parseInt(rezultat.getNrTotalArticole()) <= Constants.NR_ARTICOLE_MATHAUS_PAGINA)
			setPaginationVisibility(false);
		else {
			setPaginationVisibility(true);

			nrPagini = Integer.parseInt(rezultat.getNrTotalArticole()) / Constants.NR_ARTICOLE_MATHAUS_PAGINA;

			if (Integer.parseInt(rezultat.getNrTotalArticole()) % Constants.NR_ARTICOLE_MATHAUS_PAGINA != 0)
				nrPagini++;

			textNrPagini.setText("Pagina " + paginaCurenta + "/" + nrPagini);
		}

		ArticolMathausAdapter adapter = new ArticolMathausAdapter(context, rezultat.getListArticole());
		adapter.setArticolMathausListener(CategoriiMathausDialogNew.this);
		gridView.setAdapter(adapter);

	}

	private void setPaginationVisibility(boolean isVisible) {

		if (isVisible) {
			btnFirst.setVisibility(View.VISIBLE);
			btnPrev.setVisibility(View.VISIBLE);
			textNrPagini.setVisibility(View.VISIBLE);
			btnNext.setVisibility(View.VISIBLE);
			btnLast.setVisibility(View.VISIBLE);
		} else {
			btnFirst.setVisibility(View.INVISIBLE);
			btnPrev.setVisibility(View.INVISIBLE);
			textNrPagini.setVisibility(View.INVISIBLE);
			btnNext.setVisibility(View.INVISIBLE);
			btnLast.setVisibility(View.INVISIBLE);
		}
	}

	public void setArticolMathausListener(ArticolMathausListener listener) {
		this.listener = listener;
	}

	@Override
	public void operationComplete(EnumOperatiiMathaus methodName, Object result) {
		switch (methodName) {
		case GET_CATEGORII:
			afisCategorii((String) result);
			break;
		case GET_ARTICOLE:
		case CAUTA_ARTICOLE:
		case CAUTA_ARTICOLE_LOCAL:
			afisArticole((String) result);
			break;
		default:
			break;
		}

	}

	@Override
	public void articolMathausSelected(ArticolMathaus articol) {
		if (listener != null)
			listener.articolMathausSelected(articol);

		hide();

	}

}
