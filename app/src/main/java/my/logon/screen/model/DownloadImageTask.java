package my.logon.screen.model;

import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import my.logon.screen.model.HttpsTrustManager;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
	ImageView bmImage;

	public DownloadImageTask(ImageView bmImage) {
		this.bmImage = bmImage;
	}

	protected Bitmap doInBackground(String... urls) {
		String urldisplay = urls[0];
		Bitmap bmp = null;
		try {
			HttpsTrustManager.allowAllSSL();
			InputStream in = new java.net.URL(urldisplay).openStream();
			bmp = BitmapFactory.decodeStream(in);
		} catch (Exception e) {

			e.printStackTrace();
		}
		return bmp;
	}

	protected void onPostExecute(Bitmap result) {
		bmImage.setImageBitmap(result);
	}
}
