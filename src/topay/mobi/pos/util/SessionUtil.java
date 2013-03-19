package topay.mobi.pos.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionUtil {
	public String mesa;
	private static final String PREF_NAME="SessionUtil";
	private static SessionUtil INSTANCE;
	private Context c;

	public static SessionUtil getInstance(Context c) {

		if (INSTANCE==null) {
			INSTANCE=new SessionUtil();

			INSTANCE.c=c;
			INSTANCE.load();
		}

		return INSTANCE;
	}

	public void load() {
		SharedPreferences preferences=c.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		mesa=preferences.getString("mesa", "");	
	}

	public void commit() {

		SharedPreferences preferences=c.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor=preferences.edit();
		editor.putString("mesa", mesa);
		editor.commit();
	}	

	public String getMesa() {
		return mesa;
	}

	public void setMesa(String mesa) {
		this.mesa=mesa;
	}
	
}
