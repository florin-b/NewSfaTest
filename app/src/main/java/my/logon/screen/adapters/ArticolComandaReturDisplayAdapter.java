package my.logon.screen.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import my.logon.screen.R;
import java.util.List;

import my.logon.screen.beans.BeanArticolComandaRetur;
import my.logon.screen.beans.BeanHeaderComandaRetur;

public class ArticolComandaReturDisplayAdapter extends BaseAdapter {

    private Context context;
    private List<BeanArticolComandaRetur> listaArticoleRetur;
    private int nr;
    private String matnrClean;
    private int[] colors = new int[] { 0x3098BED9, 0x30E8E8E8 };


    static class ViewHolder {
        public TextView nrOrdin, matnr, denumireArticol, cantitateArticol, articolValoare;
    }

    public ArticolComandaReturDisplayAdapter(List<BeanArticolComandaRetur> listaArticoleRetur,Context context) {
        this.context = context;
        this.listaArticoleRetur = listaArticoleRetur;
    }



    @Override
    public int getCount() {
        return listaArticoleRetur.size();
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        int colorPos = position % colors.length;

        if (convertView==null) {

            convertView = LayoutInflater.from(context).inflate(R.layout.articol_comandaretur_row,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.nrOrdin = (TextView) convertView.findViewById(R.id.textNrOrdin);
            viewHolder.matnr = (TextView) convertView.findViewById(R.id.textMatnr);
            viewHolder.denumireArticol = (TextView) convertView.findViewById(R.id.textDenumireArticol);
            viewHolder.cantitateArticol = (TextView) convertView.findViewById(R.id.textCantitate);
            viewHolder.articolValoare = (TextView) convertView.findViewById(R.id.textValoare);

            convertView.setTag(viewHolder);

        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        BeanArticolComandaRetur articol = getItem(position);
        nr = position +1;
        matnrClean = articol.getMatnr().replaceFirst("^0+(?!$)", "");
        viewHolder.nrOrdin.setText(String.valueOf(nr));
        viewHolder.matnr.setText(matnrClean);
        viewHolder.denumireArticol.setText(articol.getArktx());
        viewHolder.cantitateArticol.setText(articol.getKwmeng()+" "+articol.getVrkme());
        viewHolder.articolValoare.setText(articol.getValoare_pozitie()+" RON");

        if (colorPos % 2 == 0)
            convertView.setBackgroundResource(R.drawable.shadow_dark);
        else
            convertView.setBackgroundResource(R.drawable.shadow_light);

        return convertView;
    }

    public BeanArticolComandaRetur getItem(int position) {
        return listaArticoleRetur.get(position);
    }
}
