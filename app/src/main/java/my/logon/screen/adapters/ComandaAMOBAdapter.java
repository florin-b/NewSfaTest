package my.logon.screen.adapters;

import java.util.List;
import my.logon.screen.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import my.logon.screen.beans.ComandaAmobAfis;

public class ComandaAMOBAdapter extends BaseAdapter {

	private Context context;
	private List<ComandaAmobAfis> listComenzi;

	static class ViewHolder {
		public TextView textIdCmd, textSuma, textData, textMoneda;
	}

	public ComandaAMOBAdapter(Context context, List<ComandaAmobAfis> listComenzi) {
		this.context = context;
		this.listComenzi = listComenzi;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;

		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.comanda_amob_afis, parent, false);

			viewHolder = new ViewHolder();
			viewHolder.textIdCmd = (TextView) convertView.findViewById(R.id.textIdCmd);

			viewHolder.textSuma = (TextView) convertView.findViewById(R.id.textSuma);
			viewHolder.textData = (TextView) convertView.findViewById(R.id.textData);
			viewHolder.textMoneda = (TextView) convertView.findViewById(R.id.textMoneda);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		ComandaAmobAfis comanda = getItem(position);

		viewHolder.textIdCmd.setText(comanda.getNumeClient());
		viewHolder.textSuma.setText(comanda.getValoare());
		viewHolder.textData.setText(comanda.getDataCreare());
		viewHolder.textMoneda.setText(comanda.getMoneda());

		return convertView;

	}

	@Override
	public int getCount() {
		return listComenzi.size();
	}

	@Override
	public ComandaAmobAfis getItem(int position) {
		return listComenzi.get(position);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

}
