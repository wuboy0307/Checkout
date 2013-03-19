package topay.mobi.pos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

public class SplashActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.splash);
		startTimer(3000);
		// FacebookServices fb = new FacebookServices(this);
		// fb.requestUserData("100001564502945");
	}

	public void startTimer(final int timeMiliSec) {
		new Thread() {
			@Override
			public void run() {
				try {
					int logoTimer = 0;
					while (logoTimer < timeMiliSec) {
						sleep(100);
						logoTimer += 100;
					}
					onFinishTimer();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	public void onFinishTimer() {

		startActivity(new Intent(this, MainActivity.class));

		finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}
}