package my.logon.screen.model;

import android.content.Context;

import my.logon.screen.model.OperatiiArticolImpl;

public class OperatiiArticolFactory {

	public static OperatiiArticolImpl createObject(String objectType, Context context) {
		OperatiiArticolImpl object = null;

		if (objectType.equals("OperatiiArticolImpl")) {
			object = new OperatiiArticolImpl(context);
		}

		return object;
	}

}
