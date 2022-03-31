/**
 * @author florinb
 *
 */
package my.logon.screen.screens;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import androidx.drawerlayout.widget.DrawerLayout;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import my.logon.screen.R;
import my.logon.screen.adapters.DrawerMenuAdapter;
import my.logon.screen.model.UserInfo;
import my.logon.screen.model.VanzariAgenti;



public class VanzariAgentiActivity extends Activity {

	final String[] fragments = { "my.logon.screen.screens.SelectArticolVanzariAg", "my.logon.screen.screens.SelectClientVanzariAg",
			"my.logon.screen.screens.SelectAgentVanzariAg", "my.logon.screen.screens.SelectIntervalVanzariAg", "my.logon.screen.screens.SelectTipComandaVanzariAg",
			"my.logon.screen.screens.AfisRaportVanzariAg", };

	public static String var1 = "Main var";
	private static ArrayList<HashMap<String, String>> menuList = new ArrayList<HashMap<String, String>>();

	ListView listViewMenu;
	DrawerMenuAdapter menuAdapter;
	DrawerLayout drawer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTheme(R.style.LRTheme);
		setContentView(R.layout.activity_vanzari_agenti);

		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

		ActionBar actionBar = getActionBar();
		actionBar.setTitle("Vanzari");
		actionBar.setDisplayHomeAsUpEnabled(true);

		VanzariAgenti vanzariInstance = VanzariAgenti.getInstance();

		try {

			if (UserInfo.getInstance().getTipAcces().equals("9") || UserInfo.getInstance().getTipAcces().equals("27")
					|| UserInfo.getInstance().getTipAcces().equals("17") || UserInfo.getInstance().getTipAcces().equals("41")) // ag,
			// ka,
			// cv
			{
				vanzariInstance.selectedAgent = UserInfo.getInstance().getCod();
				vanzariInstance.selectedFiliala = UserInfo.getInstance().getUnitLog();
			}

			if (UserInfo.getInstance().getTipAcces().equals("10") || UserInfo.getInstance().getTipAcces().equals("12")
					|| UserInfo.getInstance().getTipAcces().equals("18") || UserInfo.getInstance().getTipAcces().equals("35")
					|| UserInfo.getInstance().getTipAcces().equals("32")) // sd,sm
			{
				vanzariInstance.selectedAgent = "0";
				vanzariInstance.selectedFiliala = UserInfo.getInstance().getUnitLog();
			}

			SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.UK);
			String currentDate = sdf.format(new Date());

			vanzariInstance.startIntervalRap = currentDate;
			vanzariInstance.stopIntervalRap = currentDate;

			menuAdapter = new DrawerMenuAdapter(this, menuList, R.layout.rowlayout_menu_item, new String[] { "menuName", "menuId" }, new int[] {
					R.id.textMenuName, R.id.textMenuId });

			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.main, new FragmentOne()).commit();

			drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

			listViewMenu = (ListView) findViewById(R.id.menuDrawer);
			addListViewMenuListener();
			addMenuItems();

		} catch (Exception ex) {
			Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
		}

	}

	private void CreateMenu(Menu menu) {
		MenuItem mnu1 = menu.add(0, 0, 0, "Optiuni");
		mnu1.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {

			clearAllData();
			UserInfo.getInstance().setParentScreen("");
			Intent nextScreen = new Intent(this, MainMenu.class);
			startActivity(nextScreen);

			finish();

		}

		if (item.getItemId() == 0) {
			if (drawer.isDrawerOpen(Gravity.LEFT))
				drawer.closeDrawers();
			else
				drawer.openDrawer(Gravity.LEFT);

		}

		return super.onOptionsItemSelected(item);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		CreateMenu(menu);
		return true;
	}

	private void addListViewMenuListener() {
		listViewMenu.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, final int pos, long id) {
				drawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {

				});

				drawer.closeDrawer(listViewMenu);

				if (pos <= 5) {
					FragmentTransaction tx = getFragmentManager().beginTransaction();
					tx.replace(R.id.main, Fragment.instantiate(VanzariAgentiActivity.this, fragments[pos]));
					tx.commit();

					listViewMenu.setItemChecked(pos, true);

				} else {

					if (AfisRaportVanzariAg.arrayListRapVanz == null) {
						Toast.makeText(getApplicationContext(), "Nimic de expediat.", Toast.LENGTH_LONG).show();
					} else {

						if (AfisRaportVanzariAg.arrayListRapVanz.size() == 0) {
							Toast.makeText(getApplicationContext(), "Nimic de expediat.", Toast.LENGTH_LONG).show();
						} else {
							//startCreareXlsDocumentThread();
						}
					}

				}

			}

		});

	}

	private void addMenuItems() {

		menuList.clear();

		HashMap<String, String> temp;

		temp = new HashMap<String, String>();
		temp.put("menuName", "Articole/sintetice");
		temp.put("menuId", "1");
		menuList.add(temp);

		temp = new HashMap<String, String>();
		temp.put("menuName", this.getResources().getString(R.string.strClienti));
		temp.put("menuId", "2");
		menuList.add(temp);

		temp = new HashMap<String, String>();
		temp.put("menuName", "Agent");
		temp.put("menuId", "3");
		menuList.add(temp);

		temp = new HashMap<String, String>();
		temp.put("menuName", "Interval");
		temp.put("menuId", "4");
		menuList.add(temp);

		temp = new HashMap<String, String>();
		temp.put("menuName", "Comenzi");
		temp.put("menuId", "5");
		menuList.add(temp);

		temp = new HashMap<String, String>();
		temp.put("menuName", this.getResources().getString(R.string.strAfisRaport));
		temp.put("menuId", "6");
		menuList.add(temp);

		// directori
		if (UserInfo.getInstance().getTipAcces().equals("12") || UserInfo.getInstance().getTipAcces().equals("14")
				|| UserInfo.getInstance().getTipAcces().equals("35")) {
			temp = new HashMap<String, String>();
			temp.put("menuName", this.getResources().getString(R.string.strMail));
			temp.put("menuId", "7");
			menuList.add(temp);
		}

		listViewMenu.setAdapter(menuAdapter);

	}

	/*
	
	private void startCreareXlsDocumentThread() {
		try {

			createXlsDocument document = new createXlsDocument(this);
			document.execute("dummy");

		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
		}
	}

	private class createXlsDocument extends AsyncTask<String, Void, String> {
		String errMessage = "";
		Context mContext;
		private ProgressDialog dialog;

		private createXlsDocument(Context context) {
			super();
			this.mContext = context;
		}

		protected void onPreExecute() {
			this.dialog = new ProgressDialog(mContext);
			this.dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			this.dialog.setMessage("Creare document...");
			this.dialog.setCancelable(false);
			this.dialog.show();
		}

		@Override
		protected String doInBackground(String... url) {

			try {

				performCreateXlsDocument();

			} catch (Exception e) {
				errMessage = e.getMessage();
			}
			return "1";
		}

		@Override
		protected void onPostExecute(String result) {

			try {
				if (dialog != null) {
					dialog.dismiss();
					dialog = null;
				}

				if (!errMessage.equals("")) {

					Toast toast = Toast.makeText(getApplicationContext(), errMessage, Toast.LENGTH_SHORT);
					toast.show();

				}
			} catch (Exception e) {
				Log.e("Error", e.toString());
			}
		}

	}
	
	*/
	
	
/*
	private void performCreateXlsDocument() {

		try {

			VanzariAgenti vanzariInstance = VanzariAgenti.getInstance();

			Workbook wb = new HSSFWorkbook();

			Cell c = null;

			CellStyle cs = wb.createCellStyle();
			cs.setFillForegroundColor(HSSFColor.LIME.index);

			Font font = wb.createFont();
			font.setBoldweight(Font.BOLDWEIGHT_BOLD);
			cs.setFont(font);

			Sheet sheet1 = null;
			sheet1 = wb.createSheet("Vanzari");

			Row row = sheet1.createRow(0);

			if (vanzariInstance.articolListCode.size() == 0) {
				for (int i = 0; i < AfisRaportVanzariAg.rapHeader1.length; i++) {
					c = row.createCell(i);
					c.setCellValue(AfisRaportVanzariAg.rapHeader1[i]);
					c.setCellStyle(cs);

				}
			} else // header articole
			{

				for (int i = 0; i < AfisRaportVanzariAg.rapHeader2.length; i++) {
					c = row.createCell(i);
					c.setCellValue(AfisRaportVanzariAg.rapHeader2[i]);
					c.setCellStyle(cs);

				}

			}

			for (int i = 0; i < AfisRaportVanzariAg.arrayListRapVanz.size() - 1; i++) {
				if (vanzariInstance.articolListCode.size() == 0) {
					row = sheet1.createRow(i + 1);

					for (int j = 0; j < AfisRaportVanzariAg.rapHeader1.length; j++) {
						c = row.createCell(j);
						c.setCellValue(AfisRaportVanzariAg.arrayListRapVanz.get(i).get(AfisRaportVanzariAg.rapHeader1[j]));

					}

				} else {
					row = sheet1.createRow(i + 1);
					for (int j = 0; j < AfisRaportVanzariAg.rapHeader2.length; j++) {
						c = row.createCell(j);
						c.setCellValue(AfisRaportVanzariAg.arrayListRapVanz.get(i).get(AfisRaportVanzariAg.rapHeader2[j]));

					}

				}

			}

			// redimensionare coloane
			if (vanzariInstance.articolListCode.size() == 0) {
				for (int i = 1; i < AfisRaportVanzariAg.rapHeader1.length; i++) {
					sheet1.setColumnWidth(i, (15 * 300));

				}
			} else {

				for (int i = 1; i < AfisRaportVanzariAg.rapHeader2.length; i++) {
					sheet1.setColumnWidth(i, (15 * 300));

				}

			} // sf. redimensionare

			File file = new File(Environment.getExternalStorageDirectory() + "/download/", "RaportVanzari.xls");

			FileOutputStream os = null;

			try {
				os = new FileOutputStream(file);

				wb.write(os);

				showSendMailScreen();

			} catch (Exception e) {
				Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
			} finally {
				try {
					if (null != os)
						os.close();
				} catch (Exception ex) {
					Log.e("Error", ex.toString());
				}
			}

		} catch (Exception ex) {
			Toast.makeText(this, ex.toString(), Toast.LENGTH_SHORT).show();
		}

	}


*/

	private void showSendMailScreen() {
		try {

			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType("message/rfc822");
			intent.putExtra(Intent.EXTRA_EMAIL, new String[] { "" });
			intent.putExtra(Intent.EXTRA_SUBJECT, "Raport vanzari");
			intent.putExtra(Intent.EXTRA_TEXT, "Fisier generat de aplicatia mobila LiteSFA.");

			File file = new File(Environment.getExternalStorageDirectory() + "/download/", "RaportVanzari.xls");

			if (!file.exists() || !file.canRead()) {
				Toast.makeText(this, "Attachment Error", Toast.LENGTH_SHORT).show();
				finish();
				return;
			}
			Uri uri = Uri.parse("file://" + file);
			intent.putExtra(Intent.EXTRA_STREAM, uri);
			startActivity(Intent.createChooser(intent, "Expediere mail..."));

		} catch (Exception ex) {
			Toast.makeText(this, ex.toString(), Toast.LENGTH_SHORT).show();
		}

	}

	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		super.onCreateOptionsMenu(menu);

	}

	@Override
	public void onBackPressed() {

		clearAllData();
		UserInfo.getInstance().setParentScreen("");
		Intent nextScreen = new Intent(this, MainMenu.class);
		startActivity(nextScreen);

		finish();
		return;
	}

	private void clearAllData() {

		VanzariAgenti vanzariInstance = VanzariAgenti.getInstance();

		vanzariInstance.clientListName.clear();
		vanzariInstance.clientListCode.clear();
		vanzariInstance.articolListName.clear();
		vanzariInstance.articolListCode.clear();

		vanzariInstance.startIntervalRap = "";
		vanzariInstance.stopIntervalRap = "";
		vanzariInstance.selectedFiliala = "-1";
		vanzariInstance.selectedAgent = "0";
		vanzariInstance.tipComanda = "E";

	}

}
