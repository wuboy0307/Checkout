package topay.mobi.pos.facebook;

import org.json.JSONObject;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import android.app.Application;

public class Utility extends Application {

	public static final String APP_ID = "297754350247264";
	public static Facebook mFacebook;
    public static AsyncFacebookRunner mAsyncRunner;
    public static JSONObject mFriendsList;
    public static String[] permissions = {"email", "user_location", "user_hometown", "user_activities", "user_birthday", 
    	"publish_actions","offline_access", "publish_stream", "user_photos", "publish_checkins", "user_checkins", 
    	"friends_checkins", "user_games_activity", "friends_games_activity", "read_friendlists", "read_requests"};

}