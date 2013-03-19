package topay.mobi.pos;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import topay.mobi.pos.controller.BematechServices;
import topay.mobi.pos.util.HttpConnection;
import topay.mobi.pos.util.SessionUtil;
import topay.mobi.pos.vo.Checkin;
import topay.mobi.pos.vo.Checkout;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class CheckinFragment extends SherlockFragment {

	public static CheckinFragment instance;
	private GoogleAnalyticsTracker tracker;
	public static List<Checkin> checkinList = new ArrayList<Checkin>();
	
	private BematechServices bms;
	private AlertDialog.Builder mensagem;
	private ProgressDialog pDialog;

	public CheckinFragment() {
	}

	public CheckinFragment(List<Checkin> checkins) {
		this.checkinList = checkins;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		tracker = GoogleAnalyticsTracker.getInstance();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.checkins, container, false);

		return v;
	}

	@Override
	public void onResume() {
		super.onResume();
		tracker.startNewSession(getString(R.string.google_analytics_ua_account), Integer.parseInt(getString(R.string.google_analytics_timeout)), getActivity());
		tracker.trackPageView(getString(R.string.google_analytics_trackpage_checkins));
		tracker.dispatch();
	}

	@Override
	public void onPause() {
		super.onPause();
		tracker.stopSession();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		tracker.stopSession();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		final ListView mListView = (ListView) getActivity().findViewById(R.id.list);
				
		System.out.println("[[[[["+MainActivity.cont_in+"]]]]]");
		System.out.println("{{{{{{"+checkinList.size()+"}}}}}}");
			
		if(MainActivity.checkinList.size()<MainActivity.cont_in && MainActivity.checkoutList.size() != 0){
								
			AlertDialog.Builder mensagem = new AlertDialog.Builder(getActivity());
			mensagem.setTitle("Pagamento");			
			mensagem.setMessage("Cliente "+MainActivity.checkoutList.get(MainActivity.checkoutList.size()-1).getClient().getName()+" da mesa "+
					MainActivity.checkoutList.get(MainActivity.checkoutList.size()-1).getClient().getMesa()+" solicitou pagamento.");						
			mensagem.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					MainActivity.cont_in = checkinList.size();		
					
					System.out.println("Entrou no CheckinFragment");
					
					MainActivity.ab.setSelectedNavigationItem(1);
																				
					FragmentTransaction ft = getFragmentManager().beginTransaction();
					ft.remove(CheckinFragment.instance);
					ft.replace(R.id.root, new CheckoutFragment(MainActivity.checkoutList), "checkout");
					ft.commit();
					
					return;
				}
			});
			mensagem.show();

		}else if(MainActivity.checkinList.size()>MainActivity.cont_in && ClientDetailsFragment.dif == 0){
					

			
			MainActivity.cont_in = checkinList.size();			
			final Context context = getActivity().getApplicationContext();
			
			AlertDialog.Builder mensagem = new AlertDialog.Builder(getActivity());
			mensagem.setTitle("Checkin");
			mensagem.setMessage(MainActivity.checkinList.get(checkinList.size()-1).getClient().getName()+" acabou de entrar no estabelecimento.");
			mensagem.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					mListView.setAdapter(new CheckinListAdapter(context, R.layout.item_list, checkinList, getActivity()));				
					return;
				}
			});
			mensagem.show();
		}else {		
			if(checkinList.size()>MainActivity.cont_in){
				ClientDetailsFragment.dif = 0;
			}
			MainActivity.cont_in = checkinList.size();			
			mListView.setAdapter(new CheckinListAdapter(getActivity().getApplication(), R.layout.item_list, checkinList, getActivity()));
		}
		

	}
	
	Handler handler = new Handler() {
		public void handleMessage(Message message) {
			switch (message.what) {
				case HttpConnection.DID_START: {
					pDialog = new ProgressDialog(getActivity());
					pDialog.setMessage(getActivity().getString(R.string.detalhes_client_loading));
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

						System.out.println("RESULT ? " + result);

						if ("OK".equalsIgnoreCase(result)) {
							pDialog.dismiss();
							System.out.println("SUUUUUCESSOOOOOOOO!!!");
//							showCustomDialog();
							FragmentTransaction ft = getFragmentManager().beginTransaction();
							ft.remove(ClientDetailsFragment.instance);
							ft.replace(R.id.root, new CheckoutFragment(MainActivity.checkoutList), "checkout");
							ft.commit();
							ClientDetailsFragment.instance = null;
						} else {
							pDialog.dismiss();
							mensagem.setTitle(getActivity().getString(R.string.title_erro_client_details));
							mensagem.setMessage(getActivity().getString(R.string.msg_erro_client_details));
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
					mensagem.setTitle(getActivity().getString(R.string.title_erro_client_details));
					mensagem.setMessage(getActivity().getString(R.string.msg_erro_client_details));
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
	
	private void showCustomDialog(){

    	final Dialog dialog = new Dialog(getActivity());

    	dialog.setContentView(R.layout.pagamento_detalils);//carregando o layout do dialog do xml
    	dialog.setTitle("Pagamento");//t’tulo do dialog


    	dialog.show();//mostra o dialog

    }

}
