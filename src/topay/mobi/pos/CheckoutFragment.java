package topay.mobi.pos;


import java.util.List;

import topay.mobi.pos.controller.BematechServices;
import topay.mobi.pos.vo.Checkout;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class CheckoutFragment extends Fragment {
	private GoogleAnalyticsTracker tracker;
	private List<Checkout> checkoutList;
	private TextView positionTxt;
	private BematechServices bms;
	
	public static CheckoutFragment instance;
	
	MainActivity main;

	public CheckoutFragment() {
		// TODO Auto-generated constructor stub
	}

	public CheckoutFragment(List<Checkout> checkout) {
		this.checkoutList = checkout;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		tracker = GoogleAnalyticsTracker.getInstance();
		bms = new BematechServices();
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
		tracker.trackPageView(getString(R.string.google_analytics_trackpage_checkouts));
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
		
		System.out.println("{{{{{{"+checkoutList.size()+"}}}}}}"+"      "+"[[[[["+main.cont_out+"]]]]]: ");

		
		if(checkoutList.size()<main.cont_out && main.last_checkoutList!=null){
			System.out.println("&*&*&(*&(&*(*&(&(*&(*(&*&(*(&*&*(&*&*&*(&&*&*&**&&*(&*????:: "+main.last_checkoutList.size());
			
			
			List<Checkout> teste = main.last_checkoutList;
			
			System.out.println("VE: "+teste.size());
			
			for (int i = 0; i < main.last_checkoutList.size(); i++) {
				for (int j = 0; j < checkoutList.size(); j++) {			
					if(main.last_checkoutList.get(i).getClient().getClientId() == checkoutList.get(j).getClient().getClientId()){
						System.out.println("TESTE: "+i);
						teste.remove(i);
					}
				}
			}		
			
			System.out.println("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF: "+teste.get(0).getClient().getName());
			
			if(teste.get(0).getClient().getName()!=null){
				AlertDialog.Builder mensagem = new AlertDialog.Builder(getActivity());
				mensagem.setTitle("Pagamento");
				if(checkoutList.size()==0){
					mensagem.setMessage(teste.get(0).getClient().getName()+" efetuou o pagamento com sucesso.");
					main.last_checkoutList=null;
				}else {
					mensagem.setMessage(teste.get(0).getClient().getName()+" efetuou o pagamento com sucesso.");				
				}
				mensagem.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						main.cont_out = checkoutList.size();		
						mListView.setAdapter(new CheckoutListAdapter(getActivity().getApplication(), R.layout.item_list, checkoutList, getActivity()));				
						return;
					}
				});
				mensagem.show();
			}			
			
	
//			AlertDialog.Builder mensagem = new AlertDialog.Builder(getActivity());
//			mensagem.setTitle("Pagamento");
//			if(checkoutList.size()==0){
//				mensagem.setMessage(main.last_checkoutList.get(0).getClient().getName()+" efetuou o pagamento com sucesso.");
//				main.last_checkoutList=null;
//			}else {
//				mensagem.setMessage(checkoutList.get(checkoutList.size()-1).getClient().getName()+" efetuou o pagamento com sucesso.");				
//			}
//			mensagem.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					dialog.dismiss();
//					main.cont_out = checkoutList.size();		
//					mListView.setAdapter(new CheckoutListAdapter(getActivity().getApplication(), R.layout.item_list, checkoutList, getActivity()));				
//					return;
//				}
//			});
//			mensagem.show();

		}else{
			main.cont_out = checkoutList.size();		
			mListView.setAdapter(new CheckoutListAdapter(getActivity().getApplication(), R.layout.item_list, checkoutList, getActivity()));
		}
	
	}

}
