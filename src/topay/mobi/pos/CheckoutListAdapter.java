package topay.mobi.pos;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import topay.mobi.pos.controller.BematechServices;
import topay.mobi.pos.util.Constants;
import topay.mobi.pos.util.HttpConnection;
import topay.mobi.pos.util.Moeda;
import topay.mobi.pos.vo.Checkout;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

public class CheckoutListAdapter extends ArrayAdapter<Checkout> implements OnClickListener {

	private ArrayList<Checkout> checkouts;
	private BematechServices bms;
	private TextView positionTxt;
	private FragmentActivity activity;
	private ProgressDialog pDialog;
	Moeda moeda = new Moeda();

	private ImageView facebookImage;

	public CheckoutListAdapter(Context context, int textViewResourceId, List<Checkout> transList, FragmentActivity activity) {
		super(context, textViewResourceId, transList);
		this.checkouts = (ArrayList<Checkout>) transList;
		bms = new BematechServices();
		this.activity = activity;

	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		View v = null;

		if (v == null) {
			LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.item_list_checkout, null);

			v.setOnClickListener(this);

			TextView nomeTxt = (TextView) v.findViewById(R.id.nomeTextView);
			TextView txt_visitas = (TextView) v.findViewById(R.id.txt_visitas);
			TextView txt_gastos = (TextView) v.findViewById(R.id.txt_gastos);
			TextView txt_mesa = (TextView) v.findViewById(R.id.txt_mesa);
			TextView positionTxt = (TextView) v.findViewById(R.id.positionTextView);
			System.out.println(checkouts.get(position).toString());

			facebookImage = (ImageView) v.findViewById(R.id.faceImageView);
			String url = "http://graph.facebook.com/" + checkouts.get(position).getClient().getFacebookId() + "/picture?type=normal";
			ImageLoader.getInstance().displayImage(url, facebookImage);

			txt_mesa.setText("Mesa: " + checkouts.get(position).getClient().getMesa());

			positionTxt.setText("" + position);
			nomeTxt.setText(checkouts.get(position).getClient().getName());
			txt_visitas.setText("Visitas: " + checkouts.get(position).getClient().getTotalVisitas());

			String cen = null, dez = null;
			String valor2 = checkouts.get(position).getClient().getTotalGasto().replace(".0", "");

			if (checkouts.get(position).getClient().getTotalGasto().equalsIgnoreCase("0.0")) {
				cen = "0";
				dez = "0";
			}

			System.out.println("2 TESTE: " + valor2.length());

			for (int i = 0; i < valor2.length(); i++) {
				if (i >= (valor2.length() - 2)) {
					System.out.println("CEN: " + valor2.charAt(i));
					if (cen != null) {
						cen = cen + valor2.charAt(i);
					} else {
						cen = "" + valor2.charAt(i);
					}
				} else {
					System.out.println("DEZ: " + valor2.charAt(i));
					if (dez != null) {
						dez = dez + valor2.charAt(i);
					} else {
						dez = "" + valor2.charAt(i);
					}
				}
			}
			System.out.println("TOTAL: R$ " + dez + "," + cen);
			txt_gastos.setText("Total Gasto " + "R$" + dez + "," + cen);

		}

		return v;
	}

	private void runOnUiThread(Runnable runnable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(final View v) {
		positionTxt = (TextView) v.findViewById(R.id.positionTextView);
		System.out.println("CLICK POSITION------------------------------" + positionTxt.getText());
		int pos = Integer.parseInt(positionTxt.getText().toString());
		Checkout checkout = checkouts.get(pos);
		bms.detalhesClienteExperiencia(checkout.getClient().getEmail(), Constants.ESTABELECIMENTO_ID, detalhesHandler);
	}

	Handler detalhesHandler = new Handler() {
		public void handleMessage(Message message) {
			switch (message.what) {
				case HttpConnection.DID_START: {
					Log.d(CheckinFragment.class.toString(), "Starting connection...");
					pDialog = new ProgressDialog(activity);
					pDialog.setMessage(activity.getString(R.string.detalhes_client_loading));
					pDialog.setIndeterminate(false);
					pDialog.setCancelable(false);
					pDialog.show();
					break;
				}
				case HttpConnection.DID_SUCCEED: {

					Object obj = message.obj;

					try {

						String result = ((BufferedReader) obj).readLine();
						String totalVisitas = ((BufferedReader) obj).readLine();
						String totalGasto = ((BufferedReader) obj).readLine();

						if (totalVisitas.length() < 2)
							totalVisitas = "0" + totalVisitas;

						System.out.println("totalVisitas ? " + totalVisitas);
						System.out.println("totalGasto ? " + totalGasto);
						System.out.println("RESULT ? " + result);

						int pos = Integer.parseInt(positionTxt.getText().toString());
						Checkout checkout = checkouts.get(pos);
						checkout.setTotalGasto(totalGasto);
						checkout.setTotalVisitas(totalVisitas);

						pDialog.dismiss();

						if ("OK".equalsIgnoreCase(result)) {
							System.out.println("ENTROUUU TEM QUE SUBSTITUIR? " + checkout.getClient().getEmail());
							FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
							ft.replace(R.id.root, new ClientDetailsFragment(null, checkout), "datail_checkout");
							ft.commitAllowingStateLoss();
						} else {
							AlertDialog.Builder mensagem = new AlertDialog.Builder(activity);
							mensagem.setTitle(activity.getString(R.string.title_erro_client_details));
							mensagem.setMessage(activity.getString(R.string.msg_erro_client_details));
							mensagem.setNeutralButton("OK", null);
							mensagem.show();
						}

					} catch (IOException e) {
						e.printStackTrace();
					}

					break;
				}
				case HttpConnection.DID_ERROR: {
					pDialog.dismiss();
					AlertDialog.Builder mensagem = new AlertDialog.Builder(activity);
					mensagem.setTitle(activity.getString(R.string.title_erro_client_details));
					mensagem.setMessage(activity.getString(R.string.msg_erro_client_details));
					mensagem.setNeutralButton("OK", null);
					mensagem.show();
					Exception e = (Exception) message.obj;
					e.printStackTrace();
					Log.d(CheckinFragment.class.toString(), "Connection failed. " + e.getMessage());
					break;
				}
			}
		}
	};

}