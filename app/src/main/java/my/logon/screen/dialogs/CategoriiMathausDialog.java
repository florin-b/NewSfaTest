package my.logon.screen.dialogs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import my.logon.screen.listeners.AdapterMathausListener;
import my.logon.screen.listeners.ArticolMathausListener;
import my.logon.screen.listeners.OperatiiMathausListener;
import my.logon.screen.R;
import my.logon.screen.model.OperatiiMathaus;
import my.logon.screen.model.UserInfo;
import my.logon.screen.adapters.AdapterCategorii;
import my.logon.screen.adapters.ArticolMathausAdapter;
import my.logon.screen.adapters.CategoriiMathausAdapter;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.GridView;
import android.widget.Spinner;
import my.logon.screen.beans.ArticolMathaus;
import my.logon.screen.beans.CategorieMathaus;
import my.logon.screen.enums.EnumOperatiiMathaus;

public class CategoriiMathausDialog extends Dialog implements OperatiiMathausListener, AdapterMathausListener {

	private Context context;
	private Button okButton;
	private OperatiiMathaus opMathaus;
	private ExpandableListView expandableCategorii;

	private Spinner spinnerCategorii;
	private List<CategorieMathaus> listCategorii;

	private GridView gridView;
	private String selectedCat;
	private String selectedNode;
	private String selectedChild;
	private ArticolMathausListener listener;

	public CategoriiMathausDialog(Context context) {
		super(context);
		this.context = context;

		setContentView(R.layout.dialog_categorii_mathaus);
		setTitle("Categorii produse");
		setCancelable(true);
		opMathaus = new OperatiiMathaus(context);
		opMathaus.setOperatiiMathausListener(this);

		setUpLayout();

	}

	private void setUpLayout() {

		opMathaus.getCategorii(new HashMap<String, String>());

		expandableCategorii = (ExpandableListView) findViewById(R.id.list_view);
		expandableCategorii.setOnGroupExpandListener(new OnGroupExpandListener() {
			int previousItem = -1;

			@Override
			public void onGroupExpand(int groupPosition) {
				if (groupPosition != previousItem)
					expandableCategorii.collapseGroup(previousItem);
				previousItem = groupPosition;
			}
		});

		spinnerCategorii = (Spinner) findViewById(R.id.spinnerCategorii);
		gridView = (GridView) findViewById(R.id.gridItems);
		int parentHeight = (int) (context.getResources().getDisplayMetrics().heightPixels * 0.77);

		gridView.getLayoutParams().height = parentHeight;
		gridView.requestLayout();

		okButton = (Button) findViewById(R.id.btnOk);
		addOkButtonListener();
	}

	private void addOkButtonListener() {
		okButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dismiss();
				

			}
		});

	}

	private void afisCategorii(String result) {

		listCategorii = opMathaus.deserializeCategorii(result);
		ArrayList<String> parentNodes = new ArrayList<String>();
		HashMap<String, List<String>> listDataChild1 = new HashMap<String, List<String>>();

		List<CategorieMathaus> categNodes = new ArrayList<CategorieMathaus>();

		for (CategorieMathaus cat : listCategorii) {
			if (cat.getCodParinte().trim().isEmpty()) {

				CategorieMathaus categ = new CategorieMathaus();
				categ.setNume(cat.getNume());
				categ.setCod(cat.getCod());
				categNodes.add(categ);

			}
		}

		for (CategorieMathaus parent : categNodes) {
			List<String> listChild1 = new ArrayList<String>();
			String catName = "";

			for (CategorieMathaus cat : listCategorii) {

				if (cat.getCod().equals(parent.getCod()) && cat.getCodParinte().trim().isEmpty())
					catName = cat.getNume();

				if (cat.getCodParinte().equals(parent.getCod())) {
					listChild1.add(cat.getNume());

				}

			}
			listDataChild1.put(catName, listChild1);
		}

		Collections.sort(categNodes);

		for (CategorieMathaus categ : categNodes) {
			parentNodes.add(categ.getNume());
		}

		parentNodes.add(0, "Selectati o categorie");

		AdapterCategorii adapter = new AdapterCategorii(context, parentNodes);
		spinnerCategorii.setAdapter(adapter);
		setCategoriiListener();

	}

	private void setCategoriiListener() {
		spinnerCategorii.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

				if (position > 0) {
					selectedCat = spinnerCategorii.getSelectedItem().toString();
					loadExpandableList(spinnerCategorii.getSelectedItem().toString());
					setTitle(selectedCat);
				} else
					selectedCat = "";

			}

			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
	}

	private void loadExpandableList(String numeCategorie) {

		final List<String> listParents = new ArrayList<String>();
		List<String> listCodes = new ArrayList<String>();
		
		List<CategorieMathaus> categNodes = new ArrayList<CategorieMathaus>();

		ArticolMathausAdapter adapterArt = new ArticolMathausAdapter(context, new ArrayList<ArticolMathaus>());
		gridView.setAdapter(adapterArt);

		HashMap<String, List<String>> childsL2 = new HashMap<String, List<String>>();

		String nodeCode = "";

		for (CategorieMathaus cat : listCategorii) {
			if (cat.getNume().equals(numeCategorie)) {
				nodeCode = cat.getCod();
				break;
			}
		}

		for (CategorieMathaus cat : listCategorii) {
			if (cat.getCodParinte().equals(nodeCode)) {
				listCodes.add(cat.getCod());
				
				CategorieMathaus categ = new CategorieMathaus();
				categ.setNume(cat.getNume());
				categ.setCod(cat.getCod());
				categ.setCodParinte(nodeCode);
				categNodes.add(categ);
			}
		}

		for (CategorieMathaus parentL2 : categNodes) {

			List<String> listChild2 = new ArrayList<String>();
			String catName = "";

			for (CategorieMathaus cat : listCategorii) {

				if (cat.getCod().equals(parentL2.getCod()) && cat.getCodParinte().equals(parentL2.getCodParinte()))
					catName = cat.getNume();

				if (cat.getCodParinte().equals(parentL2.getCod()))
					listChild2.add(cat.getNume());

			}

			childsL2.put(catName, listChild2);

		}
		
		Collections.sort(categNodes);

		for (CategorieMathaus categ : categNodes) {
			listParents.add(categ.getNume());
		}

		final CategoriiMathausAdapter adapter = new CategoriiMathausAdapter(context, listParents, childsL2);
		expandableCategorii.setAdapter(adapter);

		expandableCategorii.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

				ArticolMathausAdapter adapterArt = new ArticolMathausAdapter(context, new ArrayList<ArticolMathaus>());
				gridView.setAdapter(adapterArt);

				String nodeName = listParents.get(groupPosition);

				selectedNode = nodeName;
				selectedChild = "";

				setTitle(selectedCat + " > " + selectedNode);

				if (!nodeHasChildren(nodeName))
					getArticole(nodeName);

				return false;
			}
		});

		expandableCategorii.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

				ArticolMathausAdapter adapterArt = new ArticolMathausAdapter(context, new ArrayList<ArticolMathaus>());
				gridView.setAdapter(adapterArt);

				String childName = adapter.getChild(groupPosition, childPosition).toString();
				selectedChild = childName;

				setTitle(selectedCat + " > " + selectedNode + " > " + selectedChild);

				getArticole(childName);

				return true;
			}
		});

	}

	private void getArticole(String numeCategorie) {

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("codCategorie", getCodCategorie(numeCategorie));
		params.put("filiala", UserInfo.getInstance().getUnitLog());
		opMathaus.getArticole(params);

	}

	private String getCodCategorie(String numeCategorie) {
		String codCategorie = "-1";

		for (CategorieMathaus cat : listCategorii) {
			if (cat.getNume().equals(numeCategorie)) {
				codCategorie = cat.getCodHybris();
				break;
			}
		}

		return codCategorie;
	}

	private boolean nodeHasChildren(String nodeName) {
		boolean hasChildren = false;

		String nodeCode = "";

		for (CategorieMathaus cat : listCategorii) {
			if (cat.getNume().equals(nodeName)) {
				nodeCode = cat.getCod();
				break;
			}
		}

		for (CategorieMathaus cat : listCategorii) {
			if (cat.getCodParinte().equals(nodeCode)) {
				hasChildren = true;
				break;
			}
		}

		return hasChildren;
	}

	private void afisArticole(String result) {
		
		/*
		List<ArticolMathaus> listArticole = opMathaus.deserializeArticole(result);

		ArticolMathausAdapter adapter = new ArticolMathausAdapter(context, listArticole);
		adapter.setArticolMathausListener(CategoriiMathausDialog.this);
		gridView.setAdapter(adapter);
		*/

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
