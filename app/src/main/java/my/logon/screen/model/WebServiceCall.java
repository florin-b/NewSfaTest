package my.logon.screen.model;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.ksoap2.HeaderProperty;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;

import my.logon.screen.connectors.ConnectionStrings;
import my.logon.screen.listeners.AsyncTaskListener;

public class WebServiceCall extends AsyncTask<Void, Void, String> {
	String errMessage = "";
	Context mContext;
	private ProgressDialog dialog;
	private AsyncTaskListener listener;
	private String methodName;
	private Map<String, String> params;
	private static TrustManager[] trustManagers;

	public WebServiceCall(Context context, String methodName, Map<String, String> params) {
		super();
		this.mContext = context;
		this.listener = (AsyncTaskListener) mContext;
		this.methodName = methodName;
		this.params = params;
	}

	protected void onPreExecute() {
		dialog = new ProgressDialog(mContext);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setMessage("Asteptati...");
		dialog.setCancelable(false);
		dialog.show();
	}

	@Override
	protected String doInBackground(Void... url) {
		String response = "";
		try {
			SoapObject request = new SoapObject(ConnectionStrings.getInstance().getNamespace(), methodName);

			for (Entry<String, String> entry : params.entrySet()) {
				request.addProperty(entry.getKey(), entry.getValue());
			}

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(request);
			allowAllSSL();
			HttpTransportSE androidHttpTransport = new HttpTransportSE(ConnectionStrings.getInstance().getUrl(), 60000);

			List<HeaderProperty> headerList = new ArrayList<HeaderProperty>();
			headerList.add(new HeaderProperty("Authorization", "Basic "
					+ org.kobjects.base64.Base64.encode("bflorin:bflorin".getBytes())));
			androidHttpTransport
					.call(ConnectionStrings.getInstance().getNamespace() + methodName, envelope, headerList);
			Object result = envelope.getResponse();
			response = result.toString();
		} catch (Exception e) {
			errMessage = e.getMessage();
		}
		return response;
	}


	@Override
	protected void onPostExecute(String result) {
		// TODO

		try {
			if (dialog != null) {
				dialog.dismiss();
				dialog = null;
			}

			if (!errMessage.equals("")) {
				Toast toast = Toast.makeText(mContext, errMessage, Toast.LENGTH_SHORT);
				toast.show();
			} else {
				listener.onTaskComplete(methodName, result);
			}
		} catch (Exception e) {
			Toast.makeText(mContext, e.toString(), Toast.LENGTH_LONG).show();
		}
	}


	public static void allowAllSSL() {

		javax.net.ssl.HttpsURLConnection
				.setDefaultHostnameVerifier(new HostnameVerifier() {
					public boolean verify(String hostname, SSLSession session) {
						return true;
					}
				});

		javax.net.ssl.SSLContext context = null;

		if (trustManagers == null) {
			trustManagers = new javax.net.ssl.TrustManager[] { new _FakeX509TrustManager() };
		}

		try {
			context = javax.net.ssl.SSLContext.getInstance("TLS");
			context.init(null, trustManagers, new SecureRandom());
		} catch (NoSuchAlgorithmException e) {
			Log.e("allowAllSSL", e.toString());
		} catch (KeyManagementException e) {
			Log.e("allowAllSSL", e.toString());
		}
		javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(context
				.getSocketFactory());
	}


}
