package my.logon.screen.dialogs;

import my.logon.screen.listeners.AdapterMathausListener;
import my.logon.screen.listeners.OperatiiMathausListener;
import my.logon.screen.R;
import my.logon.screen.model.DownloadImageTask;
import my.logon.screen.model.OperatiiMathaus;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import my.logon.screen.beans.ArticolMathaus;
import my.logon.screen.enums.EnumOperatiiMathaus;

public class ArticolMathausDialog extends Dialog implements OperatiiMathausListener {

	private Context context;
	private Button okButton;
	private OperatiiMathaus opMathaus;
	private ArticolMathaus articol;
	private AdapterMathausListener listener;
	ImageView imageArt;

	public ArticolMathausDialog(Context context, ArticolMathaus articol) {
		super(context);
		this.context = context;
		this.articol = articol;

		setContentView(R.layout.dialog_articol_mathaus);
		setTitle(articol.getNume());
		setCancelable(true);
		opMathaus = new OperatiiMathaus(context);
		opMathaus.setOperatiiMathausListener(this);

		setUpLayout();

	}
	
	
	public ArticolMathausDialog(Context context){
			super(context);
	}

	private void setUpLayout() {

		imageArt = (ImageView) findViewById(R.id.imageview_art);

		int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.55);
		int height = (int) (context.getResources().getDisplayMetrics().heightPixels * 0.55);
		imageArt.getLayoutParams().width = width;
		imageArt.getLayoutParams().height = height;

		new DownloadImageTask(imageArt).execute(articol.getAdresaImgMare());

		okButton = (Button) findViewById(R.id.btnOk);
		addOkButtonListener();
	}

	private void addOkButtonListener() {
		okButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (listener != null)
					listener.articolMathausSelected(articol);
				dismiss();

			}
		});

	}

	public void setArticolMathausListener(AdapterMathausListener listener) {
		this.listener = listener;
	}

	@Override
	public void operationComplete(EnumOperatiiMathaus methodName, Object result) {
		switch (methodName) {
		case GET_CATEGORII:

			break;
		case GET_ARTICOLE:

			break;
		default:
			break;
		}

	}

}
