/**
 * @author florinb
 *
 */
package my.logon.screen.screens;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.HashMap;

import my.logon.screen.R;
import my.logon.screen.adapters.DrawerMenuAdapterClInact;
import my.logon.screen.model.UserInfo;

public class ClientiSemiactivi extends Activity {

	public static String var1 = "Main var";
	public static ArrayList<String> clientListName = new ArrayList<>();
	public static ArrayList<String> clientListCode = new ArrayList<>();
	public static ArrayList<String> artSelListName = new ArrayList<String>();
	public static ArrayList<String> artSelListCode = new ArrayList<String>();
	public static String selectedFiliala = "-1", selectedAgent = "-1", tipComanda = "E";
	private static ArrayList<HashMap<String, String>> menuList = new ArrayList<>();
	private static ArrayList<String> docSelList = new ArrayList<String>();
	ListView listViewMenu;
	DrawerMenuAdapterClInact menuAdapter;
	DrawerLayout drawer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTheme(R.style.LRTheme);
		setContentView(R.layout.activity_clienti_semiactivi);

		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

		ActionBar actionBar = getActionBar();
		actionBar.setTitle("Clienti semiactivi");
		actionBar.setDisplayHomeAsUpEnabled(true);

		try {

			if (UserInfo.getInstance().getTipAcces().equals("9") || UserInfo.getInstance().getTipAcces().equals("27")
					|| UserInfo.getInstance().getTipAcces().equals("17")) // ag,
			// ka,
			// cv
			{
				selectedAgent = UserInfo.getInstance().getCod();
				selectedFiliala = UserInfo.getInstance().getUnitLog();
			}

			if (UserInfo.getInstance().getTipAcces().equals("10") || UserInfo.getInstance().getTipAcces().equals("12")
					|| UserInfo.getInstance().getTipAcces().equals("18") || UserInfo.getInstance().getTipAcces().equals("35")) // sd,sm
			{
				selectedAgent = "0";
				selectedFiliala = UserInfo.getInstance().getUnitLog();
			}

			
			

			menuAdapter = new DrawerMenuAdapterClInact(this, menuList, R.layout.rowlayout_menu_item, new String[] { "menuName", "menuId" }, new int[] {
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

				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction().replace(R.id.main, new FragmentOne()).commit();

				if (pos == 0)
					fragmentManager.beginTransaction().replace(R.id.main, new SelectAgentClientiSemiactivi()).commit();
				else
					fragmentManager.beginTransaction().replace(R.id.main, new AfisClientiSemiactivi()).commit();

				listViewMenu.setItemChecked(pos, true);

			}

		});

	}

	private void addMenuItems() {

		menuList.clear();

		HashMap<String, String> temp;

		temp = new HashMap<String, String>(1, 0.75f);
		temp.put("menuName", "Agent");
		temp.put("menuId", "1");
		menuList.add(temp);

		temp = new HashMap<String, String>(1, 0.75f);
		temp.put("menuName", this.getResources().getString(R.string.strAfisRaport));
		temp.put("menuId", "2");
		menuList.add(temp);

		listViewMenu.setAdapter(menuAdapter);

	}

	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		super.onCreateOptionsMenu(menu);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == 0) {
			if (drawer.isDrawerOpen(Gravity.LEFT))
				drawer.closeDrawers();
			else
				drawer.openDrawer(Gravity.LEFT);

		}

		if (item.getItemId() == android.R.id.home) {

			clearAllData();
			UserInfo.getInstance().setParentScreen("");
			Intent nextScreen = new Intent(this, MainMenu.class);
			startActivity(nextScreen);

			finish();

		}

		return super.onOptionsItemSelected(item);

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
		clientListName.clear();
		clientListCode.clear();
		artSelListName.clear();
		artSelListCode.clear();
		docSelList.clear();

		selectedFiliala = "-1";
		selectedAgent = "0";
		tipComanda = "E";

	}

}
