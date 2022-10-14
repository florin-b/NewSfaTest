package my.logon.screen.screens;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import my.logon.screen.R;
import my.logon.screen.adapters.ArticolComandaReturDisplayAdapter;
import my.logon.screen.adapters.ComandaReturStatusAfisAdapter;
import my.logon.screen.adapters.StatusComandaReturDisplayAdapter;
import my.logon.screen.beans.BeanArticolComandaRetur;
import my.logon.screen.beans.BeanHeaderComandaRetur;
import my.logon.screen.beans.BeanStatusComandaRetur;
import my.logon.screen.dialogs.SelectIntervalDialog;
import my.logon.screen.enums.EnumComenziDAO;
import my.logon.screen.listeners.ComenziDAOListener;
import my.logon.screen.listeners.CustomSpinnerClass;
import my.logon.screen.listeners.CustomSpinnerListener;
import my.logon.screen.listeners.IntervalDialogListener;
import my.logon.screen.listeners.OperatiiAgentListener;
import my.logon.screen.model.ComenziDAO;
import my.logon.screen.model.OperatiiAgent;
import my.logon.screen.model.UserInfo;
import my.logon.screen.utils.UtilsUser;

public class AfisareComandaReturStatus extends Activity implements IntervalDialogListener, OperatiiAgentListener, CustomSpinnerListener, ComenziDAOListener {

    private String intervalAfisare = "0";
    String dataSelStart = "";
    String dataSelStop = "";
    Spinner spinnerAgentiAfisCmd;
    String selectedCmd = "-1", selectedAgent = "-1", selectedFiliala = "-1", selectedClient = "", selectedCodDepart = "-1", tipAgent="-1";
    OperatiiAgent agent;
    SimpleAdapter adapterAgenti;
    CustomSpinnerClass spinnerListener = new CustomSpinnerClass();
    String  agentNr = null;
    ComenziDAO comenziRetur;
    Spinner spinnerCmdRetur;
    TextView codClientRetur, denumireClientRetur, persoanaContactRetur, telefonRetur, adresaRetur,tipTransportRetur;
    private BeanHeaderComandaRetur comandaCurenta;
    List<BeanHeaderComandaRetur> obiectPrincipalCmdRetur;
    ListView listViewArticoleComandaRetur,listViewStatusComandaRetur;
    LinearLayout mainPannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(R.style.LRTheme);
        setContentView(R.layout.activity_afisare_comanda_retur_status);

        ActionBar actionBar = getActionBar();
        actionBar.setTitle("Stare comenzi retur");
        actionBar.setDisplayHomeAsUpEnabled(true);

        mainPannel = (LinearLayout) findViewById(R.id.mainPannel);
        mainPannel.setVisibility(View.INVISIBLE);

        spinnerAgentiAfisCmd = (Spinner) findViewById(R.id.spinnerAgentiAfisCmd);
        spinnerAgentiAfisCmd.setVisibility(View.INVISIBLE);

        agent = OperatiiAgent.getInstance();
        agent.setOperatiiAgentListener(this);
        selectedFiliala = UserInfo.getInstance().getUnitLog();

        spinnerCmdRetur = (Spinner)findViewById(R.id.spinnerCmdRetur);
        spinnerCmdRetur.setVisibility(View.INVISIBLE);
        addSpinnerCmdReturListener();

        spinnerListener.setListener(this);

        comenziRetur = ComenziDAO.getInstance(this);
        comenziRetur.setComenziDAOListener(this);

        if (isSefDepart()) {
            performGetAgenti();
        }

        if (isAgent() || UtilsUser.isCGED() || UtilsUser.isOIVPD() || UtilsUser.isSSCM()) {
            selectedAgent = UserInfo.getInstance().getCod();
            tipAgent = UserInfo.getInstance().getTipAcces();
        }


        persoanaContactRetur = (TextView)findViewById(R.id.persoanaContactRetur);
        telefonRetur = (TextView) findViewById(R.id.telefonRetur);
        adresaRetur = (TextView) findViewById(R.id.adresaRetur);
        tipTransportRetur = (TextView)findViewById(R.id.tipTransportRetur);

        listViewArticoleComandaRetur = (ListView) findViewById(R.id.listVArticoleComandaRetur);
        listViewStatusComandaRetur = (ListView)findViewById(R.id.listVStatusRetur);

        if (isAgent()) {
            getComenziRetur();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        CreateMenu(menu);
        return true;
    }


    private void CreateMenu(Menu menu) {

        MenuItem mnu1 = menu.add(0, 0, 0, "Interval");
        mnu1.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case 0:
                SelectIntervalDialog intervalDialog = new SelectIntervalDialog(this);
                intervalDialog.setIntervalDialogListener(this);
                intervalDialog.setTitle("Cautare comenzi de Retur");
                intervalDialog.showDialog();
                return true;

            case android.R.id.home:

                UserInfo.getInstance().setParentScreen("");

                Intent nextScreen = new Intent(getApplicationContext(), MainMenu.class);
                startActivity(nextScreen);

                finish();
                return true;

        }
        return false;
    }

    public void operationIntervalComplete(String intervalAfisare, String dataSelStart, String dataSelStop) {
        this.intervalAfisare = intervalAfisare;
        this.dataSelStart = dataSelStart;
        this.dataSelStop = dataSelStop;


        if (isAgentSelected()) {//verificare daca in selector de agent este selectat sau nu, activare cand va fi si SD

            getComenziRetur();
        }
        else {
            Toast.makeText(getApplicationContext(),"Nu s-a selectat nici un agent!", Toast.LENGTH_SHORT).show();
        }
    }

    public void getComenziRetur() {

        try {
            HashMap<String, String> params = new HashMap<String, String>();

            String paramInterval = intervalAfisare;

            if (intervalAfisare.equals("3")) {
                String dataStart = dataSelStart.replace("#", ".");
                String dataStop = dataSelStop.replace("#", ".");
                paramInterval = dataStart + "#" + dataStop;
            }

            //Toast.makeText(getApplicationContext(), "agent-"+paramInterval, Toast.LENGTH_SHORT).show();
            params.put("codAgent", selectedAgent);
            params.put("tipAgent", "AG");
            params.put("interval", paramInterval);

            comenziRetur.getListComenziRetur(params);
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void getListaArticoleRetur(String selectedCmd) {

        try {
            HashMap<String,String> params = new HashMap<String,String>();

            params.put("nrDocument",selectedCmd);
            comenziRetur.getListaArticoleRetur(params);
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void getStareRetur(String selectedCmd) {

        try {
            HashMap<String,String> params = new HashMap<String,String>();

            params.put("nrDocument",selectedCmd);
            comenziRetur.getStareReturComanda(params);

        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    boolean isAgentSelected() {
        if (spinnerAgentiAfisCmd.getVisibility() == View.VISIBLE)
            return spinnerAgentiAfisCmd.getSelectedItemPosition() > 0;
        else

            return true;
    }



    @Override
    public void onBackPressed() {
        UserInfo.getInstance().setParentScreen("");

        Intent nextScreen = new Intent(getApplicationContext(), MainMenu.class);
        startActivity(nextScreen);

        finish();
        return;
    }

    boolean isSefDepart() {
        return UserInfo.getInstance().getTipAcces().equals("10") || UserInfo.getInstance().getTipAcces().equals("18")
                || UserInfo.getInstance().getTipAcces().equals("32") || UserInfo.getInstance().getTipAcces().equals("44")
                || UserInfo.getInstance().getTipAcces().equals("39");
    }
    boolean isDirectorDistrib() {
        return UserInfo.getInstance().getTipAcces().equals("14") || UserInfo.getInstance().getTipAcces().equals("12");
    }
    boolean isAgent() {
        return UserInfo.getInstance().getTipAcces().equals("9") || UserInfo.getInstance().getTipAcces().equals("27")
                || UserInfo.getInstance().getTipAcces().equals("17") || UserInfo.getInstance().getTipAcces().equals("41")
                || UserInfo.getInstance().getTipAcces().equals("43") || UserInfo.getInstance().getTipAcces().equals("45");
    }

    private void performGetAgenti() {

        String tipAgent = null;

        if (!isDirectorDistrib())
            selectedCodDepart = UserInfo.getInstance().getCodDepart();



        // dka
        if (UserInfo.getInstance().getTipAcces().equals("35") || UserInfo.getInstance().getTipAcces().equals("32")) {
            selectedCodDepart = "10";
        }

        // sm
        if (UserInfo.getInstance().getTipAcces().equals("18")) {
            selectedCodDepart = "11";
        }

        // smr, smw, smg
        if (UtilsUser.isSMNou()) {
            selectedCodDepart = "11";
            tipAgent = UtilsUser.getTipSMNou();
        }

        // SDCVA
        if (UserInfo.getInstance().getTipAcces().equals("39")) {

            selectedCodDepart = "11";

            if (UserInfo.getInstance().getTipUserSap().equals("SDIP")) {
                tipAgent = "SDIP";
                selectedFiliala = UserInfo.getInstance().getInitUnitLog();
            } else {

                tipAgent = "SDCVA";
            }
        }

        agent.getListaAgenti(selectedFiliala, selectedCodDepart, this, true, tipAgent);

    }

    @Override
    public void opAgentComplete(ArrayList<HashMap<String, String>> listAgenti) {

        populateAgentiList(listAgenti);
    }

    //functia care populeaza spinner cu agenti
    private void populateAgentiList(ArrayList<HashMap<String, String>> listAgenti) {

        spinnerAgentiAfisCmd.setVisibility(View.VISIBLE);
        listAgenti.remove(1);
        adapterAgenti = new SimpleAdapter(this, listAgenti, R.layout.rowlayoutagenti, new String[] { "numeAgent", "codAgent" }, new int[] { R.id.textNumeAgent,
                R.id.textCodAgent });
        spinnerAgentiAfisCmd.setAdapter(adapterAgenti);

        if (listAgenti.size() > 0) {
            spinnerAgentiAfisCmd.setOnItemSelectedListener(spinnerListener);
        }

    }

    public void onSelectedSpinnerItem(int spinnerId, HashMap<String, String> map, int position) {

        if (spinnerId == R.id.spinnerAgentiAfisCmd) {
            agentNr = map.get("codAgent");
            if (agentNr.trim().equals(""))
                agentNr = "0"; // fara selectie
            if (agentNr.equals("00000000"))
                agentNr = "0";
            selectedAgent = agentNr;
            selectedCmd = "-1";

        }
        if (!selectedAgent.equals("0")) {
            getComenziRetur();
        }

    }

    @Override
    public void operationComenziComplete(EnumComenziDAO methodName, Object result) {
        switch (methodName) {
            case GET_LIST_DOCRETUR_STARE:

                showListComenziRetur(comenziRetur.deserializeHeadeComandaRetur((String) result));
                break;
            case  GET_LIST_ARTICOLE_RETUR:
                showListArticoleComenziRetur(comenziRetur.deserializeListaArticoleRetur((String) result));
                break;

            case GET_STATUS_COMANDA_RETUR:

                showStatusComenziRetur(comenziRetur.deserializeListaStatusRetur((String) result));
                break;
            default:
                break;
        }
    }

    private void showStatusComenziRetur(List<BeanStatusComandaRetur> listStatus) {

        if (listStatus.size()>0) {
            StatusComandaReturDisplayAdapter adapterStatusComandaRetur = new StatusComandaReturDisplayAdapter(listStatus,this);
            listViewStatusComandaRetur.setAdapter(adapterStatusComandaRetur);

        }
    }

    private void showListArticoleComenziRetur(List<BeanArticolComandaRetur> listArticoleComandaRetur) {

        if (listArticoleComandaRetur.size()>0) {
            ArticolComandaReturDisplayAdapter adapterArticolComandaRetur = new ArticolComandaReturDisplayAdapter(listArticoleComandaRetur, this);
            listViewArticoleComandaRetur.setAdapter(adapterArticolComandaRetur);
        }

        getStareRetur(selectedCmd); //apelare status retur
    }

    private void showListComenziRetur(List<BeanHeaderComandaRetur> listComenziRetur) {


        if (listComenziRetur.size() > 0) {
            obiectPrincipalCmdRetur = listComenziRetur;
            spinnerCmdRetur.setVisibility(View.VISIBLE);

            ComandaReturStatusAfisAdapter adapterComenziRetur = new ComandaReturStatusAfisAdapter(listComenziRetur, this);

            spinnerCmdRetur.setAdapter(adapterComenziRetur);

            selectedCmd = adapterComenziRetur.getItem(0).getVbeln(); //sa obtine primul vbeln din lista


            mainPannel.setVisibility(View.VISIBLE);

            showDetaliiClient(listComenziRetur,selectedCmd);


        }
        else {
            mainPannel.setVisibility(View.INVISIBLE);
            spinnerCmdRetur.setVisibility(View.INVISIBLE);
        }
    }

    private void showDetaliiClient(List<BeanHeaderComandaRetur> listComenziRetur,String selectedCmd) {
        String tipTransport = "";

        for (int i = 0; i < listComenziRetur.size(); i++) {

            if (listComenziRetur.get(i).getVbeln()==selectedCmd) {

                persoanaContactRetur.setText(listComenziRetur.get(i).getPers_contact());
                telefonRetur.setText(listComenziRetur.get(i).getTel_contact());
                adresaRetur.setText(listComenziRetur.get(i).getCity1()+", "+listComenziRetur.get(i).getStreet());

                if (listComenziRetur.get(i).getTraty().trim().equals("TRAP")) {
                    tipTransport = "Transport Arabesque";
                }
                else {
                    tipTransport = listComenziRetur.get(i).getTraty();
                }
                tipTransportRetur.setText(tipTransport);
            }

        }

    }

    void addSpinnerCmdReturListener() {
        spinnerCmdRetur.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> arg0,View arg1,int arg2,long arg3) {
                comandaCurenta = (BeanHeaderComandaRetur) arg0.getAdapter().getItem(arg2);
                selectedCmd = comandaCurenta.getVbeln();

                showDetaliiClient(obiectPrincipalCmdRetur,selectedCmd);
                getListaArticoleRetur(selectedCmd);

            }

            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
    }



}












