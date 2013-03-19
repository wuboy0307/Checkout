package topay.mobi.pos.controller;

import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import topay.mobi.pos.facebook.BaseRequestListener;
import topay.mobi.pos.facebook.SessionStore;
import topay.mobi.pos.facebook.Utility;
import topay.mobi.pos.vo.Client;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class FacebookServices {
	private static final String[] PERMISSIONS = new String[] { "publish_stream", "read_stream", "offline_access" };
	Context context;
	Activity act;

	public FacebookServices(Activity act) {
		this.act = act;
		this.context = act.getApplicationContext();
		Utility.mFacebook = new Facebook(Utility.APP_ID);
		// Instancia o objeto asynrunner para chamadas de API.
		Utility.mAsyncRunner = new AsyncFacebookRunner(Utility.mFacebook);

		// restaura a sess�o, se houver
		SessionStore.restore(Utility.mFacebook, context);

		if (!Utility.mFacebook.isSessionValid())
			login();
		else
			requestUserData("100001564502945");

	}

	public void login() {
		Utility.mFacebook.authorize(act, Utility.permissions, Facebook.FORCE_DIALOG_AUTH, new LoginDialogListener());
	}

	private final class LoginDialogListener implements DialogListener {

		public void onError(DialogError error) {
			// tracker.setCustomVar(1, "Facebook", "Login Erro: " + error.getMessage());
		}

		public void onComplete(Bundle values) {
			// tracker.setCustomVar(1, "Facebook", "Login Complete");
			SessionStore.save(Utility.mFacebook, context);
			requestUserData("100001564502945");
		}

		public void onFacebookError(FacebookError error) {
			// MenssagemUtil.mensagemExibir(context, getString(R.string.mesg_erro), getString(R.string.erro_msg_checking));
			// tracker.setCustomVar(1, "Facebook", "Login FacebookErro: " + error.getMessage());
		}

		public void onCancel() {
			// tracker.setCustomVar(1, "Facebook", "Login Cancel");

		}
	}

	/*
	 * Request, com os campos que deseja pegar do usuario.
	 */
	public void requestUserData(String facebookId) {
		Bundle params = new Bundle();
		params.putString("fields", "name, email, locale, birthday, location");
		Utility.mAsyncRunner.request(facebookId, params, new UserRequestListener());

	}

	/*
	 * Captura os campos do usuario no facebook
	 */
	public class UserRequestListener extends BaseRequestListener {

		public void onComplete(final String response, final Object state) {

			JSONObject jsonObject;
			String local = null;
			String nascimento = null;
			String cidade = null;
			String estado = null;
			Client cliente = new Client();
			try {
				jsonObject = new JSONObject(response);

				String name = jsonObject.getString("name");
				cliente.setName(name);
				String idFace = jsonObject.getString("id");
				cliente.setFacebookId(idFace);
				String email = jsonObject.getString("email");
				String lingua = jsonObject.getString("locale");
				String telefone = jsonObject.getString("telefone");
				nascimento = jsonObject.getString("birthday");
				local = jsonObject.getJSONObject("location").getString("name");
				String[] t = local.split(Pattern.quote(","));
				cidade = t[0];
				estado = t[1];

				System.out.println("NOME: " + name);
				System.out.println("cidade: " + cidade);
				System.out.println("EMAIL: " + email);
				System.out.println("LOCAL: " + lingua);
				System.out.println("name: " + local);
				System.out.println("TELEFONE: " + telefone);
				System.out.println("estado: " + estado);

				cliente.setEmail(email);
				cliente.setFone(telefone);
				cliente.setNascimento(nascimento);
				cliente.setCidade(cidade);
				cliente.setEstado(estado);

			} catch (JSONException e) {
				e.printStackTrace();
			}

		}

	}
}
