package my.logon.screen.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import my.logon.screen.R;
import my.logon.screen.beans.BeanStatusComandaRetur;

public class StatusComandaReturDisplayAdapter extends BaseAdapter {

    private Context context;
    private List<BeanStatusComandaRetur> listStatus;
    private int nr;

    static class ViewHolder {
        public TextView nrDocument,status;
    }

    public StatusComandaReturDisplayAdapter(List<BeanStatusComandaRetur> listStatus, Context context) {
        this.context = context;
        this.listStatus = listStatus;
    }

    @Override
    public int getCount() {
        return listStatus.size();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView==null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.status_comanda_row,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.nrDocument = (TextView) convertView.findViewById(R.id.textNrDocumentRetur);
            viewHolder.status = (TextView) convertView.findViewById(R.id.textStatusRetur);

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        BeanStatusComandaRetur status = getItem(position);

        nr = position + 1;
        viewHolder.nrDocument.setText(status.getNrDocument().substring(2));
        viewHolder.status.setText(status.getStare());

        return convertView;

    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public BeanStatusComandaRetur getItem(int position) {
        return listStatus.get(position);
    }


}
