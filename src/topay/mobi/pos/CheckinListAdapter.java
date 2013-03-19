package topay.mobi.pos;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import topay.mobi.pos.controller.BematechServices;
import topay.mobi.pos.util.Constants;
import topay.mobi.pos.util.HttpConnection;
import topay.mobi.pos.util.Moeda;
import topay.mobi.pos.vo.Checkin;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.MemoryCacheUtil;

public class CheckinListAdapter extends ArrayAdapter<Checkin> implements OnClickListener {

	private ArrayList<Checkin> checkins;
	private BematechServices bms;
	private TextView positionTxt;
	public static FragmentActivity activity;
	private ProgressDialog pDialog;
	Moeda moeda = new Moeda();
	private ImageView facebookImage;
	String url;

	public CheckinListAdapter(Context context, int textViewResourceId, List<Checkin> transList, FragmentActivity activity) {
		super(context, textViewResourceId, transList);
		this.checkins = (ArrayList<Checkin>) transList;
		bms = new BematechServices();
		this.activity = activity;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		View v = null;

		if (v == null) {
			LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.item_list, null);

			v.setOnClickListener(this);

			TextView nomeTxt = (TextView) v.findViewById(R.id.nomeTextView);
			TextView txt_visitas = (TextView) v.findViewById(R.id.txt_visitas);
			TextView txt_gastos = (TextView) v.findViewById(R.id.txt_gastos);
			TextView txt_mesa = (TextView) v.findViewById(R.id.txt_mesa);
			TextView positionTxt = (TextView) v.findViewById(R.id.positionTextView);
			System.out.println(checkins.get(position).toString());

			txt_mesa.setText("Mesa: " + checkins.get(position).getClient().getMesa());

			positionTxt.setText("" + position);
			nomeTxt.setText(checkins.get(position).getClient().getName());
			txt_visitas.setText("Visitas: " + checkins.get(position).getClient().getTotalVisitas());
			// txt_gastos.setText("Total Gasto " + moeda.mascaraDinheiro(Double.parseDouble(checkins.get(position).getClient().getTotalGasto()), Moeda.DINHEIRO_REAL));

			String cen = null, dez = null;
			String valor2 = checkins.get(position).getClient().getTotalGasto().replace(".0", "");

			if (checkins.get(position).getClient().getTotalGasto().equalsIgnoreCase("0.0")) {
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
			System.out.println("NNNNNNNNNNNNNNNNNNNNNNN: "+checkins.get(position).getClient().getName());
			System.out.println("RRRRRRRRRRRRRRRRRRRRRRR: "+checkins.get(position).getDate());
			
			
			System.out.println("TOTAL: R$ " + dez + "," + cen);
			txt_gastos.setText("Total Gasto " + "R$" + dez + "," + cen);
			facebookImage = (ImageView) v.findViewById(R.id.faceImageView);
			url = "http://graph.facebook.com/" + checkins.get(position).getClient().getFacebookId() + "/picture?type=normal";
			ImageLoader.getInstance().displayImage(url, facebookImage);	

		}

		return v;
	}

	@Override
	public void onClick(final View v) {
		positionTxt = (TextView) v.findViewById(R.id.positionTextView);
		System.out.println("CLICK POSITION------------------------------" + positionTxt.getText());
		
		
		System.out.println("CHAVES DO CACHE: "+ImageLoader.getInstance().getMemoryCache().keys());
		
		int pos = Integer.parseInt(positionTxt.getText().toString());
		Checkin checkin = checkins.get(pos);
		bms.detalhesClienteExperiencia(checkin.getClient().getEmail(), Constants.ESTABELECIMENTO_ID, detalhesHandler);
	}

	Handler detalhesHandler = new Handler() {
		public void handleMessage(Message message) {
			switch (message.what) {
				case HttpConnection.DID_START: {
					pDialog = new ProgressDialog(activity);
					pDialog.setMessage(activity.getString(R.string.detalhes_client_loading));
					pDialog.setIndeterminate(false);
					pDialog.setCancelable(false);
					pDialog.show();
					Log.d(CheckinFragment.class.toString(), "Starting connection...");
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
						Checkin checkin = checkins.get(pos);
						checkin.setTotalGasto(totalGasto);
						checkin.setTotalVisitas(totalVisitas);

						pDialog.dismiss();

						if ("OK".equalsIgnoreCase(result)) {
							FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
							ft.remove(CheckinFragment.instance);
							ft.replace(R.id.root, new ClientDetailsFragment(checkin, null), "datail_checkin");
							ft.commit();
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
