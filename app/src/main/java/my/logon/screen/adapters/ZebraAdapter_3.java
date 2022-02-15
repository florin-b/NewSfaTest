/**
 * @author florinb
 *
 */
package my.logon.screen.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

import java.util.HashMap;
import java.util.List;

//zebra
public class ZebraAdapter_3 extends SimpleAdapter {

	private int[] colors = new int[] { 0x3098BED9, 0x30E8E8E8 };

	public ZebraAdapter_3(Context context, List<HashMap<String, String>> items,
			int resource, String[] from, int[] to) {
		super(context, items, resource, from, to);

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		int colorPos = position % colors.length;

		view.setBackgroundColor(colors[colorPos]);

		return view;

	}

}