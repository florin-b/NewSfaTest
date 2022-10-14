package my.logon.screen.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import my.logon.screen.R;
import my.logon.screen.beans.BeanHeaderComandaRetur;
import my.logon.screen.utils.UtilsFormatting;



public class ComandaReturStatusAfisAdapter extends BaseAdapter {

    private Context context;
    private List<BeanHeaderComandaRetur> listComenziRetur;

    static class ViewHolder {
        public TextView textVbeln, textNumeClient, textDataCmdRetur, textDocumentReferinta;
    }

    public ComandaReturStatusAfisAdapter(List<BeanHeaderComandaRetur> listComenziRetur,Context context) {
        this.context = context;
        this.listComenziRetur = listComenziRetur;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView==null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.cmd_returstatus_row,parent,false);

            viewHolder = new ViewHolder();
            viewHolder.textVbeln = (TextView) convertView.findViewById(R.id.textVbeln);
            viewHolder.textNumeClient = (TextView) convertView.findViewById(R.id.textNumeClient);
            viewHolder.textDataCmdRetur = (TextView) convertView.findViewById(R.id.textDataCmdRetur);
            viewHolder.textDocumentReferinta = (TextView) convertView.findViewById(R.id.textDocumentReferinta);

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        BeanHeaderComandaRetur comandaRetur =  getItem(position);

        viewHolder.textVbeln.setText(comandaRetur.getVbeln());
        viewHolder.textNumeClient.setText(comandaRetur.getName1());

        String tempData = comandaRetur.getAudat();
        String anShort = tempData.substring(2,4);
        String an = tempData.substring(0,4);
        String luna = tempData.substring(4,6);
        String ziua = tempData.substring(6,8);
        String dataToFormat = ziua+"-"+luna+"-"+anShort;
        String lunaName =  UtilsFormatting.getMonthNameFromDate(dataToFormat, 2);
        String dataLong = ziua+" "+lunaName+" "+an;
        viewHolder.textDataCmdRetur.setText(dataLong);
        viewHolder.textDocumentReferinta.setText(comandaRetur.getReferinta());

        return convertView;

    }

    public int getCount() {
        return listComenziRetur.size();
    }

    public BeanHeaderComandaRetur getItem(int position) {
        return listComenziRetur.get(position);
    }

    public long getItemId(int position) {

        return position;
    }
}












